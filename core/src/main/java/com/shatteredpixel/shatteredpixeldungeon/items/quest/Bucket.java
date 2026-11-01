/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Bucket extends Item {

	private static final String AC_FILL = "FILL";
	private boolean filled;

	{
		image = ItemSpriteSheet.BUCKET;
		unique = true;
		defaultAction = AC_FILL;
	}

	public boolean isFilled() {
		return filled;
	}

	@Override
	public String name() {
		return Messages.get(this, filled ? "name_water" : "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, filled ? "desc_water" : "desc");
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (!filled) actions.add(AC_FILL);
		return actions;
	}

	@Override
	public String defaultAction() {
		return filled ? null : AC_FILL;
	}

	@Override
	public void execute(final Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_FILL) && !filled && hero.belongings.contains(this)) {
			GameScene.selectCell(new CellSelector.Listener() {
				@Override
				public void onSelect(Integer cell) {
					if (cell == null) return;
					if (Dungeon.level instanceof SurfaceLevel
							&& ((SurfaceLevel) Dungeon.level).isWellCell(cell)
							&& (Dungeon.level.visited[cell] || Dungeon.level.mapped[cell])) {
						if (hero.handle(cell)) hero.next();
					} else {
						GLog.w(Messages.get(Bucket.this, "not_well"));
					}
				}

				@Override
				public String prompt() {
					return Messages.get(Bucket.this, "prompt");
				}
			});
		}
	}

	// Called after the hero has reached the well; the same item keeps its slot.
	public boolean fill(Hero hero, int cell) {
		if (filled || !hero.belongings.contains(this)
				|| !(Dungeon.level instanceof SurfaceLevel)
				|| !((SurfaceLevel) Dungeon.level).isWellCell(cell)
				|| !Dungeon.level.adjacent(hero.pos, cell)) {
			return false;
		}
		filled = true;
		image = ItemSpriteSheet.WATER_BUCKET;
		updateQuickslot();
		GLog.p(Messages.get(this, "filled"));
		hero.busy();
		hero.sprite.operate(cell);
		hero.spend(1f);
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	private static final String FILLED = "filled";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(FILLED, filled);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		filled = bundle.getBoolean(FILLED);
		image = filled ? ItemSpriteSheet.WATER_BUCKET : ItemSpriteSheet.BUCKET;
	}
}

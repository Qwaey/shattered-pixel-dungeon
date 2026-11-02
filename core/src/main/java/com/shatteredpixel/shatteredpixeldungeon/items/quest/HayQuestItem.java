/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HayQuest;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class HayQuestItem extends Item {

	{
		unique = true;
		defaultAction = "USE";
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add("USE");
		return actions;
	}

	@Override
	public void execute(final Hero hero, String action) {
		super.execute(hero, action);
		if (!action.equals("USE") || !hero.belongings.contains(this)) return;
		GameScene.selectCell(new CellSelector.Listener() {
			@Override
			public void onSelect(Integer cell) {
				if (cell == null) return;
				HayQuest quest = Dungeon.level instanceof SurfaceLevel
						? ((SurfaceLevel) Dungeon.level).hayQuest() : null;
				if (quest != null && quest.isInteractionCell(cell)
						&& (Dungeon.level.visited[cell] || Dungeon.level.mapped[cell])) {
					if (hero.handle(cell)) hero.next();
				} else {
					GLog.i(Messages.get(HayQuestItem.this, "wrong_place"));
				}
			}

			@Override
			public String prompt() {
				return Messages.get(HayQuestItem.this, "prompt");
			}
		});
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	public static class Rake extends HayQuestItem {
		{
			image = ItemSpriteSheet.HAY_RAKE;
		}
	}

	public static class EggBox extends HayQuestItem {
		private boolean filled;

		{
			image = ItemSpriteSheet.EMPTY_EGG_BOX;
		}

		public boolean isFilled() {
			return filled;
		}

		public void fill() {
			filled = true;
			image = ItemSpriteSheet.EGG_BOX;
			updateQuickslot();
		}

		@Override
		public String name() {
			return Messages.get(this, filled ? "name_full" : "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, filled ? "desc_full" : "desc");
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put("filled", filled);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			filled = bundle.getBoolean("filled");
			image = filled ? ItemSpriteSheet.EGG_BOX : ItemSpriteSheet.EMPTY_EGG_BOX;
		}
	}
}

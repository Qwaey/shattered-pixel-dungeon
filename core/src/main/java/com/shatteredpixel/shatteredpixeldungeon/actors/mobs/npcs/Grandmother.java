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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Bucket;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GrandmotherSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Grandmother extends NPC {

	private boolean given;
	private boolean completed;

	{
		spriteClass = GrandmotherSprite.class;
		properties.add(Property.IMMOVABLE);
	}

	@Override
	public Notes.Landmark landmark() {
		return given && !completed ? Notes.Landmark.GRANDMOTHER : null;
	}

	@Override
	public boolean interact(Char c) {
		sprite.turnTo(pos, c.pos);
		if (c != Dungeon.hero) return true;
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				showQuest();
			}
		});
		return true;
	}

	private void showQuest() {
		if (completed) {
			GameScene.show(new WndQuest(this, Messages.get(this, "after")));
		} else if (!given) {
			GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()),
					Messages.get(this, "request"),
					Messages.get(this, "accept"), Messages.get(this, "decline")) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) acceptQuest();
				}
			});
		} else {
			final Bucket bucket = Dungeon.hero.belongings.getItem(Bucket.class);
			if (bucket != null && bucket.isFilled()) {
				GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()),
						Messages.get(this, "return"),
						Messages.get(this, "give"), Messages.get(this, "later")) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) completeQuest(bucket);
					}
				});
			} else {
				GameScene.show(new WndQuest(this, Messages.get(this, "reminder")));
			}
		}
	}

	private void acceptQuest() {
		if (given || completed) return;
		Bucket bucket = new Bucket();
		if (!bucket.collect()) {
			GameScene.show(new WndQuest(this, Messages.get(this, "no_space")));
			return;
		}
		given = true;
		Notes.add(Notes.Landmark.GRANDMOTHER);
		GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", bucket.name())));
		GameScene.show(new WndQuest(this, Messages.get(this, "accepted")));
	}

	private void completeQuest(Bucket bucket) {
		if (!given || completed || !bucket.isFilled()
				|| !Dungeon.hero.belongings.contains(bucket)) return;
		bucket.detachAll(Dungeon.hero.belongings.backpack);
		completed = true;
		Notes.remove(Notes.Landmark.GRANDMOTHER);
		Food reward = new Food();
		if (reward.doPickUp(Dungeon.hero)) {
			GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())));
		} else {
			Dungeon.level.drop(reward, Dungeon.hero.pos).sprite.drop();
		}
		GameScene.show(new WndQuest(this, Messages.get(this, "thanks")));
	}

	@Override
	public int defenseSkill(Char enemy) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage(int dmg, Object src) {
		// Dialogue NPCs cannot be harmed.
	}

	@Override
	public boolean add(Buff buff) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	private static final String GIVEN = "given";
	private static final String COMPLETED = "completed";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(GIVEN, given);
		bundle.put(COMPLETED, completed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		given = bundle.getBoolean(GIVEN);
		completed = bundle.getBoolean(COMPLETED);
	}
}

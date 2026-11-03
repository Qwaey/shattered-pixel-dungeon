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
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.BerryPie;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Bucket;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.FlourSack;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.PieBerries;
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

	private enum Stage { NEW, WATER, FLOUR, BERRIES, COMPLETED }

	private Stage stage = Stage.NEW;

	{
		spriteClass = GrandmotherSprite.class;
		properties.add(Property.IMMOVABLE);
	}

	@Override
	public Notes.Landmark landmark() {
		switch (stage) {
			case WATER:   return Notes.Landmark.GRANDMOTHER;
			case FLOUR:   return Notes.Landmark.GRANDMOTHER_FLOUR;
			case BERRIES: return Notes.Landmark.GRANDMOTHER_BERRIES;
			default:      return null;
		}
	}

	public boolean needsFlour() {
		return stage == Stage.FLOUR;
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
		if (stage == Stage.COMPLETED) {
			GameScene.show(new WndQuest(this, Messages.get(this, "after")));
		} else if (stage == Stage.NEW) {
			GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()),
					Messages.get(this, "request"),
					Messages.get(this, "accept"), Messages.get(this, "decline")) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) acceptQuest();
				}
			});
		} else {
			final Stage expectedStage = stage;
			final String topic = stage.name().toLowerCase(java.util.Locale.ROOT);
			final Item ingredient;
			if (stage == Stage.WATER) {
				Bucket bucket = Dungeon.hero.belongings.getItem(Bucket.class);
				ingredient = bucket != null && bucket.isFilled() ? bucket : null;
			} else if (stage == Stage.FLOUR) {
				ingredient = Dungeon.hero.belongings.getItem(FlourSack.class);
			} else {
				ingredient = Dungeon.hero.belongings.getItem(PieBerries.class);
			}
			if (ingredient != null) {
				GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()),
						Messages.get(this, topic + "_return"),
						Messages.get(this, topic + "_give"), Messages.get(this, "later")) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) receiveIngredient(ingredient, expectedStage);
					}
				});
			} else {
				GameScene.show(new WndQuest(this, Messages.get(this, topic + "_reminder")));
			}
		}
	}

	private void acceptQuest() {
		if (stage != Stage.NEW) return;
		Bucket bucket = new Bucket();
		if (!bucket.collect()) {
			GameScene.show(new WndQuest(this, Messages.get(this, "no_space")));
			return;
		}
		stage = Stage.WATER;
		Notes.add(landmark());
		GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", bucket.name())));
		GameScene.show(new WndQuest(this, Messages.get(this, "accepted")));
	}

	private void receiveIngredient(Item ingredient, Stage expectedStage) {
		if (stage != expectedStage || !Dungeon.hero.belongings.contains(ingredient)) return;
		ingredient.detachAll(Dungeon.hero.belongings.backpack);
		Notes.remove(landmark());
		if (stage == Stage.WATER || stage == Stage.FLOUR) {
			String reply = stage == Stage.WATER ? "water_received" : "flour_received";
			stage = stage == Stage.WATER ? Stage.FLOUR : Stage.BERRIES;
			Notes.add(landmark());
			GameScene.show(new WndQuest(this, Messages.get(this, reply)));
			return;
		}
		stage = Stage.COMPLETED;
		BerryPie reward = new BerryPie();
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

	private static final String STAGE = "stage";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STAGE, stage);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		stage = bundle.getEnum(STAGE, Stage.class);
	}
}

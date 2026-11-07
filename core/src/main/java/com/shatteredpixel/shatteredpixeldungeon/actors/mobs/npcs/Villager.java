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
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LumberjackSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceHandcart;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;

public class Villager extends NPC {

	{
		spriteClass = LumberjackSprite.class;
		properties.add(Property.IMMOVABLE);
	}

	@Override
	public boolean interact(Char c) {
		sprite.turnTo(pos, c.pos);

		if (c != Dungeon.hero) {
			return true;
		}

		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				showTopics();
			}
		});
		return true;
	}

	private void showTopics() {
		boolean hasRoadTopic = CampaignQuest.canComplete(6);
		String[] topics = hasRoadTopic
				? new String[] { Messages.get(this, "ask_story"), Messages.get(this, "ask_place"),
						Messages.get(this, "road_topic"), Messages.get(this, "goodbye") }
				: new String[] { Messages.get(this, "ask_story"), Messages.get(this, "ask_place"),
						Messages.get(this, "goodbye") };
		GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()),
				Messages.get(this, "greeting"), topics) {
			@Override
			protected void onSelect(int index) {
				if (index == 0) {
					showReply("story");
				} else if (index == 1) {
					showReply("place");
				} else if (index == 2 && hasRoadTopic) {
					CampaignQuest.hearWoodcutter();
					showReply("road");
				}
			}
		});
	}

	private SurfaceHandcart handcart() {
		return Dungeon.level instanceof SurfaceLevel ? ((SurfaceLevel) Dungeon.level).handcart() : null;
	}

	private void showReply(String topic) {
		String reply = Messages.get(this, topic);
		SurfaceHandcart cart = handcart();
		if (!"road".equals(topic) && cart != null && !cart.isMoved()) {
			reply += "\n\n" + Messages.get(this, topic + "_cart");
		}
		GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()),
				reply,
				Messages.get(this, "back"),
				Messages.get(this, "goodbye")) {
			@Override
			protected void onSelect(int index) {
				if (index == 0) {
					showTopics();
				}
			}
		});
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

}

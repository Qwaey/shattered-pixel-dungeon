/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HayQuest;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MironSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;

public class Miron extends NPC {

	{
		spriteClass = MironSprite.class;
		properties.add(Property.IMMOVABLE);
	}

	@Override
	public Notes.Landmark landmark() {
		HayQuest quest = Dungeon.level instanceof SurfaceLevel
				? ((SurfaceLevel) Dungeon.level).hayQuest() : null;
		return quest == null ? null : quest.landmark();
	}

	@Override
	public boolean interact(Char c) {
		sprite.turnTo(pos, c.pos);
		if (c == Dungeon.hero) Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				HayQuest quest = Dungeon.level instanceof SurfaceLevel
						? ((SurfaceLevel) Dungeon.level).hayQuest() : null;
				if (quest != null && quest.rewardReady()) {
					quest.talk(Miron.this);
				} else {
					showTopics();
				}
			}
		});
		return true;
	}

	public void say(String key) {
		GameScene.show(new WndQuest(this, Messages.get(this, key)));
	}

	private void showTopics() {
		GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()),
				Messages.get(this, "greeting"), Messages.get(this, "ask_story"),
				Messages.get(this, "ask_work"), Messages.get(this, "ask_hay"),
				Messages.get(this, "goodbye")) {
			@Override
			protected void onSelect(int index) {
				if (index == 0 || index == 1) {
					showReply(index == 0 ? "story" : "work");
				} else if (index == 2 && Dungeon.level instanceof SurfaceLevel) {
					HayQuest quest = ((SurfaceLevel) Dungeon.level).hayQuest();
					if (quest != null) quest.talk(Miron.this);
				}
			}
		});
	}

	private void showReply(String topic) {
		GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()),
				Messages.get(this, topic), Messages.get(this, "back"), Messages.get(this, "goodbye")) {
			@Override
			protected void onSelect(int index) {
				if (index == 0) showTopics();
			}
		});
	}

	@Override
	public int defenseSkill(Char enemy) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage(int dmg, Object src) {
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

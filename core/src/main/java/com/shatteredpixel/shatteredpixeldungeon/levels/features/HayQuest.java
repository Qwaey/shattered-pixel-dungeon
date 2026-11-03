/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Senogryz;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Miron;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

// One authored encounter. Its state belongs to this particular surface level.
public class HayQuest implements Bundlable {

	public static final int FIRST_STACK_X = 19, FIRST_STACK_Y = 27;
	public static final int SECOND_STACK_X = 24, SECOND_STACK_Y = 26;
	public static final int THIRD_STACK_X = 24, THIRD_STACK_Y = 30;
	private static final int REWARD_DEW = 3;

	private boolean accepted, released, killed, completed;

	private SurfaceLevel level() {
		return Dungeon.level instanceof SurfaceLevel
				&& ((SurfaceLevel) Dungeon.level).hayQuest() == this
				? (SurfaceLevel) Dungeon.level : null;
	}

	private int cell(int x, int y) {
		return y * Dungeon.level.width() + x;
	}

	private boolean emptyStack(int cell) {
		return cell == cell(FIRST_STACK_X, FIRST_STACK_Y) || cell == cell(FIRST_STACK_X + 1, FIRST_STACK_Y)
				|| cell == cell(SECOND_STACK_X, SECOND_STACK_Y) || cell == cell(SECOND_STACK_X + 1, SECOND_STACK_Y);
	}

	private boolean thirdStack(int cell) {
		return cell == cell(THIRD_STACK_X, THIRD_STACK_Y)
				|| cell == cell(THIRD_STACK_X + 1, THIRD_STACK_Y);
	}

	public boolean isInteractionCell(int cell) {
		return accepted && level() != null && cell >= 0 && cell < Dungeon.level.length()
				&& (emptyStack(cell) || thirdStack(cell));
	}

	private boolean withinReach(Hero hero, int cell) {
		return isInteractionCell(cell)
				&& (hero.pos == cell || Dungeon.level.adjacent(hero.pos, cell));
	}

	public void interact(Hero hero, int cell) {
		if (!withinReach(hero, cell)) return;
		if (thirdStack(cell) && !released) {
			if (!releaseBeetle()) {
				message("blocked");
				return;
			}
		}
		spendWork(hero, cell);
		if (emptyStack(cell)) GLog.i(Messages.get(this, "empty_stack"));
	}

	private boolean releaseBeetle() {
		if (level() == null || released) return false;
		// Emerge beside the lowest stack, clear of the hay when approached from the north.
		int spawn = cell(THIRD_STACK_X - 1, THIRD_STACK_Y);
		if (!Dungeon.level.passable[spawn] || Actor.findChar(spawn) != null
				|| Dungeon.level.heaps.get(spawn) != null) return false;
		Senogryz beetle = new Senogryz();
		beetle.pos = spawn;
		beetle.markHayQuestBeetle();
		released = true;
		GameScene.add(beetle, Actor.TICK);
		beetle.beckon(Dungeon.hero.pos);
		Dungeon.observe();
		GLog.w(Messages.get(this, "emerged"));
		return true;
	}

	private void spendWork(Hero hero, int cell) {
		hero.busy();
		hero.sprite.operate(cell);
		hero.spend(1f);
	}

	private void message(String key) {
		GameScene.show(new WndOptions(Messages.get(this, "title"),
				Messages.get(this, key), Messages.get(this, "close")));
	}

	public boolean rewardReady() {
		return accepted && killed && !completed;
	}

	private int[] rewardDropCells(Hero hero, int count) {
		SurfaceLevel level = level();
		int[] cells = new int[count];
		int found = 0;
		// Prefer the hero's cell, then nearby cells, without merging with existing dew.
		for (int distance = 0; distance < Math.max(level.width(), level.height()) && found < count; distance++) {
			for (int cell = 0; cell < level.length() && found < count; cell++) {
				if (level.distance(hero.pos, cell) != distance
						|| !level.heroFOV[cell] || !level.passable[cell] || level.avoid[cell]
						|| level.pit[cell] || level.heaps.get(cell) != null
						|| (cell != hero.pos && Actor.findChar(cell) != null)) continue;
				cells[found++] = cell;
			}
		}
		return found == count ? cells : null;
	}

	public void talk(final Miron miron) {
		if (level() == null) return;
		if (completed) {
			miron.say("after");
		} else if (!accepted) {
			GameScene.show(new WndOptions(miron.sprite(), Messages.titleCase(miron.name()),
					Messages.get(miron, "request"), Messages.get(miron, "accept"), Messages.get(miron, "decline")) {
				@Override
				protected void onSelect(int index) {
					if (index != 0 || accepted || level() == null) return;
					accepted = true;
					updateNotes();
					miron.say("accepted");
				}
			});
		} else if (killed) {
			Hero hero = Dungeon.hero;
			Waterskin waterskin = hero.belongings.getItem(Waterskin.class);
			int collected = waterskin == null ? 0 : Math.min(REWARD_DEW, waterskin.freeCapacity());
			int[] dropCells = rewardDropCells(hero, REWARD_DEW - collected);
			if (dropCells == null) {
				miron.say("reward_blocked");
				return;
			}
			completed = true;
			updateNotes();
			for (int i = 0; i < REWARD_DEW; i++) {
				Dewdrop dew = new Dewdrop();
				if (i < collected) {
					waterskin.collectDew(dew);
				} else {
					Dungeon.level.drop(dew, dropCells[i - collected]).sprite.drop();
				}
			}
			miron.say("return");
		} else {
			miron.say(released ? "fight_reminder" : "reminder");
		}
	}

	public void beetleDied() {
		if (!released || killed) return;
		killed = true;
		updateNotes();
		GLog.i(Messages.get(this, "beetle_dead"));
	}

	public Notes.Landmark landmark() {
		if (!accepted || completed) return null;
		return killed ? Notes.Landmark.HAY_REWARD : Notes.Landmark.HAY_FIELD;
	}

	private void updateNotes() {
		Notes.remove(Notes.Landmark.HAY_FIELD);
		Notes.remove(Notes.Landmark.HAY_REWARD);
		if (landmark() != null) Notes.add(landmark());
	}

	public String stackDesc(int x, int y) {
		if ((x == FIRST_STACK_X && y == FIRST_STACK_Y)
				|| (x == SECOND_STACK_X && y == SECOND_STACK_Y)) return Messages.get(this, "hay");
		if (x != THIRD_STACK_X || y != THIRD_STACK_Y) return null;
		return Messages.get(this, killed ? "quiet" : released ? "fighting" : "hay");
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put("accepted", accepted);
		bundle.put("released", released);
		bundle.put("killed", killed);
		bundle.put("completed", completed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		accepted = bundle.getBoolean("accepted");
		released = bundle.getBoolean("released");
		killed = bundle.getBoolean("killed");
		completed = bundle.getBoolean("completed");
	}
}

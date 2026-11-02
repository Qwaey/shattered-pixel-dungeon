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
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.HayQuestItem.EggBox;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.HayQuestItem.Rake;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.HayQuestTile;
import com.shatteredpixel.shatteredpixeldungeon.tiles.HayQuestTile.Kind;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

// One authored encounter. Its state belongs to this particular surface level.
public class HayQuest implements Bundlable {

	private boolean accepted, shellFound, disturbed, released, killed, nestFound;
	private boolean boxGiven, boxed, completed, hatched;
	private int outcome; // 0 = undecided, 1 = destroyed, 2 = rescued

	public void decorate(SurfaceLevel level) {
		level.customTiles.add(new HayQuestTile(48, 30, Kind.ROPE));
		level.customTiles.add(new HayQuestTile(49, 30, Kind.SHELL));
		level.customTiles.add(new HayQuestTile(54, 32, Kind.NEST));
		level.customTiles.add(new HayQuestTile(56, 35, Kind.REFUGE));
		level.customTiles.add(new HayQuestTile(56, 36, Kind.TRACKS));
	}

	private SurfaceLevel level() {
		return Dungeon.level instanceof SurfaceLevel
				&& ((SurfaceLevel) Dungeon.level).hayQuest() == this
				? (SurfaceLevel) Dungeon.level : null;
	}

	private int cell(int x, int y) {
		return y * Dungeon.level.width() + x;
	}

	private boolean firstStack(int cell) {
		return cell == cell(49, 29) || cell == cell(50, 29);
	}

	private boolean secondStack(int cell) {
		return cell == cell(54, 31) || cell == cell(55, 31);
	}

	public boolean isInteractionCell(int cell) {
		return level() != null && cell >= 0 && cell < Dungeon.level.length()
				&& (firstStack(cell) || secondStack(cell) || cell == cell(48, 30)
				|| shellFound && cell == cell(49, 30)
				|| nestFound && !boxed && outcome == 0 && cell == cell(54, 32)
				|| cell == cell(56, 35));
	}

	private boolean withinReach(Hero hero, int cell) {
		return level() != null && isInteractionCell(cell)
				&& (hero.pos == cell || Dungeon.level.adjacent(hero.pos, cell));
	}

	public void interact(final Hero hero, final int cell) {
		if (!withinReach(hero, cell)) return;
		if (cell == cell(48, 30)) {
			message("rope");
		} else if (cell == cell(49, 30)) {
			message("shell");
		} else if (cell == cell(56, 35)) {
			if (outcome == 2) {
				message(hatched ? "hatched" : "resting");
			} else {
				EggBox box = hero.belongings.getItem(EggBox.class);
				if (boxed && outcome == 0 && box != null && box.isFilled()) {
					choose("refuge", "place", new Runnable() {
						@Override
						public void run() {
							placeBox(hero, cell);
						}
					});
				} else message("refuge");
			}
		} else if (cell == cell(54, 32)) {
			showNest(hero, cell);
		} else if (!accepted) {
			message("ask_miron");
		} else if (completed || outcome != 0 || boxed) {
			message("quiet");
		} else if (hero.belongings.getItem(Rake.class) == null) {
			message("need_rake");
		} else if (firstStack(cell)) {
			if (shellFound) message("empty_stack");
			else choose("first_stack", "search", new Runnable() {
				@Override
				public void run() {
					work(hero, cell);
				}
			});
		} else if (released && !killed) {
			message("fighting");
		} else if (nestFound) {
			showNest(hero, cell);
		} else {
			choose(killed ? "fallen_hay" : disturbed ? "warning" : "breathing",
					killed ? "clear" : disturbed ? "part_hay" : "stir", new Runnable() {
						@Override
						public void run() {
							work(hero, cell);
						}
					});
		}
	}

	private void work(Hero hero, int cell) {
		if (!withinReach(hero, cell) || !accepted || outcome != 0 || boxed
				|| hero.belongings.getItem(Rake.class) == null) return;
		if (firstStack(cell)) {
			if (shellFound) return;
			shellFound = true;
			GLog.i(Messages.get(this, "found_shell"));
		} else if (!disturbed) {
			disturbed = true;
			GLog.w(Messages.get(this, "stirred"));
		} else if (!released) {
			// A fixed exit from the nest; no random spawning or silent relocation.
			int spawn = cell(55, 32);
			if (!Dungeon.level.passable[spawn] || Actor.findChar(spawn) != null
					|| Dungeon.level.heaps.get(spawn) != null) {
				message("blocked");
				return;
			}
			released = true;
			Senogryz beetle = new Senogryz();
			beetle.pos = spawn;
			GameScene.add(beetle, 1f);
			beetle.beckon(hero.pos);
			GLog.w(Messages.get(this, "emerged"));
		} else if (killed && !nestFound) {
			nestFound = true;
			updateNotes();
			GLog.i(Messages.get(this, "found_nest"));
		} else return;
		spendWork(hero, cell);
	}

	private void showNest(final Hero hero, final int cell) {
		if (!nestFound || boxed || outcome != 0) return;
		final EggBox box = hero.belongings.getItem(EggBox.class);
		final boolean canPack = boxGiven && box != null && !box.isFilled();
		GameScene.show(new WndOptions(Messages.get(this, "title"),
				Messages.get(this, canPack ? "nest_box" : "nest"),
				Messages.get(this, "destroy"),
				Messages.get(this, canPack ? "pack" : "leave"),
				Messages.get(this, "later")) {
			@Override
			protected void onSelect(int index) {
				if (!withinReach(hero, cell) || !nestFound || boxed || outcome != 0) return;
				if (index == 0) {
					choose("destroy_confirm", "destroy", new Runnable() {
						@Override
						public void run() {
							if (!withinReach(hero, cell) || outcome != 0 || boxed) return;
							outcome = 1;
							updateNotes();
							GLog.i(Messages.get(HayQuest.this, "destroyed"));
							spendWork(hero, cell);
						}
					});
				} else if (index == 1 && canPack && hero.belongings.contains(box)) {
					box.fill();
					boxed = true;
					updateNotes();
					GLog.i(Messages.get(HayQuest.this, "packed"));
					spendWork(hero, cell);
				} else if (index == 1) message("tell_miron");
			}
		});
	}

	private void placeBox(Hero hero, int cell) {
		EggBox box = hero.belongings.getItem(EggBox.class);
		if (!withinReach(hero, cell) || !boxed || outcome != 0 || box == null || !box.isFilled()) return;
		box.detachAll(hero.belongings.backpack);
		outcome = 2;
		updateNotes();
		GLog.p(Messages.get(this, "placed"));
		spendWork(hero, cell);
	}

	private void spendWork(Hero hero, int cell) {
		GameScene.updateMap();
		Dungeon.observe();
		hero.busy();
		hero.sprite.operate(cell);
		hero.spend(1f);
	}

	private void choose(String text, String action, final Runnable work) {
		GameScene.show(new WndOptions(Messages.get(this, "title"), Messages.get(this, text),
				Messages.get(this, action), Messages.get(this, "later")) {
			@Override
			protected void onSelect(int index) {
				if (index == 0) work.run();
			}
		});
	}

	private void message(String key) {
		GameScene.show(new WndOptions(Messages.get(this, "title"),
				Messages.get(this, key), Messages.get(this, "close")));
	}

	public void talk(final Miron miron) {
		if (level() == null) return;
		if (completed) {
			miron.say(outcome == 2 ? "after_rescue" : "after_destroy");
		} else if (!accepted) {
			GameScene.show(new WndOptions(miron.sprite(), Messages.titleCase(miron.name()),
					Messages.get(miron, "request"), Messages.get(miron, "accept"), Messages.get(miron, "decline")) {
				@Override
				protected void onSelect(int index) {
					if (index != 0 || accepted || level() == null) return;
					if (!new Rake().collect()) {
						miron.say("no_space");
						return;
					}
					accepted = true;
					updateNotes();
					miron.say("accepted");
				}
			});
		} else if (outcome != 0) {
			GameScene.show(new WndOptions(miron.sprite(), Messages.titleCase(miron.name()),
					Messages.get(miron, outcome == 2 ? "return_rescue" : "return_destroy"),
					Messages.get(miron, "reward"), Messages.get(miron, "later")) {
				@Override
				protected void onSelect(int index) {
					if (index != 0 || completed || outcome == 0 || level() == null) return;
					completed = true;
					Rake rake = Dungeon.hero.belongings.getItem(Rake.class);
					if (rake != null) rake.detachAll(Dungeon.hero.belongings.backpack);
					EggBox box = Dungeon.hero.belongings.getItem(EggBox.class);
					if (box != null) box.detachAll(Dungeon.hero.belongings.backpack);
					updateNotes();
					new Gold(30).doPickUp(Dungeon.hero);
					miron.say(outcome == 2 ? "thanks_rescue" : "thanks_destroy");
				}
			});
		} else if (boxed) {
			miron.say("carry_reminder");
		} else if (nestFound && !boxGiven) {
			GameScene.show(new WndOptions(miron.sprite(), Messages.titleCase(miron.name()),
					Messages.get(miron, "eggs"), Messages.get(miron, "take_box"), Messages.get(miron, "later")) {
				@Override
				protected void onSelect(int index) {
					if (index != 0 || boxGiven || outcome != 0 || level() == null) return;
					if (!new EggBox().collect()) {
						miron.say("no_space");
						return;
					}
					boxGiven = true;
					miron.say("box_given");
				}
			});
		} else {
			miron.say(nestFound ? "box_reminder" : killed ? "clear_reminder"
					: released ? "fight_reminder" : shellFound ? "shell_reply" : "reminder");
		}
	}

	public void beetleDied() {
		if (!released || killed) return;
		killed = true;
		GLog.i(Messages.get(this, "beetle_dead"));
	}

	public Notes.Landmark landmark() {
		if (!accepted || completed) return null;
		return outcome != 0 ? Notes.Landmark.HAY_REWARD : boxed ? Notes.Landmark.HAY_DELIVERY
				: nestFound ? Notes.Landmark.HAY_EGGS : Notes.Landmark.HAY_FIELD;
	}

	private void updateNotes() {
		Notes.remove(Notes.Landmark.HAY_FIELD);
		Notes.remove(Notes.Landmark.HAY_EGGS);
		Notes.remove(Notes.Landmark.HAY_DELIVERY);
		Notes.remove(Notes.Landmark.HAY_REWARD);
		if (landmark() != null) Notes.add(landmark());
	}

	public int tileFrame(Kind kind) {
		switch (kind) {
			case ROPE: return 0;
			case SHELL: return shellFound ? 1 : -1;
			case NEST: return nestFound && !boxed && outcome == 0 ? 2 : -1;
			case REFUGE: return outcome == 2 ? hatched ? 3 : 4 : 6;
			case TRACKS: return hatched ? 5 : -1;
			default: return -1;
		}
	}

	public boolean isRescued() {
		return outcome == 2;
	}

	public boolean hasHatched() {
		return hatched;
	}

	public String stackDesc(int x, int y) {
		String key;
		if (x == 49 && y == 29) key = shellFound ? "empty_stack" : "first_stack";
		else if (x == 54 && y == 31) key = outcome != 0 || boxed ? "quiet"
				: nestFound ? "nest" : killed ? "fallen_hay" : released ? "fighting"
				: disturbed ? "warning" : "breathing";
		else return null;
		return Messages.get(this, key);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put("accepted", accepted);
		bundle.put("shell_found", shellFound);
		bundle.put("disturbed", disturbed);
		bundle.put("released", released);
		bundle.put("killed", killed);
		bundle.put("nest_found", nestFound);
		bundle.put("box_given", boxGiven);
		bundle.put("boxed", boxed);
		bundle.put("outcome", outcome);
		bundle.put("completed", completed);
		bundle.put("hatched", hatched);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		accepted = bundle.getBoolean("accepted");
		shellFound = bundle.getBoolean("shell_found");
		disturbed = bundle.getBoolean("disturbed");
		released = bundle.getBoolean("released");
		killed = bundle.getBoolean("killed");
		nestFound = bundle.getBoolean("nest_found");
		boxGiven = bundle.getBoolean("box_given");
		boxed = bundle.getBoolean("boxed");
		outcome = bundle.getInt("outcome");
		completed = bundle.getBoolean("completed");
		// The quiet aftermath appears on a later load/visit, never during delivery.
		hatched = bundle.getBoolean("hatched") || outcome == 2;
	}
}

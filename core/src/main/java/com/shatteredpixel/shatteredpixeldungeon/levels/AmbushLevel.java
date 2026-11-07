/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.function.Supplier;

/** One authored ambush, with the battlefield and wave schedule supplied by its map. */
public abstract class AmbushLevel extends CampaignLevel {

	private enum Phase { READY, WARNING, COMBAT, CLEARED }

	private static final String PHASE = "ambush_phase";
	private static final String NEXT_WAVE = "ambush_next_wave";
	private static final String TURNS = "ambush_turns";
	private static final String FIGHTERS = "ambush_fighters";
	private static final String CLOCK = "ambush_clock";

	private AmbushPlan plan;
	private AmbushClock clock;
	private volatile Phase phase = Phase.READY;
	private volatile int nextWave;
	private int turnsSinceWave;
	private final ArrayList<Integer> fighters = new ArrayList<>();

	protected static final class AmbushPlan {
		private final int quest, left, top, right, bottom;
		private final int[] entrances, exits;
		private final Wave[] waves;

		public AmbushPlan(int quest, int left, int top, int right, int bottom,
						  int[] entrances, int[] exits, Wave... waves) {
			this.quest = quest;
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
			this.entrances = entrances;
			this.exits = exits;
			this.waves = waves;
		}
	}

	protected static final class Wave {
		private final int delay;
		private final Spawn[] spawns;

		private Wave(int delay, Spawn[] spawns) {
			this.delay = delay;
			this.spawns = spawns;
		}
	}

	protected static final class Spawn {
		private final Supplier<? extends Mob> create;
		private final int[] cells;

		private Spawn(Supplier<? extends Mob> create, int[] cells) {
			this.create = create;
			this.cells = cells;
		}
	}

	protected static Spawn spawn(Supplier<? extends Mob> create, int... cells) {
		return new Spawn(create, cells);
	}

	/** Subsequent waves with this trigger wait for every living fighter to be defeated. */
	protected static Wave wave(Spawn... spawns) {
		return new Wave(-1, spawns);
	}

	/** A subsequent wave arrives at least this many turns after the previous wave spawns. */
	protected static Wave timedWave(int turns, Spawn... spawns) {
		return new Wave(Math.max(1, turns), spawns);
	}

	protected abstract AmbushPlan ambushPlan();

	protected final void prepareAmbush() {
		plan = ambushPlan();
		phase = CampaignQuest.reached(plan.quest) ? Phase.CLEARED : Phase.READY;
		for (int cell : plan.exits) {
			Painter.set(this, cell, phase == Phase.CLEARED ? Terrain.EMPTY_SP : Terrain.SURFACE_DECO);
		}
	}

	protected String ambushMessage(String key) {
		return Messages.get(AmbushLevel.class, key);
	}

	public final boolean ambushActive() {
		return phase == Phase.WARNING || phase == Phase.COMBAT;
	}

	private boolean inArena(int pos) {
		int x = pos % width(), y = pos / width();
		return pos >= 0 && x >= plan.left && x <= plan.right && y >= plan.top && y <= plan.bottom;
	}

	public final boolean startAmbush(Hero hero, int quest) {
		if (Dungeon.level != this || quest != plan.quest || phase != Phase.READY
				|| !CampaignQuest.canComplete(quest) || !inArena(hero.pos)) {
			return false;
		}
		for (int cell : plan.entrances) {
			if (Actor.findChar(cell) != null) return false;
		}
		for (int cell : plan.exits) {
			if (Actor.findChar(cell) != null) return false;
		}
		for (Mob mob : mobs) {
			if (mob.alignment == Char.Alignment.ENEMY && inArena(mob.pos)) return false;
		}

		hero.interrupt();
		phase = Phase.WARNING;
		seal();
		setGates(Terrain.SURFACE_DECO);
		Dungeon.observe();
		GameScene.updateFog();
		Sample.INSTANCE.play(Assets.Sounds.CURSED);
		GLog.w(ambushMessage("warning"));
		// One turn for the triggering interaction and another for preparation.
		clock = new AmbushClock();
		Actor.addDelayed(clock, 2 * Actor.TICK);
		return true;
	}

	@Override
	public Actor addRespawner() {
		if (!ambushActive()) return super.addRespawner();
		if (clock == null) clock = new AmbushClock();
		Actor.add(clock);
		return clock;
	}

	private void setGates(int terrain) {
		for (int cell : plan.entrances) openTerrain(cell, terrain);
		for (int cell : plan.exits) openTerrain(cell, terrain);
	}

	private void advanceAmbush() {
		fighters.removeIf(this::fighterDefeated);
		if (phase == Phase.WARNING) {
			if (!summonWave()) GLog.w(ambushMessage("blocked"));
		} else if (nextWave == plan.waves.length) {
			if (fighters.isEmpty()) finishAmbush();
		} else {
			turnsSinceWave++;
			announceNextWave();
		}
	}

	private boolean fighterDefeated(int id) {
		Actor actor = Actor.findById(id);
		if (!(actor instanceof Mob)) return true;
		Mob mob = (Mob) actor;
		return !mob.isAlive() || mob.alignment != Char.Alignment.ENEMY;
	}

	private void announceNextWave() {
		if (nextWave == plan.waves.length) return;
		int delay = plan.waves[nextWave].delay;
		if (delay < 0 ? fighters.isEmpty() : turnsSinceWave >= delay - 1) phase = Phase.WARNING;
	}

	private boolean summonWave() {
		Wave wave = plan.waves[nextWave];
		ArrayList<Integer> positions = new ArrayList<>();
		for (Spawn spawn : wave.spawns) {
			for (int cell : spawn.cells) {
				if (passable[cell] && Actor.findChar(cell) == null && !positions.contains(cell)) {
					positions.add(cell);
					break;
				}
			}
		}
		if (positions.size() != wave.spawns.length) return false;

		for (int i = 0; i < wave.spawns.length; i++) {
			Mob mob = wave.spawns[i].create.get();
			mob.pos = positions.get(i);
			fighters.add(mob.id());
			GameScene.add(mob, Actor.TICK);
		}
		nextWave++;
		turnsSinceWave = 0;
		phase = Phase.COMBAT;
		announceNextWave();
		Dungeon.hero.interrupt();
		GLog.w(ambushMessage("emerged"));
		return true;
	}

	private void finishAmbush() {
		phase = Phase.CLEARED;
		setGates(Terrain.EMPTY_SP);
		unseal();
		CampaignQuest.complete(plan.quest);
		Dungeon.observe();
		GameScene.updateFog();
		GLog.i(ambushMessage("released"));
	}

	@Override
	public Group addWallVisuals() {
		Group group = super.addWallVisuals();
		for (int pos : plan.exits) addGlow(group, pos, 0, 3, 16, 10, 0xFF704738, Phase.READY, -1);
		for (int pos : plan.entrances) addBarrier(group, pos);
		for (int pos : plan.exits) addBarrier(group, pos);
		for (int i = 0; i < plan.waves.length; i++) {
			for (Spawn spawn : plan.waves[i].spawns) {
				for (int pos : spawn.cells) addGlow(group, pos, 5, 5, 6, 6, 0xD9FFA33F, Phase.WARNING, i);
			}
		}
		return group;
	}

	private void addBarrier(Group group, int pos) {
		addGlow(group, pos, 0, 0, 16, 16, 0xDA382559, null, -1);
		addGlow(group, pos, 0, 6, 16, 4, 0xFFB68BFF, null, -1);
	}

	private void addGlow(Group group, int pos, int x, int y, int width, int height,
						 int color, Phase onlyPhase, int wave) {
		ColorBlock glow = new ColorBlock(width, height, color) {
			@Override
			public boolean isVisible() {
				return super.isVisible() && (onlyPhase == null ? ambushActive() : phase == onlyPhase)
						&& (wave < 0 || nextWave == wave);
			}
		};
		glow.x = (pos % width()) * DungeonTilemap.SIZE + x;
		glow.y = (pos / width()) * DungeonTilemap.SIZE + y;
		glow.active = false;
		group.add(glow);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PHASE, phase);
		bundle.put(NEXT_WAVE, nextWave);
		bundle.put(TURNS, turnsSinceWave);
		int[] ids = new int[fighters.size()];
		for (int i = 0; i < ids.length; i++) ids[i] = fighters.get(i);
		bundle.put(FIGHTERS, ids);
		if (clock != null) bundle.put(CLOCK, clock);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		plan = ambushPlan();
		phase = bundle.getEnum(PHASE, Phase.class);
		nextWave = bundle.getInt(NEXT_WAVE);
		turnsSinceWave = bundle.getInt(TURNS);
		fighters.clear();
		int[] ids = bundle.getIntArray(FIGHTERS);
		if (ids != null) {
			for (int id : ids) fighters.add(id);
		}
		clock = bundle.contains(CLOCK) ? (AmbushClock) bundle.get(CLOCK) : null;

		// The global quest file can be newer than the level file if saving was interrupted.
		if (phase != Phase.CLEARED && CampaignQuest.reached(plan.quest)) {
			mobs.removeIf(mob -> fighters.contains(mob.id()));
			fighters.clear();
			clock = null;
			phase = Phase.CLEARED;
			locked = false;
			setGates(Terrain.EMPTY_SP);
		}
	}

	public static class AmbushClock extends Actor {

		{
			actPriority = HERO_PRIO + 1;
		}

		@Override
		protected boolean act() {
			if (Dungeon.level instanceof AmbushLevel) {
				AmbushLevel level = (AmbushLevel) Dungeon.level;
				if (level.clock == this && level.ambushActive()) {
					level.advanceAmbush();
					if (level.ambushActive()) {
						spend(TICK);
						return true;
					}
					level.clock = null;
				}
			}
			Actor.remove(this);
			return true;
		}
	}
}

/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.OutdoorLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;

// Visual materials for authored outdoor terrain. Keep the original map and flags for gameplay.
public final class SurfaceLandscapeTiles {

	private static final int BANK = 576;
	private static final int PATH = 640;
	private static final int DECK = 688;
	private static final int BARRIER_GROUND = 704;
	private static final int BARRIER = 752;
	private static final int MASKS = 16;

	private enum Barrier {
		THORNS, REEDS, LOG, DRIFTWOOD, MOSS_ROCK, CHALK, SLATE, RUBBLE
	}

	public enum Style {
		MEADOW(0, 0, 0, false, Barrier.THORNS, Barrier.MOSS_ROCK, Barrier.LOG),
		MARSH(1, 0, 1, false, Barrier.THORNS, Barrier.LOG, Barrier.MOSS_ROCK),
		CHALK(2, 1, 2, false, Barrier.CHALK, Barrier.RUBBLE, Barrier.MOSS_ROCK),
		HIGHLAND(0, 1, 2, false, Barrier.MOSS_ROCK, Barrier.SLATE, Barrier.THORNS),
		COAST(2, 1, 2, true, Barrier.DRIFTWOOD, Barrier.RUBBLE, Barrier.MOSS_ROCK),
		EDGE(2, 2, 2, false, Barrier.SLATE, Barrier.RUBBLE, Barrier.MOSS_ROCK);

		public final boolean saltWater;
		private final int bank, path, ground;
		private final Barrier common, secondary, accent;

		Style(int bank, int path, int ground, boolean saltWater,
			  Barrier common, Barrier secondary, Barrier accent) {
			this.bank = bank;
			this.path = path;
			this.ground = ground;
			this.saltWater = saltWater;
			this.common = common;
			this.secondary = secondary;
			this.accent = accent;
		}
	}

	private SurfaceLandscapeTiles() {
	}

	static int terrainForWalls(Level level, int cell) {
		if (level instanceof OutdoorLevel && ((OutdoorLevel) level).isNaturalBarrier(cell)) {
			return Terrain.SURFACE_DECO;
		}
		return level.map[cell];
	}

	public static boolean isWater(int terrain) {
		return terrain == Terrain.WATER || terrain == Terrain.CUSTOM_DECO_WTR;
	}

	public static int waterVisual(OutdoorLevel level, Style style, int cell) {
		int mask = neighbourMask(level, cell, next -> {
			int terrain = level.map[next];
			return !isWater(terrain) && terrain != Terrain.CHASM && !level.isDeck(next);
		});
		if (mask == 0) return DungeonTileSheet.WATER;
		return BANK + (level.isInterior(cell) ? 3 : style.bank) * MASKS + mask;
	}

	public static int pathVisual(OutdoorLevel level, Style style, int cell) {
		boolean deck = level.isDeck(cell);
		int mask = deck ? neighbourMask(level, cell, level::isDeck)
				: neighbourMask(level, cell, next -> level.map[next] == Terrain.EMPTY_SP
						&& !level.isInterior(next) && !level.isDeck(next));
		return (deck ? DECK : PATH + style.path * MASKS) + mask;
	}

	public static int barrierGround(OutdoorLevel level, Style style, int cell) {
		return BARRIER_GROUND + style.ground * MASKS + neighbourMask(level, cell, level::isNaturalBarrier);
	}

	public static int barrierVisual(OutdoorLevel level, Style style, int cell) {
		int x = cell % level.width(), y = cell / level.width();
		int cluster = (x / 3 * 11 + y / 3 * 7) % 10;
		Barrier barrier = cluster < 6 ? style.common : cluster < 9 ? style.secondary : style.accent;
		if (style == Style.MARSH && neighbourMask(level, cell, next -> isWater(level.map[next])) != 0) {
			barrier = Barrier.REEDS;
		}
		int variant = (x * 3 + y * 7 + (x + y) / 3) % 4;
		return BARRIER + barrier.ordinal() * 4 + variant;
	}

	// Cardinal connections in atlas order: north, east, south, west.
	private static int neighbourMask(OutdoorLevel level, int cell, CellMatch match) {
		int width = level.width();
		int mask = 0;
		if (cell >= width && match.matches(cell - width)) mask |= 1;
		if (cell % width < width - 1 && match.matches(cell + 1)) mask |= 2;
		if (cell + width < level.length() && match.matches(cell + width)) mask |= 4;
		if (cell % width > 0 && match.matches(cell - 1)) mask |= 8;
		return mask;
	}

	private interface CellMatch {
		boolean matches(int cell);
	}
}

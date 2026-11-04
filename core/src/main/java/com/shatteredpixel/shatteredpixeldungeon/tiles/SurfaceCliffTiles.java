/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.OutdoorLevel;

// The appended sheet repeats wall slots with earthen faces and turf caps.
public class SurfaceCliffTiles {

	public static final int OFFSET = 320;
	public static final int DARK = OFFSET + DungeonTileSheet.WALLS_INTERNAL;

	public static int visual(int pos, int visual, boolean upperLayer) {
		if (!(Dungeon.level instanceof OutdoorLevel)) return visual;
		OutdoorLevel level = (OutdoorLevel) Dungeon.level;
		if (level.isPassageMouth(pos)) return upperLayer ? -1 : DARK;
		if (upperLayer && level.isPassageMouth(pos + level.width())) return -1;
		if (visual < 0) return visual;

		boolean wall = level.isEarthWall(pos);
		if (upperLayer && visual >= DungeonTileSheet.WALL_OVERHANG
				&& visual < DungeonTileSheet.WALL_OVERHANG + 4) {
			wall = level.isEarthWall(pos + level.width());
		}
		return wall ? visual + OFFSET : visual;
	}
}

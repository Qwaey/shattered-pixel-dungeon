/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

// Reuses the existing grass, flowers, leaves and blended dirt-path tiles.
public class WoodlandFloorTiles {

	private static final int[][] ROUTES = {
			{10, 1, 10, 6, 9, 9, 7, 12, 6, 15, 8, 18, 7, 21, 9, 24},
			{8, 18, 11, 19, 14, 19, 15, 18},
			{7, 12, 5, 13, 4, 15},
			{7, 21, 10, 22, 12, 23},
			{9, 24, 11, 24, 13, 25}
	};

	// Sparse flowers at the forest edge broaden into patches in the meadow.
	private static final int MEADOW_Y = 8;
	private static final String[] MEADOW = {
			".....f........f.........",
			"......f........f....f...",
			".....f.f...f....f...f...",
			"....f.f...ff........ff..",
			"...f.f..f.ff.........f..",
			"....f.f.ff...........f..",
			".....f...fff........ff..",
			"....f...f.ff.........f..",
			"...fff...f...........f..",
			"...f.ff...ff.........f..",
			"..f.f.f..f.f........ff..",
			"...ffff..f..f...f...f.f.",
			"..ff.f...f.ff..ff....ff.",
			"...fff....f.f..ff....ff.",
			"...f.fff...f.f.f.f.f.f..",
			"....ff.ff.f..f..fff..ff.",
			"....f.ff...ff...f.f.ff..",
			".....f.ff.f...f.ff.ff...",
			"......f.f.ff.f.f.f......"
	};

	public static int getVisual(int x, int y) {
		int path = SurfacePathTiles.getFaintVisual(x, y, ROUTES);
		if (path != -1) return path;
		int meadowRow = y - MEADOW_Y;
		if (meadowRow >= 0 && meadowRow < MEADOW.length
				&& x < MEADOW[meadowRow].length() && MEADOW[meadowRow].charAt(x) == 'f') {
			return 14;
		}
		if (y <= 11 && (x + 2 * y) % 7 == 0) return 13;
		if ((x + 3 * y) % 23 == 0) return DungeonTileSheet.FLOOR_ALT_2;
		return DungeonTileSheet.FLOOR;
	}
}

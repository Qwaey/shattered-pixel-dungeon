/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

// Reuses the existing grass, flowers, leaves and blended dirt-path tiles.
public class WoodlandFloorTiles {

	private static final int[][] ROUTES = {
			{10, 1, 10, 5, 10, 8, 9, 12, 8, 16, 11, 21, 8, 26, 10, 30, 14, 34},
			{10, 20, 15, 20, 19, 19, 19, 18},
			{9, 12, 12, 11, 14, 12},
			{8, 16, 6, 18, 6, 21},
			{11, 21, 14, 22, 18, 23},
			{8, 26, 11, 27, 15, 28, 18, 26},
			{10, 30, 13, 29, 17, 31},
			{14, 34, 17, 35, 19, 34}
	};

	// The lower meadow has flower patches with grass between them.
	private static final String[] MEADOW = {
			"..f...ff....",
			".ff.....f...",
			"....f.....f.",
			"f.....ff....",
			"..ff.....ff.",
			".....f......"
	};

	public static int getVisual(int x, int y) {
		int path = SurfacePathTiles.getFaintVisual(x, y, ROUTES);
		if (path != -1) return path;
		// Meadow details move with the original clearing, below the added forest.
		y -= 4;
		if (y >= 18 && MEADOW[(y - 18) % MEADOW.length].charAt(x % MEADOW[0].length()) == 'f') {
			return 14;
		}
		// Sparse flowers at the clearing, leaf litter deeper in the forest.
		if (y < 14 && (x + 2 * y) % 7 == 0) return 13;
		if (y >= 12 && (2 * x + y) % 19 == 0) return 14;
		if ((x + 3 * y) % 23 == 0) return DungeonTileSheet.FLOOR_ALT_2;
		return DungeonTileSheet.FLOOR;
	}
}

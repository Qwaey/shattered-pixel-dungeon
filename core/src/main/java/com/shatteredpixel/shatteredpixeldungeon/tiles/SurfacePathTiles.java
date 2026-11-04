/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

// Decorative ground only; routes do not change terrain, movement, or sight.
public class SurfacePathTiles {

	private static final int FIRST_TILE = 272;
	private static final float RADIUS_SQUARED = 0.85f * 0.85f;

	private static final int BARN_PATH_X = 11, BARN_PATH_Y = 13;
	// Authored corner samples: scattered worn patches leave grass between them.
	// '#' = dirt, '.' = meadow. The faint track wanders north of the western home.
	private static final String[] BARN_PATH_SHAPE = {
			"..................",
			".##..##.#..##.....",
			"..#.##...##.#.#...",
			"...............#..",
			"...............#..",
			"..............#...",
			".................."
	};

	// Authored tile centres: western home, well, and both eastern homes.
	private static final int[][] ROUTES = {
			{24, 19, 26, 19, 26, 20, 29, 20, 29, 22, 30, 24, 31, 24},
			{32, 14, 32, 15, 30, 16, 29, 18, 29, 20},
			{26, 19, 26, 18, 29, 18}
	};

	private static final int[][] VARIANTS = {
			{0, 1, 0, 2},
			{2, 0, 1, 0},
			{1, 2, 0, 1}
	};

	public static int getVisual(int x, int y) {
		if (x < 11 || x > 33 || y < 12 || y > 25) return -1;
		// Clockwise corner bits match the existing field transition tiles.
		int corners = (pathCorner(x, y) ? 1 : 0)
				| (pathCorner(x + 1, y) ? 2 : 0)
				| (pathCorner(x + 1, y + 1) ? 4 : 0)
				| (pathCorner(x, y + 1) ? 8 : 0);
		if (corners == 0) return -1;
		return FIRST_TILE + 16 * VARIANTS[y % VARIANTS.length][x % VARIANTS[0].length] + corners;
	}

	private static boolean pathCorner(int x, int y) {
		int barnX = x - BARN_PATH_X;
		int barnY = y - BARN_PATH_Y;
		if (barnX >= 0 && barnY >= 0 && barnY < BARN_PATH_SHAPE.length
				&& barnX < BARN_PATH_SHAPE[0].length()
				&& BARN_PATH_SHAPE[barnY].charAt(barnX) == '#') return true;
		for (int[] route : ROUTES) {
			for (int i = 0; i < route.length - 2; i += 2) {
				float startX = route[i] + 0.5f;
				float startY = route[i + 1] + 0.5f;
				float dx = route[i + 2] - route[i];
				float dy = route[i + 3] - route[i + 1];
				float along = ((x - startX) * dx + (y - startY) * dy) / (dx * dx + dy * dy);
				along = Math.max(0, Math.min(1, along));
				float offsetX = x - startX - along * dx;
				float offsetY = y - startY - along * dy;
				if (offsetX * offsetX + offsetY * offsetY <= RADIUS_SQUARED) return true;
			}
		}
		return false;
	}
}

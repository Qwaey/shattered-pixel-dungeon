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

	// Authored corner wear: grass breaks the trail into small, uneven dirt patches.
	private static final String[] TRAIL_WEAR = {
			"##..#..#",
			".#.#..##",
			"#...##..",
			"..##.#..",
			"##...#.#",
			".#..##..",
			"#.#...##"
	};

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
			{24, 19, 26, 19, 26, 20, 28, 20},
			{29, 22, 30, 24, 31, 24},
			{32, 14, 32, 15, 30, 16, 29, 18, 29, 20},
			{26, 19, 26, 18, 29, 18}
	};

	// Less worn branches from the well to the southern section passage.
	private static final int[][] FAINT_ROUTES = {
			{28, 20, 29, 22, 29, 27, 28, 30, 29, 33, 28, 36, 28, 38, 28, 40},
			{29, 24, 30, 27, 32, 28, 34, 28},
			{28, 30, 27, 32, 27, 34}
	};

	private static final int[][] VARIANTS = {
			{0, 1, 0, 2},
			{2, 0, 1, 0},
			{1, 2, 0, 1}
	};

	public static int getVisual(int x, int y) {
		if (x < 11 || x > 35 || y < 12 || y > 40) return -1;
		// Clockwise corner bits match the existing field transition tiles.
		int corners = (pathCorner(x, y) ? 1 : 0)
				| (pathCorner(x + 1, y) ? 2 : 0)
				| (pathCorner(x + 1, y + 1) ? 4 : 0)
				| (pathCorner(x, y + 1) ? 8 : 0);
		if (corners == 0) return getFaintVisual(x, y, FAINT_ROUTES);
		return FIRST_TILE + 16 * VARIANTS[y % VARIANTS.length][x % VARIANTS[0].length] + corners;
	}

	public static int getVisual(int x, int y, int[][] routes) {
		int corners = (routeCorner(x, y, routes) ? 1 : 0)
				| (routeCorner(x + 1, y, routes) ? 2 : 0)
				| (routeCorner(x + 1, y + 1, routes) ? 4 : 0)
				| (routeCorner(x, y + 1, routes) ? 8 : 0);
		return corners == 0 ? -1
				: FIRST_TILE + 16 * VARIANTS[y % VARIANTS.length][x % VARIANTS[0].length] + corners;
	}

	public static int getFaintVisual(int x, int y, int[][] routes) {
		int corners = (trailCorner(x, y, routes) ? 1 : 0)
				| (trailCorner(x + 1, y, routes) ? 2 : 0)
				| (trailCorner(x + 1, y + 1, routes) ? 4 : 0)
				| (trailCorner(x, y + 1, routes) ? 8 : 0);
		return corners == 0 ? -1
				: FIRST_TILE + 16 * VARIANTS[y % VARIANTS.length][x % VARIANTS[0].length] + corners;
	}

	private static boolean trailCorner(int x, int y, int[][] routes) {
		return TRAIL_WEAR[y % TRAIL_WEAR.length].charAt(x % TRAIL_WEAR[0].length()) == '#'
				&& routeCorner(x, y, routes);
	}

	private static boolean pathCorner(int x, int y) {
		int barnX = x - BARN_PATH_X;
		int barnY = y - BARN_PATH_Y;
		if (barnX >= 0 && barnY >= 0 && barnY < BARN_PATH_SHAPE.length
				&& barnX < BARN_PATH_SHAPE[0].length()
				&& BARN_PATH_SHAPE[barnY].charAt(barnX) == '#') return true;
		return routeCorner(x, y, ROUTES);
	}

	private static boolean routeCorner(int x, int y, int[][] routes) {
		for (int[] route : routes) {
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

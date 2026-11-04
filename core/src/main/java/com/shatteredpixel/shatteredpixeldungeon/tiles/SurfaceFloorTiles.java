/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

// Visuals only: none of these decorations change a cell's terrain or flags.
public class SurfaceFloorTiles {

	// Slots 13-15 and 21 are unused in the original sheet, and belong only to tiles_surface.
	private static final int LEAVES = 13;
	private static final int FLOWERS = 14;
	private static final int STRAW = 15;
	private static final int FIELD = 21;
	// A new row appended to tiles_surface; each tile blends four grass/field corners.
	private static final int FIELD_EDGE = 256;
	private static final int FIELD_X = -11;
	private static final int FIELD_Y = 4;

	// Authored corner samples, not occupied cells: '#' = field, '.' = meadow.
	// The neighbouring four samples select a rounded edge, corner, or plain field tile.
	private static final String[] FIELD_SHAPE = {
			".............",
			"....####.....",
			"..########...",
			".###########.",
			".###########.",
			"..##########.",
			".###########.",
			"..#########..",
			"...#######...",
			".....###.....",
			"............."
	};

	// An authored, sparse meadow mosaic, repeated outside the village.
	// '.' = grass, 'd' = dirt, 'p' = pebbles, 'f' = flowers.
	private static final String[] MEADOW = {
			"....p.........d.........",
			"..........f........p....",
			"..d..............p......",
			".......p..........d.....",
			".............d..........",
			"...f..............f.....",
			"..........p............d",
			".p..............d.......",
			"......d..............p..",
			"...........f............",
			"..p..............p......",
			"........d.............f.",
			"....d.........p.........",
			"..........p.........d...",
			".f..............f.......",
			".......p..............p.",
			"............d...........",
			"...p..............d.....",
			".........f............p."
	};

	private static final String[] LEAF_LITTER = {
			" l    ",
			"   l l",
			" ll   ",
			"l   l ",
			"  l  l"
	};

	// Positions are relative to the surface's centre and follow the fixed village plan.
	// Spaces leave the meadow underneath; dots deliberately keep plain grass.
	private static final Patch[] PATCHES = {
			// A short branch reaches the cart at the forest entrance.
			new Patch(2, 1,
					"ddddd",
					"     dddddddd  dd",
					"             dd"),
			// The track fades inside the small woodland nook.
			new Patch(18, -6,
					"d",
					" d",
					" d",
					" d",
					"d",
					"d",
					"d"),
			// Sparse leaf litter beneath the four yard oaks.
			new Patch(-24, -6, LEAF_LITTER),
			new Patch(-2, -15, LEAF_LITTER),
			new Patch(8, 8, LEAF_LITTER),
			new Patch(-18, -1, LEAF_LITTER),
			// A little straw on the cart's parking place beside the forest path.
			new Patch(13, 3, " s  ", "s s ", " s  "),
			new Patch(-12, 3, " f   ", "   f ", "f    "),
			new Patch(9, -2, "  f  ", "f   f", "   f "),
			new Patch(5, 9, " f  f ", "   f  ", "f     ")
	};

	public static int getVisual(int pos, int width, int height) {
		int x = pos % width;
		int y = pos / width;
		String row = MEADOW[y % MEADOW.length];
		char detail = row.charAt(x % row.length());
		int villageX = x - width / 2;
		int villageY = y - height / 2;
		int field = fieldVisual(villageX, villageY);
		if (field != -1) return field;
		int path = SurfacePathTiles.getVisual(x, y);
		if (path != -1) return path;
		for (Patch patch : PATCHES) {
			int py = villageY - patch.y;
			if (py < 0 || py >= patch.rows.length) continue;
			int px = villageX - patch.x;
			String patchRow = patch.rows[py];
			if (px >= 0 && px < patchRow.length() && patchRow.charAt(px) != ' ') {
				detail = patchRow.charAt(px);
			}
		}
		switch (detail) {
			case 'd': return DungeonTileSheet.FLOOR_ALT_1;
			case 'p': return DungeonTileSheet.FLOOR_ALT_2;
			case 'l': return LEAVES;
			case 'f': return FLOWERS;
			case 's': return STRAW;
			default:  return DungeonTileSheet.FLOOR;
		}
	}

	private static int fieldVisual(int x, int y) {
		x -= FIELD_X;
		y -= FIELD_Y;
		if (x < 0 || y < 0 || y >= FIELD_SHAPE.length - 1
				|| x >= FIELD_SHAPE[0].length() - 1) return -1;
		// Clockwise bits: north-west, north-east, south-east, south-west.
		int corners = (FIELD_SHAPE[y].charAt(x) == '#' ? 1 : 0)
				| (FIELD_SHAPE[y].charAt(x + 1) == '#' ? 2 : 0)
				| (FIELD_SHAPE[y + 1].charAt(x + 1) == '#' ? 4 : 0)
				| (FIELD_SHAPE[y + 1].charAt(x) == '#' ? 8 : 0);
		if (corners == 0) return -1;
		return corners == 15 ? FIELD : FIELD_EDGE + corners;
	}

	private static class Patch {
		final int x, y;
		final String[] rows;

		Patch(int x, int y, String... rows) {
			this.x = x;
			this.y = y;
			this.rows = rows;
		}
	}
}

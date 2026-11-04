/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceChoppingBlock;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceForest;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture.Type;
import com.shatteredpixel.shatteredpixeldungeon.tiles.WoodlandFloorTiles;

public class WoodlandLevel extends OutdoorLevel {

	private static final int HOUSE_X = 16, HOUSE_Y = 12;

	@Override
	protected boolean isHouseCell(int x, int y) {
		return insideHouse(x, y, HOUSE_X, HOUSE_Y, 8, 7);
	}

	@Override
	protected int sectionArrivalCell() {
		return 4 * width() + 10;
	}

	// Authored 28x40 location: four added northern rows frame the forest passage.
	// The northern section passage receives the hero coming south from the village.
	private static final String[] FOREST = {
			"1728..5061728",
			"3945..a617283",
			"0516..3849506",
			"6271..c945061",
			"172a.......83",
			"39A........a6",
			"05b.........7",
			"62a.........8",
			"83..........5",
			"17b.........6",
			"39a........d1",
			"05..........7",
			"62b........c8",
			"83.........d5",
			"17a.........6",
			"39..........1",
			"05b........c7",
			"627a......d83",
			"1728394506172"
	};

	@Override
	protected boolean build() {
		setSize(28, 40);
		Painter.fill(this, 1, 1, width() - 2, height() - 2, Terrain.EMPTY);
		SurfaceForest.plant(this, FOREST);

		// Open-roof house, using the village's wooden walls, floor and furniture.
		paintHouse(HOUSE_X, HOUSE_Y, 8, 7, HOUSE_X + 3, HOUSE_Y + 6);
		placeFurniture(HOUSE_X + 1, HOUSE_Y + 1, Type.BED);
		placeFurniture(HOUSE_X + 5, HOUSE_Y + 1, Type.STOVE);
		placeFurniture(HOUSE_X + 6, HOUSE_Y + 2, Type.FIREWOOD);
		placeFurniture(HOUSE_X + 1, HOUSE_Y + 5, Type.CUPBOARD);
		placeFurniture(HOUSE_X + 5, HOUSE_Y + 4, Type.TABLE);
		placeFurniture(HOUSE_X + 5, HOUSE_Y + 5, Type.STOOL);
		placeFurniture(HOUSE_X + 3, HOUSE_Y + 1, Type.WASHBASIN);

		// Existing woodcutting sprites; no new artwork or item rewards.
		customTiles.add(new SurfaceChoppingBlock(18, 23));
		Painter.set(this, 18, 23, Terrain.CUSTOM_DECO);
		placeExterior(21, 21, SurfaceExterior.Type.CHOPPING_BLOCK);
		placeExterior(23, 24, SurfaceExterior.Type.CHOPPING_BLOCK);
		placeExterior(20, 26, SurfaceExterior.Type.CHOPPING_BLOCK);
		placeWoodpile(22, 21);
		placeWoodpile(23, 21);
		placeWoodpile(22, 22);
		placeWoodpile(23, 22);
		placeWoodpile(24, 15);
		placeWoodpile(24, 16);

		placeExterior(5, 24, SurfaceExterior.Type.OAK);
		placeExterior(7, 31, SurfaceExterior.Type.OAK);
		placeExterior(21, 33, SurfaceExterior.Type.PINE);

		int passage = width() + 10;
		LevelTransition transition = new LevelTransition(this, passage,
				LevelTransition.Type.SECTION, 0, 0, LevelTransition.Type.SECTION);
		transition.set(10, 1, 11, 2);
		transitions.add(transition);
		return true;
	}

	private void placeWoodpile(int x, int y) {
		customTiles.add(new SurfaceFurniture(Type.FIREWOOD, x, y));
		Painter.set(this, x, y, Terrain.SURFACE_DECO);
	}

	@Override
	public int floorVisual(int cell) {
		int x = cell % width(), y = cell / width();
		if (x > HOUSE_X && x < HOUSE_X + 7 && y > HOUSE_Y && y < HOUSE_Y + 6) {
			return DungeonTileSheet.FLOOR_SP;
		}
		return WoodlandFloorTiles.getVisual(x, y);
	}

	@Override
	protected void createMobs() {
	}

	@Override
	protected void createItems() {
	}
}

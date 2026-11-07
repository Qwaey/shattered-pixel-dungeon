/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.CampaignNPC;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceChoppingBlock;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceForest;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture.Type;
import com.shatteredpixel.shatteredpixeldungeon.tiles.WoodlandFloorTiles;

public class WoodlandLevel extends CampaignLevel {

	private static final int HOUSE_Y = 12, HOUSE_WIDTH = 8, HOUSE_HEIGHT = 7;

	private int houseX() {
		return width() - HOUSE_WIDTH - 4;
	}

	@Override
	protected boolean isHouseCell(int x, int y) {
		return insideHouse(x, y, houseX(), HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT);
	}

	@Override
	protected int sectionArrivalCell() {
		return 4 * width() + 10;
	}

	// Meadow outline: the cliffs encroach from both sides and close off the south.
	private static final String[] GROUND = {
			"########################",
			"#......................#",
			"#......................#",
			"#......................#",
			"#......................#",
			"#......................#",
			"#......................#",
			"#......................#",
			"#......................#",
			"#......................#",
			"#......................#",
			"#.....................##",
			"##....................##",
			"##....................##",
			"###...................##",
			"###...................##",
			"##....................##",
			"##....................##",
			"#.....................##",
			"#......................#",
			"#......................#",
			"##.....................#",
			"##.....................#",
			"###....................#",
			"###...................##",
			"####.................###",
			"######............######",
			"########################"
	};

	// The north trail runs from closed thickets to scattered trees and saplings.
	private static final String[] FOREST = {
			"1728..50617",
			"3945..a617.",
			"05b....c506",
			"62a.....d8.",
			".A..a...Hc.",
			"..b........"
	};

	@Override
	protected boolean build() {
		setSize(GROUND[0].length(), GROUND.length);
		for (int y = 0; y < height(); y++) {
			for (int x = 0; x < width(); x++) {
				Painter.set(this, x, y, GROUND[y].charAt(x) == '#' ? Terrain.WALL : Terrain.EMPTY);
			}
		}
		SurfaceForest.plant(this, FOREST);

		// Open-roof house, using the village's wooden walls, floor and furniture.
		int houseX = houseX();
		paintHouse(houseX, HOUSE_Y, HOUSE_WIDTH, HOUSE_HEIGHT, houseX + 3, HOUSE_Y + 6);
		placeFurniture(houseX + 1, HOUSE_Y + 1, Type.BED);
		placeFurniture(houseX + 5, HOUSE_Y + 1, Type.STOVE);
		placeFurniture(houseX + 6, HOUSE_Y + 2, Type.FIREWOOD);
		placeFurniture(houseX + 1, HOUSE_Y + 5, Type.CUPBOARD);
		placeFurniture(houseX + 5, HOUSE_Y + 4, Type.TABLE);
		placeFurniture(houseX + 5, HOUSE_Y + 5, Type.STOOL);
		placeFurniture(houseX + 3, HOUSE_Y + 1, Type.WASHBASIN);

		// Existing woodcutting sprites; no new artwork or item rewards.
		customTiles.add(new SurfaceChoppingBlock(14, 22));
		Painter.set(this, 14, 22, Terrain.CUSTOM_DECO);
		placeExterior(17, 20, SurfaceExterior.Type.CHOPPING_BLOCK);
		placeExterior(20, 23, SurfaceExterior.Type.CHOPPING_BLOCK);
		placeExterior(15, 24, SurfaceExterior.Type.CHOPPING_BLOCK);
		placeFurniture(18, 20, Type.FIREWOOD);
		placeFurniture(19, 20, Type.FIREWOOD);
		placeFurniture(18, 21, Type.FIREWOOD);
		placeFurniture(19, 21, Type.FIREWOOD);
		placeFurniture(20, 15, Type.FIREWOOD);
		placeFurniture(20, 16, Type.FIREWOOD);
		if (CampaignQuest.reached(14)) Painter.set(this, 12, 20, Terrain.EMPTY_SP);

		int passage = width() + 10;
		LevelTransition transition = new LevelTransition(this, passage,
				LevelTransition.Type.SECTION, 0, 0, LevelTransition.Type.SECTION);
		transition.set(10, 1, 11, 2);
		transitions.add(transition);
		passage(-2, 11, 25, 12, 26, 23 * width() + 11, 6);
		return true;
	}

	@Override
	protected int outdoorFloorVisual(int cell) {
		return WoodlandFloorTiles.getVisual(cell % width(), cell / width());
	}

	@Override
	protected void createMobs() {
		for (Mob mob : mobs) {
			if (mob instanceof CampaignNPC) return;
		}
		waypoint(14, 19, 6);
		waypoint(10, 17, 9);
		waypoint(16, 13, 11);
		waypoint(21, 17, 12);
		waypoint(12, 19, 14);
		waypoint(14, 16, 15);
	}

	@Override
	public void campaignChanged() {
		super.campaignChanged();
		if (CampaignQuest.reached(14)) openTerrain(cell(12, 20), Terrain.EMPTY_SP);
	}
}

/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GreyMuzzle;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Reedcutter;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rootwalker;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior.Type;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfacePathTiles;

public final class RoadsideLevels {

	private RoadsideLevels() {
	}

	public abstract static class RiverSection extends CampaignLevel {

		protected void ground(int width, int height) {
			setSize(width, height);
			Painter.fill(this, 1, 1, width - 2, height - 2, Terrain.EMPTY);
		}

		protected void cliffs(int x, int y, int width, int height) {
			Painter.fill(this, x, y, width, height, Terrain.WALL);
		}

		protected void river(int x, int y, int width, int height) {
			Painter.fill(this, x, y, width, height, Terrain.WATER);
		}

		protected void boards(int x, int y, int width, int height) {
			Painter.fill(this, x, y, width, height, Terrain.EMPTY_SP);
		}

		protected abstract int[][] routes();

		@Override
		protected int outdoorFloorVisual(int pos) {
			int visual = SurfacePathTiles.getVisual(pos % width(), pos / width(), routes());
			return visual == -1 ? super.outdoorFloorVisual(pos) : visual;
		}

		@Override
		public void buildFlagMaps() {
			super.buildFlagMaps();
			for (int pos = 0; pos < length(); pos++) {
				if (water[pos]) {
					passable[pos] = false;
					solid[pos] = true;
				}
			}
			for (int pos = 0; pos < length(); pos++) {
				if (water[pos]) updateOpenSpace(pos);
			}
		}

		@Override
		public void updateCellFlags(int pos) {
			super.updateCellFlags(pos);
			if (water[pos]) {
				// This is the deep channel, with shallow crossings represented by planks.
				passable[pos] = false;
				solid[pos] = true;
				updateOpenSpace(pos);
			}
		}

		protected void placeMob(Mob mob, int x, int y) {
			mob.pos = cell(x, y);
			mobs.add(mob);
		}

		@Override
		protected void createMobs() {
		}
	}

	public static class OldRoad extends RiverSection {

		private static final int[][] ROUTES = {
				{12, 1, 12, 5, 11, 7, 12, 9, 14, 11, 18, 11, 20, 12, 24, 11},
				{11, 7, 8, 8, 7, 11, 9, 14, 12, 13, 14, 12}
		};

		@Override
		protected boolean build() {
			ground(26, 24);
			cliffs(1, 1, 8, 2);
			cliffs(1, 3, 3, 4);
			cliffs(17, 1, 8, 2);
			cliffs(22, 3, 3, 5);
			cliffs(24, 8, 1, 3);
			cliffs(1, 19, 4, 4);
			cliffs(5, 21, 4, 2);
			cliffs(20, 19, 5, 4);
			cliffs(24, 13, 1, 6);

			river(14, 1, 1, 22);
			river(13, 3, 1, 5);
			river(15, 16, 1, 7);
			if (CampaignQuest.reached(7)) boards(14, 11, 1, 2);
			if (CampaignQuest.reached(10)) boards(7, 9, 1, 1);
			if (CampaignQuest.reached(13)) boards(7, 16, 1, 1);

			placeExterior(5, 4, Type.PINE);
			placeExterior(8, 5, Type.OAK);
			placeExterior(4, 11, Type.PINE);
			placeExterior(6, 17, Type.OAK);
			placeExterior(11, 10, Type.CHOPPING_BLOCK);
			placeExterior(17, 6, Type.PINE);
			placeExterior(20, 16, Type.PINE);
			placeFurniture(9, 12, SurfaceFurniture.Type.FIREWOOD);
			placeFurniture(17, 15, SurfaceFurniture.Type.FIREWOOD);

			passage(-1, 12, 1, 13, 2, cell(12, 4), 0);
			passage(-3, 23, 11, 24, 12, cell(21, 11), 8);
			return true;
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(13, 11, 7);
			waypoint(16, 12, 8);
			waypoint(8, 7, CampaignQuest.FALSE_TRAIL);
			waypoint(7, 10, 10);
			waypoint(7, 15, 13);
			placeMob(new Rootwalker(), 12, 11);
		}

		public boolean revealGreyMuzzle() {
			int[] spawnCells = {cell(8, 8), cell(9, 8), cell(7, 8)};
			for (int spawn : spawnCells) {
				if (!passable[spawn] || Actor.findChar(spawn) != null) continue;
				GreyMuzzle greyMuzzle = new GreyMuzzle();
				greyMuzzle.pos = spawn;
				GameScene.add(greyMuzzle, Actor.TICK);
				return true;
			}
			return false;
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(10)) openTerrain(cell(7, 9), Terrain.EMPTY_SP);
			if (CampaignQuest.reached(13)) openTerrain(cell(7, 16), Terrain.EMPTY_SP);
			if (!CampaignQuest.reached(7) || map[cell(14, 11)] == Terrain.EMPTY_SP) return;
			openTerrain(cell(14, 11), Terrain.EMPTY_SP);
			openTerrain(cell(14, 12), Terrain.EMPTY_SP);
			for (Mob mob : mobs) {
				if (mob instanceof Rootwalker) ((Rootwalker) mob).distract(cell(10, 13));
			}
		}
	}

	public static class Ferry extends RiverSection {

		private static final int[][] ROUTES = {
				{1, 9, 7, 9, 10, 10, 14, 10, 17, 9, 22, 9},
				{10, 10, 8, 13, 8, 16}, {12, 8, 12, 10}, {16, 14, 13, 14, 10, 10}
		};

		@Override
		protected boolean build() {
			ground(24, 20);
			cliffs(1, 1, 5, 4);
			cliffs(19, 1, 4, 5);
			cliffs(1, 14, 3, 5);
			cliffs(21, 14, 2, 5);
			river(4, 16, 12, 3);
			river(5, 15, 5, 1);
			river(11, 15, 3, 1);
			boards(7, 12, 3, 6);
			boards(10, 16, 2, 2);
			if (CampaignQuest.reached(22)) boards(10, 15, 1, 1);

			paintHouse(8, 3, 8, 6, 12, 8);
			placeFurniture(9, 4, SurfaceFurniture.Type.BED);
			placeFurniture(13, 4, SurfaceFurniture.Type.TABLE);
			placeFurniture(14, 6, SurfaceFurniture.Type.STOVE);
			paintHouse(16, 12, 5, 5, 16, 14);
			placeFurniture(18, 13, SurfaceFurniture.Type.FIREWOOD);
			placeExterior(3, 6, Type.BIRCH);
			placeExterior(17, 7, Type.OAK);
			placeExterior(11, 13, Type.HANDCART);
			placeExterior(5, 12, Type.CHOPPING_BLOCK);

			passage(-2, 1, 9, 2, 10, cell(4, 9), 0);
			passage(-4, 21, 9, 22, 10, cell(19, 9), 16);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 8, 3, 8, 6) || insideHouse(x, y, 16, 12, 5, 5);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(15, 10, 16);
			waypoint(8, 14, 21);
			waypoint(10, 17, 22);
			waypoint(12, 6, 24);
			waypoint(15, 15, 28);
			waypoint(9, 12, 30);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(22)) openTerrain(cell(10, 15), Terrain.EMPTY_SP);
		}
	}

	public static class Floodplain extends RiverSection {

		private static final int[][] ROUTES = {
				{1, 12, 8, 12, 10, 11, 13, 10, 16, 10, 20, 12, 28, 12},
				{8, 12, 10, 14, 13, 17, 17, 16, 20, 14, 20, 12},
				{14, 1, 14, 6, 15, 8, 16, 10}, {17, 16, 16, 14}
		};

		@Override
		protected boolean build() {
			ground(30, 26);
			cliffs(1, 1, 7, 2);
			cliffs(1, 3, 2, 5);
			cliffs(22, 1, 7, 2);
			cliffs(27, 3, 2, 8);
			cliffs(1, 24, 12, 1);
			cliffs(23, 24, 6, 1);
			cliffs(25, 11, 4, 1);
			cliffs(25, 14, 4, 1);
			cliffs(12, 12, 7, 1);

			river(3, 3, 8, 6);
			river(5, 9, 4, 2);
			river(18, 3, 8, 7);
			river(20, 10, 4, 1);
			river(2, 16, 9, 8);
			river(4, 15, 5, 1);
			river(14, 19, 12, 5);
			river(18, 17, 6, 2);
			river(25, 12, 2, 2);
			if (CampaignQuest.reached(18)) boards(25, 12, 2, 2);

			placeExterior(12, 5, Type.BIRCH);
			placeExterior(5, 13, Type.PINE);
			placeExterior(11, 15, Type.BIRCH);
			placeExterior(15, 17, Type.BIRCH);
			placeExterior(25, 16, Type.OAK);
			placeFurniture(15, 15, SurfaceFurniture.Type.FIREWOOD);
			boards(15, 13, 3, 2);

			passage(-3, 1, 12, 2, 13, cell(4, 12), 0);
			passage(-5, 27, 12, 28, 13, cell(25, 12), 18);
			passage(-6, 14, 1, 15, 2, cell(14, 4), 0);
			return true;
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(16, 14, 17);
			waypoint(24, 11, 18);
			waypoint(10, 10, 25);
			waypoint(9, 14, 1022);
			placeMob(new Reedcutter(), 10, 9);
			placeMob(new Reedcutter(), 22, 16);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (!CampaignQuest.reached(18) || map[cell(25, 12)] == Terrain.EMPTY_SP) return;
			for (int y = 12; y <= 13; y++) {
				for (int x = 25; x <= 26; x++) {
					openTerrain(cell(x, y), Terrain.EMPTY_SP);
				}
			}
		}
	}

	public static class Hamlet extends RiverSection {

		private static final int[][] ROUTES = {
				{10, 18, 10, 14, 10, 11, 11, 10, 12, 8},
				{6, 9, 6, 11, 10, 11, 15, 11, 15, 9}
		};

		@Override
		protected boolean build() {
			ground(22, 20);
			cliffs(1, 1, 6, 2);
			cliffs(1, 3, 2, 6);
			cliffs(16, 1, 5, 2);
			cliffs(19, 3, 2, 7);
			river(1, 12, 4, 7);
			river(5, 16, 12, 3);
			river(16, 13, 5, 6);
			boards(9, 15, 4, 4);
			boards(14, 14, 3, 2);
			if (CampaignQuest.reached(27)) boards(13, 16, 4, 1);

			paintHouse(3, 4, 6, 6, 6, 9);
			placeFurniture(4, 5, SurfaceFurniture.Type.BED);
			placeFurniture(7, 5, SurfaceFurniture.Type.STOVE);
			paintHouse(13, 4, 6, 6, 15, 9);
			placeFurniture(14, 5, SurfaceFurniture.Type.TABLE);
			placeFurniture(17, 5, SurfaceFurniture.Type.BED);
			placeExterior(9, 5, Type.BIRCH);
			placeExterior(9, 9, Type.WELL);
			placeExterior(6, 13, Type.HAYSTACK);
			placeExterior(17, 11, Type.CHOPPING_BLOCK);

			passage(-4, 10, 17, 11, 18, cell(10, 15), 0);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 3, 4, 6, 6) || insideHouse(x, y, 13, 4, 6, 6);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(12, 13, 23);
			waypoint(12, 11, 26);
			waypoint(10, 13, 27);
			waypoint(14, 15, 29);
			waypoint(7, 12, 1030);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (!CampaignQuest.reached(27)) return;
			for (int x = 13; x <= 16; x++) openTerrain(cell(x, 16), Terrain.EMPTY_SP);
		}
	}

	public static class SaltYard extends RiverSection {

		private static final int[][] ROUTES = {
				{11, 1, 11, 5, 12, 8, 12, 11, 13, 14, 16, 15, 20, 14},
				{7, 11, 7, 13, 10, 14, 13, 14}, {15, 13, 12, 11},
				{12, 8, 16, 9, 22, 9}
		};

		@Override
		protected boolean build() {
			ground(24, 20);
			cliffs(1, 1, 8, 3);
			cliffs(1, 4, 3, 2);
			cliffs(16, 1, 7, 4);
			cliffs(21, 5, 2, 3);
			cliffs(1, 14, 3, 5);
			cliffs(4, 17, 6, 2);
			cliffs(17, 17, 6, 2);

			paintHouse(3, 6, 8, 6, 7, 11);
			placeFurniture(4, 7, SurfaceFurniture.Type.TABLE);
			placeFurniture(9, 7, SurfaceFurniture.Type.CUPBOARD);
			paintHouse(15, 11, 6, 6, 15, 13);
			placeFurniture(16, 12, SurfaceFurniture.Type.FIREWOOD);
			placeFurniture(19, 15, SurfaceFurniture.Type.TABLE);
			placeExterior(6, 13, Type.HANDCART);
			placeExterior(11, 16, Type.WELL);
			placeExterior(19, 6, Type.CHOPPING_BLOCK);
			placeExterior(9, 15, Type.PINE);

			passage(-5, 11, 1, 12, 2, cell(11, 4), 0);
			passage(-8, 21, 9, 22, 10, cell(19, 9), 31);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 3, 6, 8, 6) || insideHouse(x, y, 15, 11, 6, 6);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(14, 11, 31);
			waypoint(6, 12, 36);
			waypoint(17, 14, 37);
			waypoint(18, 13, 41);
		}
	}
}

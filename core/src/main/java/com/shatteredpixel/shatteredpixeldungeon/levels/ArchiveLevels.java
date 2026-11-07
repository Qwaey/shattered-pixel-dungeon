/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Cragpecker;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.EmberHound;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Emberling;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.LimestoneCrawler;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.PastureSnapper;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior.Type;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfacePathTiles;
import com.watabou.utils.Bundle;

public final class ArchiveLevels {

	private ArchiveLevels() {
	}

	private static void ground(CampaignLevel level, int width, int height, int[][] rocks) {
		level.setSize(width, height);
		Painter.fill(level, 1, 1, width - 2, height - 2, Terrain.EMPTY);
		for (int[] rock : rocks) {
			Painter.fill(level, rock[0], rock[1], rock[2], rock[3], Terrain.WALL);
		}
	}

	private static void placeMob(CampaignLevel level, Mob mob, int x, int y) {
		mob.pos = level.cell(x, y);
		level.mobs.add(mob);
	}

	private static int path(CampaignLevel level, int pos, int[][] routes) {
		return SurfacePathTiles.getVisual(pos % level.width(), pos / level.width(), routes);
	}

	private abstract static class HillSection extends CampaignLevel {
		protected abstract int[][] routes();

		@Override
		protected int outdoorFloorVisual(int pos) {
			int visual = path(this, pos, routes());
			return visual == -1 ? super.outdoorFloorVisual(pos) : visual;
		}

		@Override
		protected void createMobs() {
		}
	}

	public static class Quarry extends HillSection {
		private static final int[][] ROCKS = {
				{1, 1, 5, 6}, {6, 1, 4, 2}, {21, 1, 8, 6}, {27, 7, 2, 5},
				{7, 9, 6, 1}, {16, 9, 8, 1}, {6, 16, 7, 2}, {16, 17, 3, 2},
				{1, 19, 4, 8}, {5, 24, 6, 3}, {22, 23, 7, 4}, {25, 18, 4, 5}
		};
		private static final int[][] ROUTES = {
				{1, 13, 5, 13, 8, 12, 12, 12, 15, 11, 17, 12, 20, 13, 28, 13},
				{15, 11, 14, 9, 14, 1}, {14, 7, 8, 7}, {5, 13, 5, 18, 10, 20, 14, 19, 18, 15, 20, 13}
		};

		@Override
		protected boolean build() {
			ground(this, 30, 28, ROCKS);
			Painter.fill(this, 13, 8, 3, 3, Terrain.EMPTY_SP);
			if (CampaignQuest.reached(39)) Painter.fill(this, 11, 16, 1, 2, Terrain.EMPTY_SP);
			paintHouse(5, 3, 7, 5, 8, 7);
			placeFurniture(6, 4, SurfaceFurniture.Type.CUPBOARD);
			placeFurniture(10, 6, SurfaceFurniture.Type.TABLE);
			placeFurniture(22, 11, SurfaceFurniture.Type.FIREWOOD);
			placeExterior(4, 14, Type.CHOPPING_BLOCK);
			placeExterior(15, 21, Type.HANDCART);
			passage(-7, 1, 13, 2, 14, cell(4, 13), 0);
			passage(-9, 27, 13, 28, 14, cell(25, 13), 33);
			passage(-10, 14, 1, 15, 2, cell(14, 4), 0);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 5, 3, 7, 5);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(8, 5, 32);
			waypoint(24, 12, 33);
			waypoint(22, 12, 38);
			waypoint(9, 13, 39);
			waypoint(7, 6, 40);
			placeMob(this, new LimestoneCrawler(), 8, 11);
			placeMob(this, new LimestoneCrawler(), 21, 17);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (!CampaignQuest.reached(39)) return;
			openTerrain(cell(11, 16), Terrain.EMPTY_SP);
			openTerrain(cell(11, 17), Terrain.EMPTY_SP);
		}
	}

	public static class Archive extends HillSection {
		private static final int[][] ROCKS = {
				{1, 1, 8, 4}, {15, 1, 8, 3}, {21, 4, 2, 5},
				{1, 15, 5, 6}, {6, 18, 3, 3}, {19, 15, 4, 6}
		};
		private static final int[][] ROUTES = {
				{1, 10, 5, 10, 6, 12, 8, 14, 12, 15, 11, 20}, {12, 15, 13, 12}
		};

		@Override
		protected boolean build() {
			ground(this, 24, 22, ROCKS);
			paintHouse(8, 4, 10, 9, 13, 12);
			placeFurniture(9, 5, SurfaceFurniture.Type.CUPBOARD);
			placeFurniture(10, 5, SurfaceFurniture.Type.CUPBOARD);
			placeFurniture(16, 5, SurfaceFurniture.Type.CUPBOARD);
			placeFurniture(11, 7, SurfaceFurniture.Type.TABLE);
			placeFurniture(15, 9, SurfaceFurniture.Type.TABLE);
			placeFurniture(15, 10, SurfaceFurniture.Type.STOOL);
			placeExterior(18, 10, Type.BIRCH);
			placeExterior(15, 16, Type.WELL);
			passage(-8, 1, 10, 2, 11, cell(4, 10), 0);
			passage(-11, 11, 19, 12, 20, cell(11, 17), 35);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 8, 4, 10, 9);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(11, 8, 34);
			waypoint(11, 16, 35);
			waypoint(14, 8, 44);
			waypoint(16, 10, 45);
		}
	}

	public static class Mine extends AmbushLevel {
		private static final int[][] ROCKS = {
				{1, 1, 8, 2}, {15, 1, 8, 2}, {1, 3, 2, 9}, {20, 3, 3, 9},
				{3, 3, 16, 12}, {5, 15, 4, 1}, {16, 15, 5, 1},
				{1, 17, 5, 6}, {6, 20, 3, 3}, {19, 20, 4, 3}
		};
		private static final int[][] ROUTES = {
				{11, 22, 11, 18, 12, 14, 11, 11, 10, 8, 9, 7}, {11, 18, 15, 18, 18, 17}
		};

		@Override
		protected boolean build() {
			ground(this, 24, 24, ROCKS);
			Painter.fill(this, 4, 4, 14, 10, Terrain.EMPTY);
			Painter.set(this, 12, 14, Terrain.EMPTY_SP);
			Painter.set(this, 8, 9, Terrain.WALL);
			Painter.set(this, 13, 7, Terrain.WALL);
			if (CampaignQuest.reached(42)) Painter.set(this, 14, 16, Terrain.EMPTY_SP);
			placeFurniture(14, 17, SurfaceFurniture.Type.FIREWOOD);
			passage(-8, 11, 21, 12, 22, cell(11, 19), 0);
			prepareAmbush();
			return true;
		}

		@Override
		protected int outdoorFloorVisual(int pos) {
			int visual = path(this, pos, ROUTES);
			return visual == -1 ? super.outdoorFloorVisual(pos) : visual;
		}

		@Override
		protected void createMobs() {
			waypoint(9, 7, 43);
			waypoint(13, 17, 42);
			LimestoneCrawler crawler = new LimestoneCrawler();
			crawler.avoidCell(cell(12, 14));
			placeMob(this, crawler, 17, 18);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(42)) openTerrain(cell(14, 16), Terrain.EMPTY_SP);
		}

		@Override
		protected AmbushPlan ambushPlan() {
			return new AmbushPlan(43, 4, 4, 17, 13,
					new int[]{cell(12, 14)}, new int[0],
					wave(spawn(EmberHound::new, cell(15, 8), cell(15, 6), cell(15, 10))));
		}
	}

	public static class Foothills extends HillSection {
		private static final int[][] ROCKS = {
				{1, 1, 7, 3}, {15, 1, 8, 3}, {21, 4, 2, 10},
				{18, 8, 3, 2}, {19, 10, 2, 3}, {1, 16, 5, 5}, {16, 18, 7, 3}
		};
		private static final int[][] ROUTES = {
				{11, 1, 11, 5, 12, 10, 11, 14, 11, 20}, {12, 10, 17, 11, 19, 11}, {11, 14, 6, 13, 6, 10}
		};

		@Override
		protected boolean build() {
			ground(this, 24, 22, ROCKS);
			paintHouse(3, 5, 7, 6, 6, 10);
			placeFurniture(4, 6, SurfaceFurniture.Type.BED);
			placeFurniture(8, 6, SurfaceFurniture.Type.STOVE);
			placeFurniture(7, 8, SurfaceFurniture.Type.TABLE);
			placeExterior(14, 10, Type.HANDCART);
			placeExterior(6, 13, Type.HANDCART);
			placeExterior(16, 14, Type.WELL);
			placeExterior(9, 6, Type.PINE);
			Painter.set(this, 9, 8, CampaignQuest.reached(53) ? Terrain.EMPTY_SP : Terrain.BARRICADE);
			passage(-9, 11, 1, 12, 2, cell(11, 4), 0);
			passage(-12, 11, 19, 12, 20, cell(11, 17), 46);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 3, 5, 7, 6);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(12, 11, 46);
			waypoint(5, 7, 51);
			waypoint(6, 7, 52);
			waypoint(13, 10, 53);
			waypoint(5, 12, 59);
			waypoint(15, 11, 60);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(53)) openTerrain(cell(9, 8), Terrain.EMPTY_SP);
		}
	}

	public static class MountainRoad extends AmbushLevel {
		private static final int[][] ROCKS = {
				{1, 1, 8, 5}, {20, 1, 9, 5}, {18, 6, 5, 2},
				{1, 7, 4, 5}, {12, 8, 3, 3}, {25, 8, 4, 4},
				{10, 13, 15, 10}, {1, 21, 9, 6}, {10, 23, 4, 4},
				{24, 21, 5, 6}, {19, 24, 5, 3}
		};
		private static final int[][] ROUTES = {
				{14, 1, 14, 5, 10, 7, 10, 11, 7, 13, 1, 13},
				{7, 13, 8, 15, 10, 16, 14, 16, 16, 19, 16, 23, 14, 26},
				{14, 5, 17, 9, 24, 12, 27, 16}
		};

		private int trapCell() {
			return cell(10, 16);
		}

		@Override
		protected boolean build() {
			ground(this, 30, 28, ROCKS);
			Painter.fill(this, 11, 14, 13, 8, Terrain.EMPTY);
			Painter.set(this, 15, 15, Terrain.WALL);
			Painter.set(this, 18, 19, Terrain.WALL);
			Painter.set(this, 9, 16, Terrain.EMPTY_SP);
			Painter.set(this, 16, 22, Terrain.EMPTY_SP);
			if (CampaignQuest.reached(47)) Painter.set(this, 10, 16, Terrain.EMPTY_SP);
			placeExterior(8, 9, Type.PINE);
			placeExterior(3, 16, Type.HANDCART);
			placeFurniture(27, 18, SurfaceFurniture.Type.FIREWOOD);
			passage(-11, 14, 1, 15, 2, cell(14, 4), 0);
			passage(-13, 14, 25, 15, 26, cell(14, 23), 48);
			passage(-14, 1, 13, 2, 14, cell(4, 13), 0);
			prepareAmbush();
			return true;
		}

		@Override
		protected int outdoorFloorVisual(int pos) {
			int visual = path(this, pos, ROUTES);
			return visual == -1 ? super.outdoorFloorVisual(pos) : visual;
		}

		@Override
		protected void createMobs() {
			waypoint(7, 12, 47);
			waypoint(13, 16, 48);
			waypoint(5, 16, 1052);
			waypoint(7, 16, 1059);
			Cragpecker scree = new Cragpecker();
			scree.avoidCell(trapCell());
			placeMob(this, scree, 8, 8);
			Cragpecker ledge = new Cragpecker();
			ledge.avoidCell(trapCell());
			placeMob(this, ledge, 27, 17);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(47) && map[trapCell()] == Terrain.WALL) {
				openTerrain(trapCell(), Terrain.EMPTY_SP);
			}
		}

		@Override
		protected AmbushPlan ambushPlan() {
			return new AmbushPlan(48, 11, 14, 23, 21,
					new int[]{trapCell()}, new int[]{cell(16, 22)},
					wave(spawn(Emberling::new, cell(21, 17), cell(21, 15), cell(21, 19))),
					wave(spawn(EmberHound::new, cell(19, 20), cell(21, 20), cell(22, 18))));
		}
	}

	public static class Pass extends HillSection {
		private static final int[][] ROCKS = {
				{1, 1, 8, 7}, {19, 1, 8, 5}, {1, 8, 6, 7}, {8, 7, 3, 5},
				{16, 8, 3, 3}, {24, 7, 3, 2}, {1, 15, 9, 6}, {10, 18, 5, 3},
				{19, 16, 8, 5}, {24, 12, 3, 4}
		};
		private static final int[][] ROUTES = {
				{13, 1, 13, 6, 12, 8, 12, 13, 15, 15, 19, 14, 22, 12, 26, 10},
				{13, 6, 15, 7, 19, 6, 22, 8, 22, 12}
		};

		@Override
		protected boolean build() {
			ground(this, 28, 22, ROCKS);
			placeFurniture(17, 13, SurfaceFurniture.Type.FIREWOOD);
			placeFurniture(21, 8, SurfaceFurniture.Type.FIREWOOD);
			placeExterior(15, 15, Type.CHOPPING_BLOCK);
			placeExterior(11, 15, Type.PINE);
			if (CampaignQuest.reached(57)) Painter.fill(this, 16, 9, 3, 1, Terrain.EMPTY_SP);
			passage(-12, 13, 1, 14, 2, cell(13, 4), 0);
			passage(-15, 25, 10, 26, 11, cell(23, 10), 50);
			return true;
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(18, 13, 49);
			waypoint(22, 8, 50);
			waypoint(15, 9, 57);
			waypoint(20, 7, 58);
			waypoint(20, 11, 1060);
			placeMob(this, new Cragpecker(), 13, 11);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(57)) {
				for (int x = 16; x <= 18; x++) openTerrain(cell(x, 9), Terrain.EMPTY_SP);
			}
		}
	}

	public static class SheepValley extends HillSection {
		private static final int[][] ROCKS = {
				{1, 1, 9, 4}, {15, 1, 8, 3}, {1, 5, 4, 8}, {21, 4, 2, 7},
				{7, 14, 4, 2}, {13, 15, 3, 1}, {1, 16, 5, 7}, {6, 20, 8, 3}, {17, 18, 6, 5}
		};
		private static final int[][] ROUTES = {
				{22, 11, 16, 11, 14, 10, 12, 10, 8, 11, 6, 13}, {12, 10, 12, 8},
				{16, 11, 16, 14, 14, 17, 10, 17}
		};
		private static final String SNAPPER_STARTED = "snapper_started";
		private boolean snapperStarted;

		@Override
		protected boolean build() {
			ground(this, 24, 24, ROCKS);
			paintHouse(10, 4, 7, 5, 12, 8);
			placeFurniture(11, 5, SurfaceFurniture.Type.FIREWOOD);
			placeFurniture(15, 5, SurfaceFurniture.Type.BED);
			placeExterior(7, 7, Type.PINE);
			placeExterior(8, 10, Type.HAYSTACK);
			placeExterior(13, 12, Type.HAYSTACK);
			placeExterior(6, 13, Type.CHOPPING_BLOCK);
			placeExterior(17, 15, Type.PINE);
			if (CampaignQuest.reached(54)) Painter.fill(this, 11, 12, 2, 1, Terrain.EMPTY_SP);
			passage(-12, 21, 11, 22, 12, cell(19, 11), 0);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 10, 4, 7, 5);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(11, 11, 54);
			waypoint(14, 9, 55);
			waypoint(12, 9, 56);
			placeMob(this, new Cragpecker(), 6, 10);
			placeMob(this, new Cragpecker(), 11, 17);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(54)) {
				openTerrain(cell(11, 12), Terrain.EMPTY_SP);
				openTerrain(cell(12, 12), Terrain.EMPTY_SP);
			}
		}

		public boolean startSnapper() {
			if (Dungeon.level != this || snapperStarted || !CampaignQuest.canComplete(56)) return false;
			int[] spawnCells = {cell(15, 10), cell(17, 10), cell(17, 11)};
			for (int spawn : spawnCells) {
				if (!passable[spawn] || Actor.findChar(spawn) != null) continue;
				PastureSnapper snapper = new PastureSnapper();
				snapper.pos = spawn;
				snapperStarted = true;
				GameScene.add(snapper, Actor.TICK);
				return true;
			}
			return false;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SNAPPER_STARTED, snapperStarted);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			snapperStarted = bundle.getBoolean(SNAPPER_STARTED);
		}
	}
}

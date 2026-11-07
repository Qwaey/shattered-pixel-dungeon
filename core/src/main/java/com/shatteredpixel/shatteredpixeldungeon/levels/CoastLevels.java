/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Emberling;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Fireweaver;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Glassshooter;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Puddleskipper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shellback;
import com.shatteredpixel.shatteredpixeldungeon.levels.RoadsideLevels.RiverSection;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior.Type;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceLandscapeTiles.Style;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfacePathTiles;

public final class CoastLevels {

	private CoastLevels() {
	}

	public static class LastSettlement extends RiverSection {

		@Override
		protected boolean isDeckCell(int x, int y) {
			return inDeck(x, y, 10, 17, 2, 1);
		}

		private static final int[][] ROUTES = {
				{1, 11, 7, 11, 10, 12, 16, 12, 20, 11, 24, 11},
				{8, 9, 8, 11, 8, 14, 11, 17}, {15, 9, 15, 12}
		};

		@Override
		protected boolean build() {
			ground(26, 24);
			cliffs(1, 1, 5, 8);
			cliffs(20, 1, 5, 7);
			cliffs(1, 15, 3, 8);
			cliffs(22, 16, 3, 7);
			river(4, 19, 18, 4);
			river(10, 17, 8, 2);
			boards(7, 14, 4, 3);
			paintHouse(5, 4, 7, 6, 8, 9);
			placeFurniture(6, 5, SurfaceFurniture.Type.BED);
			placeFurniture(10, 6, SurfaceFurniture.Type.STOVE);
			paintHouse(13, 4, 7, 6, 15, 9);
			placeFurniture(14, 5, SurfaceFurniture.Type.CUPBOARD);
			placeFurniture(17, 6, SurfaceFurniture.Type.TABLE);
			placeExterior(5, 13, Type.HANDCART);
			placeExterior(11, 13, Type.WELL);
			placeExterior(19, 14, Type.OAK);
			placeFurniture(7, 15, SurfaceFurniture.Type.FIREWOOD);
			if (CampaignQuest.reached(75)) boards(10, 17, 2, 1);
			passage(-13, 1, 11, 2, 12, cell(4, 11), 0);
			passage(-16, 23, 11, 24, 12, cell(21, 11), 61);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 5, 4, 7, 6) || insideHouse(x, y, 13, 4, 7, 6);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(17, 11, 61);
			waypoint(8, 14, 66);
			waypoint(8, 7, 67);
			waypoint(18, 6, 74);
			waypoint(15, 13, 75);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(75)) {
				openTerrain(cell(10, 17), Terrain.EMPTY_SP);
				openTerrain(cell(11, 17), Terrain.EMPTY_SP);
			}
		}
	}

	public static class Orchards extends RiverSection {

		@Override
		protected boolean isDeckCell(int x, int y) {
			return inDeck(x, y, 8, 12, 5, 2) || inDeck(x, y, 14, 4, 2, 5)
					|| inDeck(x, y, 8, 14, 2, 5) || inDeck(x, y, 16, 16, 2, 2);
		}

		private static final int[][] ROUTES = {
				{1, 12, 9, 12, 13, 12, 17, 12, 22, 12, 28, 12},
				{14, 1, 14, 8, 15, 12}, {9, 13, 9, 19, 16, 19, 16, 14}
		};

		@Override
		protected boolean build() {
			ground(30, 26);
			cliffs(1, 1, 11, 3);
			cliffs(17, 1, 12, 3);
			cliffs(1, 22, 11, 3);
			cliffs(20, 22, 9, 3);
			river(1, 4, 28, 18);
			Painter.fill(this, 1, 11, 7, 4, Terrain.EMPTY);
			boards(8, 12, 5, 2);
			Painter.fill(this, 13, 9, 6, 7, Terrain.EMPTY);
			boards(14, 4, 2, 5);
			Painter.fill(this, 13, 3, 4, 1, Terrain.EMPTY);
			Painter.fill(this, 19, 11, 6, 4, Terrain.EMPTY);
			boards(8, 14, 2, 5);
			Painter.fill(this, 8, 18, 10, 3, Terrain.EMPTY);
			boards(16, 16, 2, 2);
			Painter.set(this, 12, 12, Terrain.WATER);
			Painter.set(this, 11, 19, Terrain.WATER);
			if (CampaignQuest.reached(62)) boards(25, 12, 2, 2);

			placeExterior(5, 11, Type.APPLE_TREE);
			placeExterior(16, 10, Type.APPLE_TREE);
			placeExterior(10, 18, Type.APPLE_TREE);
			placeExterior(19, 14, Type.APPLE_TREE);
			placeFurniture(14, 13, SurfaceFurniture.Type.FIREWOOD);
			passage(-15, 1, 12, 2, 13, cell(4, 12), 0);
			passage(-17, 27, 12, 28, 13, cell(25, 12), 62);
			passage(-18, 14, 1, 15, 2, cell(14, 4), 0);
			return true;
		}

		@Override
			protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(22, 11, 62);
			waypoint(10, 12, 1067);
			waypoint(17, 11, 1068);
			waypoint(12, 19, 1071);
			placeMob(new Puddleskipper(), 12, 12);
			placeMob(new Puddleskipper(), 11, 19);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (!CampaignQuest.reached(62) || map[cell(25, 12)] == Terrain.EMPTY_SP) return;
			for (int y = 12; y <= 13; y++) {
				for (int x = 25; x <= 26; x++) openTerrain(cell(x, y), Terrain.EMPTY_SP);
			}
		}
	}

	public static class Spillway extends AmbushLevel {

		@Override
		public Style landscapeStyle() {
			return Style.COAST;
		}

		@Override
		protected boolean isDeckCell(int x, int y) {
			return inDeck(x, y, 1, 9, 6, 6) || inDeck(x, y, 3, 14, 3, 5)
					|| inDeck(x, y, 4, 7, 3, 2) || (x == 7 && y == 11)
					|| inDeck(x, y, 13, 17, 3, 2) || inDeck(x, y, 13, 19, 2, 2);
		}

		private static final int[][] ROUTES = {
				{1, 11, 5, 11, 7, 11, 10, 11, 13, 11, 14, 15, 14, 18, 13, 22}, {5, 11, 5, 15, 4, 18}
		};

		private int lowerGate() {
			return cell(7, 11);
		}

		@Override
		protected boolean build() {
			setSize(28, 24);
			Painter.fill(this, 1, 1, 26, 22, Terrain.CUSTOM_DECO_WTR);
			Painter.fill(this, 1, 9, 6, 6, Terrain.EMPTY_SP);
			Painter.fill(this, 3, 14, 3, 5, Terrain.EMPTY_SP);
			Painter.fill(this, 4, 7, 3, 2, Terrain.EMPTY_SP);
			Painter.fill(this, 7, 5, 14, 12, Terrain.WALL);
			Painter.fill(this, 8, 6, 12, 10, CampaignQuest.reached(63) ? Terrain.EMPTY : Terrain.CUSTOM_DECO_WTR);
			Painter.fill(this, 9, 10, 2, 2, Terrain.EMPTY_SP);
			Painter.fill(this, 11, 8, 2, 1, Terrain.WALL);
			Painter.fill(this, 16, 12, 2, 1, Terrain.WALL);
			Painter.set(this, 7, 11, CampaignQuest.reached(63) ? Terrain.EMPTY_SP : Terrain.CUSTOM_DECO_WTR);
			Painter.fill(this, 13, 17, 3, 2, Terrain.EMPTY_SP);
			Painter.fill(this, 13, 19, 2, 2, CampaignQuest.reached(65) ? Terrain.EMPTY_SP : Terrain.CUSTOM_DECO_WTR);
			Painter.set(this, 4, 16, CampaignQuest.reached(63) ? Terrain.EMPTY_SP : Terrain.WATER);
			Painter.set(this, 5, 17, Terrain.WATER);
			placeFurniture(4, 8, SurfaceFurniture.Type.FIREWOOD);
			passage(-16, 1, 11, 2, 12, cell(4, 11), 0);
			passage(-19, 13, 21, 14, 22, cell(13, 19), 65);
			prepareAmbush();
			return true;
		}

		@Override
		protected int outdoorFloorVisual(int pos) {
			int visual = SurfacePathTiles.getVisual(pos % width(), pos / width(), ROUTES);
			return visual == -1 ? super.outdoorFloorVisual(pos) : visual;
		}

		@Override
		protected void createMobs() {
			waypoint(5, 10, 63);
			waypoint(10, 11, 64);
			waypoint(15, 17, 65);
			waypoint(4, 16, 1074);
			Puddleskipper skulker = new Puddleskipper();
			skulker.pos = cell(5, 17);
			skulker.avoidCell(lowerGate());
			mobs.add(skulker);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(63) && map[lowerGate()] == Terrain.CUSTOM_DECO_WTR) {
				for (int y = 6; y <= 15; y++) {
					for (int x = 8; x <= 19; x++) {
						int pos = cell(x, y);
						if (map[pos] == Terrain.CUSTOM_DECO_WTR) openTerrain(pos, Terrain.EMPTY);
					}
				}
				openTerrain(lowerGate(), Terrain.EMPTY_SP);
				openTerrain(cell(4, 16), Terrain.EMPTY_SP);
			}
			if (CampaignQuest.reached(65)) {
				for (int y = 19; y <= 20; y++) {
					for (int x = 13; x <= 14; x++) openTerrain(cell(x, y), Terrain.EMPTY_SP);
				}
			}
		}

		@Override
		protected AmbushPlan ambushPlan() {
			return new AmbushPlan(64, 8, 6, 19, 15,
					new int[]{lowerGate()}, new int[]{cell(14, 16)},
					wave(spawn(Fireweaver::new, cell(18, 8), cell(18, 7), cell(18, 10)),
							spawn(Emberling::new, cell(12, 13), cell(10, 14), cell(14, 14))));
		}
	}

	public static class BellYard extends RiverSection {
		private static final int[][] ROUTES = {
				{10, 18, 10, 14, 10, 11, 10, 8}, {6, 9, 6, 11, 10, 12, 14, 11, 16, 9}
		};

		@Override
		protected boolean build() {
			ground(22, 20);
			cliffs(1, 1, 6, 4);
			cliffs(15, 1, 6, 3);
			cliffs(1, 5, 2, 8);
			cliffs(19, 4, 2, 9);
			cliffs(1, 15, 5, 4);
			cliffs(16, 16, 5, 3);
			boards(9, 15, 4, 4);
			paintHouse(4, 5, 6, 5, 6, 9);
			placeFurniture(5, 6, SurfaceFurniture.Type.FIREWOOD);
			placeFurniture(8, 6, SurfaceFurniture.Type.TABLE);
			paintHouse(13, 5, 6, 5, 16, 9);
			placeFurniture(14, 6, SurfaceFurniture.Type.BED);
			placeFurniture(17, 7, SurfaceFurniture.Type.STOVE);
			placeExterior(7, 13, Type.HAYSTACK);
			placeExterior(13, 12, Type.HANDCART);
			placeExterior(11, 6, Type.BIRCH);
			Painter.fill(this, 8, 14, 1, 3, Terrain.BARRICADE);
			Painter.set(this, 4, 7, CampaignQuest.reached(69) ? Terrain.EMPTY_SP : Terrain.BARRICADE);
			if (CampaignQuest.reached(73)) boards(8, 15, 1, 1);
			passage(-16, 10, 17, 11, 18, cell(10, 15), 0);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 4, 5, 6, 5) || insideHouse(x, y, 13, 5, 6, 5);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(11, 11, 68);
			waypoint(7, 7, 69);
			waypoint(6, 7, 70);
			waypoint(16, 11, 71);
			waypoint(14, 10, 72);
			waypoint(11, 14, 73);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(69)) openTerrain(cell(4, 7), Terrain.EMPTY_SP);
			if (CampaignQuest.reached(73)) openTerrain(cell(8, 15), Terrain.EMPTY_SP);
		}
	}

	public static class CoastStation extends RiverSection {

		@Override
		public Style landscapeStyle() {
			return Style.COAST;
		}

		@Override
		protected boolean isDeckCell(int x, int y) {
			return inDeck(x, y, 10, 16, 4, 5);
		}

		private static final int[][] ROUTES = {
				{11, 1, 11, 5, 11, 9, 12, 12, 11, 17, 11, 20}, {7, 9, 7, 11, 11, 12, 16, 13, 16, 11}
		};

		@Override
		protected boolean build() {
			ground(24, 22);
			cliffs(1, 1, 7, 4);
			cliffs(16, 1, 7, 5);
			cliffs(1, 5, 2, 7);
			cliffs(21, 6, 2, 7);
			river(1, 16, 9, 5);
			river(14, 17, 9, 4);
			boards(10, 15, 4, 6);
			paintHouse(4, 5, 6, 5, 7, 9);
			placeFurniture(5, 6, SurfaceFurniture.Type.BED);
			placeFurniture(8, 7, SurfaceFurniture.Type.STOVE);
			paintHouse(14, 6, 7, 6, 16, 11);
			placeFurniture(15, 7, SurfaceFurniture.Type.TABLE);
			placeFurniture(19, 7, SurfaceFurniture.Type.CUPBOARD);
			placeExterior(8, 13, Type.WELL);
			placeExterior(17, 13, Type.HANDCART);
			passage(-17, 11, 1, 12, 2, cell(11, 4), 0);
			passage(-20, 11, 19, 12, 20, cell(11, 17), 76);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 4, 5, 6, 5) || insideHouse(x, y, 14, 6, 7, 6);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(13, 11, 76);
			waypoint(6, 7, 81);
			waypoint(16, 8, 87);
			waypoint(18, 9, 89);
		}
	}

	public static class WreckBay extends RiverSection {

		@Override
		public Style landscapeStyle() {
			return Style.COAST;
		}

		@Override
		protected boolean isDeckCell(int x, int y) {
			return inDeck(x, y, 11, 8, 8, 3) || inDeck(x, y, 11, 11, 2, 7)
					|| inDeck(x, y, 11, 17, 11, 3) || inDeck(x, y, 17, 10, 6, 7)
					|| inDeck(x, y, 14, 23, 2, 4) || inDeck(x, y, 13, 13, 4, 1);
		}

		private static final int[][] ROUTES = {
				{14, 1, 14, 5, 18, 7, 22, 8, 24, 11, 28, 13},
				{18, 7, 18, 11, 20, 15, 20, 19, 17, 21, 14, 26}, {24, 11, 25, 15, 23, 19, 17, 21}
		};

		@Override
		protected boolean build() {
			ground(30, 28);
			cliffs(1, 1, 10, 3);
			cliffs(21, 1, 8, 5);
			cliffs(26, 6, 3, 5);
			cliffs(23, 24, 6, 3);
			river(1, 4, 12, 23);
			river(13, 9, 5, 12);
			boards(11, 8, 8, 3);
			boards(11, 11, 2, 7);
			boards(11, 17, 11, 3);
			boards(17, 10, 6, 7);
			boards(14, 23, 2, 4);
			if (CampaignQuest.reached(86)) boards(13, 13, 4, 1);
			cliffs(22, 15, 1, 2);
			placeFurniture(19, 12, SurfaceFurniture.Type.FIREWOOD);
			placeFurniture(20, 16, SurfaceFurniture.Type.TABLE);
			placeFurniture(23, 11, SurfaceFurniture.Type.FIREWOOD);
			passage(-19, 14, 1, 15, 2, cell(14, 4), 0);
			passage(-21, 14, 25, 15, 26, cell(14, 23), 77);
			passage(-22, 27, 13, 28, 14, cell(25, 13), 0);
			return true;
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(20, 15, 77);
			waypoint(12, 14, 1083);
			waypoint(23, 18, 84);
			waypoint(18, 10, 86);
			placeMob(new Shellback(), 16, 9);
			placeMob(new Shellback(), 20, 19);
			placeMob(new Shellback(), 11, 16);
			placeMob(new Shellback(), 24, 21);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(86)) {
				for (int x = 13; x <= 16; x++) openTerrain(cell(x, 13), Terrain.EMPTY_SP);
			}
		}
	}

	public static class BeaconChain extends AmbushLevel {

		@Override
		public Style landscapeStyle() {
			return Style.COAST;
		}

		private static final int[][] ROUTES = {
				{13, 1, 13, 7, 13, 10, 14, 12, 18, 12, 21, 12, 26, 12}, {14, 12, 14, 16, 18, 16}
		};

		@Override
		protected boolean build() {
			setSize(28, 26);
			Painter.fill(this, 1, 1, 26, 24, Terrain.CUSTOM_DECO_WTR);
			Painter.fill(this, 10, 1, 8, 6, Terrain.EMPTY);
			Painter.fill(this, 7, 7, 15, 13, Terrain.WALL);
			Painter.fill(this, 8, 8, 13, 11, Terrain.EMPTY);
			Painter.fill(this, 22, 9, 5, 8, Terrain.EMPTY);
			Painter.set(this, 13, 7, Terrain.EMPTY_SP);
			Painter.fill(this, 11, 12, 3, 1, Terrain.WALL);
			Painter.fill(this, 15, 14, 3, 1, Terrain.WALL);
			Painter.fill(this, 18, 9, 1, 2, Terrain.WALL);
			if (CampaignQuest.reached(90)) Painter.fill(this, 24, 15, 1, 2, Terrain.EMPTY_SP);
			placeFurniture(11, 5, SurfaceFurniture.Type.FIREWOOD);
			placeExterior(15, 4, Type.CHOPPING_BLOCK);
			passage(-20, 13, 1, 14, 2, cell(13, 4), 0);
			passage(-23, 25, 12, 26, 13, cell(23, 12), 80);
			prepareAmbush();
			return true;
		}

		@Override
		protected int outdoorFloorVisual(int pos) {
			int visual = SurfacePathTiles.getVisual(pos % width(), pos / width(), ROUTES);
			return visual == -1 ? super.outdoorFloorVisual(pos) : visual;
		}

		@Override
		protected void createMobs() {
			waypoint(13, 5, 78);
			waypoint(13, 10, 79);
			waypoint(23, 11, 80);
			waypoint(12, 4, 1087);
			waypoint(16, 5, 1089);
			waypoint(25, 15, 90);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(90)) {
				openTerrain(cell(24, 15), Terrain.EMPTY_SP);
				openTerrain(cell(24, 16), Terrain.EMPTY_SP);
			}
		}

		@Override
		protected AmbushPlan ambushPlan() {
			return new AmbushPlan(79, 8, 8, 20, 18,
					new int[]{cell(13, 7)}, new int[]{cell(21, 12)},
					wave(spawn(Emberling::new, cell(11, 13), cell(10, 13), cell(11, 14))),
					timedWave(1, spawn(Glassshooter::new, cell(18, 16), cell(19, 16), cell(18, 17))));
		}
	}

	public static class Shipyard extends RiverSection {

		@Override
		public Style landscapeStyle() {
			return Style.COAST;
		}

		@Override
		protected boolean isDeckCell(int x, int y) {
			return inDeck(x, y, 14, 12, 5, 7) || inDeck(x, y, 7, 14, 7, 3);
		}

		private static final int[][] ROUTES = {
				{1, 10, 6, 10, 9, 11, 14, 11, 16, 12, 16, 17}, {12, 9, 12, 11}
		};

		@Override
		protected boolean build() {
			ground(24, 22);
			cliffs(1, 1, 6, 6);
			cliffs(17, 1, 6, 5);
			cliffs(21, 6, 2, 8);
			river(1, 15, 22, 6);
			river(1, 13, 5, 2);
			boards(14, 12, 5, 7);
			boards(7, 14, 7, 3);
			paintHouse(9, 4, 8, 6, 12, 9);
			placeFurniture(10, 5, SurfaceFurniture.Type.TABLE);
			placeFurniture(15, 5, SurfaceFurniture.Type.CUPBOARD);
			placeFurniture(15, 7, SurfaceFurniture.Type.FIREWOOD);
			placeFurniture(15, 13, SurfaceFurniture.Type.FIREWOOD);
			placeExterior(7, 11, Type.HANDCART);
			placeExterior(18, 11, Type.CHOPPING_BLOCK);
			passage(-20, 1, 10, 2, 11, cell(4, 10), 0);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 9, 4, 8, 6);
		}

		@Override
		protected int[][] routes() {
			return ROUTES;
		}

		@Override
		protected void createMobs() {
			waypoint(15, 10, 82);
			waypoint(14, 15, 83);
			waypoint(13, 6, 85);
			waypoint(17, 12, 88);
		}
	}
}

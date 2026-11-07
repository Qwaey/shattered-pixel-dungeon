/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.AshWarden;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Cliffstalker;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.EmberHound;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Emberling;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.CampaignNPC;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SheepSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior.Type;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceLandscapeTiles.Style;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundle;

public final class EdgeLevels {

	private EdgeLevels() {
	}

	private static void ground(CampaignLevel level, int width, int height) {
		level.setSize(width, height);
		Painter.fill(level, 1, 1, width - 2, height - 2, Terrain.EMPTY);
	}

	private static void cliff(CampaignLevel level, int x, int y, int width, int height) {
		Painter.fill(level, x, y, width, height, Terrain.WALL);
	}

	private static void boards(CampaignLevel level, int x, int y, int width, int height) {
		Painter.fill(level, x, y, width, height, Terrain.EMPTY_SP);
	}

	private static void placeStalker(CampaignLevel level, int x, int y) {
		Cliffstalker stalker = new Cliffstalker();
		stalker.pos = level.cell(x, y);
		level.mobs.add(stalker);
	}

	private abstract static class QuietSection extends CampaignLevel {

		@Override
		public Style landscapeStyle() {
			return Style.EDGE;
		}

		@Override
		protected void createMobs() {
		}
	}

	public static class PenSheep extends CampaignNPC {

		public PenSheep() {
			super(97);
			spriteClass = SheepSprite.class;
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			spriteClass = SheepSprite.class;
		}
	}

	public static class Highland extends QuietSection {

		@Override
		protected boolean build() {
			ground(this, 24, 22);
			cliff(this, 1, 1, 4, 5);
			cliff(this, 1, 17, 7, 4);
			cliff(this, 17, 1, 6, 4);
			cliff(this, 20, 4, 3, 4);
			cliff(this, 21, 14, 2, 7);
			paintHouse(6, 3, 8, 7, 10, 9);
			paintHouse(15, 12, 7, 6, 15, 15);
			placeFurniture(7, 4, SurfaceFurniture.Type.BED);
			placeFurniture(11, 5, SurfaceFurniture.Type.TABLE);
			placeFurniture(19, 13, SurfaceFurniture.Type.STOVE);
			placeExterior(4, 7, Type.BIRCH);
			placeExterior(9, 14, Type.WELL);
			placeExterior(12, 17, Type.OAK);
			boards(this, 9, 10, 2, 3);
			cliff(this, 16, 18, 5, 3);
			boards(this, 17, 19, 3, 1);
			if (CampaignQuest.reached(97)) boards(this, 16, 19, 1, 1);
			if (CampaignQuest.reached(96)) Painter.set(this, 10, 13, Terrain.PEDESTAL);
			if (CampaignQuest.reached(105)) Painter.fill(this, 11, 16, 3, 1, Terrain.FURROWED_GRASS);
			passage(-21, 1, 10, 2, 11, cell(4, 10), 0);
			passage(-24, 21, 10, 22, 11, cell(19, 10), 91);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 6, 3, 8, 7) || insideHouse(x, y, 15, 12, 7, 6);
		}

		@Override
		protected void createMobs() {
			waypoint(14, 10, 91);
			waypoint(11, 13, 96);
			PenSheep sheep = new PenSheep();
			sheep.pos = cell(CampaignQuest.reached(97) ? 18 : 15, 19);
			mobs.add(sheep);
			waypoint(7, 12, 101);
			waypoint(12, 15, 105);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(96)) openTerrain(cell(10, 13), Terrain.PEDESTAL);
			if (CampaignQuest.reached(97) && map[cell(16, 19)] == Terrain.WALL) {
				openTerrain(cell(16, 19), Terrain.EMPTY_SP);
				for (Mob mob : mobs) {
					if (!(mob instanceof PenSheep)) continue;
					mob.pos = cell(18, 19);
					if (mob.sprite != null) mob.sprite.place(mob.pos);
					break;
				}
			}
			if (CampaignQuest.reached(105)) {
				for (int x = 11; x <= 13; x++) openTerrain(cell(x, 16), Terrain.FURROWED_GRASS);
			}
		}
	}

	public static class Plateau extends AmbushLevel {

		@Override
		public Style landscapeStyle() {
			return Style.EDGE;
		}


		@Override
		protected boolean build() {
			ground(this, 30, 28);
			cliff(this, 1, 1, 7, 5);
			cliff(this, 8, 1, 4, 3);
			cliff(this, 1, 20, 5, 7);
			cliff(this, 20, 23, 9, 4);
			cliff(this, 26, 1, 3, 12);
			cliff(this, 26, 15, 3, 12);
			cliff(this, 11, 4, 15, 18);
			Painter.fill(this, 12, 5, 13, 16, Terrain.EMPTY);
			cliff(this, 16, 7, 2, 4);
			cliff(this, 19, 12, 2, 3);
			cliff(this, 17, 16, 2, 2);
			cliff(this, 22, 8, 1, 3);
			boards(this, 12, 10, 4, 1);
			if (CampaignQuest.reached(92)) boards(this, 11, 10, 1, 1);
			boards(this, 26, 13, 1, 2);
			placeExterior(4, 17, Type.PINE);
			placeFurniture(7, 15, SurfaceFurniture.Type.FIREWOOD);
			if (CampaignQuest.reached(98)) Painter.set(this, 6, 20, Terrain.EMPTY_SP);
			if (CampaignQuest.reached(99)) Painter.set(this, 11, 23, Terrain.PEDESTAL);
			prepareAmbush();
			passage(-23, 1, 13, 2, 14, cell(4, 13), 0);
			passage(-25, 27, 13, 28, 14, cell(26, 13), 93);
			passage(-26, 14, 25, 15, 26, cell(14, 23), 0);
			return true;
		}

		@Override
		protected AmbushPlan ambushPlan() {
			return new AmbushPlan(93, 12, 5, 24, 20,
					new int[]{cell(11, 10)}, new int[]{cell(25, 13)},
					wave(spawn(Emberling::new, cell(22, 13), cell(22, 15), cell(23, 12)),
							spawn(EmberHound::new, cell(17, 6), cell(19, 6), cell(20, 7))),
					timedWave(2, spawn(EmberHound::new, cell(22, 18), cell(23, 18), cell(23, 19))));
		}

		@Override
		protected void createMobs() {
			waypoint(8, 12, 92);
			waypoint(14, 10, 93);
			waypoint(7, 20, 98);
			waypoint(12, 23, 99);
			placePlateauStalker(7, 9);
			placePlateauStalker(9, 21);
		}

		private void placePlateauStalker(int x, int y) {
			Cliffstalker stalker = new Cliffstalker();
			stalker.pos = cell(x, y);
			stalker.avoidCell(cell(11, 10));
			mobs.add(stalker);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(92) && !ambushActive()) openTerrain(cell(11, 10), Terrain.EMPTY_SP);
			if (CampaignQuest.reached(98)) openTerrain(cell(6, 20), Terrain.EMPTY_SP);
			if (CampaignQuest.reached(99)) openTerrain(cell(11, 23), Terrain.PEDESTAL);
		}
	}

	public static class Boundary extends CampaignLevel {

		@Override
		public Style landscapeStyle() {
			return Style.EDGE;
		}

		@Override
		protected boolean isDeckCell(int x, int y) {
			return inDeck(x, y, 13, 6, 2, 2) || (x == 10 && y == 18);
		}


		@Override
		protected boolean build() {
			ground(this, 28, 24);
			cliff(this, 1, 1, 10, 5);
			cliff(this, 17, 1, 10, 5);
			cliff(this, 1, 6, 26, 2);
			cliff(this, 1, 18, 5, 5);
			cliff(this, 21, 19, 6, 4);
			cliff(this, 25, 15, 2, 4);
			cliff(this, 19, 11, 1, 4);
			boards(this, 12, 8, 4, 3);
			boards(this, 12, 4, 4, 2);
			if (CampaignQuest.reached(94)) boards(this, 13, 6, 2, 2);
			if (CampaignQuest.reached(102)) boards(this, 19, 13, 1, 1);
			Painter.set(this, 10, 18, CampaignQuest.reached(103) ? Terrain.EMPTY_SP : Terrain.WATER);
			if (CampaignQuest.reached(104)) Painter.set(this, 16, 5, Terrain.PEDESTAL);
			placeFurniture(9, 12, SurfaceFurniture.Type.FIREWOOD);
			placeExterior(16, 15, Type.CHOPPING_BLOCK);
			passage(-24, 1, 11, 2, 12, cell(4, 11), 0);
			passage(-27, 13, 1, 14, 2, cell(13, 4), 95);
			return true;
		}

		@Override
		protected void createMobs() {
			waypoint(12, 9, 94);
			waypoint(15, 5, 95);
			waypoint(6, 9, 1102);
			waypoint(12, 13, 102);
			waypoint(10, 17, 103);
			waypoint(16, 4, 104);
			placeStalker(this, 22, 15);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(94)) {
				for (int y = 6; y <= 7; y++) {
					openTerrain(cell(13, y), Terrain.EMPTY_SP);
					openTerrain(cell(14, y), Terrain.EMPTY_SP);
				}
			}
			if (CampaignQuest.reached(102)) openTerrain(cell(19, 13), Terrain.EMPTY_SP);
			if (CampaignQuest.reached(103)) openTerrain(cell(10, 18), Terrain.EMPTY_SP);
			if (CampaignQuest.reached(104)) openTerrain(cell(16, 5), Terrain.PEDESTAL);
		}
	}

	public static class Terraces extends CampaignLevel {

		@Override
		public Style landscapeStyle() {
			return Style.EDGE;
		}


		@Override
		protected boolean build() {
			ground(this, 26, 22);
			cliff(this, 1, 1, 8, 4);
			cliff(this, 19, 1, 6, 5);
			cliff(this, 1, 8, 18, 2);
			cliff(this, 8, 13, 17, 2);
			cliff(this, 10, 11, 5, 1);
			cliff(this, 1, 17, 4, 4);
			cliff(this, 21, 18, 4, 3);
			boards(this, 19, 8, 2, 2);
			boards(this, 6, 13, 2, 2);
			if (CampaignQuest.reached(100)) boards(this, 12, 8, 1, 2);
			if (CampaignQuest.reached(1101)) boards(this, 12, 11, 1, 1);
			placeExterior(15, 5, Type.PINE);
			placeExterior(12, 17, Type.OAK);
			placeFurniture(3, 12, SurfaceFurniture.Type.FIREWOOD);
			passage(-24, 12, 1, 13, 2, cell(12, 4), 0);
			return true;
		}

		@Override
		protected void createMobs() {
			waypoint(16, 7, 100);
			waypoint(11, 10, 1101);
			waypoint(19, 16, 1105);
			placeStalker(this, 17, 6);
			placeStalker(this, 15, 17);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(100)) {
				openTerrain(cell(12, 8), Terrain.EMPTY_SP);
				openTerrain(cell(12, 9), Terrain.EMPTY_SP);
			}
			if (CampaignQuest.reached(1101)) openTerrain(cell(12, 11), Terrain.EMPTY_SP);
		}
	}

	public static class Watchhouse extends QuietSection {

		@Override
		protected boolean build() {
			ground(this, 22, 20);
			cliff(this, 1, 1, 3, 8);
			cliff(this, 15, 1, 6, 5);
			cliff(this, 1, 14, 5, 5);
			cliff(this, 16, 15, 5, 4);
			paintHouse(4, 3, 8, 7, 9, 9);
			placeFurniture(5, 4, SurfaceFurniture.Type.BED);
			placeFurniture(9, 5, SurfaceFurniture.Type.STOVE);
			placeFurniture(6, 7, SurfaceFurniture.Type.TABLE);
			placeFurniture(16, 12, SurfaceFurniture.Type.FIREWOOD);
			Painter.set(this, 12, 10, Terrain.PEDESTAL);
			if (CampaignQuest.reached(112)) Painter.set(this, 7, 7, Terrain.EMPTY_DECO);
			if (CampaignQuest.reached(118)) Painter.set(this, 14, 12, Terrain.PEDESTAL);
			if (CampaignQuest.reached(119)) Painter.set(this, 7, 5, Terrain.EMPTY_DECO);
			if (CampaignQuest.reached(120)) Painter.set(this, 10, 7, Terrain.EMPTY_DECO);
			boards(this, 9, 10, 2, 3);
			passage(-25, 10, 17, 11, 18, cell(10, 15), 0);
			passage(-28, 19, 9, 20, 10, cell(17, 9), 106);
			passage(-30, 13, 1, 14, 2, cell(13, 4), 114);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 4, 3, 8, 7);
		}

		@Override
		protected void createMobs() {
			waypoint(13, 10, 106);
			waypoint(10, 10, 1111);
			waypoint(8, 7, 112);
			waypoint(6, 5, 1117);
			waypoint(14, 11, 118);
			waypoint(7, 6, 119);
			waypoint(8, 5, 120);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(112)) openTerrain(cell(7, 7), Terrain.EMPTY_DECO);
			if (CampaignQuest.reached(118)) openTerrain(cell(14, 12), Terrain.PEDESTAL);
			if (CampaignQuest.reached(119)) openTerrain(cell(7, 5), Terrain.EMPTY_DECO);
			if (CampaignQuest.reached(120)) openTerrain(cell(10, 7), Terrain.EMPTY_DECO);
		}
	}

	public static class Windwalk extends CampaignLevel {

		@Override
		public Style landscapeStyle() {
			return Style.EDGE;
		}

		@Override
		protected boolean isDeckCell(int x, int y) {
			return inDeck(x, y, 20, 11, 1, 2);
		}


		@Override
		protected boolean build() {
			ground(this, 28, 24);
			cliff(this, 1, 1, 7, 7);
			cliff(this, 16, 1, 11, 6);
			cliff(this, 1, 16, 6, 7);
			cliff(this, 13, 18, 5, 5);
			cliff(this, 20, 1, 1, 22);
			cliff(this, 21, 15, 6, 8);
			cliff(this, 9, 12, 1, 5);
			cliff(this, 14, 7, 1, 5);
			cliff(this, 16, 13, 3, 1);
			cliff(this, 7, 17, 3, 3);
			Painter.set(this, 8, 18, Terrain.EMPTY);
			boards(this, 5, 11, 8, 1);
			boards(this, 15, 11, 5, 1);
			boards(this, 21, 11, 4, 2);
			if (CampaignQuest.reached(107)) boards(this, 20, 11, 1, 2);
			if (CampaignQuest.reached(111)) Painter.set(this, 12, 7, Terrain.PEDESTAL);
			if (CampaignQuest.reached(115)) boards(this, 8, 17, 1, 1);
			if (CampaignQuest.reached(1116)) Painter.set(this, 15, 8, Terrain.EMPTY_SP);
			placeFurniture(7, 8, SurfaceFurniture.Type.FIREWOOD);
			placeFurniture(17, 14, SurfaceFurniture.Type.FIREWOOD);
			placeExterior(12, 15, Type.CHOPPING_BLOCK);
			passage(-27, 1, 11, 2, 12, cell(4, 11), 0);
			passage(-29, 25, 11, 26, 12, cell(23, 11), 107);
			passage(-30, 13, 1, 14, 2, cell(13, 4), 0);
			return true;
		}

		@Override
		protected void createMobs() {
			waypoint(19, 11, 107);
			waypoint(11, 7, 111);
			waypoint(8, 16, 115);
			waypoint(15, 9, 1116);
			placeStalker(this, 10, 9);
			placeStalker(this, 16, 15);
			placeStalker(this, 10, 18);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(107)) {
				openTerrain(cell(20, 11), Terrain.EMPTY_SP);
				openTerrain(cell(20, 12), Terrain.EMPTY_SP);
			}
			if (CampaignQuest.reached(111)) openTerrain(cell(12, 7), Terrain.PEDESTAL);
			if (CampaignQuest.reached(115)) openTerrain(cell(8, 17), Terrain.EMPTY_SP);
			if (CampaignQuest.reached(1116)) openTerrain(cell(15, 8), Terrain.EMPTY_SP);
		}
	}

	public static class Brink extends AmbushLevel {

		@Override
		public Style landscapeStyle() {
			return Style.EDGE;
		}

		@Override
		protected boolean isDeckCell(int x, int y) {
			return inDeck(x, y, 21, 12, 2, 1);
		}


		@Override
		protected boolean build() {
			setSize(26, 24);
			Painter.fill(this, 1, 9, 5, 6, Terrain.EMPTY);
			Painter.fill(this, 7, 6, 11, 13, Terrain.EMPTY);
			Painter.fill(this, 19, 9, 2, 6, Terrain.EMPTY);
			Painter.fill(this, 22, 1, 3, height() - 2, Terrain.CUSTOM_DECO);
			boards(this, 4, 11, 3, 1);
			boards(this, 5, 4, 15, 1);
			boards(this, 5, 5, 1, 6);
			boards(this, 19, 5, 1, 5);
			boards(this, 18, 12, 1, 1);
			boards(this, 22, 12, 1, 1);
			if (CampaignQuest.reached(109)) boards(this, 21, 12, 1, 1);
			cliff(this, 11, 8, 1, 1);
			cliff(this, 13, 12, 1, 2);
			cliff(this, 15, 9, 1, 1);
			cliff(this, 14, 16, 1, 1);
			prepareAmbush();
			passage(-28, 1, 11, 2, 12, cell(4, 11), 0);
			return true;
		}

		@Override
		protected AmbushPlan ambushPlan() {
			return new AmbushPlan(108, 7, 6, 17, 18,
					new int[]{cell(6, 11)}, new int[]{cell(18, 12), cell(5, 7), cell(19, 7)},
					wave(spawn(AshWarden::new, cell(16, 13), cell(16, 15), cell(16, 10))));
		}

		@Override
		protected void createMobs() {
			waypoint(9, 11, 108);
			waypoint(19, 10, 109);
			waypoint(22, 12, 110);
			waypoint(20, 13, 1118);
			waypoint(20, 11, 1120);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(109)) openTerrain(cell(21, 12), Terrain.EMPTY_SP);
		}

		@Override
		public Group addWallVisuals() {
			Group group = super.addWallVisuals();
			// The abyss has no fall transition. The overhang at (22,12) is deliberately exposed.
			addAbyss(group, 22, 1, 3, 11);
			addAbyss(group, 23, 12, 2, 1);
			addAbyss(group, 22, 13, 3, 10);
			return group;
		}

		private void addAbyss(Group group, int x, int y, int width, int height) {
			int size = DungeonTilemap.SIZE;
			ColorBlock abyss = new ColorBlock(width * size, height * size, 0xFF15121F);
			abyss.x = x * size;
			abyss.y = y * size;
			abyss.active = false;
			group.add(abyss);
		}
	}

	public static class Storehouse extends CampaignLevel {

		@Override
		public Style landscapeStyle() {
			return Style.EDGE;
		}


		@Override
		protected boolean build() {
			ground(this, 20, 18);
			cliff(this, 1, 1, 4, 3);
			cliff(this, 1, 13, 5, 4);
			cliff(this, 15, 1, 4, 5);
			cliff(this, 17, 12, 2, 5);
			paintHouse(5, 4, 8, 7, 12, 8);
			placeFurniture(6, 5, SurfaceFurniture.Type.CUPBOARD);
			placeFurniture(9, 5, SurfaceFurniture.Type.FIREWOOD);
			placeFurniture(6, 8, SurfaceFurniture.Type.TABLE);
			placeFurniture(12, 12, SurfaceFurniture.Type.FIREWOOD);
			if (!CampaignQuest.reached(113)) Painter.set(this, 10, 5, Terrain.WATER);
			boards(this, 13, 8, 5, 1);
			passage(-28, 17, 8, 18, 9, cell(15, 8), 0);
			passage(-27, 9, 15, 10, 16, cell(9, 13), 114);
			return true;
		}

		@Override
		protected boolean isHouseCell(int x, int y) {
			return insideHouse(x, y, 5, 4, 8, 7);
		}

		@Override
		protected void createMobs() {
			waypoint(11, 5, 113);
			waypoint(11, 13, 114);
			waypoint(7, 7, 116);
			waypoint(8, 9, 117);
		}

		@Override
		public void campaignChanged() {
			super.campaignChanged();
			if (CampaignQuest.reached(113)) openTerrain(cell(10, 5), Terrain.EMPTY_SP);
		}
	}
}

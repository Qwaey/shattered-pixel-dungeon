/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.CampaignNPC;

public abstract class CampaignLevel extends OutdoorLevel {

	public static Level atDepth(int depth) {
		switch (depth) {
			case 0: return new SurfaceLevel();
			case -1: return new WoodlandLevel();
			case -2: return new RoadsideLevels.OldRoad();
			case -3: return new RoadsideLevels.Ferry();
			case -4: return new RoadsideLevels.Floodplain();
			case -5: return new DikeLevel();
			case -6: return new RoadsideLevels.Hamlet();
			case -7: return new RoadsideLevels.SaltYard();
			case -8: return new ArchiveLevels.Quarry();
			case -9: return new ArchiveLevels.Archive();
			case -10: return new ArchiveLevels.Mine();
			case -11: return new ArchiveLevels.Foothills();
			case -12: return new ArchiveLevels.MountainRoad();
			case -13: return new ArchiveLevels.Pass();
			case -14: return new ArchiveLevels.SheepValley();
			case -15: return new CoastLevels.LastSettlement();
			case -16: return new CoastLevels.Orchards();
			case -17: return new CoastLevels.Spillway();
			case -18: return new CoastLevels.BellYard();
			case -19: return new CoastLevels.CoastStation();
			case -20: return new CoastLevels.WreckBay();
			case -21: return new CoastLevels.BeaconChain();
			case -22: return new CoastLevels.Shipyard();
			case -23: return new EdgeLevels.Highland();
			case -24: return new EdgeLevels.Plateau();
			case -25: return new EdgeLevels.Boundary();
			case -26: return new EdgeLevels.Terraces();
			case -27: return new EdgeLevels.Watchhouse();
			case -28: return new EdgeLevels.Windwalk();
			case -29: return new EdgeLevels.Brink();
			case -30: return new EdgeLevels.Storehouse();
			default: return new DeadEndLevel();
		}
	}

	protected int cell(int x, int y) {
		return y * width() + x;
	}

	protected void waypoint(int x, int y, int quest) {
		CampaignNPC waypoint = new CampaignNPC(quest);
		waypoint.pos = cell(x, y);
		mobs.add(waypoint);
	}

	@Override
	protected boolean isHouseCell(int x, int y) {
		return false;
	}

	@Override
	protected void createItems() {
	}
}

/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PuddleskipperSprite;

public class Puddleskipper extends ChargingRoadsideMob {

	public Puddleskipper() {
		super(PuddleskipperSprite.class, 13, 3, 8, 3, 2, 3);
	}

	@Override
	protected boolean canChargeHere() {
		return super.canChargeHere() && Dungeon.level.water[pos];
	}
}

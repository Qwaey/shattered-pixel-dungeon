/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CliffstalkerSprite;

public class Cliffstalker extends ChargingRoadsideMob {

	public Cliffstalker() {
		super(CliffstalkerSprite.class, 20, 4, 10, 4, 4, 3);
	}
}

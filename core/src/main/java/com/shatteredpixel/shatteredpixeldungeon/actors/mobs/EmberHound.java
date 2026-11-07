/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.sprites.EmberHoundSprite;

public class EmberHound extends ChargingRoadsideMob {

	public EmberHound() {
		super(EmberHoundSprite.class, 16, 3, 9, 3, 3, 4);
		viewDistance = 6;
		state = HUNTING;
	}
}

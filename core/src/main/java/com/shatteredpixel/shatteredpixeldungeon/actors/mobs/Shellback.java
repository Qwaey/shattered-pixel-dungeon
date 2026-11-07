/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ShellbackSprite;

public class Shellback extends ChargingRoadsideMob {

	public Shellback() {
		super(ShellbackSprite.class, 16, 3, 8, 3, 3, 3);
	}
}

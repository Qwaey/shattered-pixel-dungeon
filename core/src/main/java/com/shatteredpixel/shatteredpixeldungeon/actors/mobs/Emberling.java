/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EmberlingSprite;

public class Emberling extends RangedRoadsideMob {

	public Emberling() {
		super(EmberlingSprite.class, 17, 3, 9, 3, 3, 5, 2, MagicMissile.FIRE);
	}
}

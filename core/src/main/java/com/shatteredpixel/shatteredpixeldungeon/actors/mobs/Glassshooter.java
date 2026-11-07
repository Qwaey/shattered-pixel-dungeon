/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GlassshooterSprite;

public class Glassshooter extends RangedRoadsideMob {

	public Glassshooter() {
		super(GlassshooterSprite.class, 15, 3, 10, 3, 3, 7, 2, MagicMissile.FROST);
	}
}

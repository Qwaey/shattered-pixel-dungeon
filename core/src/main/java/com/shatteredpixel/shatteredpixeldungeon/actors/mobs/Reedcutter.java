/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ReedcutterSprite;
import com.watabou.utils.Bundle;

public class Reedcutter extends RoadsideMob {

	private int minY;

	public Reedcutter() {
		super(ReedcutterSprite.class, 10, 2, 8, 3, 2);
	}

	public void setMinY(int y) {
		minY = y;
	}

	@Override
	public float speed() {
		// It can catch up in the shallows, but falls behind on dry ground.
		return super.speed() * (Dungeon.level.water[pos] ? 1f : 0.8f);
	}

	@Override
	protected boolean allows(int cell) {
		return super.allows(cell) && cell / Dungeon.level.width() >= minY;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("min_y", minY);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		minY = bundle.getInt("min_y");
	}
}

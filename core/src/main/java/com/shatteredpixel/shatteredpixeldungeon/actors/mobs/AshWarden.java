/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AshWardenSprite;
import com.watabou.utils.Bundle;

import java.util.Arrays;

public class AshWarden extends RangedRoadsideMob {

	private boolean sweepNext = true;

	public AshWarden() {
		super(AshWardenSprite.class, 32, 4, 10, 4, 5, 4, 1, MagicMissile.FIRE);
	}

	@Override
	protected int[] prepareSweep(int target) {
		if (HP > HT / 2) return null;
		sweepNext = !sweepNext;
		if (sweepNext) return null;

		int width = Dungeon.level.width();
		int height = Dungeon.level.height();
		int x = target % width, y = target / width;
		int dx = Math.abs(x - pos % width), dy = Math.abs(y - pos / width);
		int[] band = new int[3];
		int count = 0;
		for (int offset = -1; offset <= 1; offset++) {
			int tx = dx < dy ? x + offset : x;
			int ty = dx < dy ? y : y + offset;
			if (tx > 0 && tx < width - 1 && ty > 0 && ty < height - 1) {
				int cell = tx + ty * width;
				if (hasClearLine(cell)) band[count++] = cell;
			}
		}
		return Arrays.copyOf(band, count);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("sweep_next", sweepNext);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		sweepNext = bundle.getBoolean("sweep_next");
	}
}

/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.sprites.RootwalkerSprite;
import com.watabou.utils.Bundle;

public class Rootwalker extends RoadsideMob {

	private int noiseCell;
	private int listeningTurns;

	public Rootwalker() {
		super(RootwalkerSprite.class, 12, 2, 8, 3, 2);
		HUNTING = new Hunting();
	}

	public void distract(int cell) {
		noiseCell = cell;
		listeningTurns = 4;
		state = HUNTING;
	}

	private class Hunting extends Mob.Hunting {
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (listeningTurns == 0) return super.act(enemyInFOV, justAlerted);

			listeningTurns--;
			enemySeen = false;
			int previous = pos;
			spend(TICK);
			return !getCloser(noiseCell) || moveSprite(previous, pos);
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("noise_cell", noiseCell);
		bundle.put("listening_turns", listeningTurns);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		noiseCell = bundle.getInt("noise_cell");
		listeningTurns = bundle.getInt("listening_turns");
	}
}

/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FireweaverSprite;

public class Fireweaver extends RangedRoadsideMob {

	public Fireweaver() {
		super(FireweaverSprite.class, 15, 3, 9, 3, 3, 5, 1, MagicMissile.FIRE);
	}

	@Override
	protected void afterShot(Ballistica path) {
		HotTrail trail = null;
		for (int cell : path.subPath(1, path.dist)) {
			if (Dungeon.level.passable[cell] && !Dungeon.level.water[cell]
					&& Blob.volumeAt(cell, HotTrail.class) == 0) {
				trail = Blob.seed(cell, 1, HotTrail.class);
			}
		}
		if (trail != null) GameScene.add(trail);
	}

	public static class HotTrail extends Blob {
		@Override
		protected void evolve() {
			for (int x = area.left; x < area.right; x++) {
				for (int y = area.top; y < area.bottom; y++) {
					int cell = x + y * Dungeon.level.width();
					off[cell] = 0;
					if (cur[cell] == 0) continue;

					Char victim = Actor.findChar(cell);
					if (victim != null && victim.alignment == Char.Alignment.ALLY
							&& !victim.isImmune(HotTrail.class)) {
						victim.damage(2, this);
						if (victim == Dungeon.hero && !victim.isAlive()) Dungeon.fail(Fireweaver.class);
					}
				}
			}
		}

		@Override
		public void use(BlobEmitter emitter) {
			super.use(emitter);
			emitter.pour(FlameParticle.FACTORY, 0.05f);
		}

		@Override
		public String tileDesc() {
			return Messages.get(this, "desc");
		}
	}
}

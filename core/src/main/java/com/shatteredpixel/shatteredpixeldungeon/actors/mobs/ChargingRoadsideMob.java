/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;

abstract class ChargingRoadsideMob extends RoadsideMob {

	private final int chargeRange;
	private int aimCell = -1;
	private int aimFrom = -1;
	private boolean recovering;

	ChargingRoadsideMob(Class<? extends CharSprite> sprite, int health, int damage,
					  int accuracy, int defense, int experience, int chargeRange) {
		super(sprite, health, damage, accuracy, defense, experience);
		this.chargeRange = chargeRange;
		HUNTING = new Hunting();
	}

	@Override
	public int aimCell() {
		return aimCell;
	}

	protected boolean canChargeHere() {
		return !rooted;
	}

	@Override
	protected boolean act() {
		if (aimCell >= 0 && state != HUNTING) clearAim();
		return super.act();
	}

	private class Hunting extends Mob.Hunting {
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (recovering) {
				recovering = false;
				spend(TICK);
				return true;
			}
			if (aimCell >= 0) {
				if (pos == aimFrom && canChargeHere()) return charge();
				clearAim();
			}
			if (enemyInFOV && canChargeHere() && !isCharmedBy(enemy)) {
				int distance = Dungeon.level.distance(pos, enemy.pos);
				if (distance >= 2 && distance <= chargeRange) {
					Ballistica path = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
					if (path.collisionPos == enemy.pos && unobstructed(path)) {
						aimCell = enemy.pos;
						aimFrom = pos;
						return markAttackLine(path);
					}
				}
			}
			return super.act(enemyInFOV, justAlerted);
		}
	}

	private boolean unobstructed(Ballistica path) {
		for (int cell : path.subPath(1, path.dist)) {
			if (!allows(cell) || !Dungeon.level.passable[cell] || Dungeon.level.pit[cell]) return false;
		}
		return true;
	}

	private boolean charge() {
		Ballistica path = new Ballistica(pos, aimCell, Ballistica.PROJECTILE);
		int previous = pos;
		clearAim();
		recovering = true;
		spend(TICK);

		for (int step : path.subPath(1, path.dist)) {
			if (!allows(step) || !Dungeon.level.passable[step] || Dungeon.level.pit[step]) break;
			Char victim = Actor.findChar(step);
			if (victim != null) {
				if (victim.alignment != alignment && victim.alignment != Alignment.NEUTRAL && !isCharmedBy(victim)) {
					attack(victim, 1f, 1f, 1f);
					Invisibility.dispel(this);
				}
				break;
			}
			move(step);
			if (pos != step || !isAlive() || paralysed > 0 || rooted) break;
		}
		return previous == pos || !isAlive() || moveSprite(previous, pos);
	}

	private void clearAim() {
		aimCell = aimFrom = -1;
		sprite.idle();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("charge_aim", aimCell);
		bundle.put("charge_origin", aimFrom);
		bundle.put("charge_recovering", recovering);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		aimCell = bundle.getInt("charge_aim");
		aimFrom = bundle.getInt("charge_origin");
		recovering = bundle.getBoolean("charge_recovering");
	}
}

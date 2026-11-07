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
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

abstract class RangedRoadsideMob extends RoadsideMob {

	private final int range;
	private final int rangedBonus;
	private final int projectile;
	private int aimCell = -1;
	private int aimFrom = -1;
	private int[] sweep;

	RangedRoadsideMob(Class<? extends CharSprite> sprite, int health, int damage, int accuracy,
					int defense, int experience, int range, int rangedBonus, int projectile) {
		super(sprite, health, damage, accuracy, defense, experience);
		this.range = range;
		this.rangedBonus = rangedBonus;
		this.projectile = projectile;
		viewDistance = range + 1;
		HUNTING = new Hunting();
		state = HUNTING;
	}

	@Override
	public int aimCell() {
		return aimCell;
	}

	protected int[] prepareSweep(int target) {
		return null;
	}

	protected void afterShot(Ballistica path) {
	}

	private class Hunting extends Mob.Hunting {
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (aimCell >= 0) {
				if (pos == aimFrom) return releaseAttack();
				clearAim();
			}
			if (enemyInFOV && !isCharmedBy(enemy)) {
				int distance = Dungeon.level.distance(pos, enemy.pos);
				if (distance >= 2 && distance <= range) {
					Ballistica path = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
					if (path.collisionPos == enemy.pos) return readyAttack(path);
				}
			}
			return super.act(enemyInFOV, justAlerted);
		}
	}

	@Override
	protected boolean act() {
		if (aimCell >= 0 && state != HUNTING) clearAim();
		return super.act();
	}

	private boolean readyAttack(Ballistica path) {
		aimCell = enemy.pos;
		aimFrom = pos;
		sweep = prepareSweep(aimCell);
		if (sweep == null) return markAttackLine(path);

		ArrayList<Integer> marked = new ArrayList<>();
		for (int cell : sweep) marked.add(cell);
		return warnAttack(marked);
	}

	private boolean releaseAttack() {
		int aimed = aimCell;
		int[] sweepCells = sweep;
		clearAim();
		spend(TICK);
		Invisibility.dispel(this);
		if (sweepCells != null) {
			if (sprite.visible) {
				sprite.attack(aimed, () -> {
					hitSweep(sweepCells);
					sprite.idle();
					next();
				});
				return false;
			}
			hitSweep(sweepCells);
			return true;
		}

		Ballistica path = new Ballistica(pos, aimed, Ballistica.PROJECTILE);
		int impact = path.collisionPos;
		if (impact == pos) return true;

		if (sprite.visible) {
			sprite.attack(impact, () -> MagicMissile.boltFromChar(sprite.parent, projectile, sprite, impact,
					() -> {
						hitCell(impact);
						afterShot(path);
						sprite.idle();
						next();
					}));
			return false;
		}
		hitCell(impact);
		afterShot(path);
		return true;
	}

	protected boolean hasClearLine(int cell) {
		return new Ballistica(pos, cell, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID).collisionPos == cell
				&& !Dungeon.level.solid[cell];
	}

	private void hitSweep(int[] cells) {
		for (int cell : cells) {
			if (hasClearLine(cell)) hitCell(cell);
		}
	}

	private void hitCell(int cell) {
		Char victim = Actor.findChar(cell);
		if (victim != null && victim.alignment != alignment
				&& victim.alignment != Alignment.NEUTRAL && !isCharmedBy(victim)) {
			attack(victim, 1f, rangedBonus, 1f);
		}
	}

	private void clearAim() {
		aimCell = aimFrom = -1;
		sweep = null;
		sprite.idle();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("aim_cell", aimCell);
		bundle.put("aim_from", aimFrom);
		if (sweep != null) bundle.put("sweep", sweep);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		aimCell = bundle.getInt("aim_cell");
		aimFrom = bundle.getInt("aim_from");
		if (bundle.contains("sweep")) sweep = bundle.getIntArray("sweep");
	}
}

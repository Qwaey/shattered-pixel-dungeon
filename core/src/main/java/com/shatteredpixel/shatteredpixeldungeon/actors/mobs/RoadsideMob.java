/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public abstract class RoadsideMob extends Mob {

	private final int damage;
	private final int accuracy;
	private int blockedCell = -1;

	RoadsideMob(Class<? extends CharSprite> sprite, int health, int damage, int accuracy, int defense, int experience) {
		spriteClass = sprite;
		HP = HT = health;
		this.damage = damage;
		this.accuracy = accuracy;
		defenseSkill = defense;
		EXP = experience;
		viewDistance = 4;
		WANDERING = new Waiting();
		state = WANDERING;
	}

	@Override
	public int damageRoll() {
		return damage;
	}

	@Override
	public int attackSkill(Char target) {
		return accuracy;
	}

	@Override
	public void rollToDropLoot() {
		// Authored rewards are tied to the encounter, not to the random loot table.
	}

	@Override
	public boolean reset() {
		return true;
	}

	public int aimCell() {
		return -1;
	}

	public void avoidCell(int cell) {
		blockedCell = cell;
	}

	protected boolean allows(int cell) {
		return cell >= 0 && cell != blockedCell;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return allows(enemy.pos) && super.canAttack(enemy);
	}

	@Override
	protected boolean getCloser(int target) {
		int previous = pos;
		return allows(target) && super.getCloser(target) && previous != pos;
	}

	@Override
	protected boolean getFurther(int target) {
		int previous = pos;
		return super.getFurther(target) && previous != pos;
	}

	@Override
	public void move(int step, boolean travelling) {
		if (allows(step)) super.move(step, travelling);
	}

	protected boolean markAttackLine(Ballistica path) {
		return warnAttack(path.subPath(1, path.dist));
	}

	protected boolean warnAttack(Iterable<Integer> cells) {
		target = enemy.pos;
		enemySeen = true;
		float delay = Math.max(TICK, Dungeon.hero.cooldown());
		if (Dungeon.level.heroFOV[pos] || enemy == Dungeon.hero) {
			GLog.w(Messages.get(this, "windup"));
			sprite.showStatus(CharSprite.WARNING, Messages.get(this, "preparing"));
			for (int cell : cells) {
				GameScene.targetedCell(cell, delay);
			}
			Dungeon.hero.interrupt();
		}
		sprite.idle();
		spendConstant(delay);
		return true;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("blocked_cell", blockedCell);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains("blocked_cell")) blockedCell = bundle.getInt("blocked_cell");
	}

	private class Waiting extends Mob.Wandering {
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			spend(TICK);
			if (enemyInFOV && !isCharmedBy(enemy)) return noticeEnemy();
			enemySeen = false;
			return true;
		}

		@Override
		protected int randomDestination() {
			return -1;
		}
	}
}

/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SenogryzSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Senogryz extends Mob {

	private int chargeCell = -1;
	private boolean recovering;
	private boolean hayQuestBeetle;

	{
		spriteClass = SenogryzSprite.class;
		HP = HT = 18;
		defenseSkill = 4;
		EXP = 4;
		HUNTING = new Hunting();
		state = HUNTING;
	}

	public boolean isPreparing() {
		return chargeCell >= 0;
	}

	public void markHayQuestBeetle() {
		hayQuestBeetle = true;
	}

	@Override
	public int damageRoll() {
		return 4;
	}

	@Override
	public int attackSkill(Char target) {
		return INFINITE_ACCURACY;
	}

	@Override
	public int drRoll() {
		return recovering ? 0 : 1;
	}

	@Override
	public void rollToDropLoot() {
		// This authored encounter has no random drops, including bonus loot.
	}

	@Override
	public void die(Object cause) {
		boolean reportQuestDeath = hayQuestBeetle;
		hayQuestBeetle = false;
		super.die(cause);
		if (reportQuestDeath && Dungeon.level instanceof SurfaceLevel
				&& ((SurfaceLevel) Dungeon.level).hayQuest() != null) {
			((SurfaceLevel) Dungeon.level).hayQuest().beetleDied();
		}
	}

	@Override
	public boolean reset() {
		return true;
	}

	private class Hunting extends Mob.Hunting {
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV && !isCharmedBy(enemy) && Dungeon.level.adjacent(pos, enemy.pos)) {
				// Close combat takes priority over winding up or recovering from a charge.
				chargeCell = -1;
				recovering = false;
				enemySeen = true;
				recentlyAttackedBy.clear();
				target = enemy.pos;
				return doAttack(enemy);
			}
			if (recovering) {
				recovering = false;
				sprite.idle();
				spend(TICK);
				return true;
			}
			if (chargeCell >= 0) return charge();
			if (enemyInFOV && !isCharmedBy(enemy) && Dungeon.level.distance(pos, enemy.pos) <= 3) {
				Ballistica path = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
				if (path.collisionPos == enemy.pos) {
					chargeCell = enemy.pos;
					sprite.turnTo(pos, chargeCell);
					enemySeen = true;
					target = enemy.pos;
					if (Dungeon.level.heroFOV[pos]) {
						GLog.w(Messages.get(Senogryz.this, "windup"));
						sprite.showStatus(CharSprite.WARNING, Messages.get(Senogryz.this, "preparing"));
						GameScene.targetedCell(chargeCell, TICK);
					}
					((SenogryzSprite) sprite).windUp();
					spend(TICK);
					return true;
				}
			}
			// Follow the last seen position, then wait instead of wandering randomly.
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy(enemy)) target = enemy.pos;
			int oldPos = pos;
			spend(TICK);
			if (target >= 0 && getCloser(target)) {
				return moveSprite(oldPos, pos);
			}
			return true;
		}
	}

	private boolean charge() {
		int aimed = chargeCell;
		chargeCell = -1;
		recovering = true;
		int oldPos = pos;
		boolean hit = false;
		if (!rooted && Dungeon.level.distance(pos, aimed) <= 3 && pos != aimed) {
			Ballistica path = new Ballistica(pos, aimed, Ballistica.PROJECTILE);
			for (int i = 1; i <= path.dist; i++) {
				int step = path.path.get(i);
				Char victim = Actor.findChar(step);
				if (victim != null) {
					if (victim.alignment != alignment && !isCharmedBy(victim)) {
						hit = attack(victim);
					}
					break;
				}
				if (!Dungeon.level.passable[step] || Dungeon.level.pit[step]) break;
				move(step);
				if (step == aimed) break;
			}
		}
		if (Dungeon.level.heroFOV[pos]) {
			GLog.i(Messages.get(this, hit ? "hit" : "missed"));
			sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "recovering"));
		}
		sprite.idle();
		spend(TICK);
		if (oldPos == pos && sprite.visible) {
			sprite.attack(aimed, new Callback() {
				@Override
				public void call() {
					next();
				}
			});
			return false;
		}
		return oldPos == pos || moveSprite(oldPos, pos);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("charge_cell", chargeCell);
		bundle.put("recovering", recovering);
		bundle.put("hay_quest_beetle", hayQuestBeetle);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		chargeCell = bundle.getInt("charge_cell");
		recovering = bundle.getBoolean("recovering");
		hayQuestBeetle = bundle.getBoolean("hay_quest_beetle");
	}
}

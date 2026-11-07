/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RoadsideMob;
import com.watabou.noosa.TextureFilm;

abstract class RoadsideSprite extends MobSprite {

	private final Animation preparation;
	private final int bloodColor;

	RoadsideSprite(String texturePath, int bloodColor) {
		texture(texturePath);
		this.bloodColor = bloodColor;
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(1, true);
		idle.frames(frames, 0);
		run = new Animation(7, true);
		run.frames(frames, 0, 1);
		attack = new Animation(10, false);
		attack.frames(frames, 2, 0);
		die = new Animation(1, false);
		die.frames(frames, 3);
		preparation = new Animation(1, true);
		preparation.frames(frames, 2);
		idle();
	}

	@Override
	public int blood() {
		return bloodColor;
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		idle();
	}

	@Override
	public void idle() {
		if (ch instanceof RoadsideMob) {
			int aim = ((RoadsideMob) ch).aimCell();
			if (aim >= 0) {
				turnTo(ch.pos, aim);
				play(preparation);
				return;
			}
		}
		super.idle();
	}
}

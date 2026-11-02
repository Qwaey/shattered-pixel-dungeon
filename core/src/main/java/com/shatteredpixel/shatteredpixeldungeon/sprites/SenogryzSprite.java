/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Senogryz;
import com.watabou.noosa.TextureFilm;

public class SenogryzSprite extends MobSprite {

	private Animation windup;

	public SenogryzSprite() {
		texture(Assets.Sprites.SENOGRYZ);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(1, true);
		idle.frames(frames, 0);
		run = new Animation(6, true);
		run.frames(frames, 0, 1);
		windup = new Animation(1, true);
		windup.frames(frames, 2);
		attack = new Animation(8, false);
		attack.frames(frames, 2, 0);
		die = new Animation(1, false);
		die.frames(frames, 3);
		idle();
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		idle();
	}

	@Override
	public void idle() {
		if (ch instanceof Senogryz && ((Senogryz) ch).isPreparing()) windUp();
		else super.idle();
	}

	public void windUp() {
		play(windup);
	}

	@Override
	public int blood() {
		return 0xFF9B9853;
	}
}

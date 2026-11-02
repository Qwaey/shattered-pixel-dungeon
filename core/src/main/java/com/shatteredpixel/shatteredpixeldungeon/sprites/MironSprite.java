/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class MironSprite extends MobSprite {

	public MironSprite() {
		texture(Assets.Sprites.MIRON);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(1, true);
		idle.frames(frames, 0);
		run = idle.clone();
		attack = idle.clone();
		die = new Animation(1, false);
		die.frames(frames, 0);
		idle();
	}
}

/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.CampaignNPC;
import com.watabou.noosa.TextureFilm;

public class CampaignPersonSprite extends MobSprite {

	private static final String PEOPLE = "sprites/campaign_people.png";

	public CampaignPersonSprite() {
		pose(-1, Assets.Sprites.MIRON);
	}

	@Override
	public void linkVisuals(Char ch) {
		super.linkVisuals(ch);
		if (!(ch instanceof CampaignNPC)) return;

		switch (((CampaignNPC) ch).quest()) {
			case 46: case 91: pose(0, Assets.Sprites.MIRON); break; // Ratmir
			case 9: pose(1, Assets.Sprites.GRANDMOTHER); break; // Darya
			case 17: pose(2, Assets.Sprites.MIRON); break; // Luka
			case 34: pose(3, Assets.Sprites.GRANDMOTHER); break; // Lada
			case 76: pose(4, Assets.Sprites.GRANDMOTHER); break; // Marfa
			case 106: pose(5, Assets.Sprites.GRANDMOTHER); break; // Efrosinya
			case 16: pose(6, Assets.Sprites.GRANDMOTHER); break; // Taisia
			case 61: pose(7, Assets.Sprites.MIRON); break; // Savva
			case 31: pose(8, Assets.Sprites.GRANDMOTHER); break; // Irina
			case 33: pose(9, Assets.Sprites.MIRON); break; // Sila
			default: pose(-1, Assets.Sprites.MIRON);
		}
	}

	private void pose(int frame, String fallback) {
		boolean hasPortrait = frame >= 0 && Gdx.files.internal(PEOPLE).exists();
		texture(hasPortrait ? PEOPLE : fallback);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		int index = hasPortrait ? frame : 0;
		idle = new Animation(1, true);
		idle.frames(frames, index);
		run = idle.clone();
		attack = idle.clone();
		die = new Animation(1, false);
		die.frames(frames, index);
		idle();
	}
}

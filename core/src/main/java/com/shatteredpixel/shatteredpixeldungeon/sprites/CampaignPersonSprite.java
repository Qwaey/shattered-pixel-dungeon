/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

public class CampaignPersonSprite extends MobSprite {

	private static final String PEOPLE = "sprites/campaign_people.png";
	private static final String VILLAGERS = "sprites/campaign_villagers.png";
	private static final int FIRST_VILLAGER = 10;

	public CampaignPersonSprite(int quest) {
		int portrait = portraitFor(quest);
		texture(portrait < FIRST_VILLAGER ? PEOPLE : VILLAGERS);
		int frame = portrait < FIRST_VILLAGER ? portrait : portrait - FIRST_VILLAGER;
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(1, true);
		idle.frames(frames, frame);
		run = idle.clone();
		attack = idle.clone();
		die = new Animation(1, false);
		die.frames(frames, frame);
		idle();
	}

	public static boolean hasPortrait(int quest) {
		return portraitFor(quest) >= 0;
	}

	private static int portraitFor(int quest) {
		switch (quest) {
			case 46: case 91: case 95: case 104: return 0; // Ratmir
			case 9: case 11: return 1; // Darya
			case 17: case 24: return 2; // Luka
			case 34: return 3; // Lada
			case 76: case 87: return 4; // Marfa
			case 106: case 120: return 5; // Efrosinya
			case 16: return 6; // Taisia
			case 61: case 74: return 7; // Savva
			case 31: case 36: return 8; // Irina
			case 33: return 9; // Sila
			case 22: case 30: return FIRST_VILLAGER; // Ferry bell keeper
			case 28: return FIRST_VILLAGER + 1; // Landing keeper
			case 1030: return FIRST_VILLAGER + 2; // Farmstead householder
			case 37: return FIRST_VILLAGER + 3; // Potter
			case 51: case 52: case 116: return FIRST_VILLAGER + 4; // Vera
			case 59: return FIRST_VILLAGER + 5; // Shelter keeper
			case 75: return FIRST_VILLAGER + 6; // Domna
			case 67: return FIRST_VILLAGER + 7; // Old man at the flood mark
			case 68: return FIRST_VILLAGER + 8; // Orchard gardener
			case 69: return FIRST_VILLAGER + 9; // Girl from the orchards
			case 71: return FIRST_VILLAGER + 10; // Householder with the boot
			case 73: return FIRST_VILLAGER + 11; // Bell Yard keeper
			case 83: return FIRST_VILLAGER + 12; // Shipyard foreman
			case 89: return FIRST_VILLAGER + 13; // Signal keeper
			case 101: return FIRST_VILLAGER + 14; // Mason
			case 105: return FIRST_VILLAGER + 15; // Terrace gardener
			default: return -1;
		}
	}
}

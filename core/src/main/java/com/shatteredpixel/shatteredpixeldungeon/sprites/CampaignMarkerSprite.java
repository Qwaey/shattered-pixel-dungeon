/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.CampaignNPC;

public class CampaignMarkerSprite extends MobSprite {

	public CampaignMarkerSprite() {
		texture(Assets.Sprites.ITEMS);
		showMarker(ItemSpriteSheet.CART_PLANK);
	}

	@Override
	public void linkVisuals(Char ch) {
		switch (((CampaignNPC) ch).quest()) {
			case 7: case 18:
				showMarker(ItemSpriteSheet.PICKAXE);
				break;
			case 8: case 20:
				showMarker(ItemSpriteSheet.IRON_KEY);
				break;
			case 19:
				showMarker(ItemSpriteSheet.SEAL);
				break;
			default:
				showMarker(ItemSpriteSheet.CART_PLANK);
		}
	}

	private void showMarker(int image) {
		idle = new Animation(1, true);
		idle.frames(ItemSpriteSheet.film.get(image));
		run = idle.clone();
		attack = idle.clone();
		die = idle.clone();
		idle();
	}

	@Override
	public void turnTo(int from, int to) {
	}
}

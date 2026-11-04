/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Tilemap;

// The original axe-and-block sprite remains separate from the village's bare stumps.
public class SurfaceChoppingBlock extends CustomTilemap {

	{
		texture = Assets.Environment.SURFACE_CHOPPING_BLOCK;
	}

	public SurfaceChoppingBlock() {
	}

	public SurfaceChoppingBlock(int x, int y) {
		pos(x, y);
	}

	@Override
	public Tilemap create() {
		Tilemap v = super.create();
		v.map(new int[]{0}, tileW);
		return v;
	}

	@Override
	public String name(int tileX, int tileY) {
		return Messages.get(this, "name");
	}

	@Override
	public String desc(int tileX, int tileY) {
		return Messages.get(this, "desc");
	}
}

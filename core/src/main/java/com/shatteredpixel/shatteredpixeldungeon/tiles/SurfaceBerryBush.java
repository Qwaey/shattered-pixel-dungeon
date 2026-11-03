/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

public class SurfaceBerryBush extends CustomTilemap {

	private boolean harvested;
	private boolean canopy;

	{
		texture = Assets.Environment.SURFACE_BERRY_BUSH;
		tileW = 2;
	}

	public SurfaceBerryBush() {
	}

	public SurfaceBerryBush(int x, int y, boolean canopy) {
		this.canopy = canopy;
		pos(x, canopy ? y - 1 : y);
	}

	public boolean isHarvested() {
		return harvested;
	}

	public void harvest() {
		harvested = true;
	}

	@Override
	public Tilemap create() {
		Tilemap v = super.create();
		v.map(mapSimpleImage(harvested ? 2 : 0, canopy ? 0 : 1, 64), tileW);
		return v;
	}

	@Override
	protected void updateAll(int[] data) {
		data[0] = (harvested ? 2 : 0) + (canopy ? 0 : 4);
		data[1] = data[0] + 1;
	}

	@Override
	public Image image(int tileX, int tileY) {
		if (canopy) return null;
		Image img = new Image(texture);
		img.frame(harvested ? 32 : 0, 0, 32, 32);
		return img;
	}

	@Override
	public String name(int tileX, int tileY) {
		return Messages.get(this, "name");
	}

	@Override
	public String desc(int tileX, int tileY) {
		return Messages.get(this, harvested ? "harvested_desc" : "desc");
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("harvested", harvested);
		bundle.put("canopy", canopy);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		harvested = bundle.getBoolean("harvested");
		canopy = bundle.getBoolean("canopy");
	}
}

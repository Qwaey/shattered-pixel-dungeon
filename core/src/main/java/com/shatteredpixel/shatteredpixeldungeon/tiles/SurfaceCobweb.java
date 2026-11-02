/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

// A floor overlay only; unlike the Web blob, it has no gameplay effects.
public class SurfaceCobweb extends CustomTilemap {

	public enum Corner {
		NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
	}

	private Corner corner = Corner.NORTH_WEST;

	{
		texture = Assets.Environment.SURFACE_COBWEBS;
	}

	// Used by the standard level save/restore mechanism.
	public SurfaceCobweb() {
	}

	public SurfaceCobweb(int x, int y, Corner corner) {
		pos(x, y);
		this.corner = corner;
	}

	@Override
	public Tilemap create() {
		Tilemap v = super.create();
		v.map(new int[]{corner.ordinal()}, tileW);
		return v;
	}

	@Override
	public Image image(int tileX, int tileY) {
		Image img = new Image(texture);
		img.frame(corner.ordinal() * SIZE, 0, SIZE, SIZE);
		return img;
	}

	@Override
	public String name(int tileX, int tileY) {
		return Messages.get(this, "name");
	}

	@Override
	public String desc(int tileX, int tileY) {
		return Messages.get(this, "desc");
	}

	private static final String CORNER = "corner";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		corner = bundle.getEnum(CORNER, Corner.class);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CORNER, corner);
	}
}

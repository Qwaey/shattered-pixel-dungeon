/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
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

public class SurfaceExterior extends CustomTilemap {

	// Sprites share a baseline on the bottom row of the 256x48 atlas.
	public enum Type {
		OAK(0, 2, 3), BIRCH(2, 2, 3), PINE(4, 2, 3), APPLE_TREE(6, 2, 3),
		WELL(8, 2, 2), HANDCART(10, 2, 1), HAYSTACK(12, 2, 2), CHOPPING_BLOCK(14, 1, 1);

		private final int column;
		public final int width, height;

		Type(int column, int width, int height) {
			this.column = column;
			this.width = width;
			this.height = height;
		}
	}

	private static final int ATLAS_WIDTH = 256;
	private static final int ATLAS_ROWS = 3;
	private Type type = Type.OAK;
	private boolean canopy;

	{
		texture = Assets.Environment.SURFACE_EXTERIOR;
	}

	// Used by the standard level save/restore mechanism.
	public SurfaceExterior() {
	}

	public SurfaceExterior(Type type, int x, int y, boolean canopy) {
		this.type = type;
		this.canopy = canopy;
		tileW = type.width;
		tileH = canopy ? type.height - 1 : 1;
		pos(x, canopy ? y - tileH : y);
	}

	public boolean blocksSight() {
		return type != Type.WELL && type != Type.HANDCART && type != Type.CHOPPING_BLOCK;
	}

	@Override
	public Tilemap create() {
		Tilemap v = super.create();
		int row = canopy ? ATLAS_ROWS - type.height : ATLAS_ROWS - 1;
		v.map(mapSimpleImage(type.column, row, ATLAS_WIDTH), tileW);
		return v;
	}

	@Override
	public Image image(int tileX, int tileY) {
		if (canopy) {
			return null;
		}
		// Examine the entire object from either cell of its base.
		Image img = new Image(texture);
		img.frame(type.column * SIZE, (ATLAS_ROWS - type.height) * SIZE,
				type.width * SIZE, type.height * SIZE);
		return img;
	}

	@Override
	public String name(int tileX, int tileY) {
		return Messages.get(this, type.name() + "_name");
	}

	@Override
	public String desc(int tileX, int tileY) {
		return Messages.get(this, type.name() + "_desc");
	}

	private static final String TYPE = "type";
	private static final String CANOPY = "canopy";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		type = bundle.getEnum(TYPE, Type.class);
		canopy = bundle.getBoolean(CANOPY);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TYPE, type);
		bundle.put(CANOPY, canopy);
	}
}

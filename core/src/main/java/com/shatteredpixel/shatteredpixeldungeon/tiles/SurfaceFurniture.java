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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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

public class SurfaceFurniture extends CustomTilemap {

	// Order matches the columns of surface_furniture.png; each column is 16x32.
	public enum Type {
		BED, TABLE, STOOL, CUPBOARD, STOVE, FIREWOOD, WASHBASIN
	}

	private static final int ATLAS_COLUMNS = 8;
	private Type type = Type.BED;
	private boolean abandoned;

	{
		texture = Assets.Environment.SURFACE_FURNITURE;
	}

	// Used by the standard level save/restore mechanism.
	public SurfaceFurniture() {
	}

	public SurfaceFurniture(Type type, int x, int y) {
		this(type, x, y, false);
	}

	public SurfaceFurniture(Type type, int x, int y, boolean abandoned) {
		this.type = type;
		this.abandoned = abandoned;
		pos(x, y);
		tileH = type == Type.BED ? 2 : 1;
	}

	@Override
	public Tilemap create() {
		Tilemap v = super.create();
		int bottom = ATLAS_COLUMNS + type.ordinal();
		v.map(type == Type.BED ? new int[]{type.ordinal(), bottom} : new int[]{bottom}, tileW);
		return v;
	}

	@Override
	public Image image(int tileX, int tileY) {
		// Show the entire bed when either of its two cells is examined.
		Image img = new Image(texture);
		img.frame(type.ordinal() * SIZE, type == Type.BED ? 0 : SIZE, SIZE, tileH * SIZE);
		return img;
	}

	@Override
	public String name(int tileX, int tileY) {
		return Messages.get(this, type.name() + "_name");
	}

	@Override
	public String desc(int tileX, int tileY) {
		return Messages.get(this, (abandoned ? "abandoned_" : "") + type.name() + "_desc");
	}

	private static final String TYPE = "type";
	private static final String ABANDONED = "abandoned";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		type = bundle.getEnum(TYPE, Type.class);
		abandoned = bundle.getBoolean(ABANDONED);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TYPE, type);
		bundle.put(ABANDONED, abandoned);
	}
}

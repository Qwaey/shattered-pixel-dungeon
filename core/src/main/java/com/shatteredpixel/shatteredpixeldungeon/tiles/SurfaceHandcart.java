/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

public class SurfaceHandcart extends CustomTilemap {

	private boolean supported;
	private boolean moved;

	{
		texture = Assets.Environment.SURFACE_HANDCART;
		tileW = 2;
	}

	public SurfaceHandcart() {
	}

	public SurfaceHandcart(int x, int y) {
		pos(x, y);
	}

	public boolean isSupported() {
		return supported;
	}

	@Override
	public void onExamine(int tileX, int tileY) {
		if (!moved) Notes.add(Notes.Landmark.HANDCART);
	}

	public boolean isMoved() {
		return moved;
	}

	public void support() {
		supported = true;
	}

	public void moveTo(int x, int y) {
		moved = true;
		Notes.remove(Notes.Landmark.HANDCART);
		pos(x, y);
		if (vis != null) {
			vis.x = x * SIZE;
			vis.y = y * SIZE;
		}
	}

	private int frame() {
		return moved ? 2 : supported ? 1 : 0;
	}

	@Override
	public Tilemap create() {
		Tilemap v = super.create();
		v.map(mapSimpleImage(frame() * tileW, 0, 96), tileW);
		return v;
	}

	@Override
	protected void updateAll(int[] data) {
		data[0] = frame() * tileW;
		data[1] = data[0] + 1;
	}

	@Override
	public Image image(int tileX, int tileY) {
		Image img = new Image(texture);
		img.frame(frame() * tileW * SIZE, 0, tileW * SIZE, SIZE);
		return img;
	}

	@Override
	public String name(int tileX, int tileY) {
		return Messages.get(this, "name");
	}

	@Override
	public String desc(int tileX, int tileY) {
		return Messages.get(this, moved ? "parked" : supported ? "ready" : "stuck");
	}

	private static final String SUPPORTED = "supported";
	private static final String MOVED = "moved";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SUPPORTED, supported);
		bundle.put(MOVED, moved);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		supported = bundle.getBoolean(SUPPORTED);
		moved = bundle.getBoolean(MOVED);
	}
}

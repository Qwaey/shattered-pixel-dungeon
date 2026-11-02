/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HayQuest;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

public class HayQuestTile extends CustomTilemap {

	public enum Kind { ROPE, SHELL, NEST, REFUGE, TRACKS }
	private Kind kind = Kind.ROPE;

	{
		texture = Assets.Environment.HAY_QUEST;
	}

	public HayQuestTile() {
	}

	public HayQuestTile(int x, int y, Kind kind) {
		pos(x, y);
		this.kind = kind;
	}

	private int frame() {
		HayQuest quest = Dungeon.level instanceof SurfaceLevel
				? ((SurfaceLevel) Dungeon.level).hayQuest() : null;
		return quest == null ? -1 : quest.tileFrame(kind);
	}

	@Override
	public Tilemap create() {
		Tilemap v = super.create();
		v.map(new int[]{frame()}, 1);
		return v;
	}

	@Override
	protected void updateAll(int[] data) {
		data[0] = frame();
	}

	@Override
	public Image image(int tileX, int tileY) {
		int frame = frame();
		if (frame < 0) return null;
		Image img = new Image(texture);
		img.frame(frame * SIZE, 0, SIZE, SIZE);
		return img;
	}

	@Override
	public String name(int tileX, int tileY) {
		return Messages.get(this, kind.name() + "_name");
	}

	@Override
	public String desc(int tileX, int tileY) {
		HayQuest quest = Dungeon.level instanceof SurfaceLevel
				? ((SurfaceLevel) Dungeon.level).hayQuest() : null;
		if (kind == Kind.REFUGE && quest != null && quest.isRescued()) {
			return Messages.get(this, quest.hasHatched() ? "refuge_hatched" : "refuge_full");
		}
		return Messages.get(this, kind.name() + "_desc");
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("kind", kind);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		kind = bundle.getEnum("kind", Kind.class);
	}
}

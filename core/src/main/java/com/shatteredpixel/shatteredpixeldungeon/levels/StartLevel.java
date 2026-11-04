/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.YendorAmulet;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;

public class StartLevel extends Level {

	{
		color1 = 0x48763c;
		color2 = 0x59994a;
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_SEWERS;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.WATER_SEWERS;
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.SEWERS_1, true);
	}

	@Override
	protected boolean build() {
		setSize(11, 9);
		Painter.fill(this, 2, 2, 7, 5, Terrain.EMPTY);

		// Moss interrupts the stone floor; the wall drain empties into the pool.
		Painter.set(this, 2, 2, Terrain.EMPTY_DECO);
		Painter.set(this, 4, 2, Terrain.EMPTY_DECO);
		Painter.set(this, 6, 3, Terrain.EMPTY_DECO);
		Painter.set(this, 3, 6, Terrain.EMPTY_DECO);
		Painter.set(this, 7, 6, Terrain.EMPTY_DECO);

		Painter.fill(this, 7, 2, 2, 1, Terrain.WATER);
		Painter.set(this, 8, 3, Terrain.WATER);
		Painter.set(this, 7, 1, Terrain.WALL_DECO);

		// Scattered barrels use the original stone-floor variant.
		Painter.set(this, 5, 2, Terrain.REGION_DECO);
		Painter.set(this, 2, 5, Terrain.REGION_DECO);
		Painter.set(this, 8, 6, Terrain.REGION_DECO);

		int stairs = 3 * width() + 3;
		map[stairs] = Terrain.ENTRANCE;
		transitions.add(new LevelTransition(this, stairs, LevelTransition.Type.SURFACE));
		return true;
	}

	@Override
	protected void createItems() {
		drop(new YendorAmulet(), 5 * width() + 7).type = Heap.Type.CHEST;
	}

	@Override
	protected void createMobs() {
	}

	@Override
	public Mob createMob() {
		return null;
	}

	@Override
	public Actor addRespawner() {
		return null;
	}

	@Override
	public boolean spawnMob(int disLimit) {
		return false;
	}

	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (transition.type == LevelTransition.Type.SURFACE
				&& hero.belongings.getItem(YendorAmulet.class) == null) {
			GLog.w(Messages.get(this, "amulet_missing"));
			return false;
		}
		return super.activateTransition(hero, transition);
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		SewerLevel.addSewerVisuals(this, visuals);
		return visuals;
	}

	@Override
	public void buildFlagMaps() {
		super.buildFlagMaps();
		for (int i = 0; i < length(); i++) {
			if (map[i] == Terrain.REGION_DECO) flamable[i] = true;
		}
	}

	@Override
	public void updateCellFlags(int cell) {
		super.updateCellFlags(cell);
		if (map[cell] == Terrain.REGION_DECO) flamable[cell] = true;
	}

	@Override
	public void destroy(int pos) {
		if (map[pos] == Terrain.REGION_DECO) {
			set(pos, Terrain.WATER);
			Splash.at(pos, 0xFF507B5D, 10);
		}
		super.destroy(pos);
	}

	@Override
	public String tileName(int tile) {
		if (tile == Terrain.WATER) return Messages.get(SewerLevel.class, "water_name");
		if (tile == Terrain.REGION_DECO) return Messages.get(SewerLevel.class, "region_deco_name");
		return super.tileName(tile);
	}

	@Override
	public String tileDesc(int tile) {
		if (tile == Terrain.EMPTY_DECO) return Messages.get(SewerLevel.class, "empty_deco_desc");
		if (tile == Terrain.REGION_DECO) return Messages.get(SewerLevel.class, "region_deco_desc");
		return super.tileDesc(tile);
	}
}

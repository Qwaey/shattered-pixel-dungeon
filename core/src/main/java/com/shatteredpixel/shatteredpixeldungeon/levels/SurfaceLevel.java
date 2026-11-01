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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Villager;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.noosa.audio.Music;

public class SurfaceLevel extends Level {

	private static final int SIZE = 100;

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_SURFACE;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.WATER_SEWERS;
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.THEME_1, true);
	}

	@Override
	protected boolean build() {
		setSize(SIZE, SIZE);
		Painter.fill(this, 1, 1, SIZE - 2, SIZE - 2, Terrain.EMPTY);

		int centerX = width() / 2;
		int centerY = height() / 2;

		paintHouse(centerX - 10, centerY - 8, 8, 7, centerX - 3, centerY - 4);
		paintHouse(centerX + 4, centerY - 8, 8, 7, centerX + 4, centerY - 4);
		paintHouse(centerX - 4, centerY + 4, 9, 7, centerX, centerY + 4);

		int stairs = centerY * width() + centerX;
		map[stairs] = Terrain.EXIT;
		transitions.add(new LevelTransition(this, stairs,
				LevelTransition.Type.REGULAR_EXIT, 1, 0, LevelTransition.Type.SURFACE));
		return true;
	}

	private void paintHouse(int x, int y, int w, int h, int doorX, int doorY) {
		Painter.fill(this, x, y, w, h, Terrain.WALL);
		Painter.fill(this, x + 1, y + 1, w - 2, h - 2, Terrain.EMPTY_SP);
		Painter.set(this, doorX, doorY, Terrain.DOOR);
	}

	@Override
	protected void createMobs() {
		for (Mob mob : mobs) {
			if (mob instanceof Villager) {
				return;
			}
		}

		// Inside the north-western house, clear of the doorway.
		Villager villager = new Villager();
		villager.pos = (height() / 2 - 5) * width() + width() / 2 - 6;
		mobs.add(villager);
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
	protected void createItems() {
	}

}

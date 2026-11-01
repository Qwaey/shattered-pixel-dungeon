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
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture.Type;
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
		furnishLumberjackHouse(centerX - 10, centerY - 8);
		furnishNorthEastHouse(centerX + 4, centerY - 8);
		furnishSouthHouse(centerX - 4, centerY + 4);
		plantTrees(centerX, centerY);
		decorateYards(centerX, centerY);

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

	private void furnishLumberjackHouse(int x, int y) {
		placeFurniture(x + 1, y + 1, Type.BED);
		placeFurniture(x + 3, y + 1, Type.CUPBOARD);
		placeFurniture(x + 6, y + 1, Type.STOVE);
		placeFurniture(x + 6, y + 2, Type.FIREWOOD);
		placeFurniture(x + 2, y + 4, Type.TABLE);
		placeFurniture(x + 3, y + 4, Type.STOOL);
		placeFurniture(x + 5, y + 1, Type.WASHBASIN);
	}

	private void furnishNorthEastHouse(int x, int y) {
		placeFurniture(x + 6, y + 1, Type.BED);
		placeFurniture(x + 4, y + 1, Type.CUPBOARD);
		placeFurniture(x + 1, y + 1, Type.STOVE);
		placeFurniture(x + 2, y + 1, Type.WASHBASIN);
		placeFurniture(x + 1, y + 2, Type.FIREWOOD);
		placeFurniture(x + 4, y + 4, Type.TABLE);
		placeFurniture(x + 5, y + 4, Type.STOOL);
	}

	private void furnishSouthHouse(int x, int y) {
		placeFurniture(x + 1, y + 3, Type.BED);
		placeFurniture(x + 1, y + 1, Type.CUPBOARD);
		placeFurniture(x + 7, y + 1, Type.STOVE);
		placeFurniture(x + 7, y + 2, Type.FIREWOOD);
		placeFurniture(x + 7, y + 4, Type.WASHBASIN);
		placeFurniture(x + 3, y + 3, Type.TABLE);
		placeFurniture(x + 2, y + 3, Type.STOOL);
	}

	private void placeFurniture(int x, int y, Type type) {
		SurfaceFurniture furniture = new SurfaceFurniture(type, x, y);
		Painter.fill(this, x, y, furniture.tileW, furniture.tileH, Terrain.CUSTOM_DECO);
		customTiles.add(furniture);
	}

	private void plantTrees(int x, int y) {
		placeExterior(x - 15, y - 6, SurfaceExterior.Type.OAK);
		placeExterior(x - 12, y - 12, SurfaceExterior.Type.BIRCH);
		placeExterior(x - 7, y - 12, SurfaceExterior.Type.PINE);
		placeExterior(x - 2, y - 10, SurfaceExterior.Type.APPLE_TREE);
		placeExterior(x + 6, y - 12, SurfaceExterior.Type.OAK);
		placeExterior(x + 14, y - 8, SurfaceExterior.Type.BIRCH);
		placeExterior(x + 15, y - 2, SurfaceExterior.Type.PINE);
		placeExterior(x + 9, y + 2, SurfaceExterior.Type.APPLE_TREE);
		placeExterior(x + 10, y + 9, SurfaceExterior.Type.OAK);
		placeExterior(x + 5, y + 15, SurfaceExterior.Type.PINE);
		placeExterior(x - 2, y + 15, SurfaceExterior.Type.BIRCH);
		placeExterior(x - 9, y + 11, SurfaceExterior.Type.APPLE_TREE);
		placeExterior(x - 11, y + 3, SurfaceExterior.Type.PINE);
		placeExterior(x - 16, y + 1, SurfaceExterior.Type.OAK);
	}

	private void decorateYards(int x, int y) {
		placeExterior(x, y - 10, SurfaceExterior.Type.WELL);
		placeExterior(x - 13, y - 3, SurfaceExterior.Type.HANDCART);
		placeExterior(x - 12, y - 5, SurfaceExterior.Type.CHOPPING_BLOCK);
		placeExterior(x - 8, y, SurfaceExterior.Type.CHOPPING_BLOCK);
		placeExterior(x + 13, y + 3, SurfaceExterior.Type.HAYSTACK);
		placeExterior(x - 7, y + 2, SurfaceExterior.Type.HAYSTACK);
		placeExterior(x + 7, y + 7, SurfaceExterior.Type.HANDCART);
	}

	private void placeExterior(int x, int y, SurfaceExterior.Type type) {
		// Only the base occupies the ground; the crown or roof extends north.
		SurfaceExterior base = new SurfaceExterior(type, x, y, false);
		Painter.fill(this, x, y, base.tileW, base.tileH, Terrain.SURFACE_DECO);
		customTiles.add(base);
		if (type.height > 1) {
			customWalls.add(new SurfaceExterior(type, x, y, true));
		}
	}

	@Override
	public void buildFlagMaps() {
		super.buildFlagMaps();
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.SURFACE_DECO) {
				losBlocking[cell] = exteriorBlocksSight(cell);
			}
		}
	}

	@Override
	public void updateCellFlags(int cell) {
		super.updateCellFlags(cell);
		if (map[cell] == Terrain.SURFACE_DECO) {
			losBlocking[cell] = exteriorBlocksSight(cell);
		}
	}

	private boolean exteriorBlocksSight(int cell) {
		int x = cell % width();
		int y = cell / width();
		for (CustomTilemap tile : customTiles) {
			if (tile instanceof SurfaceExterior
					&& x >= tile.tileX && x < tile.tileX + tile.tileW
					&& y >= tile.tileY && y < tile.tileY + tile.tileH) {
				return ((SurfaceExterior) tile).blocksSight();
			}
		}
		return true;
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

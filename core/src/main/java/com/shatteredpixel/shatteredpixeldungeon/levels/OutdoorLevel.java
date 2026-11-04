/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceBerryBush;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFloorTiles;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceForest;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceHandcart;
import com.watabou.noosa.audio.Music;

// Shared rendering and obstacles for authored locations on the surface.
public abstract class OutdoorLevel extends Level {

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_SURFACE_CLIFFS;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.WATER_SEWERS;
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.THEME_1, true);
	}

	public int floorVisual(int cell) {
		if (map[cell] == Terrain.CUSTOM_DECO) return DungeonTileSheet.FLOOR;
		return SurfaceFloorTiles.getVisual(cell, width());
	}

	protected abstract boolean isHouseCell(int x, int y);

	protected abstract int sectionArrivalCell();

	@Override
	public int transitionArrivalCell(LevelTransition transition) {
		return transition.type == LevelTransition.Type.SECTION
				? sectionArrivalCell() : super.transitionArrivalCell(transition);
	}

	public boolean isEarthWall(int cell) {
		return cell >= 0 && cell < length()
				&& (map[cell] == Terrain.WALL || map[cell] == Terrain.WALL_DECO)
				&& !isHouseCell(cell % width(), cell / width());
	}

	// The physical outer row stays sealed; the section trigger lies just inside it.
	// Its artwork is hidden so the path visibly continues into darkness.
	public boolean isPassageMouth(int cell) {
		if (cell < 0 || cell >= length()) return false;
		int x = cell % width(), y = cell / width();
		for (LevelTransition transition : transitions) {
			if (transition.type != LevelTransition.Type.SECTION) continue;
			if (x >= transition.left && x <= transition.right
					&& ((y == 0 && transition.top <= 2)
					|| (y == height() - 1 && transition.bottom >= height() - 3))) return true;
			if (y >= transition.top && y <= transition.bottom
					&& ((x == 0 && transition.left <= 2)
					|| (x == width() - 1 && transition.right >= width() - 3))) return true;
		}
		return false;
	}

	protected boolean insideHouse(int x, int y, int houseX, int houseY, int w, int h) {
		return x >= houseX && x < houseX + w && y >= houseY && y < houseY + h;
	}

	protected void paintHouse(int x, int y, int w, int h, int doorX, int doorY) {
		Painter.fill(this, x, y, w, h, Terrain.WALL);
		Painter.fill(this, x + 1, y + 1, w - 2, h - 2, Terrain.EMPTY_SP);
		Painter.set(this, doorX, doorY, Terrain.DOOR);
	}

	protected void placeFurniture(int x, int y, SurfaceFurniture.Type type) {
		placeFurniture(x, y, type, false);
	}

	protected void placeFurniture(int x, int y, SurfaceFurniture.Type type, boolean abandoned) {
		SurfaceFurniture furniture = new SurfaceFurniture(type, x, y, abandoned);
		Painter.fill(this, x, y, furniture.tileW, furniture.tileH, Terrain.CUSTOM_DECO);
		customTiles.add(furniture);
	}

	protected void placeExterior(int x, int y, SurfaceExterior.Type type) {
		// Only the base occupies the ground; the crown extends north.
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
			if (x >= tile.tileX && x < tile.tileX + tile.tileW
					&& y >= tile.tileY && y < tile.tileY + tile.tileH) {
				if (tile instanceof SurfaceHandcart || tile instanceof SurfaceBerryBush
						|| tile instanceof SurfaceFurniture) return false;
				if (tile instanceof SurfaceExterior) return ((SurfaceExterior) tile).blocksSight();
				if (tile instanceof SurfaceForest && ((SurfaceForest) tile).containsCell(x, y)) {
					return ((SurfaceForest) tile).blocksSight(x, y);
				}
			}
		}
		return true;
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
}

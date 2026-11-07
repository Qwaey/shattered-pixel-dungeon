/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.PassageFade;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.SectionPassage;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceBerryBush;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFloorTiles;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceForest;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceHandcart;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceLandscapeTiles;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

// Shared rendering and obstacles for authored locations on the surface.
public abstract class OutdoorLevel extends Level {

	@Override
	public String tilesTex() {
		return landscapeStyle() == null ? Assets.Environment.TILES_SURFACE_CLIFFS
				: Assets.Environment.TILES_CAMPAIGN_SURFACE;
	}

	@Override
	public String waterTex() {
		SurfaceLandscapeTiles.Style style = landscapeStyle();
		if (style == null) return Assets.Environment.WATER_SEWERS;
		return style.saltWater ? Assets.Environment.WATER_CAMPAIGN_COAST : Assets.Environment.WATER_CAMPAIGN_RIVER;
	}

	public SurfaceLandscapeTiles.Style landscapeStyle() {
		return null;
	}

	protected boolean keepsEarthCliffs() {
		return true;
	}

	protected boolean isDeckCell(int x, int y) {
		return false;
	}

	public boolean isInterior(int cell) {
		return isHouseCell(cell % width(), cell / width());
	}

	public boolean isDeck(int cell) {
		int terrain = map[cell];
		return (terrain == Terrain.EMPTY_SP || terrain == Terrain.EMPTY
				|| terrain == Terrain.CUSTOM_DECO || terrain == Terrain.SURFACE_DECO)
				&& !isInterior(cell) && isDeckCell(cell % width(), cell / width());
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.THEME_1, true);
	}

	public int floorVisual(int cell) {
		// A decoration can replace the terrain's flags without replacing its floor.
		if (isInterior(cell)) {
			return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.FLOOR_SP, cell);
		}
		return outdoorFloorVisual(cell);
	}

	protected int outdoorFloorVisual(int cell) {
		return SurfaceFloorTiles.getVisual(cell, width());
	}

	@Override
	public Group addWallVisuals() {
		super.addWallVisuals();
		// Above trunks, crowns and wall overhangs, below the fog of war.
		for (LevelTransition transition : transitions) {
			if (transition.type == LevelTransition.Type.SECTION) {
				wallVisuals.add(new PassageFade(this, transition));
			}
		}
		return wallVisuals;
	}

	protected abstract boolean isHouseCell(int x, int y);

	protected int sectionArrivalCell() {
		return entrance();
	}

	@Override
	public int transitionArrivalCell(LevelTransition transition) {
		if (transition instanceof SectionPassage) return ((SectionPassage) transition).arrival();
		return transition.type == LevelTransition.Type.SECTION
				? sectionArrivalCell() : super.transitionArrivalCell(transition);
	}

	protected void passage(int destDepth, int left, int top, int right, int bottom,
						   int arrival, int requiredQuest) {
		SectionPassage passage = new SectionPassage(this, destDepth, left, top, right, bottom,
				arrival, requiredQuest);
		transitions.add(passage);
		Painter.fill(this, left, top, passage.width(), passage.height(),
				passage.isOpen() ? Terrain.EMPTY : Terrain.WALL);
	}

	protected void openTerrain(int cell, int terrain) {
		if (map[cell] == terrain) return;
		Level.set(cell, terrain, this);
		if (this == Dungeon.level) GameScene.updateMap(cell);
	}

	public void campaignChanged() {
		openUnlockedPassages();
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		openUnlockedPassages();
	}

	private void openUnlockedPassages() {
		for (LevelTransition transition : transitions) {
			if (!(transition instanceof SectionPassage) || !((SectionPassage) transition).isOpen()) continue;
			for (int y = transition.top; y <= transition.bottom; y++) {
				for (int x = transition.left; x <= transition.right; x++) {
					int cell = y * width() + x;
					if (map[cell] == Terrain.WALL) openTerrain(cell, Terrain.EMPTY);
				}
			}
		}
	}

	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		return (!(transition instanceof SectionPassage) || ((SectionPassage) transition).isOpen())
				&& super.activateTransition(hero, transition);
	}

	@Override
	public int randomRespawnCell(Char ch) {
		if (!(ch instanceof Hero) || this != Dungeon.level) return super.randomRespawnCell(ch);

		// A resurrection on the surface must stay on the same side of unopened story gates.
		PathFinder.buildDistanceMap(ch.pos, passable);
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < length(); cell++) {
			if (passable[cell] && PathFinder.distance[cell] != Integer.MAX_VALUE && !heroFOV[cell]
					&& getTransition(cell) == null && Actor.findChar(cell) == null) candidates.add(cell);
		}
		return candidates.isEmpty() ? ch.pos : Random.element(candidates);
	}

	public boolean isEarthWall(int cell) {
		return keepsEarthCliffs() && isOutdoorWall(cell);
	}

	public boolean isNaturalBarrier(int cell) {
		return !keepsEarthCliffs() && isOutdoorWall(cell);
	}

	private boolean isOutdoorWall(int cell) {
		return cell >= 0 && cell < length()
				&& (map[cell] == Terrain.WALL || map[cell] == Terrain.WALL_DECO)
				&& !isInterior(cell);
	}

	// The physical outer row stays sealed; the section trigger lies just inside it.
	// Its artwork is hidden so the path visibly continues into darkness.
	public boolean isPassageMouth(int cell) {
		if (cell < 0 || cell >= length()) return false;
		int x = cell % width(), y = cell / width();
		for (LevelTransition transition : transitions) {
			if (transition.type != LevelTransition.Type.SECTION) continue;
			if (transition instanceof SectionPassage && !((SectionPassage) transition).isOpen()) continue;
			int margin = PassageFade.WALL_MARGIN;
			if (x >= transition.left - margin && x <= transition.right + margin
					&& ((y == 0 && transition.top <= 2)
					|| (y == height() - 1 && transition.bottom >= height() - 3))) return true;
			if (y >= transition.top - margin && y <= transition.bottom + margin
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

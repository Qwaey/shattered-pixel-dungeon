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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Grandmother;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Villager;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CartPlank;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceCobweb;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceForest;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceHandcart;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture.Type;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Music;

public class SurfaceLevel extends Level {

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
		setSize(SurfaceForest.WIDTH, SurfaceForest.HEIGHT);
		Painter.fill(this, 1, 1, width() - 2, height() - 2, Terrain.EMPTY);
		SurfaceForest.plant(this);

		int centerX = width() / 2;
		int centerY = height() / 2;

		// The dungeon opens into an abandoned barn west of the village.
		int barnX = centerX - 26;
		int barnY = centerY - 6;
		paintHouse(barnX, barnY, 10, 9, barnX + 9, barnY + 4);
		furnishBarn(barnX, barnY);

		paintHouse(centerX - 10, centerY - 8, 8, 7, centerX - 3, centerY - 4);
		// A two-cell lane between the northern houses leads to the well.
		paintHouse(centerX, centerY - 8, 8, 7, centerX, centerY - 4);
		paintHouse(centerX - 4, centerY + 4, 9, 7, centerX, centerY + 4);
		furnishLumberjackHouse(centerX - 10, centerY - 8);
		furnishNorthEastHouse(centerX, centerY - 8);
		furnishSouthHouse(centerX - 4, centerY + 4);
		plantTrees(centerX, centerY);
		decorateYards(centerX, centerY);

		int stairs = (barnY + 4) * width() + barnX + 3;
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

	private void furnishBarn(int x, int y) {
		// Original storage barrels with wooden flooring and their standard overhangs.
		Painter.set(this, x + 1, y + 2, Terrain.REGION_DECO_ALT);
		Painter.set(this, x + 2, y + 2, Terrain.REGION_DECO_ALT);
		Painter.set(this, x + 7, y + 2, Terrain.REGION_DECO_ALT);
		Painter.set(this, x + 8, y + 2, Terrain.REGION_DECO_ALT);
		placeFurniture(x + 4, y + 1, Type.CUPBOARD, true);
		placeFurniture(x + 5, y + 1, Type.FIREWOOD, true);
		placeFurniture(x + 1, y + 6, Type.FIREWOOD, true);
		placeFurniture(x + 6, y + 6, Type.TABLE, true);
		placeFurniture(x + 5, y + 6, Type.STOOL, true);
		// Decorative overlays preserve the wooden floor and never entangle the hero.
		customTiles.add(new SurfaceCobweb(x + 1, y + 1, SurfaceCobweb.Corner.NORTH_WEST));
		customTiles.add(new SurfaceCobweb(x + 8, y + 1, SurfaceCobweb.Corner.NORTH_EAST));
		customTiles.add(new SurfaceCobweb(x + 1, y + 7, SurfaceCobweb.Corner.SOUTH_WEST));
		customTiles.add(new SurfaceCobweb(x + 8, y + 7, SurfaceCobweb.Corner.SOUTH_EAST));
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
		placeFurniture(x, y, type, false);
	}

	private void placeFurniture(int x, int y, Type type, boolean abandoned) {
		SurfaceFurniture furniture = new SurfaceFurniture(type, x, y, abandoned);
		Painter.fill(this, x, y, furniture.tileW, furniture.tileH, Terrain.CUSTOM_DECO);
		customTiles.add(furniture);
	}

	private void plantTrees(int x, int y) {
		placeExterior(x - 15, y - 6, SurfaceExterior.Type.OAK);
		placeExterior(x - 12, y - 12, SurfaceExterior.Type.BIRCH);
		placeExterior(x - 7, y - 12, SurfaceExterior.Type.PINE);
		placeExterior(x - 2, y - 13, SurfaceExterior.Type.APPLE_TREE);
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
		SurfaceHandcart cart = new SurfaceHandcart(x - 2, y - 6);
		Painter.fill(this, cart.tileX, cart.tileY, cart.tileW, cart.tileH, Terrain.SURFACE_DECO);
		customTiles.add(cart);
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
			if (x >= tile.tileX && x < tile.tileX + tile.tileW
					&& y >= tile.tileY && y < tile.tileY + tile.tileH) {
				if (tile instanceof SurfaceHandcart) return false;
				if (tile instanceof SurfaceExterior) return ((SurfaceExterior) tile).blocksSight();
				if (tile instanceof SurfaceForest && ((SurfaceForest) tile).containsCell(x, y)) {
					return ((SurfaceForest) tile).blocksSight(x, y);
				}
			}
		}
		return true;
	}

	public boolean isWellCell(int cell) {
		if (cell < 0 || cell >= length() || map[cell] != Terrain.SURFACE_DECO) return false;
		int x = cell % width();
		int y = cell / width();
		for (CustomTilemap tile : customTiles) {
			if (tile instanceof SurfaceExterior && ((SurfaceExterior) tile).isWell()
					&& x >= tile.tileX && x < tile.tileX + tile.tileW
					&& y >= tile.tileY && y < tile.tileY + tile.tileH) {
				return true;
			}
		}
		return false;
	}

	public SurfaceHandcart handcart() {
		for (CustomTilemap tile : customTiles) {
			if (tile instanceof SurfaceHandcart) return (SurfaceHandcart) tile;
		}
		return null;
	}

	public boolean isHandcartCell(int cell) {
		SurfaceHandcart cart = handcart();
		return cell >= 0 && cell < length() && cart != null
				&& cell / width() == cart.tileY
				&& cell % width() >= cart.tileX && cell % width() < cart.tileX + cart.tileW;
	}

	public void interactHandcart(final Hero hero, final int cell) {
		final SurfaceHandcart cart = handcart();
		if (!isHandcartCell(cell) || !adjacent(hero.pos, cell)) return;
		String description;
		if (cart.isMoved()) {
			description = "parked";
		} else {
			description = cart.isSupported() ? "ready" : "stuck";
			boolean canSupport = !cart.isSupported() && hero.belongings.getItem(CartPlank.class) != null;
			GameScene.show(new WndOptions(cart.image(0, 0), Messages.get(SurfaceHandcart.class, "name"),
					Messages.get(SurfaceHandcart.class, description),
					Messages.get(SurfaceHandcart.class, canSupport ? "support" : "push"),
					Messages.get(SurfaceHandcart.class, "later")) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) workOnHandcart(hero, cell);
				}
			});
			return;
		}
		GameScene.show(new WndOptions(cart.image(0, 0), Messages.get(SurfaceHandcart.class, "name"),
				Messages.get(SurfaceHandcart.class, description), Messages.get(SurfaceHandcart.class, "close")));
	}

	private void workOnHandcart(Hero hero, int cell) {
		if (Dungeon.level != this || !isHandcartCell(cell)
				|| !adjacent(hero.pos, cell)) return;
		SurfaceHandcart cart = handcart();
		if (cart.isMoved()) return;
		if (!cart.isSupported()) {
			CartPlank plank = hero.belongings.getItem(CartPlank.class);
			if (plank == null) {
				GLog.w(Messages.get(SurfaceHandcart.class, "no_plank"));
				return;
			}
			plank.detachAll(hero.belongings.backpack);
			cart.support();
			GLog.p(Messages.get(SurfaceHandcart.class, "supported"));
		} else {
			int targetX = width() / 2 - 4;
			int targetY = height() / 2 - 10;
			// Never move the cart onto a character or an item left on its parking spot.
			for (int x = targetX; x < targetX + cart.tileW; x++) {
				int target = targetY * width() + x;
				if (!passable[target] || Actor.findChar(target) != null || heaps.get(target) != null) {
					GLog.w(Messages.get(SurfaceHandcart.class, "blocked"));
					return;
				}
			}
			int oldCell = cart.tileY * width() + cart.tileX;
			cart.moveTo(targetX, targetY);
			for (int i = 0; i < cart.tileW; i++) {
				Level.set(oldCell + i, Terrain.EMPTY, this);
				Level.set(targetY * width() + targetX + i, Terrain.SURFACE_DECO, this);
			}
			GLog.p(Messages.get(SurfaceHandcart.class, "moved"));
		}
		GameScene.updateMap();
		Dungeon.observe();
		hero.busy();
		hero.sprite.operate(cell);
		hero.spend(1f);
	}

	@Override
	protected void createMobs() {
		boolean hasVillager = false;
		boolean hasGrandmother = false;
		for (Mob mob : mobs) {
			if (mob instanceof Villager) hasVillager = true;
			if (mob instanceof Grandmother) hasGrandmother = true;
		}

		if (!hasVillager) {
			// Inside the north-western house, clear of the doorway.
			Villager villager = new Villager();
			villager.pos = (height() / 2 - 5) * width() + width() / 2 - 6;
			mobs.add(villager);
		}
		if (!hasGrandmother) {
			// West of the stove in the southern house, clear of its entrance.
			Grandmother grandmother = new Grandmother();
			grandmother.pos = (height() / 2 + 5) * width() + width() / 2 + 2;
			mobs.add(grandmother);
		}
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
		// An ordinary unlocked chest against the eastern wall of the right-hand house.
		drop(new CartPlank(), (height() / 2 - 5) * width() + width() / 2 + 6).type = Heap.Type.CHEST;
	}

	@Override
	public String tileName(int tile) {
		if (tile == Terrain.REGION_DECO_ALT) return Messages.get(this, "region_deco_name");
		return super.tileName(tile);
	}

	@Override
	public String tileDesc(int tile) {
		if (tile == Terrain.REGION_DECO_ALT) return Messages.get(this, "region_deco_desc");
		return super.tileDesc(tile);
	}

}

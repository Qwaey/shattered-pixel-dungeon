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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.CampaignNPC;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Grandmother;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Miron;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Villager;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CartPlank;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.FlourSack;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.PieBerries;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HayQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceBerryBush;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceCobweb;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceExterior;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceForest;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceHandcart;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurfaceFurniture.Type;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Bundle;

public class SurfaceLevel extends OutdoorLevel {

	private static final int BARN_X = 8, BARN_Y = 7;
	private static final int LUMBERJACK_HOUSE_X = 17, LUMBERJACK_HOUSE_Y = 16;
	private static final int NORTH_EAST_HOUSE_X = 29, NORTH_EAST_HOUSE_Y = 9;
	private static final int SOUTH_HOUSE_X = 31, SOUTH_HOUSE_Y = 21;
	private static final int WELL_X = 27, WELL_Y = 19;
	private static final int CART_X = 46, CART_Y = 20;
	private static final int PARKED_CART_X = 42, PARKED_CART_Y = 23;

	private HayQuest hayQuest;

	@Override
	protected boolean isHouseCell(int x, int y) {
		return insideHouse(x, y, BARN_X, BARN_Y, 8, 7)
				|| insideHouse(x, y, LUMBERJACK_HOUSE_X, LUMBERJACK_HOUSE_Y, 8, 6)
				|| insideHouse(x, y, NORTH_EAST_HOUSE_X, NORTH_EAST_HOUSE_Y, 8, 6)
				|| insideHouse(x, y, SOUTH_HOUSE_X, SOUTH_HOUSE_Y, 8, 6);
	}

	@Override
	protected int sectionArrivalCell() {
		return (height() - 5) * width() + 28;
	}

	public HayQuest hayQuest() {
		return hayQuest;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (hayQuest != null) bundle.put("hay_quest", hayQuest);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		hayQuest = (HayQuest) bundle.get("hay_quest");
	}

	@Override
	protected boolean build() {
		setSize(SurfaceForest.WIDTH, SurfaceForest.HEIGHT);
		Painter.fill(this, 1, 1, width() - 2, height() - 2, Terrain.EMPTY);
		SurfaceForest.plant(this);

		// The detached north-western barn opens onto the path into the village.
		paintHouse(BARN_X, BARN_Y, 8, 7, BARN_X + 4, BARN_Y + 6);
		furnishBarn(BARN_X, BARN_Y);

		// Three rectangular homes face the open square around the well.
		paintHouse(LUMBERJACK_HOUSE_X, LUMBERJACK_HOUSE_Y, 8, 6,
				LUMBERJACK_HOUSE_X + 7, LUMBERJACK_HOUSE_Y + 3);
		paintHouse(NORTH_EAST_HOUSE_X, NORTH_EAST_HOUSE_Y, 8, 6,
				NORTH_EAST_HOUSE_X + 3, NORTH_EAST_HOUSE_Y + 5);
		Painter.set(this, NORTH_EAST_HOUSE_X + 3, NORTH_EAST_HOUSE_Y + 5, Terrain.LOCKED_DOOR);
		paintHouse(SOUTH_HOUSE_X, SOUTH_HOUSE_Y, 8, 6,
				SOUTH_HOUSE_X, SOUTH_HOUSE_Y + 3);
		furnishLumberjackHouse(LUMBERJACK_HOUSE_X, LUMBERJACK_HOUSE_Y);
		furnishNorthEastHouse(NORTH_EAST_HOUSE_X, NORTH_EAST_HOUSE_Y);
		furnishSouthHouse(SOUTH_HOUSE_X, SOUTH_HOUSE_Y);
		plantTrees();
		decorateYards();
		hayQuest = new HayQuest();

		int stairs = (BARN_Y + 3) * width() + BARN_X + 3;
		map[stairs] = Terrain.EXIT;
		transitions.add(new LevelTransition(this, stairs,
				LevelTransition.Type.REGULAR_EXIT, 1, 0, LevelTransition.Type.SURFACE));
		// The southern path leads into the woodland's northern edge.
		LevelTransition woodland = new LevelTransition(this, (height() - 2) * width() + 28,
				LevelTransition.Type.SECTION, -1, 0, LevelTransition.Type.SECTION);
		woodland.set(28, height() - 3, 29, height() - 2);
		transitions.add(woodland);
		return true;
	}

	private void furnishBarn(int x, int y) {
		// Original storage barrels with wooden flooring and their standard overhangs.
		Painter.set(this, x + 1, y + 2, Terrain.REGION_DECO_ALT);
		Painter.set(this, x + 2, y + 2, Terrain.REGION_DECO_ALT);
		Painter.set(this, x + 1, y + 3, Terrain.REGION_DECO_ALT);
		Painter.set(this, x + 2, y + 3, Terrain.REGION_DECO_ALT);
		placeFurniture(x + 4, y + 1, Type.CUPBOARD, true);
		placeFurniture(x + 6, y + 2, Type.FIREWOOD, true);
		placeFurniture(x + 6, y + 3, Type.FIREWOOD, true);
		placeFurniture(x + 4, y + 4, Type.TABLE, true);
		placeFurniture(x + 5, y + 4, Type.STOOL, true);
		// Decorative overlays preserve the wooden floor and never entangle the hero.
		customTiles.add(new SurfaceCobweb(x + 1, y + 1, SurfaceCobweb.Corner.NORTH_WEST));
		customTiles.add(new SurfaceCobweb(x + 6, y + 1, SurfaceCobweb.Corner.NORTH_EAST));
		customTiles.add(new SurfaceCobweb(x + 1, y + 5, SurfaceCobweb.Corner.SOUTH_WEST));
		customTiles.add(new SurfaceCobweb(x + 6, y + 5, SurfaceCobweb.Corner.SOUTH_EAST));
	}

	private void furnishLumberjackHouse(int x, int y) {
		placeFurniture(x + 5, y + 1, Type.BED);
		placeFurniture(x + 3, y + 1, Type.CUPBOARD);
		placeFurniture(x + 1, y + 1, Type.STOVE);
		placeFurniture(x + 1, y + 2, Type.FIREWOOD);
		placeFurniture(x + 3, y + 3, Type.TABLE);
		placeFurniture(x + 2, y + 3, Type.STOOL);
		placeFurniture(x + 4, y + 1, Type.WASHBASIN);
	}

	private void furnishNorthEastHouse(int x, int y) {
		placeFurniture(x + 5, y + 1, Type.BED);
		placeFurniture(x + 3, y + 1, Type.CUPBOARD);
		placeFurniture(x + 1, y + 1, Type.STOVE);
		placeFurniture(x + 2, y + 1, Type.WASHBASIN);
		placeFurniture(x + 1, y + 2, Type.FIREWOOD);
		placeFurniture(x + 3, y + 3, Type.TABLE);
		placeFurniture(x + 4, y + 3, Type.STOOL);
	}

	private void furnishSouthHouse(int x, int y) {
		placeFurniture(x + 1, y + 1, Type.BED);
		placeFurniture(x + 3, y + 1, Type.CUPBOARD);
		placeFurniture(x + 6, y + 2, Type.STOVE);
		placeFurniture(x + 6, y + 3, Type.FIREWOOD);
		placeFurniture(x + 5, y + 1, Type.WASHBASIN);
		placeFurniture(x + 3, y + 3, Type.TABLE);
		placeFurniture(x + 4, y + 3, Type.STOOL);
	}

	private void plantTrees() {
		placeExterior(6, 15, SurfaceExterior.Type.OAK);
		placeExterior(23, 8, SurfaceExterior.Type.PINE);
		placeExterior(28, 6, SurfaceExterior.Type.OAK);
		placeExterior(39, 23, SurfaceExterior.Type.PINE);
		placeExterior(38, 29, SurfaceExterior.Type.OAK);
		placeExterior(30, 32, SurfaceExterior.Type.PINE);
		placeExterior(14, 21, SurfaceExterior.Type.PINE);
		placeExterior(12, 20, SurfaceExterior.Type.OAK);
	}

	private void decorateYards() {
		placeExterior(WELL_X, WELL_Y, SurfaceExterior.Type.WELL);
		// The forest leaves just this two-cell entrance into the secluded nook.
		SurfaceHandcart cart = new SurfaceHandcart(CART_X, CART_Y);
		Painter.fill(this, cart.tileX, cart.tileY, cart.tileW, cart.tileH, Terrain.SURFACE_DECO);
		customTiles.add(cart);
		// The berry bush grows inside the nook, beyond the cart's blocked entrance.
		SurfaceBerryBush bush = new SurfaceBerryBush(47, 12, false);
		Painter.fill(this, bush.tileX, bush.tileY, bush.tileW, bush.tileH, Terrain.SURFACE_DECO);
		customTiles.add(bush);
		customWalls.add(new SurfaceBerryBush(47, 12, true));
		placeExterior(15, 19, SurfaceExterior.Type.CHOPPING_BLOCK);
		placeExterior(20, 14, SurfaceExterior.Type.CHOPPING_BLOCK);
		// The field lies south-west of the square, below the lumberjack's home.
		placeExterior(HayQuest.FIRST_STACK_X, HayQuest.FIRST_STACK_Y, SurfaceExterior.Type.HAYSTACK);
		placeExterior(HayQuest.SECOND_STACK_X, HayQuest.SECOND_STACK_Y, SurfaceExterior.Type.HAYSTACK);
		placeExterior(HayQuest.THIRD_STACK_X, HayQuest.THIRD_STACK_Y, SurfaceExterior.Type.HAYSTACK);
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

	public Grandmother grandmother() {
		for (Mob mob : mobs) {
			if (mob instanceof Grandmother) return (Grandmother) mob;
		}
		return null;
	}

	private SurfaceBerryBush berryBush() {
		for (CustomTilemap tile : customTiles) {
			if (tile instanceof SurfaceBerryBush) return (SurfaceBerryBush) tile;
		}
		return null;
	}

	public boolean isBerryBushCell(int cell) {
		SurfaceBerryBush bush = berryBush();
		return cell >= 0 && cell < length() && bush != null
				&& cell / width() == bush.tileY
				&& cell % width() >= bush.tileX && cell % width() < bush.tileX + bush.tileW;
	}

	public void harvestBerries(Hero hero, int cell) {
		if (Dungeon.level != this || !isBerryBushCell(cell) || !adjacent(hero.pos, cell)) return;
		SurfaceBerryBush bush = berryBush();
		if (bush.isHarvested()) {
			GLog.i(Messages.get(SurfaceBerryBush.class, "empty"));
			return;
		}
		PieBerries berries = new PieBerries();
		if (berries.collect()) {
			GLog.i(Messages.capitalize(Messages.get(hero, "you_now_have", berries.name())));
		} else {
			drop(berries, hero.pos).sprite.drop();
			GLog.i(Messages.get(SurfaceBerryBush.class, "no_space"));
		}
		bush.harvest();
		for (CustomTilemap tile : customWalls) {
			if (tile instanceof SurfaceBerryBush) ((SurfaceBerryBush) tile).harvest();
		}
		GameScene.updateMap();
		GLog.p(Messages.get(SurfaceBerryBush.class, "gathered"));
		hero.busy();
		hero.sprite.operate(cell);
		hero.spend(1f);
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
			int targetX = PARKED_CART_X;
			int targetY = PARKED_CART_Y;
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
		boolean hasMiron = false;
		boolean hasBarnClue = false;
		for (Mob mob : mobs) {
			if (mob instanceof Villager) hasVillager = true;
			if (mob instanceof Grandmother) hasGrandmother = true;
			if (mob instanceof Miron) hasMiron = true;
			if (mob instanceof CampaignNPC && ((CampaignNPC) mob).quest() == 1014) hasBarnClue = true;
		}

		if (!hasVillager) {
			// Inside the western home, clear of the route to its eastern door.
			Villager villager = new Villager();
			villager.pos = (LUMBERJACK_HOUSE_Y + 3) * width() + LUMBERJACK_HOUSE_X + 4;
			mobs.add(villager);
		}
		if (!hasGrandmother) {
			// West of the stove in the southern house, clear of its entrance.
			Grandmother grandmother = new Grandmother();
			grandmother.pos = (SOUTH_HOUSE_Y + 2) * width() + SOUTH_HOUSE_X + 5;
			mobs.add(grandmother);
		}
		if (hayQuest != null && !hasMiron) {
			Miron miron = new Miron();
			// At the northern edge of the field, away from doors and haystacks.
			miron.pos = 24 * width() + 22;
			mobs.add(miron);
		}
		if (!hasBarnClue) {
			CampaignNPC clue = new CampaignNPC(1014);
			clue.pos = (BARN_Y + 5) * width() + BARN_X + 6;
			mobs.add(clue);
		}
	}

	@Override
	protected void createItems() {
		// Separate unlocked chests inside Miron's locked home.
		drop(new CartPlank(), (NORTH_EAST_HOUSE_Y + 3) * width()
				+ NORTH_EAST_HOUSE_X + 6).type = Heap.Type.CHEST;
		drop(new FlourSack(), (NORTH_EAST_HOUSE_Y + 2) * width()
				+ NORTH_EAST_HOUSE_X + 2).type = Heap.Type.CHEST;
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

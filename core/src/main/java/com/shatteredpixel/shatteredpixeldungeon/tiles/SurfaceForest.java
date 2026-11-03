/*
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

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

import java.util.Arrays;

// A closed, authored forest around the village clearing, drawn in two batches.
public class SurfaceForest extends CustomTilemap {

	// Each symbol covers a 2x2 patch; odd rows shift one cell east.
	// Digits are dense thickets; A-J are single mature trees of the same variants.
	// a/b = oak saplings, c/d = pine saplings, '.' = open grass.
	// The north-eastern nook joins the main clearing only at (46-47, 20),
	// where the handcart initially blocks the full width of the forest path.
	private static final String[] PLAN = {
			"051627384950627183945061728",
			"627183945061728394506271839",
			"1728a..G.........c061728394",
			"39A..............d283945061",
			"05H...............945...728",
			"62a................062..839",
			"839................28...506",
			"17.................617..394",
			"39450b............d83..5061",
			"05162..............b50.1728",
			"62718..................1839",
			"172839c..............b28394",
			"3945062a.............945061",
			"0516273c............c061728",
			"6271839.............6271839",
			"83945061a........d728394506",
			"172839450a......d5061728394",
			"394506271839450617283945061"
	};

	public static final int WIDTH = PLAN[0].length() * 2 + 2;
	public static final int HEIGHT = PLAN.length * 2 + 2;

	private static final int[] TREE_COLUMNS = {0, 16, 18, 20, 22, 4, 24, 26, 28, 30};
	private static final int SINGLE_TREE = 10;
	private static final int SAPLING = 20;

	private int[] trees;
	private boolean canopy;

	{
		texture = Assets.Environment.SURFACE_EXTERIOR;
	}

	// Used by the standard level save/restore mechanism.
	public SurfaceForest() {
	}

	private SurfaceForest(boolean canopy) {
		this.canopy = canopy;
		tileW = WIDTH;
		tileH = HEIGHT;
		trees = new int[PLAN.length * PLAN[0].length()];
		for (int row = 0; row < PLAN.length; row++) {
			for (int col = 0; col < PLAN[row].length(); col++) {
				char tree = PLAN[row].charAt(col);
				trees[row * PLAN[0].length() + col] = tree == '.' ? -1
						: tree >= 'a' ? SAPLING + tree - 'a'
						: tree >= 'A' ? SINGLE_TREE + tree - 'A' : tree - '0';
			}
		}
	}

	public static void plant(Level level) {
		SurfaceForest ground = new SurfaceForest(false);
		int columns = PLAN[0].length();
		for (int i = 0; i < ground.trees.length; i++) {
			int tree = ground.trees[i];
			if (tree < 0) continue;
			int row = i / columns;
			int x = 1 + (i % columns) * 2 + row % 2;
			int y = 1 + row * 2;
			if (tree < SINGLE_TREE) {
				// Only the deeper, closed thickets fill the space between trunks.
				Painter.fill(level, x, y, 2, 2, Terrain.SURFACE_DECO);
			} else {
				Painter.fill(level, spriteX(tree, x), spriteY(tree, y), spriteWidth(tree), 1, Terrain.SURFACE_DECO);
			}
		}
		level.customTiles.add(ground);
		level.customWalls.add(new SurfaceForest(true));
	}

	private static int spriteColumn(int tree) {
		return tree >= SAPLING ? 32 + tree - SAPLING : TREE_COLUMNS[tree % SINGLE_TREE];
	}

	private static int spriteWidth(int tree) {
		return tree >= SAPLING ? 1 : 2;
	}

	private static int spriteHeight(int tree) {
		return tree >= SAPLING ? 2 : 3;
	}

	private static int spriteX(int tree, int patchX) {
		// The second variant grows on the other half of its patch.
		return patchX + (tree >= SAPLING ? (tree - SAPLING) % 2 : 0);
	}

	private static int spriteY(int tree, int patchY) {
		// Saplings sit at different corners instead of lining up along each row.
		return patchY + (tree == SAPLING + 1 || tree == SAPLING + 2 ? 0 : 1);
	}

	@Override
	public Tilemap create() {
		Tilemap v = super.create();
		int[] data = new int[tileW * tileH];
		Arrays.fill(data, -1);
		int columns = (tileW - 2) / 2;
		for (int i = 0; i < trees.length; i++) {
			int tree = trees[i];
			if (tree < 0) continue;
			int treeRow = i / columns;
			int x = spriteX(tree, 1 + (i % columns) * 2 + treeRow % 2);
			int y = spriteY(tree, 1 + treeRow * 2);
			for (int row = canopy ? 3 - spriteHeight(tree) : 2; row < (canopy ? 2 : 3); row++) {
				for (int col = 0; col < spriteWidth(tree); col++) {
					data[(y - 2 + row) * tileW + x + col] =
							row * (SurfaceExterior.ATLAS_WIDTH / SIZE) + spriteColumn(tree) + col;
				}
			}
		}
		v.map(data, tileW);
		return v;
	}

	private int treeAt(int x, int y) {
		if (y < 1 || y >= tileH - 1) return -1;
		int row = (y - 1) / 2;
		int startX = 1 + row % 2;
		if (x < startX || x >= startX + tileW - 2) return -1;
		int col = (x - startX) / 2;
		int tree = trees[row * ((tileW - 2) / 2) + col];
		if (tree < SINGLE_TREE) return tree;
		int baseX = spriteX(tree, startX + col * 2);
		return y == spriteY(tree, 1 + row * 2) && x >= baseX && x < baseX + spriteWidth(tree) ? tree : -1;
	}

	public boolean containsCell(int x, int y) {
		return treeAt(x, y) >= 0;
	}

	public boolean blocksSight(int x, int y) {
		int tree = treeAt(x, y);
		return tree >= 0 && tree < SAPLING;
	}

	@Override
	public Image image(int tileX, int tileY) {
		int tree = treeAt(tileX, tileY);
		if (canopy || tree < 0) return null;
		Image img = new Image(texture);
		img.frame(spriteColumn(tree) * SIZE, (3 - spriteHeight(tree)) * SIZE,
				spriteWidth(tree) * SIZE, spriteHeight(tree) * SIZE);
		return img;
	}

	@Override
	public String name(int tileX, int tileY) {
		int tree = treeAt(tileX, tileY);
		if (canopy || tree < 0) return null;
		if (tree >= SAPLING) return Messages.get(this, tree < SAPLING + 2 ? "oak_sapling_name" : "pine_sapling_name");
		return Messages.get(SurfaceExterior.class, tree % SINGLE_TREE < 5 ? "oak_name" : "pine_name");
	}

	@Override
	public String desc(int tileX, int tileY) {
		int tree = treeAt(tileX, tileY);
		if (canopy || tree < 0) return null;
		if (tree >= SAPLING) return Messages.get(this, tree < SAPLING + 2 ? "oak_sapling_desc" : "pine_sapling_desc");
		if (tree >= SINGLE_TREE) return Messages.get(SurfaceExterior.class, tree % SINGLE_TREE < 5 ? "oak_desc" : "pine_desc");
		return Messages.get(this, "desc");
	}

	private static final String TREES = "trees";
	private static final String CANOPY = "canopy";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		trees = bundle.getIntArray(TREES);
		canopy = bundle.getBoolean(CANOPY);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TREES, trees);
		bundle.put(CANOPY, canopy);
	}
}

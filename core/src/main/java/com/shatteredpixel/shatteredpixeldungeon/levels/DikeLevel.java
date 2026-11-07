/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Emberling;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Reedcutter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class DikeLevel extends AmbushLevel {

	private static final int AMBUSH_QUEST = 19;

	private static final String[] MAP = {
			"##########################",
			"##=====================###",
			"##=====================###",
			"#=======================##",
			"##======================##",
			"##====#############======#",
			"#=====#...........#======#",
			"#===..#...........#======#",
			"#.#...#...........#======#",
			"##....#....#......#======#",
			"#+++++++++++++++++#======#",
			"#+++++#.....+#....#======#",
			"##....#.....+.....#======#",
			"#..~~+#.....+.....#======#",
			"#==~~+#.....+.....#======#",
			"#==~~~######+######======#",
			"#==~~~=====.++.==========#",
			"#===~~====~.++.~=========#",
			"###========.++.~=========#",
			"###=========++==========##",
			"###=========++========####",
			"##########################"
	};

	@Override
	protected boolean build() {
		setSize(MAP[0].length(), MAP.length);
		for (int y = 0; y < height(); y++) {
			for (int x = 0; x < width(); x++) {
				int terrain;
				switch (MAP[y].charAt(x)) {
					case '.': terrain = Terrain.EMPTY; break;
					case '+': terrain = Terrain.EMPTY_SP; break;
					case '~': terrain = Terrain.WATER; break;
					case '=': terrain = Terrain.CUSTOM_DECO_WTR; break;
					default: terrain = Terrain.WALL; break;
				}
				Painter.set(this, x, y, terrain);
			}
		}
		prepareAmbush();

		passage(-4, 1, 10, 2, 11, cell(4, 10), 0);
		passage(-7, 12, 19, 13, 20, cell(12, 17), 20);
		return true;
	}

	@Override
	protected void createMobs() {
		Reedcutter reedcutter = new Reedcutter();
		reedcutter.pos = cell(4, 15);
		reedcutter.setMinY(13);
		mobs.add(reedcutter);
		waypoint(4, 8, 1028);
		waypoint(9, 9, AMBUSH_QUEST);
		waypoint(13, 17, 20);
	}

	@Override
	protected AmbushPlan ambushPlan() {
		return new AmbushPlan(AMBUSH_QUEST, 7, 6, 17, 14,
				new int[]{cell(6, 10)}, new int[]{cell(12, 15)},
				wave(spawn(Emberling::new, cell(15, 10), cell(15, 8), cell(15, 12))));
	}

	@Override
	protected String ambushMessage(String key) {
		return Messages.get(this, key);
	}
}

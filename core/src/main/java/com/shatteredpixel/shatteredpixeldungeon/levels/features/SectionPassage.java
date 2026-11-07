/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundle;

public class SectionPassage extends LevelTransition {

	private int arrival;
	private int requiredQuest;

	public SectionPassage() {
	}

	public SectionPassage(Level level, int destDepth, int left, int top, int right, int bottom,
						  int arrival, int requiredQuest) {
		super(level, top * level.width() + left, Type.SECTION, destDepth, 0, Type.SECTION);
		set(left, top, right, bottom);
		this.arrival = arrival;
		this.requiredQuest = requiredQuest;
	}

	public int arrival() {
		return arrival;
	}

	public boolean isOpen() {
		return CampaignQuest.reached(requiredQuest);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("arrival", arrival);
		bundle.put("required_quest", requiredQuest);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		arrival = bundle.getInt("arrival");
		requiredQuest = bundle.getInt("required_quest");
	}
}

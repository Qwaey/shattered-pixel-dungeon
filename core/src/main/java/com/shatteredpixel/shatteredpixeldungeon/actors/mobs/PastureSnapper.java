/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.ArchiveLevels;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PastureSnapperSprite;

public class PastureSnapper extends ChargingRoadsideMob {

	public PastureSnapper() {
		super(PastureSnapperSprite.class, 13, 3, 8, 3, 2, 2);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		if (Dungeon.level instanceof ArchiveLevels.SheepValley) CampaignQuest.complete(56);
	}
}

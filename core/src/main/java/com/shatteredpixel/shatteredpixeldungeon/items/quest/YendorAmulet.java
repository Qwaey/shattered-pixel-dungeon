/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class YendorAmulet extends Item {

	{
		image = ItemSpriteSheet.AMULET;
		unique = true;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (!super.doPickUp(hero, pos)) return false;
		CampaignQuest.begin();
		return true;
	}

	@Override
	public void doDrop(Hero hero) {
		// The curse returns the very same object. Never detach it from the backpack.
		GLog.w(Messages.get(this, "returns"));
		hero.spendAndNext(TIME_TO_DROP);
	}

	@Override
	public void doThrow(Hero hero) {
		doDrop(hero);
	}

	@Override
	public boolean keptThroughLostInventory() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
}

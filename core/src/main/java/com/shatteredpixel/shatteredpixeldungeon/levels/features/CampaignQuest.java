/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.PieParcel;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.YendorAmulet;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.OutdoorLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.Arrays;
import java.util.HashSet;

public final class CampaignQuest {

	private static final int[] QUESTS = {
			5, 6, 7, 8, 16, 17, 18, 19, 20, 31, 32, 33, 34, 35, 46, 47, 48, 49, 50,
			61, 62, 63, 64, 65, 76, 77, 78, 79, 80, 91, 92, 93, 94, 95, 106, 107, 108, 109, 110
	};
	private static final int[] JOURNAL_DEPTHS = {
			0, 0, -2, -2, -3, -4, -4, -5, -5, -7, -8, -8, -9, -9, -11, -12, -12, -13, -13, -15,
			-16, -17, -17, -17, -19, -20, -21, -21, -21, -23, -24, -24, -25, -25, -27, -28, -29, -29, -29, -27
	};
	private static final String STAGE = "campaign_stage";
	private static final String WOODCUTTER_HINT = "woodcutter_hint";
	public static final int FALSE_TRAIL = 901;
	private static final HashSet<Integer> completedSideQuests = new HashSet<>();
	private static int completed;
	private static boolean woodcutterHint;
	private static boolean epilogue;

	private CampaignQuest() {
	}

	public static boolean reached(int quest) {
		if (quest == 0) return true;
		if (quest == 1) return Dungeon.levelHasBeenGenerated(0, 0);
		if ((quest >= 2 && quest <= 4) || CampaignSideQuest.get(quest) != null) {
			return completedSideQuests.contains(quest);
		}
		int stage = Arrays.binarySearch(QUESTS, quest);
		return stage >= 0 && stage < completed;
	}

	public static boolean canComplete(int quest) {
		CampaignSideQuest side = CampaignSideQuest.get(quest);
		if (side != null) {
			return !reached(quest) && side.available()
					&& (quest != FALSE_TRAIL || Dungeon.hero.belongings.getItem(PieParcel.class) != null);
		}
		return completed < QUESTS.length && QUESTS[completed] == quest;
	}

	public static void begin() {
		if (completed == 0) Notes.add(Notes.Landmark.ROAD, JOURNAL_DEPTHS[0]);
	}

	public static void recordVillageQuest(int quest) {
		if (completedSideQuests.add(quest)) Notes.recordCampaignQuest(quest);
	}

	public static void hearWoodcutter() {
		woodcutterHint = true;
	}

	public static boolean knowsWoodcutterHint() {
		return woodcutterHint;
	}

	public static boolean complete(int quest) {
		if (!canComplete(quest)) return false;
		CampaignSideQuest side = CampaignSideQuest.get(quest);
		if (side != null) {
			completedSideQuests.add(quest);
			if (quest <= 120) Notes.recordCampaignQuest(quest);
			if (quest == FALSE_TRAIL) {
				Notes.remove(Notes.Landmark.PIE_DELIVERY, -2);
				Notes.add(Notes.Landmark.PIE_DELIVERY, -1);
			} else if (quest == 9) {
				Notes.remove(Notes.Landmark.PIE_DELIVERY, -1);
			}
		} else {
			Notes.remove(Notes.Landmark.ROAD, JOURNAL_DEPTHS[completed]);
			completed++;
			Notes.add(Notes.Landmark.ROAD, JOURNAL_DEPTHS[completed]);
		}
		if (Dungeon.level instanceof OutdoorLevel) ((OutdoorLevel) Dungeon.level).campaignChanged();
		if (side == null) giveSupplies(quest);
		else side.award();
		return true;
	}

	private static void giveSupplies(int quest) {
		switch (quest) {
			case 6: case 16: case 31:
				give(new Food());
				break;
			case 8: case 17:
				give(new PotionOfHealing().identify());
				break;
			case 46: case 61: case 76: case 91: case 106:
				give(new Food());
				give(new PotionOfHealing().quantity(quest == 106 ? 2 : 1).identify());
				break;
		}
	}

	static void give(Item item) {
		if (item.collect()) {
			GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", item.name())));
		} else {
			Dungeon.level.drop(item, Dungeon.hero.pos).sprite.drop();
		}
	}

	public static boolean finishJourney() {
		if (!reached(110) || epilogue) return false;
		epilogue = true;
		Dungeon.win(YendorAmulet.class);
		return true;
	}

	public static String journal() {
		if (epilogue) return Messages.get(CampaignQuest.class, "epilogue");
		if (completed == 1 && woodcutterHint) return Messages.get(CampaignQuest.class, "journal_1_hint");
		return Messages.get(CampaignQuest.class, "journal_" + completed);
	}

	public static String deliveryNote() {
		return Messages.get(CampaignQuest.class, reached(FALSE_TRAIL) ? "delivery_return" : "delivery_out");
	}

	public static void reset() {
		completed = 0;
		woodcutterHint = false;
		completedSideQuests.clear();
		epilogue = false;
	}

	public static void storeInBundle(Bundle bundle) {
		bundle.put(STAGE, completed == 0 ? 0 : QUESTS[completed - 1]);
		bundle.put(WOODCUTTER_HINT, woodcutterHint);
		bundle.put("side_quests", completedSideQuests.stream().mapToInt(Integer::intValue).toArray());
		bundle.put("epilogue", epilogue);
	}

	public static void restoreFromBundle(Bundle bundle) {
		completed = Math.max(0, Arrays.binarySearch(QUESTS, bundle.getInt(STAGE)) + 1);
		woodcutterHint = bundle.getBoolean(WOODCUTTER_HINT);
		completedSideQuests.clear();
		int[] sideQuests = bundle.contains("side_quests") ? bundle.getIntArray("side_quests") : null;
		if (sideQuests != null) {
			for (int quest : sideQuests) completedSideQuests.add(quest);
		}
		epilogue = bundle.getBoolean("epilogue");
	}
}

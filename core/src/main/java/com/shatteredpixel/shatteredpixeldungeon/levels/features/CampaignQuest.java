/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.CampaignNPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.PieParcel;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.YendorAmulet;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.OutdoorLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;

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
	private static final String STARTED_SIDE_QUESTS = "started_side_quests";
	private static final int LAST_JOURNAL_QUEST = 120;
	public static final int FALSE_TRAIL = 901;
	private static final HashSet<Integer> completedSideQuests = new HashSet<>();
	private static final HashSet<Integer> startedSideQuests = new HashSet<>();
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
		if (completedSideQuests.add(quest)) {
			Notes.recordCampaignQuest(quest);
			announceCompletion(quest);
		}
	}

	public static void startSideQuest(int quest) {
		if (CampaignSideQuest.get(quest) == null || !startedSideQuests.add(quest)) return;
		if (quest > 1000) {
			announceStart(quest - 1000);
		} else if (quest <= LAST_JOURNAL_QUEST && quest != 9 && !reached(quest + 1000)) {
			announceStart(quest);
		}
	}

	public static void announceStart(int quest) {
		GameScene.queueQuestNotice(journalTitle(quest, false), false);
	}

	private static void announceCompletion(int quest) {
		GameScene.queueQuestNotice(journalTitle(quest, true), true);
	}

	public static void onFirstSurfaceArrival() {
		announceCompletion(1);
		if (completed == 0) announceStart(QUESTS[0]);
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
			startedSideQuests.remove(quest);
			if (quest <= LAST_JOURNAL_QUEST) Notes.recordCampaignQuest(quest);
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

		// The last main quest is only finished after the epilogue conversation.
		if (quest <= LAST_JOURNAL_QUEST && quest != 110) announceCompletion(quest);
		if (side == null && completed < QUESTS.length) announceStart(QUESTS[completed]);
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
		announceCompletion(110);
		return true;
	}

	public static String journal() {
		if (epilogue) return Messages.get(CampaignQuest.class, "epilogue");
		if (completed == 0 && !reached(1)) return Messages.get(CampaignQuest.class, "prologue_carried");
		if (completed == 1 && woodcutterHint) return Messages.get(CampaignQuest.class, "journal_1_hint");
		return Messages.get(CampaignQuest.class, "journal_" + completed);
	}

	public static String deliveryNote() {
		return Messages.get(CampaignQuest.class, reached(FALSE_TRAIL) ? "delivery_return" : "delivery_out");
	}

	public static int[] mainJournalQuests() {
		return IntStream.concat(IntStream.of(1), Arrays.stream(QUESTS)).toArray();
	}

	public static int[] sideJournalQuests() {
		return IntStream.rangeClosed(2, LAST_JOURNAL_QUEST)
				.filter(quest -> quest <= 4 || CampaignSideQuest.get(quest) != null)
				.toArray();
	}

	public static String journalTitle(int quest, boolean finished) {
		if (!finished) {
			int stage = Arrays.binarySearch(QUESTS, quest);
			if (stage >= 0) {
				if (quest == 110 && reached(110)) stage = QUESTS.length;
				return Messages.get(CampaignQuest.class, "journal_title_" + stage);
			}
			int openingScene = quest + 1000;
			if (!startedSideQuests.contains(quest)
					&& (startedSideQuests.contains(openingScene) || reached(openingScene))) {
				return sceneText(openingScene, "name");
			}
		}
		switch (quest) {
			case 1: return Messages.get(CampaignQuest.class, "prologue_title");
			case 5: return Messages.get(CampaignQuest.class, "amulet_title");
			case 9: return Messages.get(Notes.Landmark.class, "pie_delivery");
			default: return sceneText(quest, "name");
		}
	}

	public static boolean journalCompleted(int quest) {
		return quest == 110 ? epilogue : reached(quest);
	}

	public static String completedJournalText(int quest) {
		switch (quest) {
			case 1: return Messages.get(CampaignQuest.class, "prologue_done");
			case 5: return Messages.get(CampaignQuest.class, "amulet_done");
			case 110: return sceneText(quest, "done") + "\n\n" + journal();
			default: return sceneText(quest, "done");
		}
	}

	// A null entry is still hidden in the journal.
	public static String activeJournalText(int quest) {
		if (quest == 1) {
			boolean hasAmulet = Dungeon.hero.belongings.getItem(YendorAmulet.class) != null;
			return Messages.get(CampaignQuest.class, hasAmulet ? "prologue_carried" : "prologue_active");
		}
		if (quest == 110 && reached(110)) return journal();
		if (completed < QUESTS.length && QUESTS[completed] == quest && reached(1)) return journal();

		switch (quest) {
			case 2:
				return villageNote(Notes.Landmark.GRANDMOTHER_BERRIES, Notes.Landmark.GRANDMOTHER_FLOUR,
						Notes.Landmark.GRANDMOTHER);
			case 3:
				return villageNote(Notes.Landmark.HANDCART);
			case 4:
				return villageNote(Notes.Landmark.HAY_REWARD, Notes.Landmark.HAY_FIELD);
			case 9:
				return Notes.contains(Notes.Landmark.PIE_DELIVERY, -2)
						|| Notes.contains(Notes.Landmark.PIE_DELIVERY, -1) ? deliveryNote() : null;
		}

		if (startedSideQuests.contains(quest)) return sceneText(quest, "desc");
		// The opening scene for a multi-stage side quest uses the same ID plus 1000.
		int openingScene = quest + 1000;
		if (reached(openingScene)) return sceneText(openingScene, "done");
		if (startedSideQuests.contains(openingScene)) return sceneText(openingScene, "desc");
		return null;
	}

	private static String villageNote(Notes.Landmark... landmarks) {
		for (Notes.Landmark landmark : landmarks) {
			if (Notes.contains(landmark, 0)) return new Notes.LandmarkRecord(landmark, 0).desc();
		}
		return null;
	}

	private static String sceneText(int quest, String key) {
		return Messages.get(CampaignNPC.class, "q" + quest + "_" + key);
	}

	public static void reset() {
		completed = 0;
		woodcutterHint = false;
		completedSideQuests.clear();
		startedSideQuests.clear();
		epilogue = false;
	}

	public static void storeInBundle(Bundle bundle) {
		bundle.put(STAGE, completed == 0 ? 0 : QUESTS[completed - 1]);
		bundle.put(WOODCUTTER_HINT, woodcutterHint);
		bundle.put("side_quests", completedSideQuests.stream().mapToInt(Integer::intValue).toArray());
		bundle.put(STARTED_SIDE_QUESTS, startedSideQuests.stream().mapToInt(Integer::intValue).toArray());
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
		startedSideQuests.clear();
		if (bundle.contains(STARTED_SIDE_QUESTS)) {
			for (int quest : bundle.getIntArray(STARTED_SIDE_QUESTS)) startedSideQuests.add(quest);
		}
		epilogue = bundle.getBoolean("epilogue");
	}
}

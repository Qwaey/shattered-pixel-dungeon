/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.watabou.utils.PathFinder;

import java.util.HashMap;
import java.util.function.Supplier;

final class CampaignSideQuest {

	private enum Reward {
		NONE(null),
		FOOD(Food::new),
		HEAL(() -> new PotionOfHealing().identify()),
		HEAL2(() -> new PotionOfHealing().quantity(2).identify()),
		HEAL_DEW2(() -> new PotionOfHealing().identify(), 2),
		DEW(null, 1), DEW2(null, 2), DEW3(null, 3),
		TORCH(Torch::new),
		HASTE(() -> new PotionOfHaste().identify()),
		PURITY(() -> new PotionOfPurity().identify()),
		INVIS(() -> new PotionOfInvisibility().identify()),
		LEVITATION(() -> new PotionOfLevitation().identify()),
		FROST(() -> new PotionOfFrost().identify()),
		PARAGAS(() -> new PotionOfParalyticGas().identify()),
		SUNGRASS(Sungrass.Seed::new),
		EARTHROOT(Earthroot.Seed::new),
		IDENTIFY(() -> new ScrollOfIdentify().identify()),
		MAPPING(() -> new ScrollOfMagicMapping().identify()),
		UNCURSE(() -> new ScrollOfRemoveCurse().identify()),
		TELEPORT(() -> new ScrollOfTeleportation().identify()),
		INTUITION(StoneOfIntuition::new),
		DEEP_SLEEP(StoneOfDeepSleep::new),
		DISARM(StoneOfBlink::new),
		FLOCK(StoneOfFlock::new),
		DART2(() -> new Dart().quantity(2));

		private final Supplier<Item> item;
		private final int dew;

		Reward(Supplier<Item> item) {
			this(item, 0);
		}

		Reward(Supplier<Item> item, int dew) {
			this.item = item;
			this.dew = dew;
		}
	}

	private static final HashMap<Integer, CampaignSideQuest> QUESTS = new HashMap<>();

	static {
		add(CampaignQuest.FALSE_TRAIL, Reward.NONE, 6);
		add(9, Reward.HEAL2, CampaignQuest.FALSE_TRAIL);
		add(10, Reward.PURITY, 9);
		add(21, Reward.FOOD, 8);
		add(23, Reward.TORCH, 16);
		add(25, Reward.SUNGRASS, 16);
		add(26, Reward.FOOD, 16);
		add(27, Reward.NONE, 16);
		add(29, Reward.INVIS, 23);
		add(43, Reward.HASTE, 32);
		add(56, Reward.DEEP_SLEEP, 46);
		add(66, Reward.FOOD, 50);
		add(72, Reward.DEW, 61);
		add(82, Reward.SUNGRASS, 76);
		add(113, Reward.HEAL2, 106);
		add(114, Reward.NONE, 113);

		add(11, Reward.FOOD, 6);
		add(12, Reward.HEAL, 11);
		add(13, Reward.HEAL2, 7);
		add(1014, Reward.NONE, 6);
		add(14, Reward.TORCH, 1014);
		add(15, Reward.DEW, 6);
		add(1022, Reward.NONE, 16);
		add(22, Reward.NONE, 1022);
		add(24, Reward.DEW3, 18);
		add(1028, Reward.NONE, 18);
		add(28, Reward.IDENTIFY, 1028);
		add(1030, Reward.NONE, 22);
		add(30, Reward.FOOD, 1030);
		add(36, Reward.FOOD, 31);
		add(37, Reward.PURITY, 31);
		add(38, Reward.IDENTIFY, 31);
		add(39, Reward.NONE, 31);
		add(40, Reward.EARTHROOT, 31);
		add(41, Reward.UNCURSE, 37);
		add(42, Reward.TORCH, 31);
		add(44, Reward.MAPPING, 33);
		add(45, Reward.PARAGAS, 44, 38);
		add(51, Reward.DEW2);
		add(1052, Reward.NONE, 51, 46);
		add(52, Reward.FOOD, 1052);
		add(53, Reward.NONE, 46);
		add(54, Reward.FOOD);
		add(55, Reward.HEAL_DEW2, 54);
		add(57, Reward.NONE, 48);
		add(58, Reward.LEVITATION, 48);
		add(1059, Reward.NONE, 47);
		add(59, Reward.DISARM, 1059);
		add(1060, Reward.NONE, 50);
		add(60, Reward.DEW, 1060);
		add(1067, Reward.NONE, 61);
		add(67, Reward.DART2, 1067);
		add(1068, Reward.NONE, 61);
		add(68, Reward.SUNGRASS, 1068);
		add(69, Reward.INTUITION);
		add(70, Reward.PURITY, 69);
		add(1071, Reward.NONE, 61);
		add(71, Reward.FOOD, 1071);
		add(73, Reward.FLOCK, 72);
		add(1074, Reward.NONE, 63);
		add(74, Reward.FROST, 1074);
		add(75, Reward.HEAL, 65);
		add(81, Reward.FOOD);
		add(1083, Reward.NONE, 76);
		add(83, Reward.DART2, 1083);
		add(84, Reward.PURITY, 76);
		add(85, Reward.FLOCK);
		add(86, Reward.NONE, 85);
		add(1087, Reward.NONE, 77);
		add(87, Reward.TORCH, 1087);
		add(88, Reward.FOOD);
		add(1089, Reward.NONE, 77);
		add(89, Reward.HASTE, 1089);
		add(90, Reward.IDENTIFY, 80);
		add(96, Reward.HEAL, 87, 91);
		add(97, Reward.FOOD, 91);
		add(98, Reward.FOOD, 91);
		add(99, Reward.IDENTIFY, 91);
		add(100, Reward.NONE, 91);
		add(1101, Reward.NONE, 100);
		add(101, Reward.TELEPORT, 1101);
		add(1102, Reward.NONE, 93);
		add(102, Reward.NONE, 1102);
		add(103, Reward.INVIS, 93);
		add(104, Reward.FOOD, 95);
		add(1105, Reward.NONE, 91);
		add(105, Reward.EARTHROOT, 1105);
		add(1111, Reward.NONE, 106);
		add(111, Reward.HEAL, 1111);
		add(112, Reward.FOOD, 106);
		add(115, Reward.PURITY, 106);
		add(1116, Reward.NONE, 107);
		add(116, Reward.PURITY, 1116);
		add(1117, Reward.NONE, 106);
		add(117, Reward.FOOD, 1117);
		add(1118, Reward.NONE, 108);
		add(118, Reward.HEAL, 1118);
		add(119, Reward.HEAL, 112, 117);
		add(1120, Reward.NONE, 109);
		add(120, Reward.PURITY, 1120);
	}

	private final Reward reward;
	private final int[] prerequisites;

	private CampaignSideQuest(Reward reward, int[] prerequisites) {
		this.reward = reward;
		this.prerequisites = prerequisites;
	}

	private static void add(int id, Reward reward, int... prerequisites) {
		QUESTS.put(id, new CampaignSideQuest(reward, prerequisites));
	}

	static CampaignSideQuest get(int id) {
		return QUESTS.get(id);
	}

	boolean available() {
		for (int quest : prerequisites) {
			if (!CampaignQuest.reached(quest)) return false;
		}
		return true;
	}

	void award() {
		if (reward.item != null) CampaignQuest.give(reward.item.get());
		if (reward.dew == 0) return;

		Waterskin skin = Dungeon.hero.belongings.getItem(Waterskin.class);
		int toDrop = reward.dew;
		while (skin != null && skin.freeCapacity() > 0 && toDrop > 0) {
			skin.collectDew(new Dewdrop());
			toDrop--;
		}
		for (int offset : PathFinder.NEIGHBOURS9) {
			if (toDrop == 0) break;
			int cell = Dungeon.hero.pos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]) {
				Dungeon.level.drop(new Dewdrop(), cell).sprite.drop();
				toDrop--;
			}
		}
	}
}

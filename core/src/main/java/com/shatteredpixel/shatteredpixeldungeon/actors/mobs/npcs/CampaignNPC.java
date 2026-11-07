/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.PieParcel;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.YendorAmulet;
import com.shatteredpixel.shatteredpixeldungeon.levels.AmbushLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.ArchiveLevels;
import com.shatteredpixel.shatteredpixeldungeon.levels.RoadsideLevels;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CampaignMarkerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CampaignPersonSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

/** A stationary character or fixture for the first road quests. */
public class CampaignNPC extends NPC {

	private int quest;

	public CampaignNPC() {
		properties.add(Property.IMMOVABLE);
	}

	public CampaignNPC(int quest) {
		this();
		setQuest(quest);
	}

	private void setQuest(int quest) {
		this.quest = quest;
		switch (quest) {
			case 9: case 16: case 17: case 31: case 33: case 34:
			case 46: case 61: case 76: case 91: case 106:
				spriteClass = CampaignPersonSprite.class;
				break;
			default:
				spriteClass = CampaignMarkerSprite.class;
		}
	}

	public int quest() {
		return quest;
	}

	private String line(String key) {
		return Messages.get(this, "q" + quest + "_" + key);
	}

	@Override
	public String name() {
		return line("name");
	}

	@Override
	public String description() {
		return line("desc");
	}

	@Override
	public boolean interact(Char c) {
		if (c != Dungeon.hero) return true;
		sprite.turnTo(pos, c.pos);
		Game.runOnRenderThread(() -> showScene((Hero) c));
		return true;
	}

	private void showScene(Hero hero) {
		String description = quest == 43 && CampaignQuest.reached(110) ? line("after_end") : line("desc");
		if (quest == 106 && CampaignQuest.reached(110)) {
			CampaignQuest.finishJourney();
			GameScene.show(new WndQuest(this, Messages.get(this, "epilogue")));
		} else if (CampaignQuest.reached(quest)) {
			GameScene.show(new WndQuest(this, line("done")));
		} else if (quest == CampaignQuest.FALSE_TRAIL && hero.belongings.getItem(PieParcel.class) == null) {
			GameScene.show(new WndQuest(this, line("quiet")));
		} else if (!CampaignQuest.canComplete(quest) || quest == 6 && !CampaignQuest.knowsWoodcutterHint()) {
			GameScene.show(new WndQuest(this, description));
		} else if (quest == CampaignQuest.FALSE_TRAIL) {
			GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()), description,
					line("action"), line("follow"), Messages.get(this, "later")) {
				@Override
				protected void onSelect(int index) {
					if (index == 1) {
						if (!CampaignQuest.canComplete(quest) || !(Dungeon.level instanceof RoadsideLevels.OldRoad)) return;
						if (!((RoadsideLevels.OldRoad) Dungeon.level).revealGreyMuzzle()) {
							GameScene.show(new WndQuest(CampaignNPC.this, line("blocked")));
							return;
						}
					}
					if (index == 0 || index == 1) resolveScene(hero);
				}
			});
		} else {
			GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()), description,
					line("action"), Messages.get(this, "later")) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) resolveScene(hero);
				}
			});
		}
	}

	private void resolveScene(Hero hero) {
		if (!CampaignQuest.canComplete(quest) || !hero.isAlive() || !canInteract(hero)) return;
		if (quest == 56 && Dungeon.level instanceof ArchiveLevels.SheepValley) {
			if (((ArchiveLevels.SheepValley) Dungeon.level).startSnapper()) {
				GameScene.show(new WndQuest(this, line("appeared")));
				hero.spendAndNext(TICK);
			}
			return;
		}
		if (isAmbush() && !(quest == 43 && CampaignQuest.reached(110))) {
			if (Dungeon.level instanceof AmbushLevel && ((AmbushLevel) Dungeon.level).startAmbush(hero, quest)) {
				hero.spendAndNext(TICK);
			} else {
				GameScene.show(new WndQuest(this, Messages.get(this, "ambush_wait")));
			}
			return;
		}
		if (quest == 110) {
			YendorAmulet amulet = hero.belongings.getItem(YendorAmulet.class);
			if (amulet == null) return;
			amulet.detachAll(hero.belongings.backpack);
		}
		if (quest == 9) {
			PieParcel parcel = hero.belongings.getItem(PieParcel.class);
			if (parcel == null) return;
			parcel.detachAll(hero.belongings.backpack);
		}
		if (CampaignQuest.complete(quest)) {
			GameScene.show(new WndQuest(this, line("done")));
			hero.spendAndNext(TICK);
		}
	}

	private boolean isAmbush() {
		switch (quest) {
			case 19: case 43: case 48: case 64: case 79: case 93: case 108:
				return true;
			default:
				return false;
		}
	}

	@Override
	public int defenseSkill(Char enemy) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage(int dmg, Object src) {
	}

	@Override
	public boolean add(Buff buff) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("quest", quest);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		setQuest(bundle.getInt("quest"));
	}
}

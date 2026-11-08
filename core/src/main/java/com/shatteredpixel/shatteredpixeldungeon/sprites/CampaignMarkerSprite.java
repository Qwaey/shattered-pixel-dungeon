/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.CampaignQuest;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

public class CampaignMarkerSprite extends MobSprite {

	private static final String FIXTURES = "sprites/campaign_fixtures.png";

	// Frame order in campaign_fixtures.png.
	private enum Fixture {
		SIGNPOST, GATE, PUMP, LEVER,
		BELL, ROPE, LANTERN, HANDPRINTS,
		FOOTPRINTS, CRATE, LENS, WINCH,
		STEPS, ANCHOR, SAIL, CRADLE
	}

	private boolean groundTrace;

	public CampaignMarkerSprite(int quest) {
		switch (quest) {
			case 6: case 19: case 21: case 50: case 58: case 63:
			case 102: case 1067: case 1102:
				showFixture(Fixture.SIGNPOST);
				break;
			case 8: case 56: case 96: case 114:
				showFixture(Fixture.GATE);
				break;
			case 18:
				showFixture(Fixture.PUMP);
				break;
			case 7: case 20: case 47: case 62: case 86:
				showFixture(Fixture.LEVER);
				break;
			case 55: case 72: case 118:
				showFixture(Fixture.BELL);
				break;
			case 13: case 14: case 27: case 29: case 39: case 57: case 1022:
				showFixture(Fixture.ROPE);
				break;
			case 23: case 79: case 90: case 108: case 1087:
				showFixture(Fixture.LANTERN);
				break;
			case 43: case 93: case 1120:
				showFixture(Fixture.HANDPRINTS);
				break;
			case 10: case 48: case 64: case 110:
				showFixture(Fixture.FOOTPRINTS);
				break;
			case 15: case 41: case 42: case 66: case 81:
				showFixture(Fixture.CRATE);
				break;
			case 77: case 78: case 80: case 111: case 1089: case 1111:
				showFixture(Fixture.LENS);
				break;
			case 49: case 92:
				showFixture(Fixture.WINCH);
				break;
			case 65: case 109:
				showFixture(Fixture.STEPS);
				break;
			case 107: case 1083:
				showFixture(Fixture.ANCHOR);
				break;
			case 82: case 1116:
				showFixture(Fixture.SAIL);
				break;
			case 94:
				showFixture(Fixture.CRADLE);
				break;
			case 12: case 85:
				showScenery("environment/furniture/firewood.png", 1);
				break;
			case 53:
				showScenery("environment/exterior/handcart.png", 0.5f);
				break;
			case 100:
				showScenery("environment/exterior/well.png", 0.5f);
				break;
			case 112:
				showScenery("environment/furniture/table.png", 1);
				break;
			case 25: case 1014:
				showItem(ItemSpriteSheet.ARTIFACT_HORN1);
				break;
			case 26:
				showItem(ItemSpriteSheet.RATION);
				break;
			case 32: case 115: case 1101:
				showItem(ItemSpriteSheet.GEO_BOULDER);
				break;
			case 35: case 45: case 117: case 1060:
				showItem(ItemSpriteSheet.ARTIFACT_SPELLBOOK);
				break;
			case 38: case 99: case 1028:
				showItem(ItemSpriteSheet.SEAL_SHARD);
				break;
			case 40: case 84:
				showItem(ItemSpriteSheet.PETRIFIED_SEED);
				break;
			case 44:
				showItem(ItemSpriteSheet.GUIDE_PAGE);
				break;
			case 54: case 113:
				showItem(ItemSpriteSheet.FLOUR_SACK);
				break;
			case 60:
				showItem(ItemSpriteSheet.TORN_PAGE);
				break;
			case 70:
				showItem(ItemSpriteSheet.IRON_KEY);
				break;
			case 88:
				showItem(ItemSpriteSheet.STEAMED_FISH);
				break;
			case 98:
				showItem(ItemSpriteSheet.HONEYPOT);
				break;
			case 103: case 1059:
				showItem(ItemSpriteSheet.WATER_BUCKET);
				break;
			case 119: case 1074:
				showItem(ItemSpriteSheet.TOKEN);
				break;
			case CampaignQuest.FALSE_TRAIL:
				showItem(ItemSpriteSheet.ARTIFACT_CLOAK);
				break;
			case 1052: case 1117:
				showItem(ItemSpriteSheet.SEAL);
				break;
			case 1068:
				showItem(ItemSpriteSheet.CLOAK_SCRAP);
				break;
			case 1071:
				showItem(ItemSpriteSheet.ARTIFACT_BOOTS);
				break;
			case 1105:
				showItem(ItemSpriteSheet.STONE_DETECT);
				break;
			case 1118:
				showItem(ItemSpriteSheet.BROKEN_HILT);
				break;
			default:
				showItem(ItemSpriteSheet.SOMETHING);
		}
	}

	private void showFixture(Fixture fixture) {
		groundTrace = fixture == Fixture.FOOTPRINTS;
		if (groundTrace) perspectiveRaise = 0;
		texture(FIXTURES);
		showFrame(new TextureFilm(texture, 16, 16).get(fixture.ordinal()));
	}

	private void showScenery(String asset, float size) {
		texture(asset);
		scale.set(size);
		showFrame(new TextureFilm(texture).get(null));
	}

	private void showItem(int item) {
		texture(Assets.Sprites.ITEMS);
		showFrame(ItemSpriteSheet.film.get(item));
	}

	private void showFrame(RectF frame) {
		idle = new Animation(1, true);
		idle.frames(frame);
		run = idle.clone();
		attack = idle.clone();
		die = idle.clone();
		idle();
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		if (groundTrace) renderShadow = false;
	}

	@Override
	public void turnTo(int from, int to) {
	}
}

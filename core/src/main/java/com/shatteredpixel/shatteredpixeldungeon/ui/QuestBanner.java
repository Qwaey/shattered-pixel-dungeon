/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

/** A temporary quest notice with no input handlers. */
public class QuestBanner extends Component {

	private static final float ENTER_TIME = 0.32f;
	private static final float HOLD_TIME = 1.7f;
	private static final float EXIT_TIME = 0.35f;
	private static final float PADDING = 9;

	private final NinePatch background;
	private final Image icon;
	private final RenderedTextBlock title;
	private final RenderedTextBlock status;
	private float elapsed;

	public QuestBanner(Camera camera, String questName, String label, boolean completed) {
		this.camera = camera;
		background = Chrome.get(Chrome.Type.TOAST_TR_HEAVY);
		add(background);

		icon = (completed ? Document.HALLS_KING : Document.CITY_WARLOCK).pageSprite();
		add(icon);

		int textWidth = (int) Math.min(172, camera.width - PADDING * 4);
		title = PixelScene.renderTextBlock(11);
		title.text(questName, textWidth);
		title.align(RenderedTextBlock.CENTER_ALIGN);
		add(title);

		status = PixelScene.renderTextBlock(8);
		status.text(label, textWidth);
		status.align(RenderedTextBlock.CENTER_ALIGN);
		status.hardlight(completed ? 0xA5D783 : 0xF0D794);
		add(status);

		setSize(Math.max(88, Math.max(title.width(), status.width()) + 2 * PADDING),
				icon.height() + title.height() + status.height() + 2 * PADDING + 7);
		advance(0);
	}

	@Override
	protected void layout() {
		background.x = x;
		background.y = y;
		background.size(width, height);
		icon.x = PixelScene.align(camera, x + (width - icon.width()) / 2);
		icon.y = PixelScene.align(camera, y + PADDING);
		title.setPos(PixelScene.align(camera, x + (width - title.width()) / 2),
				PixelScene.align(camera, icon.y + icon.height() + 3));
		status.setPos(PixelScene.align(camera, x + (width - status.width()) / 2),
				PixelScene.align(camera, title.bottom() + 4));
	}

	public boolean advance(float delta) {
		elapsed += delta;
		float leaving = Math.max(0, (elapsed - ENTER_TIME - HOLD_TIME) / EXIT_TIME);
		if (leaving >= 1) return false;

		float entering = Math.max(0, 1 - elapsed / ENTER_TIME);
		float opacity = 1 - entering * entering - leaving;
		float centerX = (camera.width - width) / 2;
		float centerY = (camera.height - height) / 2;
		float offset = 8 * entering * entering - 4 * leaving;
		float px = PixelScene.align(camera, centerX);
		float py = PixelScene.align(camera, centerY + offset);
		if (px != x || py != y) setPos(px, py);
		background.alpha(opacity);
		icon.alpha(opacity);
		title.alpha(opacity);
		status.alpha(opacity);
		return true;
	}
}

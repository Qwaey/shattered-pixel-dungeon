/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 * Licensed under the GNU General Public License, version 3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.levels.OutdoorLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.Visual;

import java.nio.Buffer;
import java.nio.FloatBuffer;

// A soft black overlay, using the engine's two-color ramp rather than an art asset.
public class PassageFade extends Visual {

	public static final int DEPTH = 7;
	public static final int WALL_MARGIN = 3;
	private static final float CORE_MARGIN = 1f;
	private static final float FEATHER = 2f;
	private static final int GRID_STEP = 4;
	private static final int[] RAMP = {0x00000000, 0xFF000000};

	private final SmartTexture texture;
	private final FloatBuffer vertices;
	private final int quads;
	private final boolean vertical, nearEdge;
	private Vertexbuffer buffer;

	public PassageFade(OutdoorLevel level, LevelTransition transition) {
		super(0, 0, 0, 0);
		texture = TextureCache.createGradient(RAMP);
		vertical = transition.top <= 2 || transition.bottom >= level.height() - 3;
		nearEdge = vertical ? transition.top <= 2 : transition.left <= 2;
		float size = DungeonTilemap.SIZE;
		float margin = CORE_MARGIN + FEATHER;
		if (vertical) {
			x = (transition.left - margin) * size;
			y = nearEdge ? 0 : (level.height() - DEPTH) * size;
			width = (transition.width() + 2 * margin) * size;
			height = DEPTH * size;
		} else {
			x = nearEdge ? 0 : (level.width() - DEPTH) * size;
			y = (transition.top - margin) * size;
			width = DEPTH * size;
			height = (transition.height() + 2 * margin) * size;
		}

		int columns = (int) Math.ceil(width / GRID_STEP);
		int rows = (int) Math.ceil(height / GRID_STEP);
		quads = columns * rows;
		vertices = Quad.createSet(quads);
		for (int row = 0; row < rows; row++) {
			float top = height * row / rows, bottom = height * (row + 1) / rows;
			for (int col = 0; col < columns; col++) {
				float left = width * col / columns, right = width * (col + 1) / columns;
				vertex(left, top);
				vertex(right, top);
				vertex(right, bottom);
				vertex(left, bottom);
			}
		}
		((Buffer) vertices).position(0);
		active = false;
	}

	private void vertex(float x, float y) {
		float distance = vertical ? y : x;
		if (!nearEdge) distance = DEPTH * DungeonTilemap.SIZE - distance;
		// The outermost row is fully dark; the approach fades in with zero slope.
		float depth = smooth((DEPTH * DungeonTilemap.SIZE - distance)
				/ ((DEPTH - 1) * DungeonTilemap.SIZE));
		float across = vertical ? x : y;
		float span = vertical ? width : height;
		float feather = FEATHER * DungeonTilemap.SIZE;
		float alpha = depth * smooth(across / feather) * smooth((span - across) / feather);
		// The ramp's texel centers map precisely to opacity 0 and 1.
		vertices.put(x).put(y).put(0.25f + 0.5f * alpha).put(0.5f);
	}

	private static float smooth(float value) {
		value = Math.max(0, Math.min(1, value));
		return value * value * (3 - 2 * value);
	}

	@Override
	public void draw() {
		super.draw();
		if (buffer == null) buffer = new Vertexbuffer(vertices);
		NoosaScript script = NoosaScriptNoLighting.get();
		texture.bind();
		script.camera(camera());
		script.uModel.valueM4(matrix);
		script.drawQuadSet(buffer, quads, 0);
	}

	@Override
	public void destroy() {
		if (buffer != null) buffer.delete();
		super.destroy();
	}
}

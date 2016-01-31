package com.ggj16.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.screen.GameScreen;

/**
 * Created by st on 1/31/16.
 */
public class Fog {

    private static final String FOG_EFFECT = "fog.p";

    private GameScreen screen;
    private ParticleEffect[] effects;
    private ParticleEffect left, top, right, bottom;

    public static final ParticleEffect buildFogEffect() {
        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal(FOG_EFFECT), ImageCache.getAtlas());
        return effect;
    }

    public Fog(GameScreen screen) {
        this.screen = screen;

        Floor floor = screen.getFloor();

        left = buildFogEffect();
        top = buildFogEffect();
        right = buildFogEffect();
        bottom = buildFogEffect();
        effects = new ParticleEffect[] { left, top, right, bottom };
        rescaleAllEffects();

        for (ParticleEffect effect: effects) {
            effect.start();
        }
    }

    public void draw(Batch batch, float delta) {
        left.draw(batch, delta);
        top.draw(batch, delta);
        right.draw(batch, delta);
        bottom.draw(batch, delta);
    }

    public void rescaleAllEffects() {
        Floor floor = screen.getFloor();
        scaleEffect(left, 0, 0, floor.getVisibleLeft(), floor.getVisibleTop());
        scaleEffect(top, 0, floor.getVisibleTop(), floor.getVisibleRight(),
                floor.getHeightInPixels() - floor.getVisibleTop());
        scaleEffect(right, floor.getVisibleRight(), floor.getVisibleBottom(),
                floor.getWidthInPixels() - floor.getVisibleRight(),
                floor.getHeightInPixels() - floor.getVisibleBottom());
        scaleEffect(bottom, floor.getVisibleLeft(), 0,
                floor.getWidthInPixels() - floor.getVisibleLeft(),
                floor.getVisibleBottom());
    }

    private void scaleEffect(ParticleEffect effect, float x, float y, float width, float height) {
        ParticleEmitter emitter = effect.getEmitters().get(0);
        emitter.setPosition(x + width / 2, y + height / 2);
        emitter.getSpawnWidth().setHigh(width);
        emitter.getSpawnWidth().setLow(width);
        emitter.getSpawnHeight().setHigh(height);
        emitter.getSpawnHeight().setLow(height);
    }

}

package com.ggj16.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.data.SkinCache;
import com.ggj16.game.data.SoundCache;
import com.ggj16.game.screen.MenuScreen;

public class GGJGame extends Game {

	@Override
	public void create () {
		ImageCache.load();
		SkinCache.load();
		SoundCache.load();
		Gdx.input.setCatchBackKey(true);
		setMenuScreen();
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose() {
		Screen screen = getScreen();
		if (screen != null) screen.dispose();
		super.dispose();
	}

	@Override
	public void setScreen(Screen screen) {
		Screen old = getScreen();
		super.setScreen(screen);
		if (old != null) old.dispose();
	}

	public void setMenuScreen() {
		setScreen(new MenuScreen(this));
	}

	public void setGameScreen() {
		setScreen(new com.ggj16.game.screen.GameScreen(this));
	}

	public void exit() {
		Gdx.app.exit();
	}
}

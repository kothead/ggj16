package com.ggj16.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.ggj16.game.GGJGame;
import com.ggj16.game.data.Configuration;

import java.io.File;
import java.io.FileFilter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		packAssets();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.width = Configuration.GAME_HEIGHT;
		config.height = Configuration.GAME_WIDTH;
		config.samples = 8;
		new LwjglApplication(new GGJGame(), config);
	}

	private static void packAssets() {
		File dir = new File("../../images");

		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.edgePadding = true;
		settings.duplicatePadding = true;
		settings.paddingX = 4;
		settings.paddingY = 4;

		for (File childDir: dir.listFiles(filter)) {
			TexturePacker.process(settings, childDir.getPath(), "image", childDir.getName());
		}
	}
}

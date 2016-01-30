package com.ggj16.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegraph;

import java.util.Random;

public class Utils {

    public static boolean isLandscape() {
        return Gdx.input.getNativeOrientation() == Input.Orientation.Landscape
                && (Gdx.input.getRotation() == 0 || Gdx.input.getRotation() == 180)
                || Gdx.input.getNativeOrientation() == Input.Orientation.Portrait
                && (Gdx.input.getRotation() == 90 || Gdx.input.getRotation() == 270);
    }

    public static void sendMessage(Telegraph sender, Telegraph receiver, int messageType) {
        MessageManager.getInstance().dispatchMessage(
                0.0f,
                sender,
                receiver,
                messageType);
    }

    public static int randInt(int min, int max) {
        Random rand = new Random(100);

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}

package com.ggj16.game.processor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.ggj16.game.state.MessageType;
import com.ggj16.game.util.Utils;

/**
 * Created by kettricken on 30.01.2016.
 */
public class InputProcessor extends InputAdapter {

    Telegraph telegraph;

    public InputProcessor(Telegraph telegraph) {
        this.telegraph = telegraph;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Utils.sendMessage(telegraph, telegraph, MessageType.TAP);
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode)
        {
            case Input.Keys.ESCAPE:
            case Input.Keys.BACK:
                Utils.sendMessage(telegraph, telegraph, MessageType.BACK_PRESS);
                break;
        }
        return true;
    }
}

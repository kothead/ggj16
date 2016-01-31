package com.ggj16.game.processor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.data.SkinCache;
import com.ggj16.game.screen.GameScreen;
import com.ggj16.game.state.MessageType;
import com.ggj16.game.util.Utils;

/**
 * Created by kettricken on 30.01.2016.
 */
public class ViewProcessor {

    private final GameScreen gameScreen;
    private final Stage stage;

    private Table pauseTable = new Table();
    private Table gameOverTable = new Table();
    private Table waveTable = new Table();

    private Image gameOver;
    private Image restart;
    private Label wave;

    private boolean restartEnabled = false;

    public ViewProcessor(GameScreen gameScreen, Stage stage) {
        this.stage = stage;
        this.gameScreen = gameScreen;
    }

    public void initViews() {
        gameOverTable.setFillParent(true);
        pauseTable.setFillParent(true);
        waveTable.setFillParent(true);
        gameOver = new Image(ImageCache.getTexture("game-over"));
        Image pause = new Image(ImageCache.getTexture("pause"));
        Image continueBtn = new Image(ImageCache.getTexture("continue"));
        Image exitBtn = new Image(ImageCache.getTexture("exit"));
        restart = new Image(ImageCache.getTexture("tap-to-restart"));
        wave = new Label(String.format("New Wave %d", gameScreen.getWaveCount()), SkinCache.getDefaultSkin());
        wave.getStyle().font.getData().scale(0.7f);
        wave.getStyle().fontColor = new Color(1f, 0.6f, 0.1f, 1);
        gameOverTable.add(gameOver);
        gameOverTable.row();
        gameOverTable.add(restart);
        pauseTable.add(pause).colspan(2).center().spaceBottom(50);
        pauseTable.row();
        pauseTable.add(continueBtn).spaceRight(50);
        pauseTable.add(exitBtn);
        waveTable.add(wave).center();

        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Utils.sendMessage(gameScreen, gameScreen, MessageType.BACK_PRESS);
            }
        });

        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.getGame().exit();
            }
        });

        gameOverTable.addAction(Actions.alpha(0));
        stage.addActor(gameOverTable);
    }

    public void showPauseTable() {
        stage.addActor(pauseTable);
        pauseTable.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.15f), new Action() {
            @Override
            public boolean act(float delta) {
                pauseTable.setTouchable(Touchable.enabled);
                return true;
            }
        }));
    }

    public void hidePauseTable() {
        Action action = new Action() {
            @Override
            public boolean act(float delta) {
                pauseTable.getParent().removeActor(pauseTable);
                return true;
            }
        };
        pauseTable.setTouchable(Touchable.disabled);
        pauseTable.addAction(Actions.sequence(Actions.fadeOut(0.15f), action));
    }

    public void showGameOverTable() {
        restart.addAction(Actions.alpha(0));
        gameOver.addAction(Actions.alpha(0));
        gameOverTable.addAction(Actions.alpha(1));
        gameOver.addAction(Actions.sequence(Actions.fadeIn(0.20f), new Action() {
            @Override
            public boolean act(float delta) {
                restart.addAction(Actions.alpha(1));
                restartEnabled = true;
                return true;
            }
        }));
    }

    public void hideGameOverTable() {
        gameOverTable.addAction(Actions.alpha(0));
        restartEnabled = false;
    }

    public void showWaveTable(Action action) {
        stage.addActor(waveTable);
        if (wave != null)
            wave.setText(String.format("NEW WAVE %d", gameScreen.getWaveCount()));
        waveTable.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.20f), action, Actions.delay(2f),
                Actions.fadeOut(0.20f)));
    }

    public boolean isRestartEnabled() {
        return restartEnabled;
    }
}

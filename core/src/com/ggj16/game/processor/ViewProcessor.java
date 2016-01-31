package com.ggj16.game.processor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
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
    private Label restart;

    private boolean restartEnabled = false;

    public ViewProcessor(GameScreen gameScreen, Stage stage) {
        this.stage = stage;
        this.gameScreen = gameScreen;
    }

    public void initViews() {
        gameOverTable.setFillParent(true);
        pauseTable.setFillParent(true);
        waveTable.setFillParent(true);
        gameOver = new Image(ImageCache.getTexture("game_over"));
        Image pause = new Image(ImageCache.getTexture("pause"));
        Image continueBtn = new Image(ImageCache.getTexture("continue"));
        Image exitBtn = new Image(ImageCache.getTexture("exit"));
        restart = new Label("Tap to restart", SkinCache.getDefaultSkin());
        Image wave = new Image(ImageCache.getTexture("wave"));
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
                Gdx.app.log("Test", "GAME_OVER show rester");
                restart.addAction(Actions.alpha(1));
                restartEnabled = true;
                return true;
            }
        }));
        //gameOverTable.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.15f),));
    }

    public void hideGameOverTable() {
        gameOverTable.addAction(Actions.alpha(0));
        restartEnabled = false;
    }

    public void showWaveTable(Action action) {
        stage.addActor(waveTable);
        waveTable.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.20f), action, Actions.delay(0.5f),
                Actions.fadeOut(0.20f)));
    }

    public boolean isRestartEnabled() {
        return restartEnabled;
    }
}

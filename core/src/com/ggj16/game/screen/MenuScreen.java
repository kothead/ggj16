package com.ggj16.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ggj16.game.GGJGame;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.model.TiledSprite;

public class MenuScreen extends BaseScreen {

    private static final String TEXTURE_BACKGROUND = "floor-tile-v7";

    Stage stage;
    TiledSprite background;

    public MenuScreen(GGJGame game) {
        super(game);
        stage = new Stage(getViewport());
        background = new TiledSprite(ImageCache.getTexture(TEXTURE_BACKGROUND));
        background.setSize(getWorldWidth(), getWorldHeight());
        background.setPosition(0, 0);
    }

    @Override
    public void show() {
        super.show();

        Image title = new Image(new Texture(Gdx.files.internal("image/title.png")));
        final Image tap = new Image(ImageCache.getTexture("tap-to-start"));

        Table table = new Table();
        table.setFillParent(true);
        table.add(title);
        table.row();
        table.add(tap);

        stage.addActor(table);

        Action act = new Action(){
            @Override
            public boolean act(float delta) {
                tap.addAction(Actions.alpha(1));
                Gdx.input.setInputProcessor(new InputListener());
                return false;
            }
        };
        tap.addAction(Actions.sequence(Actions.alpha(0)));
        title.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1), act));
    }

    public void resize (int width, int height) {
        super.resize(width, height);
        stage.setViewport(getViewport());
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        batch().begin();
        background.draw(batch(), 0, 0);
        batch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    @Override
    protected void layoutViewsLandscape(int width, int height) {

    }

    @Override
    protected void layoutViewsPortrait(int width, int height) {

    }

    private class InputListener extends InputAdapter {

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            getGame().setGameScreen();
            return super.touchUp(screenX, screenY, pointer, button);
        }

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode)
            {
                case Input.Keys.ESCAPE:
                case Input.Keys.BACK:
                    Gdx.app.exit();
                    break;
            }
            return true;
        }
    }
}

package ru.geekbrains.sprite.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.screen.GameScreen;
import ru.geekbrains.sprite.menu.ScaledTouchUpButton;

public class StartNewGame extends ScaledTouchUpButton {

    private GameScreen gameScreen;

    public StartNewGame(TextureAtlas atlas, GameScreen gameScreen) {
        super(atlas.findRegion("button_new_game"));
        this.gameScreen = gameScreen;
        setHeightProportion(0.05f);
        setTop(-0.012f);
    }

    @Override
    public void action() {
        gameScreen.startNewGame();
    }
}

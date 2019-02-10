package ru.geekbrains.sprite.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Sprite;
import ru.geekbrains.math.Rect;

public class GameOver extends Sprite {
    private Vector2 v = new Vector2(0.0f, -0.1f);

    public GameOver(TextureAtlas atlas) {
        super(atlas.findRegion("message_game_over"));
    }

    @Override
    public void resize(Rect worldBounds) {
        setBottom(worldBounds.getTop());
        setHeightProportion(0.1f);
    }

    @Override
    public void update(float delta) {
        if(pos.y - getHalfHeight() > 0)
            pos.mulAdd(v, delta);
    }
}

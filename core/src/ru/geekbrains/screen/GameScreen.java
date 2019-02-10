package ru.geekbrains.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import ru.geekbrains.base.Base2DScreen;
import ru.geekbrains.math.Rect;
import ru.geekbrains.pool.BulletPool;
import ru.geekbrains.pool.EnemyPool;
import ru.geekbrains.pool.ExplosionPool;
import ru.geekbrains.sprite.Background;
import ru.geekbrains.sprite.Star;
import ru.geekbrains.sprite.game.Bullet;
import ru.geekbrains.sprite.game.Enemy;
import ru.geekbrains.sprite.game.GameOver;
import ru.geekbrains.sprite.game.MainShip;
import ru.geekbrains.sprite.game.StartNewGame;
import ru.geekbrains.utils.EnemyEmitter;
import ru.geekbrains.utils.Font;

public class GameScreen extends Base2DScreen {

    private static final String SCORE = "Score: ";
    private static final String HEALTH = "HP: ";
    private static final String LEVEL = "Level: ";

    private TextureAtlas atlas;
    private Texture bg;
    private Background background;
    private Star star[];
    private MainShip mainShip;
    private GameOver gameover;
    private StartNewGame startNewGame;

    private BulletPool bulletPool;
    private ExplosionPool explosionPool;
    private EnemyPool enemyPool;

    private EnemyEmitter enemyEmitter;

    private Font font;
    Texture healthBar;
    private StringBuilder sbScore = new StringBuilder();
    private StringBuilder sbHealth = new StringBuilder();
    private StringBuilder sbLevel = new StringBuilder();

    int score = 0;

    private Music music;

    @Override
    public void show() {
        super.show();
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        music.setLooping(true);
        music.setVolume(0.8f);
        music.play();
        bg = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(bg));
        atlas = new TextureAtlas("textures/mainAtlas.tpack");
        font = new Font("font/font.fnt", "font/font.png");
        font.setSize(0.02f);

        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 0, 0, 0.75f);
        pixmap.fillRectangle(0, 0, 64, 64);
        healthBar = new Texture(pixmap);
        pixmap.dispose();

        gameover = new GameOver(atlas);
        startNewGame = new StartNewGame(atlas, this);

        star = new Star[64];
        for (int i = 0; i < star.length; i++) {
            star[i] = new Star(atlas);
        }
        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(atlas);
        mainShip = new MainShip(atlas, bulletPool, explosionPool);
        enemyPool = new EnemyPool(bulletPool, worldBounds, explosionPool, mainShip);

        enemyEmitter = new EnemyEmitter(atlas, enemyPool, worldBounds);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        if (!mainShip.isDestroyed()) {
            checkCollisions();
        }
        deleteAllDestroyed();
        draw();
    }

    private void update(float delta) {
        for (Star aStar : star) {
            aStar.update(delta);
        }
        if (!mainShip.isDestroyed()) {
            mainShip.update(delta);
            enemyEmitter.generate(delta, score);
        } else {
            gameover.update(delta);
            startNewGame.update(delta);
        }

        bulletPool.updateActiveSprites(delta);
        explosionPool.updateActiveSprites(delta);
        enemyPool.updateActiveSprites(delta);
    }

    private void checkCollisions() {
        List<Enemy> enemyList = enemyPool.getActiveObjects();
        for (Enemy enemy : enemyList) {
            if (enemy.isDestroyed()) {
                continue;
            }
            float minDist = enemy.getHalfWidth() + mainShip.getHalfWidth();
            if (enemy.pos.dst2(mainShip.pos) < minDist * minDist) {
                enemy.destroy();
                mainShip.damage(enemy.getDamage());
                return;
            }
        }
        List<Bullet> bulletList = bulletPool.getActiveObjects();

        for (Bullet bullet : bulletList) {
            if (bullet.getOwner() == mainShip || bullet.isDestroyed()) {
                continue;
            }
            if (mainShip.isBulletCollision(bullet)) {
                mainShip.damage(bullet.getDamage());
                bullet.destroy();
            }
        }

        for (Enemy enemy : enemyList) {
            if (enemy.isDestroyed()) {
                continue;
            }
            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() != mainShip || bullet.isDestroyed()) {
                    continue;
                }
                if (enemy.isBulletCollision(bullet)) {
                    enemy.damage(mainShip.getDamage());
                    if (enemy.isDestroyed()) {
                        ++score;
                    }
                    bullet.destroy();
                }
            }
        }
    }

    private void deleteAllDestroyed() {
        bulletPool.freeAllDestroyedActiveSprites();
        explosionPool.freeAllDestroyedActiveSprites();
        enemyPool.freeAllDestroyedActiveSprites();
    }

    private void draw() {
        Gdx.gl.glClearColor(0.5f, 0.2f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);

        for (Star aStar : star) {
            aStar.draw(batch);
        }

        if (!mainShip.isDestroyed())
            mainShip.draw(batch);
        bulletPool.drawActiveSprites(batch);
        explosionPool.drawActiveSprites(batch);
        enemyPool.drawActiveSprites(batch);
        if (mainShip.isDestroyed()) {
            gameover.draw(batch);
            if (gameover.isStopped())
                startNewGame.draw(batch);
        }
        printInfo();
        batch.end();
    }

    public void printInfo() {
        sbScore.setLength(0);
        sbHealth.setLength(0);
        sbLevel.setLength(0);
        batch.draw(healthBar, -worldBounds.getHalfWidth() + (worldBounds.getWidth() - mainShip.getHealth() * worldBounds.getWidth() / 100.0f) / 2.0f, worldBounds.getTop() - 0.02f, mainShip.getHealth() * worldBounds.getWidth() / 100.0f, 0.04f);
        font.draw(batch, sbScore.append(SCORE).append(score), worldBounds.getLeft(), worldBounds.getTop());
        font.draw(batch, sbHealth.append(HEALTH).append(mainShip.getHealth()), worldBounds.pos.x, worldBounds.getTop(), Align.center);
        font.draw(batch, sbLevel.append(LEVEL).append(enemyEmitter.getLevel()), worldBounds.getRight(), worldBounds.getTop(), Align.right);
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (Star aStar : star) {
            aStar.resize(worldBounds);
        }
        gameover.resize(worldBounds);
        mainShip.resize(worldBounds);
    }

    @Override
    public void dispose() {
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        explosionPool.dispose();
        enemyPool.dispose();
        mainShip.dispose();
        music.dispose();
        super.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!mainShip.isDestroyed()) {
            mainShip.keyDown(keycode);
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!mainShip.isDestroyed()) {
            mainShip.keyUp(keycode);
        }
        return super.keyUp(keycode);
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        if (!mainShip.isDestroyed()) {
            mainShip.touchDown(touch, pointer);
        } else {
            if (gameover.isStopped())
                startNewGame.touchDown(touch, pointer);
        }
        return super.touchDown(touch, pointer);
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        if (!mainShip.isDestroyed()) {
            mainShip.touchUp(touch, pointer);
        } else {
            if (gameover.isStopped())
                startNewGame.touchUp(touch, pointer);
        }
        return super.touchUp(touch, pointer);
    }

    public void startNewGame() {
        score = 0;
        mainShip.startNewGame();
        enemyEmitter.setLevel(1);
    }
}

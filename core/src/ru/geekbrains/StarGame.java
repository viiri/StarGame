package ru.geekbrains;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Vector;

public class StarGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Texture background;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		img = new Texture("badlogic.jpg");

		Vector2 v1 = new Vector2(1,3);
		Vector2 v2 = new Vector2(0, -1);
		System.out.println("v1 + v2 = (" + v1.x + ", " + v1.y + ")");

		v1.set(6, 4);
		v2.set(1, 2);
		v1.sub(v2);
		System.out.println("v1 - v2 = (" + v1.x + ", " + v1.y + ")");

		System.out.println("v1 len = " + v1.len());
		v1.nor();
		System.out.println("v1 len = " + v1.len());
		System.out.println("v1 = (" + v1.x + ", " + v1.y + ")");

		v1.scl(3);
		System.out.println("v1 * n = (" + v1.x + ", " + v1.y + ")");

		v1.set(1,1);
		v2.set(-1, 1);
		v1.nor();
		v2.nor();
		System.out.println(Math.toDegrees(Math.acos(v1.dot(v2))));
		System.out.println(v1.dot(v2));

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.5f, 0.2f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(background, 0, 0);
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

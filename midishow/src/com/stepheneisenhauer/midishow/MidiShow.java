package com.stepheneisenhauer.midishow;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class MidiShow implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
    private ShapeRenderer shapeRenderer;
    private boolean[] noteStates;
    private int[] noteVeloc;

    @Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

        shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera(1, h/w);

        // Set up note states array
        noteStates = new boolean[109];
        noteVeloc = new int[109];
	}

	@Override
	public void dispose() {
		//batch.dispose();
		//texture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(1);

        // camera.update();
        // shapeRenderer.setProjectionMatrix(camera.combined);

        for (int i=21; i<=108; i++) {
            // Fade out if note is off
            if (noteStates[i] == false && noteVeloc[i] > 0)
                noteVeloc[i]--;

            float amt = noteVeloc[i] / 127.0f;
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(0.7f*amt, 0.85f*amt, amt, 1);
            shapeRenderer.line(0, 6*i, Gdx.graphics.getWidth(), 6*i);
            shapeRenderer.end();
        }
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	/* MIDI Methods */
	
	public void onMidiNoteOn(int channel, int note, int velocity) {
        noteVeloc[note] = velocity;
        noteStates[note] = true;
	}

    public void onMidiNoteOff(int channel, int note, int velocity) {
        //noteVeloc[note] = 0;
        noteStates[note] = false;
    }
}

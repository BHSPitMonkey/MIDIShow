package com.stepheneisenhauer.midishow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CirclesEffect implements Effect {
    private MidiShow appListener;
    private ShapeRenderer shapeRenderer;
    private int[] noteAlpha;

    @Override
    public void onCreate(MidiShow caller) {
        appListener = caller;
        shapeRenderer = new ShapeRenderer();
        noteAlpha = new int[109];
    }

    @Override
    public void onRender() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(1);

        // camera.update();
        // shapeRenderer.setProjectionMatrix(camera.combined);
        float cx = Gdx.graphics.getWidth() / 2.0f;
        float cy = Gdx.graphics.getHeight() / 2.0f;
        float maxRadius = Math.min(cx, cy);

        for (int i=21; i<=108; i++) {
            // If the note is on, use its velocity as the note's alpha
            if (appListener.getNoteStates()[i])
                noteAlpha[i] = appListener.getNoteVeloc()[i];
            // If the note is off but has a positive alpha, fade it out a little
            else if (noteAlpha[i] > 0)
                noteAlpha[i]--;

            float rad = maxRadius * (i / 108.0f);

            float amt = noteAlpha[i] / 127.0f;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Circle);
            shapeRenderer.setColor(0.7f*amt, 0.85f*amt, amt, 1);
            //shapeRenderer.line(0, 6*i, Gdx.graphics.getWidth(), 6*i);
            shapeRenderer.circle(cx, cy, rad, 256);
            shapeRenderer.end();
        }
    }

    @Override
    public void onMidiNoteOn(int channel, int note, int velocity) {

    }

    @Override
    public void onMidiNoteOff(int channel, int note, int velocity) {

    }
}

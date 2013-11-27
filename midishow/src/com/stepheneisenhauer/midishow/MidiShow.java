package com.stepheneisenhauer.midishow;

import com.badlogic.gdx.ApplicationListener;

public class MidiShow implements ApplicationListener {
	//private OrthographicCamera camera;
    private boolean[] noteStates;
    private int[] noteVeloc;
    private Effect currentEffect;

    // Getters

    public int[] getNoteVeloc() {
        return noteVeloc;
    }

    public boolean[] getNoteStates() {
        return noteStates;
    }

    // Libgdx Implementation

    @Override
	public void create() {		
		//camera = new OrthographicCamera(1, h/w);

        // Set up note states array
        // 109 is used because it covers the standard 88-key keyboard. I'll clean this up later...
        // TODO: Clean up magic number usage (109)
        noteStates = new boolean[109];
        noteVeloc = new int[109];

        // Prepare the effect
        currentEffect = new CirclesEffect();
        currentEffect.onCreate(this);
	}

	@Override
	public void dispose() {
		//batch.dispose();
		//texture.dispose();
	}

	@Override
	public void render() {
        if (currentEffect != null)
            currentEffect.onRender();
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
	
	// MIDI Methods
	
	public void onMidiNoteOn(int channel, int note, int velocity) {
        noteVeloc[note] = velocity;
        noteStates[note] = true;
        if (currentEffect != null)
            currentEffect.onMidiNoteOn(channel, note, velocity);
	}

    public void onMidiNoteOff(int channel, int note, int velocity) {
        //noteVeloc[note] = 0;
        noteStates[note] = false;
        if (currentEffect != null)
            currentEffect.onMidiNoteOff(channel, note, velocity);
    }
}

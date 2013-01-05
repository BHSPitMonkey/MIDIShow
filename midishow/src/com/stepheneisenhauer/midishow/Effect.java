package com.stepheneisenhauer.midishow;

public interface Effect {
    public void onCreate(MidiShow caller);
    public void onRender();
    public void onMidiNoteOn(int channel, int note, int velocity);
    public void onMidiNoteOff(int channel, int note, int velocity);
}

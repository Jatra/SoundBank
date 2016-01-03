package uk.co.jatra.soundbanklib;

/**
 * Created by tim on 03/01/2016.
 */
public interface API {
    public void preload(String[] ids);
    public void play(String id);
    public void record(String id);
    public boolean available(String id);
    public void stopRecording(boolean save);
    public void stopPlaying();
    boolean isRecording();
    boolean isPlaying();
    void setStateListener(StateListener stateListener);
    void setVolume(float leftVolume, float rightVolume);

    public interface StateListener {
        void started(String id);
        void completed();
    }
}

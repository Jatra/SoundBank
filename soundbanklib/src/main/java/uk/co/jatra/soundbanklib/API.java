package uk.co.jatra.soundbanklib;

/**
 * THe interface of the Soundbank.
 */
public interface API {
    /**
     * Play the specified audio.
     * @param id of the wanted sound in the bank
     */
    void play(String id);

    /**
     * Record the specified audio.
     * @param id of the sound to be saved
     */
    void record(String id);

    /**
     * Test if sound name is available.
     * @param id wanted sound.
     * @return true if the soundbank has the sound
     */
    boolean available(String id);

    /**
     * Finish the current recording.
     * @param save whether to save the recording into the bank.
     */
    void stopRecording(boolean save);

    /**
     * Finish playing the sound in progress.
     */
    void stopPlaying();

    /**
     * Determine if the soundbank is currently recording a sound.
     * @return true if recording
     */
    boolean isRecording();

    /**
     * Determine if the soundbank is currently playing a sound.
     * @return true if playing
     */
    boolean isPlaying();

    /**
     * Set a listener for started/completed of playing/recording.
     * @param stateListener an instance implementing the callback methods
     */
    void setStateListener(StateListener stateListener);

    /**
     * Set the soundbank play volume
     * @param leftVolume left volume 0 to 1.0
     * @param rightVolume right volume 0 to 1.0
     */
    void setVolume(float leftVolume, float rightVolume);

    /**
     * Instances implementing StateListener and set via setStateListener
     * will be notified of audio start/completion.
     */
    interface StateListener {
        /**
         * The named audio has started.
         * @param id the id of the started audio.
         */
        void started(String id);

        /**
         * The soundbank has completed its current audio.
         */
        void completed();
    }
}

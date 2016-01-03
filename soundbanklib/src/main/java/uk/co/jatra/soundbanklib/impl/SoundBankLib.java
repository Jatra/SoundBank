package uk.co.jatra.soundbanklib.impl;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import uk.co.jatra.soundbanklib.API;


/**
 * Created by tim on 03/01/2016.
 */
public class SoundBankLib implements API, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String LOG_TAG = SoundBankLib.class.getSimpleName();
    private MediaRecorder recorder;
    private MediaPlayer player;
    private StateListener stateListener;


    @Override
    public void preload(String[] ids) {
        //??
    }

    @Override
    public void play(String id) {
        player = new MediaPlayer();
        try {
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            player.setOnPreparedListener(new PreparedListener(id));
            player.setDataSource(makeFilename(id));
            player.prepareAsync();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    @Override
    public void record(String id) {
        startRecording(id);
    }

    @Override
    public boolean available(String id) {
        return new File(makeFilename(id)).canRead();
    }

    @Override
    public void stopRecording(boolean save) {
        recorder.stop();
        recorder.release();
        recorder = null;
        completed();
    }

    @Override
    public void stopPlaying() {
        player.stop();
        player.release();
        player = null;
        completed();
    }

    public boolean isRecording() {
        return recorder != null;
    }

    @Override
    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public void setStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (player != null) {
            player.setVolume(leftVolume, rightVolume);
        }
    }

    private String makeFilename(String id) {
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/" + id + ".3gp";
        return fileName;
    }

    private void startRecording(String id) {
        String fileName = makeFilename(id);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed", e);
        }
        recorder.start();
        if (stateListener != null) {
            stateListener.started(id);
        }
    }


    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        completed();
    }

    private void completed() {
        if (isPlaying()) {
            stopPlaying();
        }
        if (stateListener != null) {
            stateListener.completed();
        }
    }

    /**
     * Called to indicate an error.
     *
     * @param mp    the MediaPlayer the error pertains to
     * @param what  the type of error that has occurred:
     *              <ul>
     *              <li>{@link #MEDIA_ERROR_UNKNOWN}
     *              <li>{@link #MEDIA_ERROR_SERVER_DIED}
     *              </ul>
     * @param extra an extra code, specific to the error. Typically
     *              implementation dependent.
     *              <ul>
     *              <li>{@link #MEDIA_ERROR_IO}
     *              <li>{@link #MEDIA_ERROR_MALFORMED}
     *              <li>{@link #MEDIA_ERROR_UNSUPPORTED}
     *              <li>{@link #MEDIA_ERROR_TIMED_OUT}
     *              <li><code>MEDIA_ERROR_SYSTEM (-2147483648)</code> - low-level system error.
     *              </ul>
     * @return True if the method handled the error, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the OnCompletionListener to be called.
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        onCompletion(mp);
        return false;
    }


    public class PreparedListener implements MediaPlayer.OnPreparedListener {

        private String id;

        public PreparedListener(String id) {
            this.id = id;
        }

        /**
         * Called when the media file is ready for playback.
         *
         * @param mp the MediaPlayer that is ready for playback
         */
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (player != null) {
                if (stateListener != null) {
                    stateListener.started(id);
                }
                player.start();
            }
        }
    }
}

package uk.co.jatra.soundbank;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.jatra.soundbanklib.API;
import uk.co.jatra.soundbanklib.impl.SoundBankLib;
public class MainActivity extends AppCompatActivity
        implements API.StateListener, AudioManager.OnAudioFocusChangeListener {

    private TextView id;
    private API soundBank;
    private SoundItem[] soundItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        soundBank = new SoundBankLib();
        soundBank.setStateListener(this);


        ViewGroup soundItemsGroup = (ViewGroup)findViewById(R.id.sound_items);
        int childCount = soundItemsGroup.getChildCount();
        soundItems = new SoundItem[childCount];
        for (int i=0; i<childCount; i++) {
            soundItems[i] = new SoundItem(soundItemsGroup.getChildAt(i), soundBank);
            soundItems[i].setId(Integer.toString(i));
        }
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // could not get audio focus.
        }


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void started(String idStarted) {
        enableSoundItems(false, idStarted);
    }

    @Override
    public void completed() {
        enableSoundItems(true);
    }

    private void enableSoundItems(boolean enable, String exlcudeId) {
        for (int i=0; i < soundItems.length; i++) {
            if (!soundItems[i].getId().equals(exlcudeId)) {
                soundItems[i].enableAudio(enable);
            }
        }
    }

    private void enableSoundItems(boolean enable) {
        enableSoundItems(enable, null);
    }

    /**
     * Called on the listener to notify it the audio focus for this listener has been changed.
     * The focusChange value indicates whether the focus was gained,
     * whether the focus was lost, and whether that loss is transient, or whether the new focus
     * holder will hold it for an unknown amount of time.
     * When losing focus, listeners can use the focus change information to decide what
     * behavior to adopt when losing focus. A music player could for instance elect to lower
     * the volume of its music stream (duck) for transient focus losses, and pause otherwise.
     *
     * @param focusChange the type of focus change, one of {@link AudioManager#AUDIOFOCUS_GAIN},
     *                    {@link AudioManager#AUDIOFOCUS_LOSS}, {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT}
     *                    and {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK}.
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                enableSoundItems(true);
                soundBank.setVolume(1f, 1f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                if (soundBank.isPlaying()) {
                    soundBank.stopPlaying();
                }
                enableSoundItems(false);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (soundBank.isPlaying()) {
                    soundBank.stopPlaying();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (soundBank.isPlaying()) {
                    soundBank.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }
}

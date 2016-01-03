package uk.co.jatra.soundbank;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import uk.co.jatra.soundbanklib.API;

/**
 * Created by tim on 03/01/2016.
 */

public class SoundItem {
    private final TextView idField;
    private final ImageButton record;
    private final ImageButton play;
    private API soundBank;

    public SoundItem(View view, API api) {
        this.soundBank = api;
        idField = (TextView) view.findViewById(R.id.id);
        idField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                enableAudio(enable);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        record = (ImageButton) view.findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundBank.isRecording()) {
                    record.setActivated(false);
                    soundBank.stopRecording(true);
                    enablePlay(true);
                } else {
                    record.setActivated(true);
                    soundBank.record(getId());
                    enablePlay(false);
                }
            }
        });

        play = (ImageButton) view.findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundBank.isPlaying()) {
                    play.setActivated(false);
                    soundBank.stopPlaying();
                    enableRecord(true);
                } else {
                    play.setActivated(true);
                    enableRecord(false);
                    soundBank.play(getId());
                }
            }
        });
        enableAudio(false);
    }

    @NonNull
    public String getId() {
        return idField.getText().toString();
    }

    public void enableAudio(boolean enable) {
        enableRecord(enable);
        enablePlay(enable);
    }

    private void enablePlay(boolean enable) {
        enable &= soundBank.available(getId());
        play.setActivated(!enable);
        play.setClickable(enable);
        play.setEnabled(enable);
    }

    private void enableRecord(boolean enable) {
        record.setActivated(!enable);
        record.setClickable(enable);
        record.setEnabled(enable);
    }

    public void setId(String id) {
        idField.setText(id);
    }
}


package com.example.roger_roger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        GameAudio audio = GameAudio.get(this);

        SwitchCompat switchMusic = findViewById(R.id.switchMusic);
        SwitchCompat switchSound = findViewById(R.id.switchSound);
        SeekBar seekVolume = findViewById(R.id.seekVolume);
        TextView txtVolume = findViewById(R.id.txtVolume);

        // Set the saved values first, then attach listeners so setup doesn't trigger them
        switchMusic.setChecked(audio.isMusicOn());
        switchSound.setChecked(audio.isSoundOn());
        seekVolume.setProgress(audio.getVolume());
        txtVolume.setText(audio.getVolume() + "%");

        switchMusic.setOnCheckedChangeListener((button, on) -> {
            audio.setMusicOn(on);
            audio.playClick();
        });

        switchSound.setOnCheckedChangeListener((button, on) -> {
            audio.setSoundOn(on);
            audio.playClick();            // only audible when sound was just turned on
        });

        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                audio.setVolume(progress);
                txtVolume.setText(progress + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar bar) { }
            @Override
            public void onStopTrackingTouch(SeekBar bar) {
                audio.playClick();        // lets the player hear the new level
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(GameAudio.withClick(this, v -> goToMainMenu()));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToMainMenu();
            }
        });
    }

    private void goToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
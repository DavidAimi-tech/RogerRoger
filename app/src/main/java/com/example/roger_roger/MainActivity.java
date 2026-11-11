package com.example.roger_roger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView start, settings, exit;
    private ImageView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.btnStart);
        help = findViewById(R.id.btnHelp);
        exit  = findViewById(R.id.btnExit);
        settings = findViewById(R.id.btnSettings);

        if (start != null) {
            start.setOnClickListener(GameAudio.withClick(this, v -> {
                v.setSelected(true);
                v.postDelayed(() -> {
                    v.setSelected(false);
                    startActivity(new Intent(this, ArmyBuilderActivity.class));
                }, 150);
            }));
        }

        if (settings != null) {
            settings.setOnClickListener(GameAudio.withClick(this, v -> {
                v.setSelected(true);
                v.postDelayed(() -> {
                    v.setSelected(false);
                    startActivity(new Intent(this, SettingsActivity.class));
                }, 150);
            }));
        }

        if (help != null) {
            help.setOnClickListener(GameAudio.withClick(this, v -> {
                v.setSelected(true);
                new AlertDialog.Builder(this)
                        .setTitle("How to Play")
                        .setMessage("Build an army of 15, 20 or 30 soldiers with exactly 100 strength points.\n\n"
                                + "Each round your top soldier fights the enemy's. The higher strength wins "
                                + "and usually captures the loser.\n\n"
                                + "Skip to dodge a bad matchup, or use a trump to play your strongest soldier. "
                                + "Capture all enemy soldiers to win.")
                        .setPositiveButton("Got it", null)
                        .setOnDismissListener(d -> v.setSelected(false))
                        .show();
            }));
        }

        if (exit != null) {
            exit.setOnClickListener(GameAudio.withClick(this, v -> {
                v.setSelected(true);
                v.postDelayed(this::finishAffinity, 150);
            }));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (start != null) start.setSelected(false);
        if (settings != null) settings.setSelected(false);
        if (help != null) help.setSelected(false);
        if (exit != null) exit.setSelected(false);
    }
}



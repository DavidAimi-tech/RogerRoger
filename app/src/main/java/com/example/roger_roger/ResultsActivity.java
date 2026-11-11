package com.example.roger_roger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent in = getIntent();

        TextView txtOutcome = findViewById(R.id.txtOutcome);
        TextView txtStats = findViewById(R.id.txtStats);

        String state = in.getStringExtra("outcome");
        switch (state == null ? "" : state) {
            case "WIN":  txtOutcome.setText("You won!");  break;
            case "LOSS": txtOutcome.setText("You lost");  break;
            default:     txtOutcome.setText("Draw");      break;
        }

        txtStats.setText("Rounds: " + in.getIntExtra("rounds", 0)
                + "\nCaptures: " + in.getIntExtra("captures", 0)
                + "\nUser Army Casualties: " + in.getIntExtra("userCasualties", 0)
                + "\nEnemy Soldiers killed: " + in.getIntExtra("enemy killed", 0)
                + "\nKills resisted:" + in.getIntExtra("resisted", 0)
                + "\nSoldiers combusted: " + in.getIntExtra("combusted", 0)
                + "\nSkips used: " + in.getIntExtra("skips", 0)
                + "\nTrumps used: " + in.getIntExtra("trumps", 0)
                + "\nYou Left: " + in.getIntExtra("userLeft", 0)
                + "\nEnemies Left: " + in.getIntExtra("enemyLeft", 0));

        findViewById(R.id.btnReplay).setOnClickListener(GameAudio.withClick(this, v -> {
            v.setSelected(true);
            v.postDelayed(() -> {
                Intent replay = new Intent(this, BattleActivity.class);
                replay.putExtra("strengths", in.getIntArrayExtra("strengths"));
                replay.putExtra("clan", in.getStringExtra("clan"));
                replay.putExtra("budget", in.getIntExtra("budget", BattleConfig.MEDIUM));
                startActivity(replay);
                finish();
            }, 150);
        }));

        findViewById(R.id.btnMainMenu).setOnClickListener(GameAudio.withClick(this, v -> {
            v.setSelected(true);
            v.postDelayed(() -> {
                Intent menu = new Intent(this, MainActivity.class);
                menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(menu);
                finish();
            }, 150);
        }));
    }
}

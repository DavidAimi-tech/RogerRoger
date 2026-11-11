package com.example.roger_roger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class enterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        View btnOpen = findViewById(R.id.btnOpen);
        if (btnOpen != null) {
            btnOpen.setOnClickListener(GameAudio.withClick(this, v -> {
                v.setSelected(true);
                v.postDelayed(() -> {
                    Intent intent = new Intent(enterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }, 150);
            }));
        }
    }
}

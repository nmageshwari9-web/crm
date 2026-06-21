package com.carrom.autoplay;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.carrom.autoplay.view.GameView;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private boolean autoPlayOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout container = findViewById(R.id.gameContainer);
        gameView = new GameView(this);
        container.addView(gameView);

        Button btnAutoPlay = findViewById(R.id.btnAutoPlay);
        Button btnReset = findViewById(R.id.btnReset);
        Button btnFire = findViewById(R.id.btnFire);
        TextView tvStatus = findViewById(R.id.tvStatus);

        btnAutoPlay.setOnClickListener(v -> {
            autoPlayOn = !autoPlayOn;
            gameView.autoPlayEnabled = autoPlayOn;
            btnAutoPlay.setText(autoPlayOn ? "■ STOP" : "▶ AUTO PLAY");
            tvStatus.setText(autoPlayOn ? "AUTO PLAY: ON" : "AUTO PLAY: OFF");
        });

        btnReset.setOnClickListener(v -> {
            gameView.resetBoard();
            tvStatus.setText("BOARD RESET");
        });

        btnFire.setOnClickListener(v -> {
            // manual fire: shoot straight up from base line
            gameView.fire(0f, -1f, 16f);
            tvStatus.setText("FIRED");
        });
    }
}

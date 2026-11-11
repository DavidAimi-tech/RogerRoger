package com.example.roger_roger;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.View;

public final class GameAudio {

    private static final String PREFS = "roger_settings";
    private static final float MUSIC_GAIN = 0.6f;    // music sits quieter than the effects
    private static final float DUCK_LEVEL = 0.2f;    // music level while a win/lose sound plays

    private static GameAudio instance;

    public static synchronized GameAudio get(Context context) {
        if (instance == null) instance = new GameAudio(context.getApplicationContext());
        return instance;
    }

    public static void click(Context context) {
        get(context).playClick();
    }

    // Wraps a click listener so it plays the click sound first
    public static View.OnClickListener withClick(Context context, View.OnClickListener listener) {
        return v -> {
            click(context);
            listener.onClick(v);
        };
    }

    private final Context context;
    private final SharedPreferences prefs;
    private final SoundPool pool;
    private final int clickId;
    private MediaPlayer music;
    private boolean musicScreenShowing;   // true while a non-battle screen is in front
    private float duck = 1f;

    private GameAudio(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        pool = new SoundPool.Builder().setMaxStreams(4).setAudioAttributes(attrs).build();
        clickId = pool.load(context, R.raw.button_audio, 1);
    }

    // ---------- saved settings ----------

    public boolean isMusicOn() { return prefs.getBoolean("music_on", true); }
    public boolean isSoundOn() { return prefs.getBoolean("sound_on", true); }
    public int getVolume()     { return prefs.getInt("volume", 70); }       // 0-100

    public void setMusicOn(boolean on) {
        prefs.edit().putBoolean("music_on", on).apply();
        syncMusic();
    }

    public void setSoundOn(boolean on) {
        prefs.edit().putBoolean("sound_on", on).apply();
    }

    public void setVolume(int volume) {
        prefs.edit().putInt("volume", volume).apply();
        applyMusicVolume();
    }

    // ---------- music, driven by which screen is showing ----------

    public void screenResumed(boolean musicScreen) {
        musicScreenShowing = musicScreen;
        syncMusic();
    }

    public void screenPaused() {
        musicScreenShowing = false;
        syncMusic();
    }

    private void syncMusic() {
        if (musicScreenShowing && isMusicOn()) {
            if (music == null) {
                music = MediaPlayer.create(context, R.raw.bg_music);
                if (music == null) return;
                music.setLooping(true);
            }
            applyMusicVolume();
            if (!music.isPlaying()) music.start();
        } else if (music != null && music.isPlaying()) {
            music.pause();
        }
    }

    private void applyMusicVolume() {
        if (music == null) return;
        float v = (getVolume() / 100f) * MUSIC_GAIN * duck;
        music.setVolume(v, v);
    }

    // ---------- effects ----------

    public void playClick() {
        if (!isSoundOn()) return;
        float v = getVolume() / 100f;
        pool.play(clickId, v, v, 1, 0, 1f);
    }

    // Plays the win or lose sound and quiets the music until it finishes
    public void playResult(boolean win) {
        if (!isSoundOn()) return;
        MediaPlayer player = MediaPlayer.create(context, win ? R.raw.win_sound : R.raw.lose_sound);
        if (player == null) return;
        float v = getVolume() / 100f;
        player.setVolume(v, v);
        duck = DUCK_LEVEL;
        applyMusicVolume();
        player.setOnCompletionListener(mp -> {
            mp.release();
            duck = 1f;
            applyMusicVolume();
        });
        player.start();
    }
}

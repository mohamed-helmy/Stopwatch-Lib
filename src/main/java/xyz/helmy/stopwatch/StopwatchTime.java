package xyz.helmy.stopwatch;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;

import androidx.annotation.Nullable;


public class StopwatchTime {
    private LinkedList<Split> splits;
    private TextView textView;
    private long start, current, elapsedTime, lapTime;
    private boolean started, paused, logEnabled;
    private OnTickListener onTickListener;
    private long clockDelay;
    private Handler handler;


    private final Runnable runnable = this::run;


    public StopwatchTime() {
        start = System.currentTimeMillis();
        current = System.currentTimeMillis();
        elapsedTime = 0;
        started = false;
        paused = false;
        logEnabled = false;
        splits = new LinkedList<>();
        textView = null;
        lapTime = 0;
        onTickListener = null;
        clockDelay = 100;
        handler = new Handler();
    }


    private static String getFormattedTime(long elapsedTime) {
        final StringBuilder displayTime = new StringBuilder();

        int milliseconds = (int) ((elapsedTime % 1000) / 10);
        int seconds = (int) ((elapsedTime / 1000) % 60);
        int minutes = (int) (elapsedTime / (60 * 1000) % 60);
        int hours = (int) (elapsedTime / (60 * 60 * 1000));

        NumberFormat f = new DecimalFormat("00");

        if (minutes == 0)
            displayTime.append(f.format(seconds)).append('.').append(f.format(milliseconds));

        else if (hours == 0)
            displayTime.append(f.format(minutes)).append(":").append(f.format(seconds)).append(f.format(milliseconds));

        else
            displayTime.append(hours).append(":").append(f.format(minutes)).append(":").append(f.format(seconds));

        return displayTime.toString();
    }


    public boolean isStarted() {
        return started;
    }


    public boolean isPaused() {
        return paused;
    }


    public long getElapsedTime() {
        return elapsedTime;
    }


    public long getStart() {
        return start;
    }


    public LinkedList<Split> getSplits() {
        return splits;
    }

    public long getClockDelay() {
        return clockDelay;
    }


    public void setClockDelay(long clockDelay) {
        this.clockDelay = clockDelay;
    }

    public void setDebugMode(boolean debugMode) {
        logEnabled = debugMode;
    }


    public void setTextView(@Nullable TextView textView) {
        this.textView = textView;
    }


    public void setOnTickListener(OnTickListener onTickListener) {
        this.onTickListener = onTickListener;
    }

    public void start() {
        if (started)
            throw new IllegalStateException("Already Started");
        else {
            started = true;
            paused = false;
            start = System.currentTimeMillis();
            current = System.currentTimeMillis();
            lapTime = 0;
            elapsedTime = 0;
            splits.clear();
            handler.post(runnable);
        }
    }


    public void stop() {
        if (!started)
            throw new IllegalStateException("Not Started");
        else {
            updateElapsed(System.currentTimeMillis());
            started = false;
            paused = false;
            handler.removeCallbacks(runnable);
        }
    }


    public void pause() {
        if (paused)
            throw new IllegalStateException("Already Paused");
        else if (!started)
            throw new IllegalStateException("Not Started");
        else {
            updateElapsed(System.currentTimeMillis());
            paused = true;
            handler.removeCallbacks(runnable);
        }
    }


    public void resume() {
        if (!paused)
            throw new IllegalStateException("Not Paused");
        else if (!started)
            throw new IllegalStateException("Not Started");
        else {
            paused = false;
            current = System.currentTimeMillis();
            handler.post(runnable);
        }
    }


    public void split() {

        if (!started)
            throw new IllegalStateException("Not Started");
        Split split = new Split(elapsedTime, lapTime);
        lapTime = 0;
        if (logEnabled)
            Log.d("STOPWATCH", "split at " + split.getSplitTime() + ". Lap = " + split.getLapTime());
        splits.add(split);
    }


    private void updateElapsed(long time) {
        elapsedTime += time - current;
        lapTime += time - current;
        current = time;
    }



    private void run() {
        if (!started || paused) {
            handler.removeCallbacks(runnable);
            return;
        }
        updateElapsed(System.currentTimeMillis());
        handler.postDelayed(runnable,clockDelay);

        if (logEnabled)
            Log.d("STOPWATCH", elapsedTime / 1000 + " seconds, " + elapsedTime % 1000 + " milliseconds");

        if (onTickListener != null)
            onTickListener.onTick(this);

        if (textView != null) {
            String displayTime = getFormattedTime(elapsedTime);
            textView.setText(displayTime);
        }
    }
}
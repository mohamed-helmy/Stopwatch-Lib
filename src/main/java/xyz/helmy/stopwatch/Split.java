package xyz.helmy.stopwatch;

import androidx.annotation.RestrictTo;

public class Split {
    private long splitTime, lapTime;
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Split(long splitTime, long lapTime){
        this.splitTime = splitTime;
        this.lapTime = lapTime;
    }
    public long getLapTime() {
        return lapTime;
    }
    public long getSplitTime() {
        return splitTime;
    }
}
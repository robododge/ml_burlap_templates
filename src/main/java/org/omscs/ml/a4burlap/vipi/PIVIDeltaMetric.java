package org.omscs.ml.a4burlap.vipi;

public class PIVIDeltaMetric {

    private double delta;
    private long wallClockMillis;
    private long viIterations;

    public PIVIDeltaMetric(double delta, long wallClockMillis) {
        this.delta = delta;
        this.wallClockMillis = wallClockMillis;
    }

    public PIVIDeltaMetric(double delta, long wallClockMillis, long viIterations) {
        this.delta = delta;
        this.wallClockMillis = wallClockMillis;
        this.viIterations = viIterations;
    }

    public double getDelta() {
        return delta;
    }

    public long getWallClockMillis() {
        return wallClockMillis;
    }

    public long getViIterations() {
        return viIterations;
    }
}

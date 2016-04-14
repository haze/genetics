package pw.haze.genetics;

/**
 * |> Author: haze
 * |> Since: 4/14/16
 */
public class Timer {

    private long resetMS = 0L;

    public Timer() {
        resetMS = getCurrentTime();
    }

    public void reset() {
        resetMS = getCurrentTime();
    }

    public void addReset(long addedReset) {
        resetMS += addedReset;
    }

    public void setReset(long setReset) {
        resetMS = setReset;
    }

    public boolean hasDelayRun(long delay) {
        return getCurrentTime() >= resetMS + delay;
    }

    private long getCurrentTime() {
        return (long) (System.nanoTime() / 1E6);
    }


}

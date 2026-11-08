package org.customCollector;

public class MyIntSummaryStatistics {

    private long count;
    private long sum;
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    public void accept(int value) {
        count++;
        sum += value;
        min = Math.min(min, value);
        max = Math.max(max, value);
    }

    public void combine(MyIntSummaryStatistics other) {
        count += other.count;
        sum += other.sum;
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
    }

    public long getCount() {
        return count;
    }

    public long getSum() {
        return sum;
    }

    public int getMin() {
        return count == 0 ? 0 : min;
    }

    public int getMax() {
        return count == 0 ? 0 : max;
    }

    public double getAverage() {
        return count == 0 ? 0.0 : (double) sum / count;
    }

    @Override
    public String toString() {
        return "Count = " + getCount()
                + ", Sum = " + getSum()
                + ", Min = " + getMin()
                + ", Average = " + getAverage()
                + ", Max = " + getMax();
    }
}

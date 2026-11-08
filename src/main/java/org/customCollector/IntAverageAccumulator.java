package org.customCollector;

public class IntAverageAccumulator {

    long sum;
    long count;

    public void add(int value) {
        sum += value;
        count++;
    }

    public void combine(IntAverageAccumulator other) {
        this.sum += other.sum;
        this.count += other.count;
    }

    public double average() {
        return count == 0 ? 0.0 : (double) sum / count;
    }
}
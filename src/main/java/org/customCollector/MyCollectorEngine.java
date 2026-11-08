package org.customCollector;

import java.util.List;

public class MyCollectorEngine {

    public static <T, A, R> R collect(List<T> input, MyCollector<T, A, R> collector) {

        A container = collector.supplier().get();

        var accumulator = collector.accumulator();

        for(T element : input) {
            accumulator.accept(container, element);
        }

        if(collector.characteristics().contains(MyCollector.Characteristics.IDENTITY_FINISH)) {

            @SuppressWarnings("unchecked")
            R result = (R) container;
            return result;
        }

        return collector.finisher().apply(container);
    }


}

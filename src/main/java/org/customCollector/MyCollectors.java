package org.customCollector;

import java.util.*;
import java.util.function.*;

public class MyCollectors {

    public static <T> MyCollector<T, List<T>, List<T>> toList() {


        Supplier<List<T>> supplier = ArrayList::new;

        BiConsumer<List<T>, T> accumulator = List::add;

        BinaryOperator<List<T>> combiner = (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };

        Set<MyCollector.Characteristics> characteristics = Set.of(MyCollector.Characteristics.IDENTITY_FINISH);

        Function<List<T>, List<T>> finisher = Collections::unmodifiableList;

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                characteristics
        );
    }

    public static <T> MyCollector<T, Set<T>, Set<T>> toSet() {


        Supplier<Set<T>> supplier = HashSet::new;

        BiConsumer<Set<T>, T> accumulator = Set::add;

        BinaryOperator<Set<T>> combiner = (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        };

        Function<Set<T>, Set<T>> finisher = set -> set;

        Set<MyCollector.Characteristics> characteristics = Set.of(
                MyCollector.Characteristics.IDENTITY_FINISH,
                MyCollector.Characteristics.UNORDERED
        );

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                characteristics
                );
    }

    public static MyCollector<String, StringBuilder, String> joining() {


        Supplier<StringBuilder> supplier = StringBuilder::new;

        BiConsumer<StringBuilder, String> accumulator = StringBuilder::append;

        BinaryOperator<StringBuilder> combiner = (sb1, sb2) -> {
            sb1.append(sb2);
            return sb1;
        };

        Function<StringBuilder, String> finisher = StringBuilder::toString;

        Set<MyCollector.Characteristics> characteristics = Set.of();

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                characteristics
        );
    }

    public static MyCollector<String, StringBuilder, String> joining(String delimiter) {


        Supplier<StringBuilder> supplier = StringBuilder::new;

        BiConsumer<StringBuilder, String> accumulator = (sb, str) -> {
            if (!sb.isEmpty()) {
                sb.append(delimiter);
            }
            sb.append(str);
        };

        BinaryOperator<StringBuilder> combiner = (sb1, sb2) -> {
            if (!sb1.isEmpty() && !sb2.isEmpty()) {
                sb1.append(delimiter);
            }
            sb1.append(sb2);
            return sb1;
        };

        Function<StringBuilder, String> finisher = StringBuilder::toString;

        Set<MyCollector.Characteristics> characteristics = Set.of();

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                characteristics
        );
    }

    public static MyCollector<String, StringBuilder, String> joining(
            String delimiter,
            String prefix,
            String suffix
    ) {
        MyCollector<String, StringBuilder, String> joining = MyCollectors.joining(delimiter);

        Function<StringBuilder, String> finisher = (sb) -> prefix + sb.toString() + suffix;

        return new MyCollectorImpl<>(
                joining.supplier(),
                joining.accumulator(),
                joining.combiner(),
                finisher,
                joining.characteristics()
        );
    }

    public static <T, U, A, R> MyCollector<T, A, R> mapping(
            Function<? super T, ? extends U> mapper, MyCollector<U, A, R> downstream
    ) {

        BiConsumer<A, U> downstreamAccumulator =
                downstream.accumulator();

        BiConsumer<A, T> newAcc = (aa, t) -> {
            U transformed = mapper.apply(t);
            downstreamAccumulator.accept(aa, transformed);
        };

        return new MyCollectorImpl<>(
                downstream.supplier(),
                newAcc,
                downstream.combiner(),
                downstream.finisher(),
                downstream.characteristics()
        );
    }
}

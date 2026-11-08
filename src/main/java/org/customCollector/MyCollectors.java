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

    public static <T, U, A, R> MyCollector<T, A, R> filtering(
            Predicate<? super T> filter, MyCollector<T, A, R> downstream
    ) {

        BiConsumer<A, T> downstreamAccumulator =
                downstream.accumulator();

        BiConsumer<A, T> newAcc = (a, t) -> {
            if(filter.test(t)) {
                downstreamAccumulator.accept(a, t);
            }
        };

        return new MyCollectorImpl<>(
                downstream.supplier(),
                newAcc,
                downstream.combiner(),
                downstream.finisher(),
                downstream.characteristics()
        );
    }

    public static <T> MyCollector<T, ?, Long> counting() {

        Supplier<long[]> supplier = () -> new long[]{0};

        BiConsumer<long[], T> accumulator = (counter, val) -> counter[0]++;

        BinaryOperator<long[]> combiner = (counter1, counter2) -> {
            counter1[0] += counter2[0];
            return counter1;
        };

        Function<long[], Long> finisher = counter -> counter[0];

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                Set.of()
        );
    }

    public static <T> MyCollector<T, int[], Integer> summingInt(ToIntFunction<? super T> mapper) {

        Supplier<int[]> supplier = () -> new int[]{0};

        BiConsumer<int[], T> accumulator = (counter, val) -> counter[0] += mapper.applyAsInt(val);

        BinaryOperator<int[]> combiner = (counter1, counter2) -> {
            counter1[0] += counter2[0];
            return counter1;
        };

        Function<int[], Integer> finisher = counter -> counter[0];

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                Set.of()
        );
    }

    public static <T> MyCollector<T, IntAverageAccumulator, Double> averagingInt(
            ToIntFunction<? super T> mapper
    ) {

        Supplier<IntAverageAccumulator> supplier =
                IntAverageAccumulator::new;

        BiConsumer<IntAverageAccumulator, T> accumulator =
                (container, value) ->
                        container.add(mapper.applyAsInt(value));

        BinaryOperator<IntAverageAccumulator> combiner = (a, b) -> {
            a.combine(b);
            return a;
        };

        Function<IntAverageAccumulator, Double> finisher =
                IntAverageAccumulator::average;

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                Set.of()
        );
    }

    public static <T> MyCollector<T, MyIntSummaryStatistics, MyIntSummaryStatistics>
    summarizingInt(ToIntFunction<? super T> mapper) {

        Supplier<MyIntSummaryStatistics> supplier =
                MyIntSummaryStatistics::new;

        BiConsumer<MyIntSummaryStatistics, T> accumulator =
                (stats, value) -> stats.accept(mapper.applyAsInt(value));

        BinaryOperator<MyIntSummaryStatistics> combiner = (stats1, stats2) -> {
            stats1.combine(stats2);
            return stats1;
        };

        Function<MyIntSummaryStatistics, MyIntSummaryStatistics> finisher =
                stats -> stats;

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                Set.of(MyCollector.Characteristics.IDENTITY_FINISH)
        );
    }

    public static <T, K> MyCollector<T, Map<K, List<T>>, Map<K, List<T>>> groupingBy(
            Function<? super T, ? extends K> keyMapper) {

        Supplier<Map<K, List<T>>> supplier = HashMap::new;

        BiConsumer<Map<K, List<T>>, T> accumulatore = (map, element) -> {
            K key = keyMapper.apply(element);
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(element);
        };

        BinaryOperator<Map<K, List<T>>> combiner = (map1, map2) -> {

            map2.forEach((key, values) -> map1.merge(key, values, (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            }));
            return map1;
        };


        Function<Map<K, List<T>>, Map<K, List<T>>> finisher = map -> map;

        Set<MyCollector.Characteristics> characteristics = Set.of(MyCollector.Characteristics.IDENTITY_FINISH);

        return new MyCollectorImpl<>(
                supplier,
                accumulatore,
                combiner,
                finisher,
                characteristics
        );
    }

    public static <T, K, A, D> MyCollector<
            T,
            Map<K, A>,
            Map<K, D>
            > groupingBy(
            Function<? super T, ? extends K> classifier,
            MyCollector<T, A, D> downstream
    ) {

        // 1. Create the outer map
        Supplier<Map<K, A>> supplier = HashMap::new;

        // 2. Cache downstream components
        Supplier<A> downstreamSupplier = downstream.supplier();

        BiConsumer<A, T> downstreamAccumulator =
                downstream.accumulator();

        // 3. Accumulate elements into their respective groups
        BiConsumer<Map<K, A>, T> accumulator = (map, element) -> {

            K key = classifier.apply(element);

            A container = map.computeIfAbsent(
                    key,
                    k -> downstreamSupplier.get()
            );

            downstreamAccumulator.accept(container, element);
        };

        // 4. Combine two partial maps
        BinaryOperator<A> downstreamCombiner = downstream.combiner();

        BinaryOperator<Map<K, A>> combiner = (map1, map2) -> {

            map2.forEach((key, value) ->
                    map1.merge(key, value, downstreamCombiner)
            );

            return map1;
        };

        // 5. Finish each group's accumulation container
        Function<A, D> downstreamFinisher = downstream.finisher();

        Function<Map<K, A>, Map<K, D>> finisher = map -> {

            Map<K, D> result = new HashMap<>();

            map.forEach((key, value) ->
                    result.put(key, downstreamFinisher.apply(value))
            );

            return result;
        };

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                Set.of()
        );
    }

    public static <T> MyCollector<
            T,
            Map<Boolean, List<T>>,
            Map<Boolean, List<T>>
            > partitioningBy(Predicate<? super T> predicate) {

        Supplier<Map<Boolean, List<T>>> supplier = () -> {
            Map<Boolean, List<T>> map = new HashMap<>();
            map.put(true, new ArrayList<>());
            map.put(false, new ArrayList<>());
            return map;
        };

        BiConsumer<Map<Boolean, List<T>>, T> accumulator =
                (map, element) -> {
                    boolean key = predicate.test(element);
                    map.get(key).add(element);
                };

        BinaryOperator<Map<Boolean, List<T>>> combiner =
                (map1, map2) -> {
                    map1.get(true).addAll(map2.get(true));
                    map1.get(false).addAll(map2.get(false));
                    return map1;
                };

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                Function.identity(),
                Set.of(MyCollector.Characteristics.IDENTITY_FINISH)
        );
    }

    public static <T, A, D> MyCollector<
            T,
            Map<Boolean, A>,
            Map<Boolean, D>
            > partitioningBy(
            Predicate<? super T> predicate,
            MyCollector<T, A, D> downstream
    ) {

        // 1. Supplier
        Supplier<Map<Boolean, A>> supplier = () -> {
            Map<Boolean, A> map = new HashMap<>();

            map.put(true, downstream.supplier().get());
            map.put(false, downstream.supplier().get());

            return map;
        };

        // 2. Accumulator
        BiConsumer<A, T> downstreamAccumulator = downstream.accumulator();

        BiConsumer<Map<Boolean, A>, T> accumulator = (map, element) -> {
            boolean key = predicate.test(element);

            A container = map.get(key);

            downstreamAccumulator.accept(container, element);
        };

        // 3. Combiner
        BinaryOperator<A> downstreamCombiner = downstream.combiner();

        BinaryOperator<Map<Boolean, A>> combiner = (map1, map2) -> {
            map1.merge(true, map2.get(true), downstreamCombiner);
            map1.merge(false, map2.get(false), downstreamCombiner);

            return map1;
        };

        // 4. Finisher
        Function<A, D> downstreamFinisher = downstream.finisher();

        Function<Map<Boolean, A>, Map<Boolean, D>> finisher = map -> {
            Map<Boolean, D> result = new HashMap<>();

            result.put(true, downstreamFinisher.apply(map.get(true)));
            result.put(false, downstreamFinisher.apply(map.get(false)));

            return result;
        };

        return new MyCollectorImpl<>(
                supplier,
                accumulator,
                combiner,
                finisher,
                Set.of()
        );
    }
}

package me.vadim.ja.kc.util.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @link <a href="https://gist.githubusercontent.com/Pyknic/04cf0d7dc0184d79dd9a/raw/1b9c755354fdd77d7cff34fae83bd6be5bffa11e/CollectorUtil.java">Source</a>
 */
public final class CollectorUtil {

    /**
     * Returns a new {@link MapStream} where the elements have been grouped together using
     * the specified function.
     * 
     * @param <T>      the stream element type
     * @param <C>      the type of the key to group by
     * @param grouper  the function to use for grouping
     * @return         a {@link MapStream} grouped by key
     */
    public static <T, C> Collector<T, ?, MapStream<C, List<T>>> groupBy(Function<T, C> grouper) {
        return new CollectorImpl<>(
                () -> new GroupHolder<>(grouper),
                GroupHolder::add,
                GroupHolder::merge,
                GroupHolder::finisher,
                Collections.emptySet()
        );
    }

    private static class GroupHolder<C, T> {

        private final Function<T, C> grouper;
        private final Map<C, List<T>> elements;

        private final Function<C, List<T>> createList = c -> new ArrayList<>();

        public GroupHolder(Function<T, C> grouper) {
            this.grouper = grouper;
            this.elements = new HashMap<>();
        }

        public void add(T element) {
            final C key = grouper.apply(element);
            elements.computeIfAbsent(key, createList)
                .add(element);
        }

        public GroupHolder<C, T> merge(GroupHolder<C, T> holder) {
            holder.elements.entrySet().stream()
                .forEach(e
                    -> elements.computeIfAbsent(e.getKey(), createList)
                    .addAll(e.getValue())
                );

            return this;
        }

        public MapStream<C, List<T>> finisher() {
            return MapStream.of(elements);
        }
    }

    /**
     * Simple implementation class for {@code Collector}.
     *
     * @param <T> the type of elements to be collected
     * @param <A> the type of the intermediate holder
     * @param <R> the type of the result
     */
    static class CollectorImpl<T, A, R> implements Collector<T, A, R> {

        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier,
            BiConsumer<A, T> accumulator,
            BinaryOperator<A> combiner,
            Function<A, R> finisher,
            Set<Collector.Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        @SuppressWarnings("unchecked")
        CollectorImpl(Supplier<A> supplier,
            BiConsumer<A, T> accumulator,
            BinaryOperator<A> combiner,
            Set<Collector.Characteristics> characteristics) {

            this(supplier, accumulator, combiner, i -> (R) i, characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return characteristics;
        }
    }
    
    /**
     * Utility classes should not be instantiated.
     */
    private CollectorUtil() {}
}
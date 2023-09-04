// @formatter:off -- IntelliJ not fucking with this file
/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package me.vadim.ja.kc.util.collection;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * A java {@code Stream} wrapper that stream over Key-Value pairs. With this
 * wrapper you get access to additional operators for working with two valued
 * collections.
 * 
 * @author Emil
 * @param <K>  the key type
 * @param <V>  the value type
 * @link <a href="https://gist.githubusercontent.com/Pyknic/04cf0d7dc0184d79dd9a/raw/1b9c755354fdd77d7cff34fae83bd6be5bffa11e/MapStream.java">Source</a>
 */
public final class MapStream<K, V> implements Stream<Map.Entry<K, V>> {
    
    private Stream<Map.Entry<K, V>> inner;
    
    public static <K, V> MapStream<K, V> of(Map.Entry<K, V> entry) {
        return new MapStream<>(Stream.of(entry));
    }
    
    public static <K, V> MapStream<K, V> of(Map.Entry<K, V>... entries) {
        return new MapStream<>(Stream.of(entries));
    }
    
    public static <K, V> MapStream<K, V> of(Map<K, V> map) {
        return new MapStream<>(map.entrySet().stream());
    }
    
    public static <K, V> MapStream<K, V> of(Stream<Map.Entry<K, V>> stream) {
        return new MapStream<>(stream);
    }
    
    public static <K, V> MapStream<K, V> empty() {
        return new MapStream<>(Stream.empty());
    }

    @Override
    public MapStream<K, V> filter(Predicate<? super Map.Entry<K, V>> predicate) {
        inner = inner.filter(predicate);
        return this;
    }
    
    public MapStream<K, V> filter(BiPredicate<? super K, ? super V> predicate) {
        return filter(e -> predicate.test(e.getKey(), e.getValue()));
    }

    @Override
    public <R> Stream<R> map(Function<? super Map.Entry<K, V>, ? extends R> mapper) {
        return inner.map(mapper);
    }
    
    public <R> Stream<R> map(BiFunction<? super K, ? super V, ? extends R> mapper) {
        return map(e -> mapper.apply(e.getKey(), e.getValue()));
    }
    
    public <R> MapStream<R, V> mapKey(BiFunction<? super K, ? super V, ? extends R> mapper) {
        return new MapStream<>(inner.map(e -> 
            new AbstractMap.SimpleEntry<>(
                mapper.apply(e.getKey(), e.getValue()),
                e.getValue()
            )
        ));
    }
    
    public <R> MapStream<K, R> mapValue(BiFunction<? super K, ? super V, ? extends R> mapper) {
        return new MapStream<>(inner.map(e -> 
            new AbstractMap.SimpleEntry<>(
                e.getKey(), 
                mapper.apply(e.getKey(), e.getValue())
            )
        ));
    }
    
    public <R> MapStream<R, V> flatMapKey(BiFunction<? super K, ? super V, ? extends Stream<? extends R>> mapper) {
        return new MapStream<>(inner.flatMap(e -> 
            mapper.apply(e.getKey(), e.getValue())
                .map(k ->
                    new AbstractMap.SimpleEntry<>(
                        k,
                        e.getValue()
                    )
                )
        ));
    }
    
    public <R> MapStream<K, R> flatMapValue(BiFunction<? super K, ? super V, ? extends Stream<? extends R>> mapper) {
        return new MapStream<>(inner.flatMap(e -> 
            mapper.apply(e.getKey(), e.getValue())
                .map(v ->
                    new AbstractMap.SimpleEntry<>(
                        e.getKey(), 
                        v
                    )
                )
        ));
    }


    //vadim start -- add method reference support
    public <R> MapStream<R, V> mapKey(Function<? super K, ? extends R> mapper) {
        return new MapStream<>(inner.map(e ->
            new AbstractMap.SimpleEntry<>(
                mapper.apply(e.getKey()),
                e.getValue()
            )
        ));
    }

    public <R> MapStream<K, R> mapValue(Function<? super V, ? extends R> mapper) {
        return new MapStream<>(inner.map(e ->
            new AbstractMap.SimpleEntry<>(
                e.getKey(),
                mapper.apply(e.getValue())
            )
        ));
    }

    public <R> MapStream<R, V> flatMapKey(Function<? super K, ? extends Stream<? extends R>> mapper) {
        return new MapStream<>(inner.flatMap(e ->
            mapper.apply(e.getKey())
                .map(k ->
                    new AbstractMap.SimpleEntry<>(
                        k,
                        e.getValue()
                    )
                )
        ));
    }

    public <R> MapStream<K, R> flatMapValue(Function<? super V, ? extends Stream<? extends R>> mapper) {
        return new MapStream<>(inner.flatMap(e ->
            mapper.apply(e.getValue())
                .map(v ->
                    new AbstractMap.SimpleEntry<>(
                        e.getKey(),
                        v
                    )
                )
        ));
    }
    //vadim end

    @Override
    public IntStream mapToInt(ToIntFunction<? super Map.Entry<K, V>> mapper) {
        return inner.mapToInt(mapper);
    }
    
    public IntStream mapToInt(ToIntBiFunction<? super K, ? super V> mapper) {
        return inner.mapToInt(e -> mapper.applyAsInt(e.getKey(), e.getValue()));
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super Map.Entry<K, V>> mapper) {
        return inner.mapToLong(mapper);
    }
    
    public LongStream mapToLong(ToLongBiFunction<? super K, ? super V> mapper) {
        return inner.mapToLong(e -> mapper.applyAsLong(e.getKey(), e.getValue()));
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super Map.Entry<K, V>> mapper) {
        return inner.mapToDouble(mapper);
    }
    
    public DoubleStream mapToDouble(ToDoubleBiFunction<? super K, ? super V> mapper) {
        return inner.mapToDouble(e -> mapper.applyAsDouble(e.getKey(), e.getValue()));
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super Map.Entry<K, V>, ? extends Stream<? extends R>> mapper) {
        return inner.flatMap(mapper);
    }
    
    public <R> Stream<R> flatMap(BiFunction<? super K, ? super V, ? extends Stream<? extends R>> mapper) {
        return inner.flatMap(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    @Override
    public IntStream flatMapToInt(Function<? super Map.Entry<K, V>, ? extends IntStream> mapper) {
        return inner.flatMapToInt(mapper);
    }
    
    public IntStream flatMapToInt(BiFunction<? super K, ? super V, ? extends IntStream> mapper) {
        return inner.flatMapToInt(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    @Override
    public LongStream flatMapToLong(Function<? super Map.Entry<K, V>, ? extends LongStream> mapper) {
        return inner.flatMapToLong(mapper);
    }
    
    public LongStream flatMapToLong(BiFunction<? super K, ? super V, ? extends LongStream> mapper) {
        return inner.flatMapToLong(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super Map.Entry<K, V>, ? extends DoubleStream> mapper) {
        return inner.flatMapToDouble(mapper);
    }
    
    public DoubleStream flatMapToDouble(BiFunction<? super K, ? super V, ? extends DoubleStream> mapper) {
        return inner.flatMapToDouble(e -> mapper.apply(e.getKey(), e.getValue()));
    }
    
    public Stream<K> keys() {
        return inner.map(Map.Entry::getKey);
    }
    
    public Stream<V> values() {
        return inner.map(Map.Entry::getValue);
    }

    @Override
    public MapStream<K, V> distinct() {
        inner = inner.distinct();
        return this;
    }

    @Override
    public MapStream<K, V> sorted() {
        final Comparator<K> c = (a, b) -> {
            if (a == null && b == null) {
                return 0;
            } else if (a != null && b != null) {
                if (a instanceof Comparable<?>) {
                    @SuppressWarnings("unchecked")
                    final Comparable<K> ac = (Comparable<K>) a;

                    return ac.compareTo(b);
                }
            }
            
            throw new UnsupportedOperationException("Can only sort keys that implement Comparable.");
        };

        inner = inner.sorted((a, b) -> c.compare(a.getKey(), b.getKey()));
        return this;
    }

    @Override
    public MapStream<K, V> sorted(Comparator<? super Map.Entry<K, V>> comparator) {
        inner = inner.sorted(comparator);
        return this;
    }

    public MapStream<K, V> sorted(ToIntBiFunction<? super K, ? super V> comparator) {
        inner = inner.sorted((e1, e2) -> 
            comparator.applyAsInt(e1.getKey(), e1.getValue()) -
            comparator.applyAsInt(e2.getKey(), e2.getValue())
        );
        return this;
    }

    @Override
    public MapStream<K, V> peek(Consumer<? super Map.Entry<K, V>> action) {
        inner = inner.peek(action);
        return this;
    }
    
    public MapStream<K, V> peek(BiConsumer<? super K, ? super V> action) {
        inner = inner.peek(e -> action.accept(e.getKey(), e.getValue()));
        return this;
    }

    @Override
    public MapStream<K, V> limit(long maxSize) {
        inner = inner.limit(maxSize);
        return this;
    }

    @Override
    public MapStream<K, V> skip(long n) {
        inner = inner.skip(n);
        return this;
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<K, V>> action) {
        inner.forEach(action);
    }
    
    public void forEach(BiConsumer<? super K, ? super V> action) {
        inner.forEach(e -> action.accept(e.getKey(), e.getValue()));
    }

    @Override
    public void forEachOrdered(Consumer<? super Map.Entry<K, V>> action) {
        inner.forEachOrdered(action);
    }
    
    public void forEachOrdered(BiConsumer<? super K, ? super V> action) {
        inner.forEachOrdered(e -> action.accept(e.getKey(), e.getValue()));
    }

    @Override
    public Object[] toArray() {
        return inner.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return inner.toArray(generator);
    }

    @Override
    public Map.Entry<K, V> reduce(Map.Entry<K, V> identity, BinaryOperator<Map.Entry<K, V>> accumulator) {
        return inner.reduce(identity, accumulator);
    }

    @Override
    public Optional<Map.Entry<K, V>> reduce(BinaryOperator<Map.Entry<K, V>> accumulator) {
        return inner.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super Map.Entry<K, V>, U> accumulator, BinaryOperator<U> combiner) {
        return inner.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Map.Entry<K, V>> accumulator, BiConsumer<R, R> combiner) {
        return inner.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super Map.Entry<K, V>, A, R> collector) {
        return inner.collect(collector);
    }
    
    public <K2> MapStream<K2, List<V>> groupBy(Function<V, K2> grouper) {
        return inner.map(Map.Entry::getValue)
            .collect(CollectorUtil.groupBy(grouper));
    }

    @Override
    public Optional<Map.Entry<K, V>> min(Comparator<? super Map.Entry<K, V>> comparator) {
        return inner.min(comparator);
    }
    
    public Optional<Map.Entry<K, V>> min(ToIntBiFunction<? super K, ? super V> comparator) {
        return inner.min((e1, e2) -> 
            comparator.applyAsInt(e1.getKey(), e1.getValue()) -
            comparator.applyAsInt(e2.getKey(), e2.getValue())
        );
    }

    @Override
    public Optional<Map.Entry<K, V>> max(Comparator<? super Map.Entry<K, V>> comparator) {
        return inner.max(comparator);
    }
    
    public Optional<Map.Entry<K, V>> max(ToIntBiFunction<? super K, ? super V> comparator) {
        return inner.max((e1, e2) -> 
            comparator.applyAsInt(e1.getKey(), e1.getValue()) -
            comparator.applyAsInt(e2.getKey(), e2.getValue())
        );
    }

    @Override
    public long count() {
        return inner.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super Map.Entry<K, V>> predicate) {
        return inner.anyMatch(predicate);
    }
    
    public boolean anyMatch(BiPredicate<? super K, ? super V> predicate) {
        return inner.anyMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    @Override
    public boolean allMatch(Predicate<? super Map.Entry<K, V>> predicate) {
        return inner.allMatch(predicate);
    }
    
    public boolean allMatch(BiPredicate<? super K, ? super V> predicate) {
        return inner.allMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    @Override
    public boolean noneMatch(Predicate<? super Map.Entry<K, V>> predicate) {
        return inner.noneMatch(predicate);
    }
    
    public boolean noneMatch(BiPredicate<? super K, ? super V> predicate) {
        return inner.noneMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    @Override
    public Optional<Map.Entry<K, V>> findFirst() {
        return inner.findFirst();
    }

    @Override
    public Optional<Map.Entry<K, V>> findAny() {
        return inner.findAny();
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return inner.iterator();
    }

    @Override
    public Spliterator<Map.Entry<K, V>> spliterator() {
        return inner.spliterator();
    }

    @Override
    public boolean isParallel() {
        return inner.isParallel();
    }

    @Override
    public MapStream<K, V> sequential() {
        inner = inner.sequential();
        return this;
    }

    @Override
    public MapStream<K, V> parallel() {
        inner = inner.parallel();
        return this;
    }

    @Override
    public MapStream<K, V> unordered() {
        inner = inner.unordered();
        return this;
    }

    @Override
    public MapStream<K, V> onClose(Runnable closeHandler) {
        inner = inner.onClose(closeHandler);
        return this;
    }

    @Override
    public void close() {
        inner.close();
    }
    
    public Map<K, V> toMap() {
        return inner.collect(Collectors.toMap(
            Map.Entry::getKey, 
            Map.Entry::getValue
        ));
    }
    
    public List<Map.Entry<K, V>> toList() {
        return inner.collect(Collectors.toList());
    }
    
    private MapStream(Stream<Map.Entry<K, V>> inner) {
        this.inner = inner;
    }
}
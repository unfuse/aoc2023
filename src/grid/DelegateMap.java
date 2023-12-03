package grid;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class DelegateMap<K, V>
        implements Map<K, V> {

    protected final Map<K, V> delegateMap;

    protected DelegateMap() {
        this(new HashMap<>());
    }

    protected DelegateMap(final Map<K, V> delegateMap) {
        this.delegateMap = delegateMap;
    }

    @Override
    public int size() {
        return this.delegateMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegateMap.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.delegateMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.delegateMap.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        return this.delegateMap.get(key);
    }

    @Override
    public V put(final K key, final V value) {
        return this.delegateMap.put(key, value);
    }

    @Override
    public V remove(final Object key) {
        return this.delegateMap.remove(key);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        this.delegateMap.putAll(m);
    }

    @Override
    public void clear() {
        this.delegateMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.delegateMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.delegateMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.delegateMap.entrySet();
    }

    @Override
    public boolean equals(final Object o) {
        return this.delegateMap.equals(o);
    }

    @Override
    public int hashCode() {
        return this.delegateMap.hashCode();
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        return this.delegateMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        this.delegateMap.forEach(action);
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        this.delegateMap.replaceAll(function);
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        return this.delegateMap.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        return this.delegateMap.remove(key, value);
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        return this.delegateMap.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(final K key, final V value) {
        return this.delegateMap.replace(key, value);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        return this.delegateMap.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.delegateMap.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.delegateMap.compute(key, remappingFunction);
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return this.delegateMap.merge(key, value, remappingFunction);
    }
}

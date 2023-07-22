package balbucio.pacqit.utils;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class SimpleEntry<K, V, I> implements Map.Entry<K,V> {

    private K key;
    private V value;
    private I value2;

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        return value;
    }

    public I getValue2() {
        return value2;
    }

    public void setValue2(I value2) {
        this.value2 = value2;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

package com.aspire.employee_api_v3.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache<K,V> {

    private final ConcurrentHashMap<K, HashMap<Long,V>> cache;

    public Cache() {
        this.cache = new ConcurrentHashMap<>();
    }

    public V get(K key){

        HashMap<Long,V> timeStampedValue = cache.getOrDefault(key,null);

        if(timeStampedValue!=null) {
            V value = timeStampedValue.values().stream().toList().getFirst();
            timeStampedValue.clear();
            timeStampedValue.put(System.currentTimeMillis(),value);
            return value;
        }

        return null;
    }

    public void put(K key, V value){
        int CACHE_SIZE = 50;

        cache.put(key, new HashMap<>(Map.of(System.currentTimeMillis(),value)));

        if (cache.size() >= CACHE_SIZE){
            long oldestTimeStamp = System.currentTimeMillis();
            long currentTimeStamp;
            K currentKey = null;
            for (K keyFromKeySet : cache.keySet()) {
                currentTimeStamp = cache.get(keyFromKeySet).keySet().stream().toList().getFirst();
                if(currentTimeStamp < oldestTimeStamp) {
                    oldestTimeStamp = currentTimeStamp;
                    currentKey = keyFromKeySet;
                }
            }
            if(currentKey!=null)
                cache.remove(currentKey);

        }
    }
    public void delete(K key){
        cache.remove(key);
    }

    public void clear(){
        cache.clear();
    }

    public boolean containsKey(K key){
        return cache.containsKey(key);
    }
}

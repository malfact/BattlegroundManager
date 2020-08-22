package net.malfact.bgmanager.util;

import java.util.*;
import java.util.function.BiConsumer;

public class BGDataContainer {

    private Map<String, Object> data = new HashMap<>();

    public Object remove(String key){
        return data.remove(key);
    }

    public boolean containsKey(String key){
        return data.containsKey(key);
    }

    public boolean containsValue(Object value){
        return data.containsValue(value);
    }

    public Collection<Object> values(){
        return data.values();
    }

    public Set<String> keySet(){
        return data.keySet();
    }

    public Set<Map.Entry<String, Object>> entrySet(){
        return data.entrySet();
    }

    public Iterator<Map.Entry<String, Object>> iterator(){
        return entrySet().iterator();
    }

    public void forEach(BiConsumer<String, Object> action){
        data.forEach(action);
    }

    public Object get(String key){
        return data.get(key);
    }

    public <C> C get(String key, Class<C> type){
        Object v =  data.get(key);
        if (v != null){
            return type.cast(v);
        }

        return null;
    }

    public Boolean getBool(String key){
        Boolean v = get(key, Boolean.class);
        return v == null ? false : v;
    }

    public Integer getInt(String key){
        Integer v = get(key, Integer.class);
        return v == null ? 0 : v;
    }

    public Double getDouble(String key){
        Double v = get(key, Double.class);
        return v == null ? 0D : v;
    }

    public Float getFloat(String key){
        Float v = get(key, Float.class);
        return v == null ? 0F : v;
    }

    public String getString(String key){
        String v = get(key, String.class);
        return v == null ? "" : v;
    }

    public Object put(String key, Object value){
        return data.put(key, value);
    }

}

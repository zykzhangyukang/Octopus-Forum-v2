package com.zhangyu.coderman.web.cache;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HotTagCache {


    private  Map<String,Integer> properties=new HashMap<>();

    public void setProperties(Map<String, Integer> properties) {
        this.properties = properties;
    }

    public Map<String, Integer> getProperties() {
        return properties;
    }

    public List<String> updateTags(){
        List<String> objects = new ArrayList<>();
        Map<String, Integer> map = HotTagCache.sortByValueDescending(this.properties);
        for(Map.Entry<String,Integer> entry:map.entrySet()){
            String key = entry.getKey();
            if(key!=null&&key.length()!=1&&key.length()<15){
                objects.add(key);
            }
        }
        int size = objects.size();
        if(size>=33){
            return objects.subList(0,33);
        }
        return objects;
    }
    //降序排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> {
            int compare = (o1.getValue()).compareTo(o2.getValue());
            return -compare;
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}

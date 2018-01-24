package com.tpadsz.lab.storm.tutorial.starter.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roger.wang on 2016/3/22.
 */
public enum TridentTransactionType {
    TRANSACTIONAL("transactional"),OPAQUE("opaque"),NON_TRANSACTIONAL("non-transactional");


    private String name;
    private static Map<String,TridentTransactionType> map;
    static{
        map=new HashMap<>(values().length);
        for(TridentTransactionType type:values()){
            map.put(type.getName(),type);
        }
    }
    TridentTransactionType(String name){
        this.name=name;
    }

    public static  TridentTransactionType fromString(String name){
        return map.get(name);
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

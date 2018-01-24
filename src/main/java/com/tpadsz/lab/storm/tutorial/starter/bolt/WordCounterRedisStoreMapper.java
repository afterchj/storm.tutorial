package com.tpadsz.lab.storm.tutorial.starter.bolt;

import backtype.storm.tuple.ITuple;
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;

/**
 * Created by roger.wang on 2016/3/25.
 */
public class WordCounterRedisStoreMapper implements RedisStoreMapper {
    RedisDataTypeDescription type=new RedisDataTypeDescription(RedisDataTypeDescription.RedisDataType.STRING);

    @Override
    public RedisDataTypeDescription getDataTypeDescription() {
        return type;
    }

    @Override
    public String getKeyFromTuple(ITuple tuple) {
        return "word.counter";
    }

    @Override
    public String getValueFromTuple(ITuple tuple) {
        return tuple.getIntegerByField("new.counter").toString();
    }
}

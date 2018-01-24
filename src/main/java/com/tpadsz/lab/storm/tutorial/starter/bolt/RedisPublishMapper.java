package com.tpadsz.lab.storm.tutorial.starter.bolt;

import backtype.storm.tuple.ITuple;
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;

/**
 * Created by roger.wang on 2016/4/22.
 */
public class RedisPublishMapper implements RedisStoreMapper {
    RedisDataTypeDescription type=new RedisDataTypeDescription(RedisDataTypeDescription.RedisDataType.PUBLISH);
    @Override
    public RedisDataTypeDescription getDataTypeDescription() {
        return type;
    }

    @Override
    public String getKeyFromTuple(ITuple tuple) {
        return "redis.drpc.response";
    }

    @Override
    public String getValueFromTuple(ITuple tuple) {
        return tuple.getIntegerByField("return").toString();
    }
}

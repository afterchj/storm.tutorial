package com.tpadsz.lab.storm.tutorial.starter.bolt;

import backtype.storm.tuple.ITuple;
import com.tpadsz.lab.storm.tutorial.starter.utils.Constants;
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;

/**
 * Created by roger.wang on 2016/3/10.
 */
public class EventCounterRedisStoreMapper implements RedisStoreMapper {
    RedisDataTypeDescription type=new RedisDataTypeDescription(RedisDataTypeDescription.RedisDataType.STRING);
    @Override
    public RedisDataTypeDescription getDataTypeDescription() {
        return type;
    }

    @Override
    public String getKeyFromTuple(ITuple iTuple) {
        return Constants.getConstants().getRedisKeyTridentEventCounter();
//        return iTuple.getStringByField("redis.key");
    }

    @Override
    public String getValueFromTuple(ITuple iTuple) {
        return iTuple.getIntegerByField("count").toString();
    }
}

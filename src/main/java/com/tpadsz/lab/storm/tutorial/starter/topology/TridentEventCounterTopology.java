package com.tpadsz.lab.storm.tutorial.starter.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.tuple.Fields;
import com.tpadsz.lab.storm.tutorial.starter.bolt.EventCounterRedisLookupMapper;
import com.tpadsz.lab.storm.tutorial.starter.bolt.EventCounterRedisStoreMapper;
import com.tpadsz.lab.storm.tutorial.starter.entities.TickBean;
import com.tpadsz.lab.storm.tutorial.starter.function.AccumulateTicksFunction;
import com.tpadsz.lab.storm.tutorial.starter.serializer.SimpleKryoSerializer;
import com.tpadsz.lab.storm.tutorial.starter.spout.TridentEventEmittingSpout;
import com.tpadsz.lab.storm.tutorial.starter.utils.Arguments;
import com.tpadsz.lab.storm.tutorial.starter.utils.Constants;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.trident.state.RedisState;
import org.apache.storm.redis.trident.state.RedisStateQuerier;
import org.apache.storm.redis.trident.state.RedisStateUpdater;
import storm.trident.TridentState;
import storm.trident.TridentTopology;

/**
 * Created by roger.wang on 2016/3/11.
 */
public class TridentEventCounterTopology {
    public static void main(String[] args){
        Config config = new Config();
        RedisState.Factory factory=new RedisState.Factory(new JedisPoolConfig.Builder().setHost(Constants.getConstants().getRedisHost()).build());
        TridentTopology topology = new TridentTopology();
        TridentEventEmittingSpout spout=new TridentEventEmittingSpout();
        TridentState counts =
                topology.newStream("spout1", spout)
                        .stateQuery(topology.newStaticState(factory),new Fields("tick"),new RedisStateQuerier(new EventCounterRedisLookupMapper()),new Fields("old.count"))
                        .each(new Fields("tick", "old.count"), new AccumulateTicksFunction(), new Fields("count", "redis.key", "processId"))
                        .groupBy(new Fields("count"))
                        .toStream()
                        .partitionPersist(factory, new Fields("count"), new RedisStateUpdater(new EventCounterRedisStoreMapper()), new Fields())
                        .parallelismHint(2);

        Arguments arg=new Arguments(args);


        if (arg.getLocal()==false) {
            try {
                config.put(Config.NIMBUS_HOST, Constants.getConstants().getNimbusHost());
                config.put(Config.NIMBUS_THRIFT_PORT,Constants.getConstants().getNimbusPort());
                config.registerSerialization(TickBean.class, SimpleKryoSerializer.class);
                config.setNumWorkers(arg.getNumberOfWorkers());
                System.out.format("Prepare to submit topology %s to host %s.\n", arg.getTopologyName(), config.get(Config.NIMBUS_HOST));
                StormSubmitter.submitTopology(arg.getTopologyName(), config,
                        topology.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            config.setDebug(true);
            config.put(Config.NIMBUS_HOST, "192.168.200.160");
            config.put(Config.NIMBUS_THRIFT_PORT,6627);
            config.setMaxTaskParallelism(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(arg.getTopologyName(), config,topology.build());
        }
    }
}

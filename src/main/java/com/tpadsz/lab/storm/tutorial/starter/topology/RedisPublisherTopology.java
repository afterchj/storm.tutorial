package com.tpadsz.lab.storm.tutorial.starter.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.tpadsz.lab.storm.tutorial.starter.bolt.RedisPublishMapper;
import com.tpadsz.lab.storm.tutorial.starter.entities.TickBean;
import com.tpadsz.lab.storm.tutorial.starter.serializer.SimpleKryoSerializer;
import com.tpadsz.lab.storm.tutorial.starter.utils.Arguments;
import com.tpadsz.lab.storm.tutorial.starter.utils.Constants;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.trident.state.RedisState;
import org.apache.storm.redis.trident.state.RedisStateUpdater;
import org.apache.storm.redis.trident.state.TridentRedisSubscriberSpout;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;

/**
 * Created by roger.wang on 2016/4/22.
 */
public class RedisPublisherTopology {
    public static void main(String[] argv){
        Arguments arg = new Arguments(argv);
        Config config = new Config();
        Constants constants = Constants.getConstants();
        TridentTopology topology = new TridentTopology();
        StateFactory stateFactory = new RedisState.Factory(new JedisPoolConfig.Builder().setHost(constants.getRedisHost()).build());
        Stream stream = topology.newStream("redis.drpc",new TridentRedisSubscriberSpout(new JedisPoolConfig.Builder().setHost(constants.getRedisHost()).build(),"redis.drpc.request",new Fields("request")));

        stream.each(new Fields("request"), new BaseFunction() {
            @Override
            public void execute(TridentTuple tuple, TridentCollector collector) {
                String str=tuple.getStringByField("request");
                collector.emit(new Values(str.length()));
            }
        },new Fields("return"))
                .partitionPersist(stateFactory, new Fields("return"), new RedisStateUpdater(new RedisPublishMapper()))
                .parallelismHint(2);

        if (arg.getLocal() == false) {
            try {
                config.put(Config.NIMBUS_HOST, Constants.getConstants().getNimbusHost());
                config.put(Config.NIMBUS_THRIFT_PORT, Constants.getConstants().getNimbusPort());
//                config.registerSerialization(TickBean.class, SimpleKryoSerializer.class);
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
            config.put(Config.NIMBUS_THRIFT_PORT, 6627);
            config.setMaxTaskParallelism(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(arg.getTopologyName(), config, topology.build());
        }
    }
}

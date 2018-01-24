package comtpadsz.lab.storm.tutorial.starter.test;

import com.tpadsz.lab.storm.tutorial.starter.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by roger.wang on 2016/4/22.
 */

public class JedisPusblishSubscribeTest {
    Constants contants = null;
    JedisPool jedisPoolPublisher = null;
    JedisPool jedisPoolSubscriber = null;
    String channelName="testChannel";
    String message="testMessage";

    @Before
    public void setUP(){

        contants = Constants.getConstants();
        System.out.println("Redis host:" + contants.getRedisHost());
        JedisPoolConfig config = new JedisPoolConfig();
        jedisPoolPublisher = new JedisPool(config,contants.getRedisHost(), contants.getRedisPort());
        jedisPoolSubscriber = new JedisPool(config,contants.getRedisHost(), contants.getRedisPort());



    }

    @Test
    public void testPublish(){

        jedisPoolSubscriber.getResource().subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                super.onMessage(channel, message);
                assert channel.equals(channelName);
                assert message.equals(JedisPusblishSubscribeTest.this.message);
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                super.onSubscribe(channel, subscribedChannels);
                jedisPoolPublisher.getResource().publish(message,channelName);
            }
        },channelName);

    }

    @After
    public void tearDown(){
        jedisPoolPublisher.close();
        jedisPoolSubscriber.close();
    }
}

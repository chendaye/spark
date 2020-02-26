package sparkoffline.redis;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisAPI {
    private String host = "192.168.0.104";
    private int port = 6379;
    private Jedis jedis;

    @Before
    public void setUp(){
        jedis = new Jedis(host, port);
    }

    @After
    public void tearDown(){
        jedis.close();
    }

    @Test
    public void test01(){
        jedis.set("lengo", "is rich");
        Assert.assertEquals("is rich", jedis.get("lengo"));
    }

    @Test
    public void redisPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setMaxWaitMillis(1000);
        jedisPoolConfig.setTestOnBorrow(true);

        // 连接池
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port);
        Jedis jedis = jedisPool.getResource();
        Assert.assertEquals("is rich", jedis.get("lengo"));
    }

    @Test
    public void testRedisPool(){
        Jedis jedis = RedisUtils.getJedis();
        Assert.assertEquals("is rich", jedis.get("lengo"));
    }
}

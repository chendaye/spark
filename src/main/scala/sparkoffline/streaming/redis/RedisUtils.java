package sparkoffline.streaming.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {
    private static JedisPool jedisPool = null;
    private static final String HOST = "master";
    private static final int PORT = 6379;

    /**
     * 单例
     *
     * https://juejin.im/post/5d0c8101e51d455a694f954a
     *
     * 静态方法是属于“类”，不属于某个实例，是所有对象实例所共享的方法。
     * 也就是说如果在静态方法上加入synchronized，那么它获取的就是这个类的锁，锁住的就是这个类
     * @return
     */
    public static synchronized Jedis getJedis(){
        if (jedisPool == null){
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(10);
            jedisPoolConfig.setMaxTotal(100);
            jedisPoolConfig.setMaxWaitMillis(1000);
            jedisPoolConfig.setTestOnBorrow(true);
            // 连接池
            jedisPool = new JedisPool(jedisPoolConfig, HOST, PORT);
        }

        return jedisPool.getResource();
    }
}

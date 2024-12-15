package by.smertex.redis.adapter.realisation;

import by.smertex.redis.adapter.interfaces.ConnectionManager;
import by.smertex.redis.adapter.interfaces.RedisMapAdapter;
import by.smertex.redis.exception.ConnectionManagerException;

import java.lang.reflect.Proxy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionManagerBasicRealisation implements ConnectionManager<RedisMapAdapter> {

    private final BlockingQueue<RedisMapAdapter> pool;

    private final PoolConfiguration poolConfiguration;

    private void feelingPool(int size){
        for(int i = 0; i < size; i++){
            pool.add(proxyRedisMapAdapter(initAdapter()));
        }
    }

    private RedisMapAdapter proxyRedisMapAdapter(RedisMapAdapter redisMapAdapter) {
        return (RedisMapAdapter) Proxy.newProxyInstance(ConnectionManagerBasicRealisation.class.getClassLoader(),
                new Class[]{RedisMapAdapter.class},
                ((proxy, method, args) -> {
                    if (method.getName().equals("close") && poolConfiguration.poolSize() > pool.size())
                        synchronized (pool) {
                            if (poolConfiguration.poolSize() > pool.size())
                                return pool.add((RedisMapAdapter) proxy);
                        }
                    return method.invoke(redisMapAdapter, args);
                }));
    }

    private void init(){
        feelingPool(poolConfiguration.poolSize());
    }

    private RedisMapAdapter initAdapter(){
        if(poolConfiguration.password() == null)
            return new RedisMapAdapterBasicRealisation(poolConfiguration.host(), poolConfiguration.port());
        return new RedisMapAdapterBasicRealisation(poolConfiguration.password(), poolConfiguration.port(), poolConfiguration.password());
    }

    private void expansionPool(){
        feelingPool(poolConfiguration.poolExpansion());
    }

    @Override
    public RedisMapAdapter getConnection() {
        try {
            if(pool.isEmpty()){
                synchronized (ConnectionManagerBasicRealisation.class) {
                    if(pool.isEmpty()) expansionPool();
                }
            }
            return pool.take();
        } catch (Exception e) {
            throw new ConnectionManagerException(e.getMessage());
        }
    }

    @Override
    public int getSize() {
        return pool.size();
    }

    public ConnectionManagerBasicRealisation(PoolConfiguration poolConfiguration) {
        this.pool = new LinkedBlockingQueue<>();
        this.poolConfiguration = poolConfiguration;
        init();
    }
}

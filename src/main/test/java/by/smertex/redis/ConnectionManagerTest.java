package by.smertex.redis;

import by.smertex.redis.adapter.interfaces.RedisMapAdapter;
import by.smertex.redis.adapter.realisation.ConnectionManagerBasicRealisation;
import by.smertex.redis.adapter.realisation.PoolConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ConnectionManagerTest {

    private static final String HOST_TEST = "127.0.0.1";

    private static final int PORT_TEST = 6379;

    private static final String PASSWORD_TEST = null;

    private static final int POOL_SIZE_TEST = 10;

    private static final int POOL_EXPANSION_TEST = 5;

    @Test
    public void initPool(){
        PoolConfiguration poolConfiguration = new PoolConfiguration(HOST_TEST, PORT_TEST, PASSWORD_TEST, POOL_SIZE_TEST, POOL_EXPANSION_TEST);
        ConnectionManagerBasicRealisation connectionManagerBasicRealisation = new ConnectionManagerBasicRealisation(poolConfiguration);
        assert connectionManagerBasicRealisation.getSize() == POOL_SIZE_TEST;
    }

    @Test
    public void poolExpansionTest() throws InterruptedException, IOException {
        int testRange = 2;
        PoolConfiguration poolConfiguration = new PoolConfiguration(HOST_TEST, PORT_TEST, PASSWORD_TEST, POOL_SIZE_TEST, POOL_EXPANSION_TEST);
        ConnectionManagerBasicRealisation connectionManagerBasicRealisation = new ConnectionManagerBasicRealisation(poolConfiguration);

        for(int i = 0; i < POOL_SIZE_TEST; i++){
            new Thread(() -> {
                try (RedisMapAdapter redisMapAdapter = connectionManagerBasicRealisation.getConnection()) {
                    Thread.sleep(1500);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        Thread.sleep(200);
        assert connectionManagerBasicRealisation.getSize() == 0;

        for(int i = 0; i < testRange; i++){
            new Thread(() -> {
                try (RedisMapAdapter redisMapAdapter = connectionManagerBasicRealisation.getConnection()) {
                    Thread.sleep(1500);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        Thread.sleep(200);
        assert connectionManagerBasicRealisation.getSize() == POOL_EXPANSION_TEST - testRange;

        Thread.sleep(1500);
        System.out.println(connectionManagerBasicRealisation.getSize());
        assert connectionManagerBasicRealisation.getSize() == POOL_SIZE_TEST;
    }
}

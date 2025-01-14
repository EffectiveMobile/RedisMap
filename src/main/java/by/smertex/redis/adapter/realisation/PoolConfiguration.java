package by.smertex.redis.adapter.realisation;

public record PoolConfiguration(String host,
                                int port,
                                String password,
                                int poolSize,
                                int poolExpansion) {
}

package by.smertex.redis.adapter.interfaces;

import java.io.Closeable;

public interface ConnectionManager<T extends Closeable> {

    T getConnection();

    int getSize();

}

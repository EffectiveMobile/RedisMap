package by.smertex.redis.adapter.interfaces;

public interface ConnectionManager<T> {

    T getConnection();

    int getSize();

}

package com.progteamf.test.imageviewer.db.core;

import java.util.List;

public interface CRUD<T> {

    void create(T t);

    T read(String id);

    List<T> readAll();

    void update(T t);

    void delete(String id);
}

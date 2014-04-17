package ru.adhocapp.instaprint.db.entity;

import java.io.Serializable;

/**
 * Created by Lenovo on 12.04.2014.
 */
public abstract class Entity  implements Serializable{
    protected Long id;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

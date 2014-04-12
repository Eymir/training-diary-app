package ru.adhocapp.instaprint.db.entity;

/**
 * Created by Lenovo on 12.04.2014.
 */
public abstract class Entity {
    protected Long id;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

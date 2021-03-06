package com.kwery.models;

import com.google.common.annotations.VisibleForTesting;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractBaseModel {
    public static final String ID_COLUMN = "id";
    public static final String CREATED_COLUMN = "created";
    public static final String UPDATED_COLUMN = "updated";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = ID_COLUMN)
    protected Integer id;

    @Column(name = CREATED_COLUMN)
    protected Long created;

    @Column(name = UPDATED_COLUMN)
    protected Long updated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCreated() {
        return created;
    }

    //This value is overridden in the Dao while saving the object through JPA
    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    //This value is overridden in the Dao while saving the object through JPA
    public void setUpdated(Long updated) {
        this.updated = updated;
    }
}

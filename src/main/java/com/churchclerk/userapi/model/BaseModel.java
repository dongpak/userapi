/*
 *
 */
package com.churchclerk.userapi.model;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 *
 */
public abstract class BaseModel {
    private UUID    id;
    private boolean active;
    private Date    createdDate;
    private String  createdBy;
    private Date    updatedDate;
    private String  updatedBy;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModel baseModel = (BaseModel) o;
        return active == baseModel.active &&
                Objects.equals(id, baseModel.id) &&
                Objects.equals(createdDate, baseModel.createdDate) &&
                Objects.equals(createdBy, baseModel.createdBy) &&
                Objects.equals(updatedDate, baseModel.updatedDate) &&
                Objects.equals(updatedBy, baseModel.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, active, createdDate, createdBy, updatedDate, updatedBy);
    }

    /**
     *
     * @param source
     */
    public void copy(BaseModel source) {
        setId(source.getId());
        setActive(source.isActive());
        setCreatedDate(source.getCreatedDate());
        setCreatedBy(source.getCreatedBy());
        setUpdatedDate(source.getUpdatedDate());
        setUpdatedBy(source.getUpdatedBy());
    }
}

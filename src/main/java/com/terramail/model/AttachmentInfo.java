package com.terramail.model;

import java.io.Serializable;
import java.util.Objects;

public class AttachmentInfo implements Serializable {

    private final String name;
    private final long size;
    private final String contentType;

    public AttachmentInfo(String name, long size, String contentType) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.size = size;
        this.contentType = contentType != null ? contentType : "application/octet-stream";
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttachmentInfo that = (AttachmentInfo) o;
        return size == that.size && Objects.equals(name, that.name) && Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size, contentType);
    }

    @Override
    public String toString() {
        return name + " (" + size + " bytes)";
    }
}

package com.terramail.model;

import java.util.Objects;

public class SortOrder {

    public enum Field {
        SUBJECT, FROM, DATE, TO, CC
    }

    public enum Direction {
        ASC, DESC
    }

    private final Field field;
    private final Direction direction;

    public SortOrder(Field field, Direction direction) {
        this.field = Objects.requireNonNull(field, "Field cannot be null");
        this.direction = Objects.requireNonNull(direction, "Direction cannot be null");
    }

    public Field getField() {
        return field;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortOrder sortOrder = (SortOrder) o;
        return field == sortOrder.field && direction == sortOrder.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, direction);
    }

    public SortOrder reversed() {
        return new SortOrder(field, direction == Direction.ASC ? Direction.DESC : Direction.ASC);
    }

    @Override
    public String toString() {
        return field + " " + direction;
    }
}

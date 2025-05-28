package com.simon.simontree.enums;

public enum TypeOfModification {
    ADD(1),
    REMOVE(-1),
    MODIFY(3);

    private final int value;

    TypeOfModification(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

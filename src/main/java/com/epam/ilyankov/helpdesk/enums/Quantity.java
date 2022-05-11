package com.epam.ilyankov.helpdesk.enums;

public enum Quantity {
    TEN(10),FIVE(5),TWENTY(20);

    public final Integer label;

    Quantity(Integer label) {
        this.label = label;
    }
}

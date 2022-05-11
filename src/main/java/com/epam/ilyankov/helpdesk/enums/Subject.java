package com.epam.ilyankov.helpdesk.enums;

public enum Subject {
    NEW_TICKET_FOR_APPROVAL("New ticket for approval"),
    TICKET_WAS_APPROVED(" Ticket was approved"),
    TICKET_WAS_DECLINED("Ticket was declined"),
    TICKET_WAS_CANCELLED("Ticket was cancelled"),
    TICKET_WAS_DONE("Ticket was done"),
    FEEDBACK_WAS_PROVIDED("Feedback was provided");

    public final String meaning;

    Subject(String meaning) {
        this.meaning = meaning;
    }
}

package com.epam.ilyankov.helpdesk.domain.ticket;

public enum State {
    Draft("Draft"),
    New("New"),
    Approved("Approved"),
    Declined("Declined"),
    In_Progress("In Progress"),
    Done("Done"),
    Canceled("Canceled");

    public final String meaning;

    State(String meaning) {
        this.meaning = meaning;
    }
}

package com.ilyankov.helpdesk.domain.history;

public enum Story {
    TICKET_IS_CREATED("Ticket is created"),
    TICKET_IS_EDITED("Ticket is edited"),
    TICKET_STATUS_IS_CHANGED("Ticket Status is changed"),
    TICKET_STATUS_IS_CHANGED_FROM_X_TO_Y("Ticket Status is changed from %s to %s"),
    FILE_IS_ATTACHED("File is attached"),
    FILE_ID_ATTACHED_NAME("File is attached: %s"),
    FILE_IS_REMOVED("File is removed"),
    FILE_ID_REMOVED_NAME("File is removed: %s");

    public final String meaning;

    Story(String meaning) {
        this.meaning = meaning;
    }
}

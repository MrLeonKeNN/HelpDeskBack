package com.ilyankov.helpdesk.domain.ticket;

public enum Action {
    Submit("Submit"),
    Cancel("Cancel"),
    Approve("Approve"),
    Decline("Decline"),
    Assign_to_Me("Assign to Me"),
    Done("Done"),
    LEAVE_FEEDBACK("Leave Feedback"),
    VIEW_FEEDBACK("View Feedback");

    public final String meaning;

    Action(String meaning) {
        this.meaning = meaning;
    }
}

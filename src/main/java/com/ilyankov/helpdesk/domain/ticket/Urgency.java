package com.ilyankov.helpdesk.domain.ticket;

public enum  Urgency implements Comparable<Urgency> {
    Critical,
    High,
    Average,
    Low
}

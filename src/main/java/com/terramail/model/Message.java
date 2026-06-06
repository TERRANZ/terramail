package com.terramail.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Message implements Serializable {

    private long id;
    private long folderId;
    private String from;
    private String to;
    private String cc;
    private String subject;
    private Instant date;
    private String body;
    private boolean seen;
    private boolean flagged;
    private List<AttachmentInfo> attachments;

    public Message() {
        this.attachments = List.of();
    }

    public Message(long folderId, String from, String to, String cc, String subject, Instant date, String body) {
        this.folderId = folderId;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.subject = subject;
        this.date = date;
        this.body = body;
        this.attachments = List.of();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public List<AttachmentInfo> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentInfo> attachments) {
        this.attachments = attachments != null ? attachments : List.of();
    }

    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{id=" + id + ", subject='" + subject + "', from='" + from + "'}";
    }
}

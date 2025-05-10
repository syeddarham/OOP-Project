package com.emailplatform.model;

import java.util.Date;

public class Email {
    private int id;
    private int senderId;
    private String recipientEmail;
    private String subject;
    private String body;
    private Date sentDate;
    private boolean isRead;
    private boolean isDeleted;
    private boolean isSpam;
    private boolean isSnoozed;
    private Date snoozeDate;

    public Email(int id, int senderId, String recipientEmail, String subject, String body,
            Date sentDate, boolean isRead, boolean isDeleted, boolean isSpam,
            boolean isSnoozed, Date snoozeDate) {
        this.id = id;
        this.senderId = senderId;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
        this.sentDate = sentDate;
        this.isRead = isRead;
        this.isDeleted = isDeleted;
        this.isSpam = isSpam;
        this.isSnoozed = isSnoozed;
        this.snoozeDate = snoozeDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public void setSpam(boolean isSpam) {
        this.isSpam = isSpam;
    }

    public boolean isSnoozed() {
        return isSnoozed;
    }

    public void setSnoozed(boolean isSnoozed) {
        this.isSnoozed = isSnoozed;
    }

    public Date getSnoozeDate() {
        return snoozeDate;
    }

    public void setSnoozeDate(Date snoozeDate) {
        this.snoozeDate = snoozeDate;
    }
}
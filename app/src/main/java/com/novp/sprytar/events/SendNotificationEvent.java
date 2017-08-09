package com.novp.sprytar.events;


import android.support.annotation.NonNull;

public class SendNotificationEvent {

    private String subject;
    private String message;

    private SendNotificationEvent(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Builder {
        private String subject;
        private String message;

        public Builder setSubject(@NonNull String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setMessage(@NonNull String message) {
            this.message = message;
            return this;
        }

        public SendNotificationEvent build() {
            return new SendNotificationEvent(subject, message);
        }
    }
}

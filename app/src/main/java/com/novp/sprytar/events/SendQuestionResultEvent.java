package com.novp.sprytar.events;

public class SendQuestionResultEvent {

    private int questionId;
    private int localQuestionId;
    private boolean questionStatus;

    public SendQuestionResultEvent(int questionId, int localQuestionId,boolean questionStatus) {
        this.questionId = questionId;
        this.localQuestionId = localQuestionId;
        this.questionStatus = questionStatus;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public boolean isQuestionStatus() {
        return questionStatus;
    }

    public void setQuestionStatus(boolean questionStatus) {
        this.questionStatus = questionStatus;
    }

    public int getLocalQuestionId() {
        return localQuestionId;
    }

    public void setLocalQuestionId(int localQuestionId) {
        this.localQuestionId = localQuestionId;
    }
}

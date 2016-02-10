package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Aaditya Deowanshi
 */

public class Question {
    @SerializedName("questionId")
    private int questionId;
    @SerializedName("question")
    private String question;
    @SerializedName("options")
    private List<Option> options;
    @SerializedName("isAnswered")
    private boolean isAnswered;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setIsAnswered(boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

}

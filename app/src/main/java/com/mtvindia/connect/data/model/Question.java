package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sibi on 02/11/15.
 */
public class Question {
    @SerializedName("id")
    private int questionId;
    @SerializedName("question")
    private String question;
    @SerializedName("options")
    private List<Option> options;

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
}

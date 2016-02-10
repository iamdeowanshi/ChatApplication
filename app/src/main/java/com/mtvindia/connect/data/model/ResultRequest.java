package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aaditya Deowanshi
 */

public class ResultRequest {

    @SerializedName("primaryQuestionId")
    private int primaryQuestionId;
    @SerializedName("questionId")
    private int questionId;
    @SerializedName("optionId")
    private int optionId;

    public int getPrimaryQuestionId() {
        return primaryQuestionId;
    }

    public void setPrimaryQuestionId(int primaryQuestionId) {
        this.primaryQuestionId = primaryQuestionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

}

package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sibi on 02/11/15.
 */
public class AnswerRequest {
    @SerializedName("primaryQuestionId")
    private int primaryQuestionId;
    @SerializedName("primaryOptionId")
    private int primaryOptionId;
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

    public int getPrimaryOptionId() {
        return primaryOptionId;
    }

    public void setPrimaryOptionId(int primaryOptionId) {
        this.primaryOptionId = primaryOptionId;
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

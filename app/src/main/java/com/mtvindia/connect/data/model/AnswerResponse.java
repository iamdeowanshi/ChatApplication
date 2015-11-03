package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sibi on 02/11/15.
 */
public class AnswerResponse {
    @SerializedName("questionId")
    private int questionId;
    @SerializedName("question")
    private String question;
    @SerializedName("optionChoosed")
    private Option option;
    @SerializedName("matchingUserCount")
    private int matchingUserCount;

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

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public int getMatchingUserCount() {
        return matchingUserCount;
    }

    public void setMatchingUserCount(int matchingUserCount) {
        this.matchingUserCount = matchingUserCount;
    }
}

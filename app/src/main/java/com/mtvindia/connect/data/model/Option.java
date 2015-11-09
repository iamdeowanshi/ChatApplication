package com.mtvindia.connect.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sibi on 02/11/15.
 */
public class Option {
    @SerializedName("optionId")
    private int optionId;
    @SerializedName("option")
    private String option;
    @SerializedName("optionUrl")
    private String optionUrl;

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getOptionUrl() {
        return optionUrl;
    }

    public void setOptionUrl(String optionUrl) {
        this.optionUrl = optionUrl;
    }
}

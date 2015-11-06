package com.mtvindia.connect.util;

import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.Question;
import com.mtvindia.connect.data.model.ResultResponse;
import com.mtvindia.connect.data.model.User;

import javax.inject.Inject;

/**
 * Created by Sibi on 06/11/15.
 */
public class UserPreference {

    @Inject PreferenceUtil preferenceUtil;

    public UserPreference() {
        Injector.instance().inject(this);
    }

    public void saveUser(User user) {
        preferenceUtil.save(PreferenceUtil.USER, user);
    }

    public User readUser() {
        return (User) preferenceUtil.read(PreferenceUtil.USER, User.class);
    }

    public void saveQuestionCount(int count) {
        preferenceUtil.save(PreferenceUtil.QUESTIONS_ANSWERED, count);
    }

    public int readQuestionCount() {
        return preferenceUtil.readInt(PreferenceUtil.QUESTIONS_ANSWERED, 0);
    }

    public void saveQuestionResponse(Question question) {
        preferenceUtil.save(PreferenceUtil.QUESTION_RESPONSE, question);
    }

    public Question readQuestionResponse() {
        return (Question) preferenceUtil.read(PreferenceUtil.QUESTION_RESPONSE, Question.class);
    }

    public void saveResultResponse(ResultResponse response) {
        preferenceUtil.save(PreferenceUtil.RESULT_RESPONSE, response);
    }

    public ResultResponse readResultResponse() {
        return (ResultResponse) preferenceUtil.read(PreferenceUtil.RESULT_RESPONSE, ResultResponse.class);
    }

    public void savePrimaryQuestionId(int id) {
        preferenceUtil.save(PreferenceUtil.PRIMARY_QUESTION_ID, id);
    }

    public int readPrimaryQuestionId() {
        return preferenceUtil.readInt(PreferenceUtil.PRIMARY_QUESTION_ID, 0);
    }

    public void saveLoginStatus(boolean status) {
        preferenceUtil.save(PreferenceUtil.IS_IN_REGISTRATION, status);
    }

    public boolean readLoginStatus() {
        return preferenceUtil.readBoolean(PreferenceUtil.IS_IN_REGISTRATION, false);
    }

}

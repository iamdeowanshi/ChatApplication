package com.mtvindia.connect.util;

import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.Question;
import com.mtvindia.connect.data.model.ResultResponse;

import javax.inject.Inject;

/**
 * Created by Sibi on 06/11/15.
 */

/**
 * Utility class that which is used to save question related data and makes use of Preference util
 * class.
 */
public class QuestionPreference {

    @Inject PreferenceUtil preferenceUtil;

    public static final String QUESTIONS_ANSWERED = "_NO_OF_QUESTIONS_ANSWERED";
    public static final String PRIMARY_QUESTION_ID = "_PRIMARY_QUESTION_ID";
    public static final String RESULT_RESPONSE = "_RESULT_RESPONSE";
    public static final String QUESTION_RESPONSE = "_QUESTION_RESPONSE";
    public static final String OPTION_SELECTED =  "_OPTION_SELECTED";

    public QuestionPreference() {
        Injector.instance().inject(this);
    }

    /**
     * Saves number of questions answered.
     * @param count
     */
    public void saveQuestionCount(int count) {
        preferenceUtil.save(QuestionPreference.QUESTIONS_ANSWERED, count);
    }

    /**
     * Returns number of questions answered.
     * @return
     */
    public int readQuestionCount() {
        return preferenceUtil.readInt(QuestionPreference.QUESTIONS_ANSWERED, 0);
    }

    /**
     * Saves last unanswered questions.
     * @param question
     */
    public void saveQuestionResponse(Question question) {
        preferenceUtil.save(QuestionPreference.QUESTION_RESPONSE, question);
    }

    /**
     * Returns last saved question.
     * @return
     */
    public Question readQuestionResponse() {
        return (Question) preferenceUtil.read(QuestionPreference.QUESTION_RESPONSE, Question.class);
    }

    /**
     * Saves result of last answered question.
     * @param response
     */
    public void saveResultResponse(ResultResponse response) {
        preferenceUtil.save(QuestionPreference.RESULT_RESPONSE, response);
    }

    /**
     * Returns result of last answered question.
     * @return
     */
    public ResultResponse readResultResponse() {
        return (ResultResponse) preferenceUtil.read(QuestionPreference.RESULT_RESPONSE, ResultResponse.class);
    }

    /**
     * Saves primary/first question Id.
     * On the basis of first question, other question will be displayed.
     * @param id
     */
    public void savePrimaryQuestionId(int id) {
        preferenceUtil.save(QuestionPreference.PRIMARY_QUESTION_ID, id);
    }

    /**
     * Returns primary question Id.
     * @return
     */
    public int readPrimaryQuestionId() {
        return preferenceUtil.readInt(QuestionPreference.PRIMARY_QUESTION_ID, 0);
    }

    /**
     * Saves id of answer selected.
     * @param id
     */
    public void saveOptionSelected(int id) {
        preferenceUtil.save(QuestionPreference.OPTION_SELECTED, id);
    }

    /**
     * Returns id of last answer selected.
     * @return
     */
    public int readOptionSelected() {
        return preferenceUtil.readInt(QuestionPreference.OPTION_SELECTED, 0);
    }

    /**
     * Clears question shared preference.
     */
    public void clearPreference() {
        preferenceUtil.remove(QUESTION_RESPONSE);
        preferenceUtil.remove(PRIMARY_QUESTION_ID);
        preferenceUtil.remove(RESULT_RESPONSE);
        preferenceUtil.remove(QUESTION_RESPONSE);
    }

}

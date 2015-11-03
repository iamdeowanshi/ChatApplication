package com.mtvindia.connect.data.api;

import com.mtvindia.connect.app.Config;
import com.mtvindia.connect.data.model.AnswerRequest;
import com.mtvindia.connect.data.model.AnswerResponse;
import com.mtvindia.connect.data.model.LoginRequest;
import com.mtvindia.connect.data.model.MatchUserResponse;
import com.mtvindia.connect.data.model.Question;
import com.mtvindia.connect.data.model.User;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;
import rx.Observable;

/**
 * @author Farhan Ali
 */
public interface MtvConnectApi {

    @POST(Config.USER_LOGIN)
    Observable<Response> login(@Body LoginRequest request);

    @PUT(Config.USER_UPDATE)
    Observable<User> update(@Body User user, @Header("Authorization") String accessToken);

    @GET(Config.QUESTION_REQUEST)
    Observable<Question> primaryQuestionRequest();

    @GET(Config.QUESTION_REQUEST)
    Observable<Question> secondaryQuestionRequest(@Query("questionId") Integer pq_id);

    @POST(Config.ANSWER_REQUEST)
    Observable<AnswerResponse> answerDetails(@Body AnswerRequest answerRequest);

    @GET(Config.MATCH_USER)
    Observable<MatchUserResponse> matchUser();

}
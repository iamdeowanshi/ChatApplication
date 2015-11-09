package com.mtvindia.connect.app;

/**
 * This class is responsible for keeping application configuration as constants.
 *
 * @author Farhan Ali
 */
public class Config {

    //--------------------------------------------------------------------------------
    // API related constants/configurations - used in ApiModule
    //--------------------------------------------------------------------------------
    public static final String BASE_URL_PRODUCTION  = "http://54.254.231.131:1337";
    public static final String BASE_URL_DEVELOP     = "http://mtvlogin.getsandbox.com";
    public static final String BASE_URL_MOCK     = "http://mtvconnect.getsandbox.com";

    // Active base url
    public static final String BASE_URL             = BASE_URL_MOCK;

    public static final String USER_LOGIN           = "/login";
    public static final String USER_UPDATE          = "/profile/update";
    public static final String QUESTION_REQUEST     = "/question";
    public static final String ANSWER_REQUEST       = "/answer";
    public static final String MATCH_USER           = "/matches";

    // Cache size in bytes, 50 MB = 50 * 1024 KB, 1 KB = 1024 Bytes
    public static final int HTTP_DISK_CACHE_SIZE    = (int) 50 * 1024 * 1024;

    // User agent - must required for some api
    public static final String USER_AGENT           = "MtvConnect-Android-App";

}

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
    public static final String BASE_URL_DEVELOP     = "http://54.254.231.131:1337";
    public static final String BASE_URL_MOCK        = "http://mtvconnect.getsandbox.com";
    public static final String CHAT_SERVER          = "54.254.231.131";
    public static final int PORT                    = 1337;

    // Active base url
    public static final String BASE_URL             = BASE_URL_DEVELOP;

    public static final String USER_LOGIN           = "/login/";
    public static final String USER_UPDATE          = "/profile/update/";
    public static final String QUESTION_REQUEST     = "/question/";
    public static final String ANSWER_REQUEST       = "/answer/";
    public static final String MATCH_USER           = "/matches/";
    public static final String ABOUT_USER           = "/profile/{user_id}";
    public static final String PROFILE_PIC_UPDATE   = "/profile/upload";
    public static final String CHAT_LIST            = "/user/chatlist";

    // Cache size in bytes, 50 MB = 50 * 1024 KB, 1 KB = 1024 Bytes
    public static final int HTTP_DISK_CACHE_SIZE    = (int) 50 * 1024 * 1024;

    // User agent - must required for some api
    public static final String USER_AGENT           = "MtvConnect-Android-App";


    //--------------------------------------------------------------------------------
    // Database related constants/configurations - used in OrmModule
    //--------------------------------------------------------------------------------

    public static final String DB_NAME               = "MTV_DATABASE";
    public static final long DB_VERSION              = 1;
}

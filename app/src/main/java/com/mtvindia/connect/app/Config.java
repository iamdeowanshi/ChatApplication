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
    //public static final String BASE_URL_PRODUCTION  = "http://mtvLoginApi.getsandbox.com";
    public static final String BASE_URL_PRODUCTION  = "http://mtvlogin.getsandbox.com";
    public static final String BASE_URL_DEVELOP     = "";

    public static final String BASE_URL             = BASE_URL_PRODUCTION;

    public static final String USER_LOGIN           = "/login";
    public static final String USER_UPDATE          = "/update";

    // Cache size in bytes, 50 MB = 50 * 1024 KB, 1 KB = 1024 Bytes
    public static final int HTTP_DISK_CACHE_SIZE    = (int) 50 * 1024 * 1024;

    // User agent - must required for some api
    public static final String USER_AGENT           = "MtvConnect-Android-App";

}

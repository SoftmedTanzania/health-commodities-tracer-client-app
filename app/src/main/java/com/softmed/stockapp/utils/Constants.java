package com.softmed.stockapp.utils;


public class Constants {

    public static String BASE_URL = "http://173.255.220.51";

    /** Database Names **/
    public static final String DEVICE_REGISTRATION_ID = "device_registration_id";

    public static final int REFERRAL_STATUS_NEW = 0;
    public static final int REFERRAL_STATUS_COMPLETED = 1;
    public static final int REFERRAL_STATUS_REJECTED = -1;

    public static final String POST_DATA_TYPE_REFERRAL = "r";
    public static final String POST_DATA_REFERRAL_FEEDBACK = "rf";
    public static final String POST_DATA_TYPE_PATIENT = "p";
    public static final String POST_DATA_TYPE_TB_PATIENT = "tp";
    public static final String POST_DATA_TYPE_ENCOUNTER = "e";
    public static final String POST_DATA_TYPE_APPOINTMENTS = "a";

    public static final int ENTRY_NOT_SYNCED = 0;
    public static final int ENTRY_SYNCED = 1;

    public static final int RESPONCE_SUCCESS = 200;
    public static final int RESPONCE_CREATED = 201;


}
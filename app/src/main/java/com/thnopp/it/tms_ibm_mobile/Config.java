package com.thnopp.it.tms_ibm_mobile;

/**
 * Created by CEVAUser on 5/30/2017.
 */

public class Config {

    // File upload url (replace the ip with your server address)
    // public static final String CHECK_LIMIT_URL = "http://192.168.137.153/qc/getlimit_model.php";

    // public static final String UPDATE_TIME_URL = "http://192.168.137.153/qc/update_time.php";

    public static final String LOGIN_URL = "http://203.154.71.73:8080/ibm_tms/api/login";

    public static final String LOAD_VIN_URL = "http://203.154.71.73:8080/ibm_tms/api/vin";//  = "http://203.154.71.73/tms/loadvin.php";

    public static final String CHK_DEALER = "http://203.154.71.73/tms/chkdealer.php";

    public static final String CHK_VIN = "http://203.154.71.73:8080/ibm_tms/api/updatevin";

    public static final String UPDATE_URL = "http://203.154.71.73/tms_ibm/ibm_tms.apk";

    public static final String DEALER_INST_URL = "http://203.154.71.73:8080/ibm_tms/api/inst";
    public static final String UPDATE_GPS_URL = "http://203.154.71.73:8080/ibm_tms/api/gps";

    public static final String WI_URL = "http://203.154.71.73:8080/ibm_tms/api/wi";
    public static final String GET_STATUS_URL = "http://203.154.71.73:8080/ibm_tms/api/getstatus";
    public static final String UPDATE_STATUS_URL = "http://203.154.71.73:8080/ibm_tms/api/sendstatus";
    public static final String GET_ORDER_URL = "http://203.154.71.73:8080/ibm_tms/api/vin";
    public static final String ADD_ORDER_URL = "http://203.154.71.73:8080/ibm_tms/api/save";
    public static final String DEL_ORDER_URL = "http://203.154.71.73:8080/ibm_tms/api/del";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    public static final String HEAD_KEY = "Authorization";
    public static final String HEAD_VALUE = "Basic Y2V2YTpWcjF0ZWFtIQ==";

}
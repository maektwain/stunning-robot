package com.upscale.front.config;

/**
 * Application constants.
 */
public final class Constants {

    //Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";
    // Spring profile for development and production, see http://jhipster.github.io/profiles/
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    // Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
    public static final String SPRING_PROFILE_CLOUD = "cloud";
    // Spring profile used when deploying to Heroku
    public static final String SPRING_PROFILE_HEROKU = "heroku";
    // Spring profile used to disable swagger
    public static final String SPRING_PROFILE_NO_SWAGGER = "no-swagger";
    // Spring profile used to disable running liquibase
    public static final String SPRING_PROFILE_NO_LIQUIBASE = "no-liquibase";
    // Google Credentials Download
    public static final String GOOGLE_CREDENTIALS_URL = "https://s3.ap-south-1.amazonaws.com/upscale-mumbai/3db8973f445e.json";
    // Google Credentials File download Path
    public static final String GOOGLE_CREDENTIALS_DOWNLOAD_PATH = "/home/vcap";
    // File download path
    public static final String FILE_TEMP_DOWNLOAD = "/home/vcap/tmp";
    // Mifos URL
    public static final String MIFOS_URL = "https://ec2-52-88-126-139.us-west-2.compute.amazonaws.com:8443/fineract-provider/api/v1";

    public static final String SYSTEM_ACCOUNT = "system";

    private Constants() {
    }
}

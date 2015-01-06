package io.github.data4all;

public class Constants {

    private static final boolean DEV_MODE              = true;

    private static final String  CONSUMER_KEY_PROD     = "5bSHgylPtS0mf3q3hDBG2BOz6PoHWolr2W8PMDCT";
    private static final String  CONSUMER_SECRET_PROD  = "9ej0TvsLOQeJPb4xVKFoeoFSLA1iF9jHqll2Jovb";
    private static final String  CONSUMER_KEY_DEV      = "pXTyQeCIqpXN3FrHc3lsDQoZbmaH3wnhMANrRNyH";
    private static final String  CONSUMER_SECRET_DEV   = "4vN5TU43n7JlGRjBKxZ18VZZXj8XLy9kKvlAkcPl";
    
    private static final String  OSM_API_URL_DEV       = "http://master.apis.dev.openstreetmap.org";
    private static final String  OSM_API_URL_PROD      = "http://www.openstreetmap.org";

    public static final String   CONSUMER_KEY          = (DEV_MODE) ? CONSUMER_KEY_DEV
                                                               : CONSUMER_KEY_PROD;
    public static final String   CONSUMER_SECRET   = (DEV_MODE) ? CONSUMER_SECRET_DEV
                                                               : CONSUMER_SECRET_PROD;
    
    public static final String   SCOPE                 = (DEV_MODE) ? OSM_API_URL_DEV
                                                               : OSM_API_URL_PROD;

    public static final String   REQUEST_URL           = SCOPE
                                                               + "/oauth/request_token";
    public static final String   ACCESS_URL            = SCOPE
                                                               + "/oauth/access_token";
    public static final String   AUTHORIZE_URL         = SCOPE
                                                               + "/oauth/authorize";
    
    public static final String API_USERDETAILS       = SCOPE + "api/0.6/user/details";

    public static final String   ENCODING              = "UTF-8";

    public static final String   OAUTH_CALLBACK_SCHEME = "x-oauthflow";
    public static final String   OAUTH_CALLBACK_HOST   = "callback";
    public static final String   OAUTH_CALLBACK_URL    = OAUTH_CALLBACK_SCHEME
                                                               + "://"
                                                               + OAUTH_CALLBACK_HOST;

}
package io.github.data4all;

public class Constants {

    public static final String CONSUMER_KEY          = "haIEyxHINeAW4dQAzzCYDkCeN9DtpoPBr7zy9hdm";
    public static final String CONSUMER_SECRET       = "0RMfQptjND9hEBDPuCykeqkYK6I6YGBDcDo2vggM";

    public static final String SCOPE                 = "http://www.openstreetmap.org";
    public static final String REQUEST_URL           = "http://www.openstreetmap.org/oauth/request_token";
    public static final String ACCESS_URL            = "http://www.openstreetmap.org/oauth/access_token";
    public static final String AUTHORIZE_URL         = "http://www.openstreetmap.org/oauth/authorize";

    public static final String ENCODING              = "UTF-8";

    public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow";
    public static final String OAUTH_CALLBACK_HOST   = "callback";
    public static final String OAUTH_CALLBACK_URL    = OAUTH_CALLBACK_SCHEME
                                                             + "://"
                                                             + OAUTH_CALLBACK_HOST;

}
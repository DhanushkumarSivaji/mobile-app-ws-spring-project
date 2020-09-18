package com.dhanush.app.ws.security;

import com.dhanush.app.ws.SpringApplicationContext;

public class SecurityConstants {
	public static final long EXPIRATION_TIME = 864000000; //10 DAYS
	public static final long RESET_PASSWORD_TOKEN_EXPIRATION_TIME = 3600000; //1 HOUR
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	public static final String EMAIL_VERIFICATION_URL = "/users/email-verification";
	public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
	public static final String PASSWORD_RESET_URL = "/users/password-reset";
	public static final String H2_CONSOLE = "/h2-console/**";
	
	 public static String getTokenSecret()
	    {
	        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
	        return appProperties.getTokenSecret();
	    }
}

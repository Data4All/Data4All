package io.github.data4all.model.data;

/**
 * Simple model for the osm user.
 * @author fkirchge
 *
 */
public class User {
	
	/**
	 * User details for the login procedure
	 */
	private String username;
	private String oauthToken; 
	private boolean isLoggedIn; 
	
	/**
	 * Default constructor
	 * @param username
	 * @param loginToken
	 */
	public User(String username, String loginToken) {
		this.username = username;
		this.oauthToken = loginToken;
	}
	
	/**
	 * Default constructor 
	 * @param username
	 * @param loginToken
	 * @param status
	 */
	public User(String username, String loginToken, boolean status) {
		this.username = username;
		this.oauthToken = loginToken;
		this.isLoggedIn = status;
	}	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLoginToken() {
		return oauthToken;
	}

	public void setLoginToken(String loginToken) {
		this.oauthToken = loginToken;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

}
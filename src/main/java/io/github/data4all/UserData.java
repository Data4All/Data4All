package io.github.data4all;


public class UserData {

    private String      username;
    private String      accesstoken;
    private String      accesstokensecret;

    public UserData() {
    }

    public UserData(String username) {
        this.username = username;
    }

    public UserData(String username, String accesstoken,
            String accesstokensecret) {
        this.username = username;
        this.setAccesstoken(accesstoken);
        this.setAccesstokenSecret(accesstokensecret);
    }

    /**
     * return the username for this server, may be null
     * 
     * @return
     */
    public String getUsername() {
        return username;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getAccesstokenSecret() {
        return accesstokensecret;
    }

    public void setAccesstokenSecret(String accesstokensecret) {
        this.accesstokensecret = accesstokensecret;
    }
}

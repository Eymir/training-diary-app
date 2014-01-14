package ru.td.portal.domain;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 1/14/14
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserData {
    String googleAuthToken;

    public String getGoogleAuthToken() {
        return googleAuthToken;
    }

    public void setGoogleAuthToken(String googleAuthToken) {
        this.googleAuthToken = googleAuthToken;
    }
}

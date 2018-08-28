package vn.ditagis.com.tanhoa.qlsc.entities;

public class User {
    private String userName;
    private String passWord;
    private String displayName;

    private boolean isCreate;
    private boolean isValid;
    private String token;

    public User() {

    }

    public User(String userName, String passWord, String displayName, boolean isCreate, boolean isValid) {
        this.userName = userName;
        this.passWord = passWord;
        this.displayName = displayName;
        this.isCreate = isCreate;
        this.isValid = isValid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
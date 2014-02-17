package myApp.trainingdiary.service;


/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 1/14/14
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */


public class UserData {
    private int id;
    private String db;
    private String registrationId;
    private String registrationChannel;
    private String email;
    private String dbPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationChannel() {
        return registrationChannel;
    }

    public void setRegistrationChannel(String registrationChannel) {
        this.registrationChannel = registrationChannel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }


    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "id=" + id +
                ", db='" + db + '\'' +
                ", registrationId='" + registrationId + '\'' +
                ", registrationChannel='" + registrationChannel + '\'' +
                ", email='" + email + '\'' +
                ", dbPath='" + dbPath + '\'' +
                '}';
    }
}

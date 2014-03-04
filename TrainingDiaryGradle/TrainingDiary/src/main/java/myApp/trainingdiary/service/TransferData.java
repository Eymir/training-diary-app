package myApp.trainingdiary.service;


import java.util.HashMap;
import java.util.Map;

/**
 * User: boris_dev
 * Date: 1/14/14
 * Time: 3:58 PM
 */


public class TransferData {
    public static final String REG_CHANNEL = "registration_channel";
    public static final String REG_ID = "registration_id";
    public static final String DATABASE_STRING = "database_string";

    private Map<String, Object> map;

    public Map<String, Object> getMap() {
        if (map == null) {
            return new HashMap<String, Object>();
        }
        return map;
    }

    public TransferData() {
        if (map == null) {
            map = new HashMap<String, Object>();
        }
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}

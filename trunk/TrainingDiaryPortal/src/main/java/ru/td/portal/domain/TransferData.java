package ru.td.portal.domain;


import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * User: boris_dev
 * Date: 1/14/14
 * Time: 3:58 PM
 */

@XmlRootElement
public class TransferData {
    public static final String REG_CHANNEL = "registration_channel";
    public static final String REG_ID = "registration_id";
    public static final String DATABASE_STRING = "database_string";
    private Map<String, Object> map;


    public TransferData() {
        if (map == null) {
            map = new HashMap<String, Object>();
        }
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}

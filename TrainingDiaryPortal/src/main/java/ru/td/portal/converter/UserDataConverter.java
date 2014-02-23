package ru.td.portal.converter;

import ru.td.portal.domain.TransferData;
import ru.td.portal.domain.UserData;

/**
 * User: Vladimir Koba
 * Date: 23.02.14
 * Time: 22:38
 */
public class UserDataConverter {

    public static TransferData convertUserDataToTransferDataObject(UserData userData) {
        TransferData result = new TransferData();
        result.getMap().put(TransferData.DATABASE_STRING, userData.getDb());
        result.getMap().put(TransferData.REG_CHANNEL, userData.getRegistrationChannel());
        result.getMap().put(TransferData.REG_ID, userData.getRegistrationId());
        return result;
    }

    public static UserData convertTransferDataObjectToUserData(TransferData transferData) {
        UserData result = new UserData();
        result.setDb((String) transferData.getMap().get(TransferData.DATABASE_STRING));
        result.setRegistrationChannel((String) transferData.getMap().get(TransferData.REG_CHANNEL));
        result.setRegistrationId((String) transferData.getMap().get(TransferData.REG_ID));
        return result;
    }
}

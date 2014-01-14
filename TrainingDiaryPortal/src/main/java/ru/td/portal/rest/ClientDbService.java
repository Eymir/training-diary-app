package ru.td.portal.rest;

import ru.td.portal.domain.UserData;

import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 1/14/14
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ClientDbService {

    public String uploadClientDb(UserData userData, byte [] file);
}

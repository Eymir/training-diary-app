package utils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import ru.vkoba.ts.domain.StatisticElement;

import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 12/3/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class RestTestClient {

    public static void main(String[] argv) {

        String uri = "http://adhocdev.j.rsnx.ru:80/trainingdiary/rest/tds/add";
        StatisticElement element = new StatisticElement();
        element.setDeviceId("Java-Java");
        element.setTrainingStart(new Date(System.currentTimeMillis() - 5 * 60 * 1000));
        element.setTrainingEnd(new Date());

        try {
            Client client = Client.create();
            WebResource r = client.resource(uri);
            ClientResponse response = r.type(MediaType.APPLICATION_XML).post(ClientResponse.class, element);
            System.out.println(response.getEntity(String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

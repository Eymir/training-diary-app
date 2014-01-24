package ru.td.portal;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;



//Тупой класс для запуска из консоли.
public class RestTest {

    public static void main(String[] args) {
        try {

            Client client = Client.create();

            WebResource webResource = client
                    .resource("http://localhost:8080/TrainingDiaryPortal/api/downloadDb");
            client.setReadTimeout(10000);
            client.setConnectTimeout(10000);
            ClientResponse response = webResource.path("").queryParam("id","1").queryParam("channel","mobile").type("application/json")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            System.out.println("Output from Server .... \n");
            String output = response.getEntity(String.class);
            System.out.println(output);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public static void testUpdateDb() {
        Client client = Client.create();

        WebResource webResource = client
                .resource("http://localhost:8080/TrainingDiaryPortal");


        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("id", "1");
        params.add("channel", "mobile");

        ClientResponse response = webResource.path("/updateDb").queryParams(params).type("application/json")
                .post(ClientResponse.class);
    }

}

package it.alnao.restClient;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;

public class App{
    public static void main( String[] args )    {
    	System.out.println("App 11RestClient");
        ClientConfig config = new ClientConfig();

        Client client = ClientBuilder.newClient();// newClient(config);

        WebTarget target = client.target(getBaseURI());
        javax.ws.rs.core.Response response = target.path("webapi").
                            path("myresource/json").
                            request().
                            accept(MediaType.APPLICATION_JSON).
                            get(Response.class);//.toString();
        //String htmlAnswer=
        //        target.path("rest").path("hello").request().accept(MediaType.TEXT_HTML).get(String.class);

        System.out.println(response.toString());
        System.out.println(response.readEntity( String.class ) );
        //System.out.println(htmlAnswer);
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8081/11RestServer").build();
    }


}

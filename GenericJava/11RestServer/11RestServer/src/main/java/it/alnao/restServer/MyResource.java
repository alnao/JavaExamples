package it.alnao.restServer;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/myresource")
public class MyResource {
	//private static final ObjectMapper mapper = new ObjectMapper();
	
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Hello " + "</title>"
	        + "<body><h1>" + "Hello " + "</body></h1>" + "</html> ";
	  }
	    @GET
	    @Path("/json")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response hello() {
	        //ObjectNode json = mapper.createObjectNode();
	        //json.put("result", "Jersey JSON example using Jackson 2.x");
	    	Persona p=new Persona("Alberto");
	        return Response.status(Response.Status.OK).entity(p).build();
	    }
	    public class Persona {
	    	public Persona(String nome) {
				super();
				this.nome = nome;
			}
			String nome;
			public String getNome() {
				return nome;
			}
			public void setNome(String nome) {
				this.nome = nome;
			}
	    }
	    
	  //https://mkyong.com/webservices/jax-rs/json-example-with-jersey-jackson/
/*
	    // Object to JSON
	    @Path("/{name}")
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public User hello(@PathParam("name") String name) {
	        return new User(1, name);
	    }

	    // A list of objects to JSON
	    @Path("/all")
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public List<User> helloList() {
	        return Arrays.asList(
	                new User(1, "mkyong"),
	                new User(2, "zilap")
	        );
	    }

	    @Path("/create")
	    @POST
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response create(User user) {

	        ObjectNode json = mapper.createObjectNode();
	        json.put("status", "ok");
	        return Response.status(Response.Status.CREATED).entity(json).build();
	    }
*/	    
}

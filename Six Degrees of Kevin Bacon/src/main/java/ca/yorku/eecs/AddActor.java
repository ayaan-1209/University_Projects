package ca.yorku.eecs;

import java.io.*;
import org.json.*;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Record;

import com.sun.net.httpserver.*;

public class AddActor implements HttpHandler {

	private Driver driver;
	private String name;
	private String actorId;

	public AddActor() {

	}

	@Override
	public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("PUT")) {
                handlePut(r);
            } else {
            	throw new BadRequestException("Bad Request");
            }
        } catch (BadRequestException exceptiona) {
			r.sendResponseHeaders(400, -1);
        } catch (JSONException exceptionb) {
			r.sendResponseHeaders(400, -1);
        } catch (Exception exceptionc) {
			r.sendResponseHeaders(500, -1);
        }
	}

	private void handlePut(HttpExchange r) throws Exception, BadRequestException, IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject obj = new JSONObject(body);
        String name = "";
       	String actorId = "";

       	if (obj.has("name") && obj.has("actorId")) {
       		name = obj.getString("name");
       		actorId = obj.getString("actorId");
       	}
       	else {
       		throw new JSONException("Bad Request: Missing information");
       	}	
       	
        Neo4jDB neo4j = new Neo4jDB();
        neo4j.addActor(name, actorId);
	    r.sendResponseHeaders(200, -1);     
	    neo4j.close();
	}
}

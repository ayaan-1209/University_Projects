package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HasRelationship implements HttpHandler {

	@Override
	public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("GET")) {
                handleGet(r);
            } else {
            	throw new BadRequestException("Invalid Request");
            }

        } catch (BadRequestException ea) {
			r.sendResponseHeaders(400, -1);
		} catch (JSONException eb) {
			r.sendResponseHeaders(400, -1);
		} catch (NotFoundException ec) {
			r.sendResponseHeaders(404, -1);
		} catch (Exception ed) {
			r.sendResponseHeaders(500, -1);
		}
	}

	private void handleGet(HttpExchange r) throws IOException, NotFoundException, JSONException, BadRequestException, Exception {
		String body = Utils.convert(r.getRequestBody());
        JSONObject d = new JSONObject(body);
		
        String movieId = "";
        String actorId = "";
        
        if (d.has("movieId") && d.has("actorId")) {
            movieId = d.getString("movieId");
        	actorId = d.getString("actorId");
        } else {
        	throw new JSONException("Missing information in JSON");
        }
        
        Neo4jDB nDB = new Neo4jDB();
        String relationshipJSON = nDB.hasRelationship(movieId, actorId);     
        r.sendResponseHeaders(200, relationshipJSON.length());
        OutputStream outputStream = r.getResponseBody();
        outputStream.write(relationshipJSON.getBytes());
        outputStream.close(); 
	}

}

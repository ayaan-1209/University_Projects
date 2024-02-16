package ca.yorku.eecs;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AddLocation implements HttpHandler {

    @Override
    public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("PUT")) {
                handlePut(r);
            } else {
                throw new BadRequestException("Bad Request");
            }
        } catch (BadRequestException exception1) {
            r.sendResponseHeaders(400, -1);
        } catch (JSONException exception2) {
            r.sendResponseHeaders(400, -1);
        } catch (Exception exception3) {
            r.sendResponseHeaders(500, -1);
        }
    }

    /**
     * 
     * @param r
     * @throws Exception
     * @throws IOException
     * @throws BadRequestException
     * @throws JSONException
     */
    private void handlePut(HttpExchange r) throws Exception, IOException, BadRequestException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject d = new JSONObject(body);
        String name = "";
        String locationId = "";

        if (d.has("name") && d.has("locationId")) {
            name = d.getString("name");
            locationId = d.getString("locationId");
        } else {
            throw new JSONException("Missing information in JSON");
        }

        Neo4jDB neo4j = new Neo4jDB();
        neo4j.addLocation(name, locationId);
        r.sendResponseHeaders(200, -1);
        neo4j.close();
    }
}
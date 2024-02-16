package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AddFilmedIn implements HttpHandler {

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
        } catch (NotFoundException exception3) {
            r.sendResponseHeaders(404, -1);
        } catch (Exception exception4) {
            r.sendResponseHeaders(500, -1);
        }
    }

    /**
     *
     * @param r
     * @throws Exception
     * @throws IOException
     * @throws NotFoundException
     * @throws BadRequestException
     * @throws JSONException
     */
    private void handlePut(HttpExchange r) throws Exception, IOException, NotFoundException, BadRequestException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject d = new JSONObject(body);
        String movieId = "";
        String locationId = "";

        if (d.has("movieId") && d.has("locationId")) {
            movieId = d.getString("movieId");
            locationId = d.getString("locationId");
        } else {
            throw new JSONException("Missing information in JSON");
        }

        Neo4jDB neo4j = new Neo4jDB();
        neo4j.addFilmedIn(movieId, locationId);
        r.sendResponseHeaders(200, -1);
        neo4j.close();
    }

}
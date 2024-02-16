package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class HasFilmedIn implements HttpHandler {
    @Override
    public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("GET")) {
                handleGet(r);
            } else {
                throw new BadRequestException("Invalid Request");
            }

        } catch (BadRequestException e1) {
            r.sendResponseHeaders(400, -1);
        } catch (JSONException e2) {
            r.sendResponseHeaders(400, -1);
        } catch (NotFoundException e3) {
            r.sendResponseHeaders(404, -1);
        } catch (Exception e4) {
            r.sendResponseHeaders(500, -1);
        }
    }

    private void handleGet(HttpExchange r) throws IOException, NotFoundException, JSONException, BadRequestException, Exception {
        String body = Utils.convert(r.getRequestBody());
        JSONObject d = new JSONObject(body);

        String movieId = "";
        String locationId = "";

        if (d.has("movieId") && d.has("locationId")) {
            movieId = d.getString("movieId");
            locationId = d.getString("locationId");
        } else {
            throw new JSONException("Missing information");
        }

        Neo4jDB nDB = new Neo4jDB();
        String relationshipJSON = nDB.hasFilmedIn(locationId, movieId);
        r.sendResponseHeaders(200, relationshipJSON.length());
        OutputStream outputStream = r.getResponseBody();
        outputStream.write(relationshipJSON.getBytes());
        outputStream.close();
    }

}
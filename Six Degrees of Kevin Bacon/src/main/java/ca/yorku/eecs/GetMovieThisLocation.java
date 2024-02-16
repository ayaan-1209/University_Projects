package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class GetMovieThisLocation implements HttpHandler {

    public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("GET")) {
                handleGet(r);}
            else {
                throw new BadRequestException("Invalid Request");}}
        catch (BadRequestException e1) {
            r.sendResponseHeaders(400, -1);}
        catch (JSONException e2) {
            r.sendResponseHeaders(400, -1);}
        catch (NotFoundException e3) {
            r.sendResponseHeaders(404, -1);}
        catch (Exception e4) {
            r.sendResponseHeaders(500, -1);}
    }

    private void handleGet(HttpExchange r) throws Exception {

        String body = Utils.convert(r.getRequestBody());
        JSONObject d = new JSONObject(body);
        String locationId = "";
        String movieId = "";
        String result;
        if (d.has(String.valueOf("locationId"))){
            result = d.getString("locationId");}
        else{
            throw new JSONException("Missing information in JSON");}
        Neo4jDB nDatabase = new Neo4jDB();
        String lcJS = nDatabase.getMovieThisLocation(locationId);
        r.sendResponseHeaders(200, lcJS.length());
        OutputStream outstream = r.getResponseBody();
        outstream.write(lcJS.getBytes());
        outstream.close();
    }

}
package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class GetMovie implements HttpHandler {

    public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("GET")) {
                handleGet(r);}
            else {
                throw new BadRequestException("Bad Request");}}
        catch (BadRequestException exceptiona) {
            r.sendResponseHeaders(400, -1);}
        catch (JSONException exceptionb) {
            r.sendResponseHeaders(400, -1);}
        catch (NotFoundException exceptionc) {
            r.sendResponseHeaders(404, -1);}
        catch (Exception exceptiond) {
            r.sendResponseHeaders(500, -1);}
    }


    private void handleGet(HttpExchange r) throws Exception {
        String body = Utils.convert(r.getRequestBody());
        JSONObject d = new JSONObject(body);
        String movieId;
        if (d.has("movieId")){
            movieId = d.getString("movieId");}
        else{
            throw new JSONException("Missing information!");}
        Neo4jDB nDatabase = new Neo4jDB();
        String mvJS = nDatabase.getMovie(movieId);
        r.sendResponseHeaders(200, mvJS.length());
        OutputStream outstream = r.getResponseBody();
        outstream.write(mvJS.getBytes());
        outstream.close();
        }
    }
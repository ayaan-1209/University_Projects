package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;



import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
public class GetActor implements HttpHandler {

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
        String actorId = "";
        if (d.has("actorId")){
            actorId = d.getString("actorId");}
        else{
            throw new JSONException("Missing information");}
        Neo4jDB nDatabase = new Neo4jDB();
        String acJS = nDatabase.getActor(actorId);
        r.sendResponseHeaders(200, acJS.length());
        OutputStream outstream = r.getResponseBody();
        outstream.write(acJS.getBytes());
        outstream.close();
    }

}
package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ComputeBaconPath implements HttpHandler {

    public void handle(HttpExchange r) throws IOException {
        try {
            if (r.getRequestMethod().equals("GET")) {
                handleGet(r);}
            else {
                throw new BadRequestException("Invalid Request");}}
        catch (BadRequestException ea) {
            r.sendResponseHeaders(400, -1);}
        catch (JSONException eb) {
            r.sendResponseHeaders(400, -1);}
        catch (NotFoundException ec) {
            r.sendResponseHeaders(404, -1);}
        catch (Exception ed) {
            r.sendResponseHeaders(500, -1);}
    }


    private void handleGet(HttpExchange r) throws Exception, IOException, NotFoundException, JSONException, BadRequestException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject d = new JSONObject(body);
        String actorid = "";
        if (d.has("actorid")) {
            actorid = d.getString("actorid");}
        else {
            throw new JSONException("Missing information in JSON");}
        Neo4jDB nDataBase = new Neo4jDB();
        String BNJS = nDataBase.computeBaconPath(actorid);
        r.sendResponseHeaders(200, BNJS.length());
        OutputStream outstream = r.getResponseBody();
        outstream.write(BNJS.getBytes());
        outstream.close();
    }
}
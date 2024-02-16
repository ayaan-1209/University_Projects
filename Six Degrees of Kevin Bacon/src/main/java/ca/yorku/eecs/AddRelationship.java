package ca.yorku.eecs;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AddRelationship implements HttpHandler {

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
		} catch (NotFoundException exceptionc) {
			r.sendResponseHeaders(404, -1);
		} catch (Exception exceptiond) {
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
		String actorId = "";
		String movieId = "";

		if (d.has("actorId") && d.has("movieId")) {
			actorId = d.getString("actorId");
			movieId = d.getString("movieId");
		} else {
			throw new JSONException("Missing information in JSON");
		}

		Neo4jDB neo4j = new Neo4jDB();
		neo4j.addRelationship(actorId, movieId);
		r.sendResponseHeaders(200, -1);
		neo4j.close();
	}

}

package ca.yorku.eecs;

import static org.neo4j.driver.v1.Values.parameters;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Transaction;

public class Neo4jDB {

	public static String uriDb;
	public static String uriUser = "http://localhost:8080";
	public static Config config = Config.builder().withoutEncryption().build();
	public static Driver driver;

	public Neo4jDB() {
		uriDb = "bolt://localhost:7687";
		driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j", "1234"), config);
	}
	/**
	 * Checks whether the given name exists in the database.
	 * 
	 * @param name
	 * @param string
	 * @return boolean exists
	 */
	private boolean nameExists(String name, String string) {
		Session session = driver.session();
		boolean exists = false;
		Transaction t = session.beginTransaction();
		StatementResult r = t.run("MATCH (n:" + string + "RETURN n.name");

		while (r.hasNext()) {
			Record record = r.next();
			if (name.equals(record.get("n.name").asString())) {
				exists = true;
			}
		}
		return exists;
	}

	/**
	 * Checks whether the given id exists in the database.
	 * 
	 * @param id
	 * @param string
	 * @return boolean exists
	 */
	private boolean IdExists(String id, String string) {
		Session session = driver.session();
		boolean exists = false;
		Transaction t = session.beginTransaction();
		StatementResult r = t.run("MATCH (n:" + string + "RETURN n.id");

		while (r.hasNext()) {
			Record record = r.next();
			if (id.equals(record.get("n.id").asString())) {
				exists = true;
			}
		}
		return exists;
	}

	/**
	 * Checks whether the given movie and actor already has a relationship.
	 * 
	 * @param actorId
	 * @param movieId
	 * @return boolean exists
	 */
	private boolean relationshipExists(String actorId, String movieId) {
		Session session = driver.session();
		boolean exists = false;
		Transaction t = session.beginTransaction();
		StatementResult r = t.run("MATCH ({id: $x})-[r]->({id: $y})\n" + "RETURN type(r)",
				parameters("x", actorId, "y", movieId));

		while (r.hasNext()) {
			Record record = r.next();
			if (record.get(0).asString().equals("ACTED_IN")) {
				exists = true;
			}
		}
		return exists;
	}

	/**
	 * Checks whether the given location and movie already has a relationship.
	 * 
	 * @param locationId
	 * @param movieId
	 * @return boolean exists
	 */
	private boolean locationRelationshipExists(String locationId, String movieId) {
		Session session = driver.session();
		boolean exists = false;
		Transaction t = session.beginTransaction();
		StatementResult r = t.run("MATCH ({id: $x})-[r]->({id: $y})\n" + "RETURN type(r)",
				parameters("x", locationId, "y", movieId));

		while (r.hasNext()) {
			Record record = r.next();
			if (record.get(0).asString().equals("FILMED_IN")) {
				exists = true;
			}
		}
		return exists;
	}

	/**
	 * Inserts a new actor into the database.
	 * 
	 * @param name
	 * @param actorId
	 * @throws BadRequestException
	 */
	public void addActor(String name, String actorId) throws BadRequestException {
		Session session = driver.session();
		if (nameExists(name, "actor") || IdExists(actorId, "actor")) {
			throw new BadRequestException("Name or ID already exists!");
		}
		session.writeTransaction(t -> t.run("MERGE (a:actor {name: $x, id: $y})", parameters("x", name, "y", actorId)));
		session.close();
	}

	/**
	 * Inserts new movie into the database.
	 * 
	 * @param movie
	 * @param movieId
	 * @throws BadRequestException
	 */
	public void addMovie(String movie, String movieId, int year) throws BadRequestException {
		Session session = driver.session();
		if (nameExists(movie, "movie") || IdExists(movieId, "movie")) {
			throw new BadRequestException("Movie and/or ID already exists!");
		}
		session.writeTransaction(t -> t.run("MERGE (a:movie {name: $x, id: $y, year: $z})",
				parameters("x", movie, "y", movieId, "z", year)));
		session.close();
	}

	/**
	 * Inserts new location into the database.
	 * 
	 * @param location
	 * @param locationId
	 * @throws BadRequestException
	 */
	public void addLocation(String location, String locationId) throws BadRequestException {
		Session session = driver.session();
		if (nameExists(location, "location") || IdExists(locationId, "location")) {
			throw new BadRequestException("Location and/or ID already exists!");
		}
		session.writeTransaction(
				t -> t.run("MERGE (a:location {name: $x, id: $y})", parameters("x", location, "y", locationId)));
		session.close();
	}

	/**
	 * Inserts new relationship between actor and movie.
	 * 
	 * @param actorId
	 * @param movieId
	 * @throws BadRequestException
	 */
	public void addRelationship(String actorId, String movieId) throws BadRequestException {
		Session session = driver.session();
		if (!IdExists(actorId, "actor") || !IdExists(movieId, "movie")) {
			throw new BadRequestException("ActorId and/or MovieId does not exist!");
		}
		if (relationshipExists(actorId, movieId)) {
			throw new BadRequestException("Relationship already exists!");
		}
		session.writeTransaction(tx -> tx.run(
				"MATCH (a:actor {id:$x})," + "(t:movie {id:$y})\n" + "MERGE (a)-[r:ACTED_IN]->(t)\n" + "RETURN r",
				parameters("x", actorId, "y", movieId)));
		session.close();
	}

	/**
	 * Inserts new relationship between location and movie.
	 * 
	 * @param locationId
	 * @param movieId
	 * @throws BadRequestException
	 */
	public void addFilmedIn(String locationId, String movieId) throws BadRequestException {
		Session session = driver.session();
		if (!IdExists(locationId, "location") || !IdExists(movieId, "movie")) {
			throw new BadRequestException("LocationID and/or MovieId does not exist!");
		}
		if (locationRelationshipExists(locationId, movieId)) {
			throw new BadRequestException("Relationship already exists!");
		}
		session.writeTransaction(tx -> tx.run(
				"MATCH (l:location {id:$x})," + "(t:movie {id:$y})\n" + "MERGE (a)-[r:FILMED_IN]->(t)\n" + "RETURN r",
				parameters("x", locationId, "y", movieId)));
		session.close();
	}

	/**
	 * Gets the actor information and list of movies they've appeared in.
	 * 
	 * @param actorId
	 * @return
	 * @throws BadRequestException
	 * @throws JSONException
	 */
	public String getActor(String actorId) throws BadRequestException, JSONException {
		String aName = "";
		JSONObject obj = new JSONObject();
		try (Session session = driver.session();) {
			try (Transaction t = session.beginTransaction();) {
				// Actor name
				StatementResult r = t.run("MATCH (a:actor {id: $x})\n" + "RETURN a.name", parameters("x", actorId));
				if (r.hasNext()) {
					Record record = r.next();
					aName = record.get(0).asString();
				} else {
					throw new BadRequestException("ActorId does not exist!");
				}
				// List of movies
				r = t.run("MATCH (a:actor {id: $x})-[:ACTED_IN]->(movie)\n" + "RETURN movie.id",
						parameters("x", actorId));
				List<Record> recordList = r.list();
				List<String> movieList = new ArrayList<String>();

				for (Record record : recordList) {
					movieList.add(record.get(0).asString());
				}

				obj.put("name", aName);
				obj.put("actorId", actorId);
				obj.put("movies", new JSONArray(movieList));

			}
		}
		return obj.toString();
	}

	/**
	 * Gets the movie information and the list of actors appeared in the movie.
	 * 
	 * @param movieId
	 * @return
	 * @throws BadRequestException
	 * @throws JSONException
	 */
	public String getMovie(String movieId) throws BadRequestException, JSONException {
		String mName = "";
		JSONObject obj = new JSONObject();
		try (Session session = driver.session();) {
			try (Transaction t = session.beginTransaction();) {
				// Movie name
				StatementResult r = t.run("MATCH (m:movie {id: $x})\n" + "RETURN m.name", parameters("x", movieId));
				if (r.hasNext()) {
					Record record = r.next();
					mName = record.get(0).asString();
				} else {
					throw new BadRequestException("MovieId does not exist!");
				}
				// List of actors
				r = t.run("MATCH (m:movie {id: $x})-[:ACTED_IN]->(actor)\n" + "RETURN movie.id",
						parameters("x", movieId));
				List<Record> recordList = r.list();
				List<String> actorList = new ArrayList<String>();

				for (Record record : recordList) {
					actorList.add(record.get(0).asString());
				}

				obj.put("name", mName);
				obj.put("movieId", movieId);
				obj.put("actors", new JSONArray(actorList));

			}
		}
		return obj.toString();
	}

	/**
	 * Checks if a movie and an actor has an ACTED IN relationship.
	 * 
	 * @param movieId
	 * @param actorId
	 * @return
	 * @throws NotFoundException
	 * @throws JSONException
	 */
	public String hasRelationship(String movieId, String actorId) throws NotFoundException, JSONException {
		boolean hasR = false;
		JSONObject obj = new JSONObject();
		try (Session session = driver.session();) {
			try (Transaction t = session.beginTransaction();) {

				// Exception & boolean relationship
				StatementResult r = t.run("MATCH (a:actor {id: $x})\n" + "RETURN a.name", parameters("x", actorId));
				if (!r.hasNext()) {
					throw new NotFoundException("ActorId does not exist!");

				}
				r = t.run("MATCH (m:movie {id: $x})\n" + "RETURN m.name", parameters("x", movieId));
				if (!r.hasNext()) {
					throw new NotFoundException("MovieId does not exist!");
				}

				r = t.run("MATCH (a:actor {id: $x})-[r]->(m:movie {id: $y})" + "RETURN type(r)",
						parameters("x", actorId, "y", movieId));
				if (r.hasNext()) {
					Record record = r.next();
					if (record.get(0).asString().equals("ACTED_IN")) {
						hasR = true;
					}

				}

				obj.put("name", actorId);
				obj.put("movieId", movieId);
				obj.put("hasRelationship", hasR);

			}

		}
		return obj.toString();
	}

	/**
	 * Checks bacon number of an actor to Kevin Bacon. Kevin Bacon has a bNum of 0.
	 * 
	 * @param actorId
	 * @return
	 * @throws JSONException
	 * @throws NotFoundException
	 */
	public String computeBaconNumber(String actorId) throws JSONException, NotFoundException {
		String kevinBacon = "nm0000102";
		int bNum = 0;
		JSONObject obj = new JSONObject();

		// Edge case
		if (actorId.equals(kevinBacon)) {
			obj.put("baconNumber", bNum);
		} else {
			try (Session session = driver.session();) {
				try (Transaction t = session.beginTransaction();) {

					// Exceptions
					StatementResult r = t.run("MATCH (a:actor {id: $x})\n" + "RETURN a.name", parameters("x", actorId));
					if (!r.hasNext()) {
						throw new NotFoundException("ActorId does not exist!");
					}
					r = t.run(
							"MATCH shortestPath=shortestPath((:actor {id: $id1}) - [*] - (:actor {id: $id2}))"
									+ " RETURN length(shortestPath) as size",
							parameters("id1", actorId, "id2", kevinBacon));
					if (!r.hasNext()) {
						throw new NotFoundException("Path does not exist!");
					}

					bNum = r.list().get(0).get("size").asInt();
					obj.put("baconNumber", String.valueOf(bNum));
				}
			}
		}
		return obj.toString();
	}

	public String computeBaconPath(String actorId) throws JSONException, NotFoundException {
		// TODO Auto-generated method stub
		String kevinBacon = "nm0000102";
		int bNum = 0;

		JSONObject obj = new JSONObject();
		JSONArray bPath = new JSONArray();

		// Edge case
		if (actorId.equals(kevinBacon)) {
			obj.put("baconNumber", bNum);
			obj.put("baconPath", bPath);
		} else {
			try (Session session = driver.session();) {
				try (Transaction t = session.beginTransaction();) {

					// Exceptions
					StatementResult r = t.run("MATCH (a:actor {id: $x})\n" + "RETURN a.name", parameters("x", actorId));
					if (!r.hasNext()) {
						throw new NotFoundException("ActorId does not exist!");
					}
					r = t.run(
							"MATCH shortestPath=shortestPath((:actor {id: $id1}) - [*] - (:actor {id: $id2}))"
									+ " RETURN length(shortestPath) as size",
							parameters("id1", actorId, "id2", kevinBacon));
					if (!r.hasNext()) {
						throw new NotFoundException("Path does not exist!");
					}

					bNum = r.list().get(0).get("size").asInt();
					obj.put("baconNumber", String.valueOf(bNum));
				}
			}
		}
		return obj.toString();
	}

	/**
	 * Checks if a movie and a location has an FILMED IN relationship.
	 * 
	 * @param locationId
	 * @param movieId
	 * @return
	 * @throws JSONException
	 * @throws NotFoundException
	 */
	public String hasFilmedIn(String locationId, String movieId) throws JSONException, NotFoundException {
		boolean hasR = false;
		JSONObject obj = new JSONObject();
		try (Session session = driver.session();) {
			try (Transaction t = session.beginTransaction();) {

				// Exception & boolean relationship
				StatementResult r = t.run("MATCH (l:location {id: $x})\n" + "RETURN l.name",
						parameters("x", locationId));
				if (!r.hasNext()) {
					throw new NotFoundException("LocationId does not exist!");

				}
				r = t.run("MATCH (m:movie {id: $x})\n" + "RETURN m.name", parameters("x", movieId));
				if (!r.hasNext()) {
					throw new NotFoundException("MovieId does not exist!");
				}

				r = t.run("MATCH (l:location {id: $x})-[r]->(m:movie {id: $y})" + "RETURN type(r)",
						parameters("x", locationId, "y", movieId));
				if (r.hasNext()) {
					Record record = r.next();
					if (record.get(0).asString().equals("ACTED_IN")) {
						hasR = true;
					}
				}
				obj.put("name", locationId);
				obj.put("movieId", movieId);
				obj.put("hasRelationship", hasR);

			}

		}
		return obj.toString();

	}

	public String getMovieThisYear(int year) {
		// TODO Auto-generated method stub
		return null;

	}

	public String getActorsStarredTogether(String actor1, String actor2) throws NotFoundException, JSONException {
		// TODO Auto-generated method stub
		boolean hasR = false;
		JSONObject obj = new JSONObject();
		try (Session session = driver.session();) {
			try (Transaction t = session.beginTransaction();) {

				// Exception & boolean relationship
				StatementResult r = t.run("MATCH (a:actor {id: $x})\n" + "RETURN a.name", parameters("x", actor1));
				if (!r.hasNext()) {
					throw new NotFoundException("Actor1 does not exist!");

				}
				r = t.run("MATCH (a:actor {id: $x})\n" + "RETURN a.name", parameters("x", actor2));
				if (!r.hasNext()) {
					throw new NotFoundException("Actor2 does not exist!");

				}

				/*
				 * r = t.run("MATCH (a:actor {id: $x})-[r]->(m:movie {id: $y})" +
				 * "RETURN type(r)", parameters("x", actor1, "y", actor2)); if (r.hasNext()) {
				 * Record record = r.next(); if (record.get(0).asString().equals("ACTED_IN")) {
				 * hasR = true; }
				 * 
				 * }
				 */

				obj.put("name1", actor1);
				obj.put("name2", actor2);
				obj.put("starredTogether", hasR);

			}

		}
		return obj.toString();
	}

	/**
	 * Returns a list of movies that has been filmed in the given location.
	 * 
	 * @param locationId
	 * @return
	 * @throws NotFoundException
	 * @throws JSONException
	 */
	public String getMovieThisLocation(String locationId) throws NotFoundException, JSONException {
		String lName = "";
		JSONObject obj = new JSONObject();
		try (Session session = driver.session();) {
			try (Transaction t = session.beginTransaction();) {
				// Movie name
				StatementResult r = t.run("MATCH (m:movie {id: $x})\n" + "RETURN m.name", parameters("x", locationId));
				if (r.hasNext()) {
					Record record = r.next();
					lName = record.get(0).asString();
				} else {
					throw new NotFoundException("MovieId does not exist!");
				}
				// List of movies
				r = t.run("MATCH (m:movie {id: $x})-[:FILMED_IN]->(location)\n" + "RETURN movie.id",
						parameters("x", locationId));
				List<Record> recordList = r.list();
				List<String> movieList = new ArrayList<String>();

				for (Record record : recordList) {
					movieList.add(record.get(0).asString());
				}

				obj.put("name", lName);
				obj.put("movies", new JSONArray(movieList));

			}
		}
		return obj.toString();
	}

	/**
	 * 
	 */
	public void close() {
		driver.close();
	}

}

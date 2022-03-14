package ca.ubc.cs317.dict.net;

import ca.ubc.cs317.dict.model.Definition;
import ca.ubc.cs317.dict.model.Database;
import ca.ubc.cs317.dict.model.MatchingStrategy;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

public class DictionaryConnection {

    private static final int DEFAULT_PORT = 2628;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    /**
     * Establishes a new connection with a DICT server using an explicit host and port number, and handles initial
     * welcome messages.
     *
     * @param host Name of the host where the DICT server is running
     * @param port Port number used by the DICT server
     * @throws DictConnectionException If the host does not exist, the connection can't be established, or the messages
     *                                 don't match their expected value.
     */
    public DictionaryConnection(String host, int port) throws DictConnectionException {

        // TODO Add your code here
        try {
            socket = new Socket(host, port);// Connection Established
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));//input stream created
            output = new PrintWriter(socket.getOutputStream(), true);//output stream created
            Status status = Status.readStatus(input);
            if (status.getStatusCode() != 220) { //text msg-id
                throw new DictConnectionException();
            }
        } catch (Exception e) {
            throw new DictConnectionException(); //Error
        }
    }

    /**
     * Establishes a new connection with a DICT server using an explicit host, with the default DICT port number, and
     * handles initial welcome messages.
     *
     * @param host Name of the host where the DICT server is running
     * @throws DictConnectionException If the host does not exist, the connection can't be established, or the messages
     *                                 don't match their expected value.
     */
    public DictionaryConnection(String host) throws DictConnectionException {
        this(host, DEFAULT_PORT);
    }

    /**
     * Sends the final QUIT message and closes the connection with the server. This function ignores any exception that
     * may happen while sending the message, receiving its reply, or closing the connection.
     */
    public synchronized void close() {

        // TODO Add your code here
        try {
            output.println("QUIT");//final quit message
            Status.readStatus(input);
            socket.close();
        } catch (Exception e) { //ignore exception
        } finally {
            try {
                socket.close(); //closes socket
            } catch (Exception e) {
            }
        }
    }

    /**
     * Requests and retrieves a map of database name to an equivalent database object for all valid databases used in the server.
     *
     * @return A map linking database names to Database objects for all databases supported by the server, or an empty map
     * if no databases are available.
     * @throws DictConnectionException If the connection was interrupted or the messages don't match their expected value.
     */
    public synchronized Map<String, Database> getDatabaseList() throws DictConnectionException {
        Map<String, Database> databaseMap = new HashMap<>();

        // TODO Add your code here
        String message = "SHOW DB";
        try{
            output.println(message);
            Status status = Status.readStatus(input);
            int code = status.getStatusCode();
        if (code == 554) { //No databases present
            return databaseMap;}
            if (code == 110) { //n databases present - text follows
                try{
                do{
                String a = input.readLine();
                  if(a.equals(".")) break;
                  String[] data = DictStringParser.splitAtoms(a);
                  Database database = new Database(data[0], data[1]);
                  databaseMap.put(database.getName(), database);}
                while(true);}
            catch(Exception e){}}
            status = Status.readStatus(input);
            if (status.getStatusCode() != 250) { // checks if connection terminated
                throw new DictConnectionException(status.getDetails());
            }}
            catch(Exception e){
                throw new DictConnectionException(e);}
        return databaseMap;
    }

    /**
     * Requests and retrieves a list of all valid matching strategies supported by the server.
     *
     * @return A set of MatchingStrategy objects supported by the server, or an empty set if no strategies are supported.
     * @throws DictConnectionException If the connection was interrupted or the messages don't match their expected value.
     */
    public synchronized Set<MatchingStrategy> getStrategyList() throws DictConnectionException {
        Set<MatchingStrategy> set = new LinkedHashSet<>();

        // TODO Add your code here
        String message = "SHOW STRAT";
        try {
            output.println(message);
            Status status = Status.readStatus(input);
            int code = status.getStatusCode();
            if (code == 555) { //No strategies available
                return set;
            }
            if (code == 111) { // n strategies available - text follows
                int num = Integer.parseInt(status.getDetails().split(" ")[0]);
                try {
                    for (int i = 0; i < num; i++) {
                        String[] a = DictStringParser.splitAtoms(input.readLine());
                        set.add(new MatchingStrategy(a[0], a[1]));
                    }
                } catch (Exception e) { // ignore
                }
                String b = input.readLine();
                if (!b.equals(".")) {
                    throw new DictConnectionException();
                }
            }
                status = Status.readStatus(input);
                if (status.getStatusCode() != 250) { //checks if connection terminated
                    throw new DictConnectionException(status.getDetails());
                }
        } catch (Exception e) {
            throw new DictConnectionException(e);
        }
        return set;
    }



    /** Requests and retrieves a list of matches for a specific word pattern.
     *
     * @param word     The word whose definition is to be retrieved.
     * @param strategy The strategy to be used to retrieve the list of matches (e.g., prefix, exact).
     * @param database The database to be used to retrieve the definition. A special database may be specified,
     *                 indicating either that all regular databases should be used (database name '*'), or that only
     *                 matches in the first database that has a match for the word should be used (database '!').
     * @return A set of word matches returned by the server, or an empty set if no matches were found.
     * @throws DictConnectionException If the connection was interrupted, the messages don't match their expected
     * value, or the database or strategy are invalid.
     */
    public synchronized Set<String> getMatchList(String word, MatchingStrategy strategy, Database database) throws DictConnectionException {
        Set<String> set = new LinkedHashSet<>();

            // TODO Add your code here
            String message = "MATCH " + database.getName() + " " + strategy.getName() + " " + "\"" + word + "\"";
            try {
                output.println(message);
                Status status = Status.readStatus(input);
                int code = status.getStatusCode();
                if (code == 552){ //No match
                    return set;}
                if(code == 550){ //Invalid database,
                    throw new DictConnectionException();}
                if(code == 551){// Invalid strategy
                    throw new DictConnectionException();}
                if (code == 152) { //n matches found - text follows
                    int num = Integer.parseInt(status.getDetails().split(" ")[0]);
                    try {
                        for (int i = 0; i < num; i++) {
                            String[] a = DictStringParser.splitAtoms(input.readLine());
                            set.add(a[1]);
                        }
                    } catch (Exception e) {
                        throw new DictConnectionException();
                    }
                    String a = input.readLine();
                    if (!a.equals(".")) {
                        throw new DictConnectionException();
                    }
                }
                status = Status.readStatus(input);
                if (status.getStatusCode() != 250){ //checks if connection terminated
                    throw new DictConnectionException(status.getDetails());}}
            catch (Exception e) {
                throw new DictConnectionException(e);
            }
            return set;
        }

    /** Requests and retrieves all definitions for a specific word.
     *
     * @param word The word whose definition is to be retrieved.
     * @param database The database to be used to retrieve the definition. A special database may be specified,
     *                 indicating either that all regular databases should be used (database name '*'), or that only
     *                 definitions in the first database that has a definition for the word should be used
     *                 (database '!').
     * @return A collection of Definition objects containing all definitions returned by the server, or an empty
     * collection if no definitions were returned.
     * @throws DictConnectionException If the connection was interrupted, the messages don't match their expected
     * value, or the database is invalid.
     */
    public synchronized Collection<Definition> getDefinitions(String word, Database database) throws DictConnectionException {
        Collection<Definition> set = new ArrayList<>();

        // TODO Add your code here
        String message = "DEFINE " + database.getName() + " " + "\"" + word + "\"";
        try
        {
            output.println(message);
            Status status = Status.readStatus(input);
            int code = status.getStatusCode();
            String details = status.getDetails();
            if (code == 552) //No match
            {
                return set;
            }
            if (code == 550)// Invalid database
            {
                throw new DictConnectionException();
            }
            if (code == 150) //n definitions retrieved - definitions follows
            {
            String ParsedDetails[] = DictStringParser.splitAtoms(details);
                int num = Integer.parseInt(status.getDetails().split(" ")[0]);
            for (int i = 0; i < num; i++)
            {
                status = Status.readStatus(input);
                details = status.getDetails();
                if (status.getStatusCode() != 151)//if word database name is not found
                {
                    throw new DictConnectionException();
                }
                String Details[] = DictStringParser.splitAtoms(details);
                String db = new String(Details[1]);
                Definition definition = new Definition(Details[0], db);
                String a = input.readLine();
                    while(!a.equals(".")) {
                        try{
                        definition.appendDefinition(a);
                        a = input.readLine();
                    }
                        catch(Exception e){
                            throw new DictConnectionException();
                        }}
                set.add(definition);
            }}
                status = Status.readStatus(input);
                if (status.getStatusCode() != 250){ //checks if connection terminated
                    throw new DictConnectionException(status.getDetails());}}
            catch (Exception e) {
                throw new DictConnectionException(e);
            }
        return set;
    }
    }

/*
 * Author: Jonatan Schroeder
 * Updated: March 2022
 *
 * This code may not be used without written consent of the authors.
 */

package ca.yorku.rtsp.client.net;

import ca.yorku.rtsp.client.exception.RTSPException;
import ca.yorku.rtsp.client.model.Frame;
import ca.yorku.rtsp.client.model.Session;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;


/**
 * This class represents a connection with an RTSP server.
 */
public class RTSPConnection {

    private static final int BUFFER_LENGTH = 0x10000;


    private Session session;
    private Socket socket;
    private BufferedReader Breader;
    private PrintWriter writer;
    static int status;
    final static int begin = 0;
    final static int ready = 1;
    final static int playing = 2;
    static DatagramSocket socketr;
    static int port;
    int seq = 0;
    static String Name;
    private String Sessioncode;
    private DatagramPacket packet;

    // TODO Add additional fields, if necessary

    /**
     * Establishes a new connection with an RTSP server. No message is sent at this point, and no stream is set up.
     *
     * @param session The Session object to be used for connectivity with the UI.
     * @param server  The hostname or IP address of the server.
     * @param port    The TCP port number where the server is listening to.
     * @throws RTSPException If the connection couldn't be accepted, such as if the host name or port number are invalid
     *                       or there is no connectivity.
     */
    public RTSPConnection(Session session, String server, int port) throws RTSPException {
        this.session = session;
        try {
            socket = new Socket(server, port);//creates new socket
            Breader = new BufferedReader(new InputStreamReader(socket.getInputStream()));//creates new reader
            writer = new PrintWriter(socket.getOutputStream());//creates new writer
            status = begin;//using status to keep track
            System.out.println("RTSP status: Beginning");
        } catch (Exception e) {
            throw new RTSPException(e);
        }
        // TODO
    }


    /**
     * Sends a SETUP request to the server. This method is responsible for sending the SETUP request, receiving the
     * response and retrieving the session identification to be used in future messages. It is also responsible for
     * establishing an RTP datagram socket to be used for data transmission by the server. The datagram socket should be
     * created with a random UDP port number, and the port number used in that connection has to be sent to the RTSP
     * server for setup. This datagram socket should also be defined to timeout after 1 second if no packet is
     * received.
     *
     * @param videoName The name of the video to be setup.
     * @throws RTSPException If there was an error sending or receiving the RTSP data, or if the RTP socket could not be
     *                       created, or if the server did not return a successful response.
     */
    public synchronized void setup(String videoName) throws RTSPException {
        try {
            Name = videoName;//initializing video name
            socketr = new DatagramSocket();//rtp socket
            socketr.setSoTimeout(1000);//set timeout as required
        } catch (Exception e) {
            System.out.println("Invalid Server");
        }
        seq++;//incrementing session code
        port = socketr.getLocalPort();//getting the port
        try {
            writer.write("SETUP " + Name + " RTSP/1.0\nCSeq: " + seq + "\nTransport: RTP/UDP; client_port= " + port + "\n\n");
            writer.flush();
            RTSPResponse code = readRTSPResponse();//reading the response
            if (code.getResponseCode() != 200) {
                throw new RTSPException("Wrong Response Code");//if the right status is not received
            }
            status = ready;//changing status
            System.out.println("RTSP status: Ready");
            Sessioncode = code.getHeaderValue("SESSION");//saving the session value

        } catch (Exception e) {
            throw new RTSPException(e);
        }
    }


    /**
     * Sends a PLAY request to the server. This method is responsible for sending the request, receiving the response
     * and, in case of a successful response, starting a separate thread responsible for receiving RTP packets with
     * frames.
     *
     * @throws RTSPException If there was an error sending or receiving the RTSP data, or if the server did not return a
     *                       successful response.
     */
    public synchronized void play() throws RTSPException {
        seq++;//incrementing session code
        try {
            if (status == ready) {
                writer.write("PLAY " + Name + " RTSP/1.0\nCSeq: " + seq + "\nSession: " + Sessioncode + "\n\n");
                writer.flush();
                RTSPResponse code = readRTSPResponse();//reading the response
                if (code.getResponseCode() != 200) {
                    throw new RTSPException("Wrong Response");//if the right status is not received
                }
                RTPReceivingThread thread = new RTPReceivingThread();//initializing thread
                thread.start();//starting thread
                status = playing;//changing staus
                System.out.println("RTSP status: Playing");
                Sessioncode = code.getHeaderValue("SESSION");//saving the session value
            }
        } catch (Exception e) {
            throw new RTSPException(e);
        }
    }

    private class RTPReceivingThread extends Thread {
        /**
         * Continuously receives RTP packets until the thread is cancelled. Each packet received from the datagram
         * socket is assumed to be no larger than BUFFER_LENGTH bytes. This data is then parsed into a Frame object
         * (using the parseRTPPacket method) and the method session.processReceivedFrame is called with the resulting
         * packet. The receiving process should be configured to timeout if no RTP packet is received after two seconds.
         */
        @Override
        public void run() {
            try {
                byte[] buffer = new byte[BUFFER_LENGTH];
                while (!Thread.interrupted()) {//if thread is not interrupted so it keeps receiving
                    packet = new DatagramPacket(buffer, buffer.length);
                    socketr.receive(packet);//receiving packet
                    Frame frame = parseRTPPacket(packet);//parsing it
                    session.processReceivedFrame(frame);//calling the processing function
                    // TODO
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Sends a PAUSE request to the server. This method is responsible for sending the request, receiving the response
     * and, in case of a successful response, stopping the thread responsible for receiving RTP packets with frames.
     *
     * @throws RTSPException If there was an error sending or receiving the RTSP data, or if the server did not return a
     *                       successful response.
     */
    public synchronized void pause() throws RTSPException {
        seq++;//incrementing session code
        try {
            if (status == playing) {
                writer.write("PAUSE " + Name + " RTSP/1.0\nCSeq: " + seq + "\nSession: " + Sessioncode + "\n\n");
                writer.flush();
                RTSPResponse code = readRTSPResponse();//reading the response
                if (code.getResponseCode() != 200) {
                    throw new RTSPException("Wrong Response");//if the right status is not received
                }
                status = ready;//caning status
                System.out.println("RTSP status: Ready");
                Sessioncode = code.getHeaderValue("SESSION");//saving session value
            }
        } catch (Exception e) {
            throw new RTSPException(e);
        }
    }

    /**
     * Sends a TEARDOWN request to the server. This method is responsible for sending the request, receiving the
     * response and, in case of a successful response, closing the RTP socket. This method does not close the RTSP
     * connection, and a further SETUP in the same connection should be accepted. Also this method can be called both
     * for a paused and for a playing stream, so the thread responsible for receiving RTP packets will also be
     * cancelled.
     *
     * @throws RTSPException If there was an error sending or receiving the RTSP data, or if the server did not return a
     *                       successful response.
     */
    public synchronized void teardown() throws RTSPException {
        seq++;//incrementing session code
        try {
            writer.write("TEARDOWN " + Name + " RTSP/1.0\nCSeq: " + seq + "\nSession: " + Sessioncode + "\n\n");
            writer.flush();
            RTSPResponse code = readRTSPResponse();//reading response
            if (code.getResponseCode() != 200) {
                throw new RTSPException("Wrong Response");//if the right status is not received
            }
            status = begin;//changing status to the first one as it will go back
            System.out.println("RTSP status: Begin");
            socketr.close();//closing socket
            Sessioncode = code.getHeaderValue("SESSION");//saving session value
        } catch (Exception e) {
            throw new RTSPException(e);
        }
    }

    /**
     * Closes the connection with the RTSP server. This method should also close any open resource associated to this
     * connection, such as the RTP connection, if it is still open.
     */
    public synchronized void closeConnection() {
        try {
            Breader.close();
            writer.close();
            socketr.close();
            socket.close();
                //closing everything i can to pass the test
            System.out.println("Connections Closed");
        } catch (Exception e) {
            System.out.println(e);
        }
        // TODO
    }

    /**
     * Parses an RTP packet into a Frame object.
     *
     * @param packet the byte representation of a frame, corresponding to the RTP packet.
     * @return A Frame object.
     */
    public static Frame parseRTPPacket(DatagramPacket packet) {
        byte payloadType = (byte) (packet.getData()[1] & 0x7f);
        boolean marker;
        if (((packet.getData()[1] & 0xff) & 0x80) ==  0x80){
            marker = true;
        }else{
            marker = false;
        }
        short SeqNum = (short) ((packet.getData()[2]<<8) | (packet.getData()[3] & 0xff));
        int timestamp =((packet.getData()[4]& 0xff) << 24 | (packet.getData()[5] & 0xff) << 16 | (packet.getData()[6] & 0xff) << 8 | (packet.getData()[7] & 0xff));
        byte[] payload = packet.getData();
        int offset = 12;// offset by 12
        int length = packet.getLength() - offset;
        Frame frame = new Frame(payloadType, marker, SeqNum, timestamp, payload, offset,length);//initialing frame
        // TODO
        return frame;//returning the frame
    }



    /**
     * Reads and parses an RTSP response from the socket's input.
     *
     * @return An RTSPResponse object if the response was read completely, or null if the end of the stream was reached.
     * @throws IOException   In case of an I/O error, such as loss of connectivity.
     * @throws RTSPException If the response doesn't match the expected format.
     */
    public RTSPResponse readRTSPResponse() throws RTSPException, IOException {
        // TODO

        String serverResponse = Breader.readLine();//reading the line
        try {
                if (serverResponse == null) {
                    throw new IOException("Loss of Connectivity");//if connection is lost
                }


            String[] responsesplit = serverResponse.split(" ", 3);//parsing into three parts

            if (responsesplit.length != 3) {
                throw new RTSPException("Invalid response from RTSP server.");
            }
            RTSPResponse response = new RTSPResponse(responsesplit[0], Integer.parseInt(responsesplit[1]), responsesplit[2]);

            String header;
            while ((header = Breader.readLine()) != null && !header.equals("")) {//for the header to get the cseq and session values

                String[] headerSplit = header.split(":", 2);
                response.addHeaderValue(headerSplit[0], headerSplit[1]);//getting the session and cseq


            }
            return response;//returning response


        } catch (Exception e) {
            throw new RTSPException(e);
        }
    }
}






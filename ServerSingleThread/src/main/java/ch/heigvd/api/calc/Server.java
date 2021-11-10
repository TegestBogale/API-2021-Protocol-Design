package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Calculator server implementation - single threaded
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());

    /**
     * Main function to start the server
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        (new Server()).start();
    }

    /**
     * Start the server on a listening socket.
     */
    private void start() {
        /* TODO: implement the receptionist server here.
         *  The receptionist just creates a server socket and accepts new client connections.
         *  For a new client connection, the actual work is done by the handleClient method below.
         */
        // My Add ! //
        ServerSocket serverSocket;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(3013); // numéro de port a changer
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }
        while (true) {
            try {
                LOG.log(Level.INFO, "Single-threaded: Waiting for a new client on port {0}",3013);// a revoir cette ligne
                clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            } catch (IOException ex1) {
              if(clientSocket != null){
                  try {
                    clientSocket.close();
                  } catch (IOException ex2) {
                      LOG.log(Level.SEVERE, ex2.getMessage(), ex2);
                  }

              }
            }

        }


    }

    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) {

        /* TODO: implement the handling of a client connection according to the specification.
         *   The server has to do the following:
         *   - initialize the dialog according to the specification (for example send the list
         *     of possible commands)
         *   - In a loop:
         *     - Read a message from the input stream (using BufferedReader.readLine)
         *     - Handle the message
         *     - Send to result to the client
         */
        // My Add!
        BufferedReader in = null;
        BufferedWriter out = null;
        String line;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            out.write("WELCOME CRLF\n-OPERATIONS CRLF\n-ADD 2 CRLF\n-MUL 2 CRLF");
            out.flush();
            LOG.info("Reading until client sends QUIT CRLF or closes the connection...");
            while ( (line = in.readLine()) != null ) {

                if (line.equalsIgnoreCase("QUIT CRLF")) {
                    break;
                }
                out.write(result(line) + "\n");
                out.flush();
            }
            LOG.info("Cleaning up resources...");
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException ex2){
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex3) {
                    LOG.log(Level.SEVERE, ex3.getMessage(), ex3);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex3) {
                    LOG.log(Level.SEVERE, ex3.getMessage(), ex3);
                }
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex3) {
                    LOG.log(Level.SEVERE, ex3.getMessage(), ex3);
                }
            }
            LOG.log(Level.SEVERE, ex2.getMessage(), ex2);

        }

    }

    public String result (String str) {
        String message = "";
        String separator = " ";
        String arguments[] = str.split(separator);
        String error_400 = "ERROR 400 SYNTAX ERROR CRLF";
        String error_300 = "ERROR 300 UKNOWN OPERATION CRLF";
        double res;
        if(arguments.length != 4){
            message = error_400;
        } else if (!arguments[3].equals("CRLF")){
            message = error_400;
        } else if(!isNumeric(arguments[2]) || !isNumeric(arguments[1])){
            message = error_400;
        } else if(!(arguments[0].equals("ADD")||arguments[0].equals("MUL"))){
            message = error_300;
        } else if (arguments[0].equals("ADD")){
           res = Double.parseDouble(arguments[1]) + Double.parseDouble(arguments[2]);
           message = "RESULT " + String.valueOf(res);
        } else {
            res = Double.parseDouble(arguments[1]) * Double.parseDouble(arguments[2]);
            message = "RESULT " + String.valueOf(res);
        }

        return message;
    }

    public static boolean isNumeric(String str) {
        return str != null && str.matches("[-+]?\\d*\\.?\\d+");
    }
}
package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    // My add
    private Socket clientSocket;
    private BufferedReader in  = null;
    private PrintWriter    out = null;
    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        /* TODO: prepare everything for the ServerWorker to run when the
         *   server calls the ServerWorker.run method.
         *   Don't call the ServerWorker.run method here. It has to be called from the Server.
         */
        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {

        /* TODO: implement the handling of a client connection according to the specification.
         *   The server has to do the following:
         *   - initialize the dialog according to the specification (for example send the list
         *     of possible commands)
         *   - In a loop:
         *     - Read a message from the input stream (using BufferedReader.readLine)
         *     - Handle the message
         *     - Send to result to the client
         */
        String line;
        boolean shouldRun = true;

        out.println("WELCOME CRLF\n-OPERATIONS CRLF\n-ADD 2 CRLF\n-MUL 2 CRLF");
        out.flush();
        try {
            LOG.info("Reading until client sends BYE or closes the connection...");
            while ((shouldRun) && (line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("QUIT CRLF")) {
                    shouldRun = false;
                }
                out.println(result(line) + "\n");
                out.flush();
            }

            LOG.info("Cleaning up resources...");
            clientSocket.close();
            in.close();
            out.close();

        } catch (IOException ex) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
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

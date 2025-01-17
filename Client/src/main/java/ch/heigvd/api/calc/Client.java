package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Calculator client implementation

 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    /* Main function to run client
     *
             * @param args no args required
     */
    public static void main(String[] args) {

        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        BufferedReader stdin = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        Socket clientSocket = null;


        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */

        try {
            clientSocket = new Socket("127.0.0.1", 3013);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            stdin = new BufferedReader(new InputStreamReader(System.in));

            String init;
            while((init = in.readLine()) != null)
                System.out.println(init);

            String userInput = null;
            // System.out.println(userInput);
            while(true){
                userInput = stdin.readLine();
                out.write(userInput + "\n");
                out.flush();
                LOG.log(Level.INFO, "*** Response sent by the server: ***");
                String response;
                if((response = in.readLine()) != null) {
                    System.out.println(response);
                    LOG.log(Level.INFO, response);
                }
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
            try {
                if (stdin != null)
                    out.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
            try {
                if (clientSocket != null && ! clientSocket.isClosed())
                    clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }




    }
}
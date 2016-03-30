package sample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by venujan on 27/03/16.
 */
public class Server {

    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;

    public Server() throws Exception {
        serverSocket = new ServerSocket(8080);
    }

    //accept connection
    public void createThread() throws Exception {
        clientSocket = serverSocket.accept();
        ServerThread clientHandler = new ServerThread(clientSocket);
        Thread serverThread = new Thread(clientHandler);
        serverThread.start();
    }

    public void disconnect() throws Exception {
        serverSocket.close();
        clientSocket.close();
    }

    public class ServerThread implements Runnable {

        BufferedReader in = null;
        InputStream is = null;
        InputStreamReader isr = null;
        String requestLine = null;

        public ServerThread(Socket socket) {
            try {
                is = socket.getInputStream();
                isr = new InputStreamReader(is);
                in = new BufferedReader(isr);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                requestLine = in.readLine();
                String[] request = requestLine.split(" ");

                //Client uploads and server downloads files

                if (request[0].equals("UPLOAD")) {
                    String filename = request[1];

                    InputStream sin = clientSocket.getInputStream();
                    OutputStream out = new FileOutputStream(new File("ServerFiles/" + filename));

                    OutputStream ready = clientSocket.getOutputStream();
                    ready.write(1);

                    copyAllBytes(sin, out);

                    sin.close();
                    out.close();
                    in.close();
                }

                //Client downloads and server uploads files
                else if (request[0].equals("DOWNLOAD")) {
                    String filename = request[1];

                    //copying file to serverfolder location
                    OutputStream out = clientSocket.getOutputStream();
                    InputStream fin = new FileInputStream(new File("ServerFiles/" + filename));
                    copyAllBytes(fin, out);

                    fin.close();
                    out.close();
                    in.close();
                }

                //Server sends an updated list of files

                else if (request[0].equals("DIR")) {
                    File serverFolder = new File("ServerFiles/");
                    File[] files = serverFolder.listFiles();
                    String titles = "";
                    for (int i = 0; i < files.length; i++) {
                        titles += (files[i].getName() + ",");
                    }

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                    out.println(titles);
                    out.flush();

                    out.close();
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void copyAllBytes(InputStream in, OutputStream out) throws IOException
        {
            int cByte = 0;
            try {
                while ((cByte = in.read()) != -1) {
                    out.write(cByte);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            out.flush();
        }
    }
}

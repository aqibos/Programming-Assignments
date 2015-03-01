import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.StringTokenizer;

/**
 * A File Transfer Protocol (FTP) Server that can handle multiple clients.
 *
 * Created by Aqib Shah.
 */
public class myftpserver
{
    //Instance var's
    private ServerSocket server = null;                         //The server socket
    private Socket client = null;                               //The client socket
    private boolean acceptMoreConnections;                      //Allow server to continue accepting connections

    /**
     * Main - Creates myftpserver with given port.
     * @param args - String array (of length 1) that includes the port number.
     */
    public static void main (String [] args)
    {
        if (correctLengthArray (args, 1))
        {
            new myftpserver(Integer.parseInt(args[0]));
        }
        else
        {
            printToServer (usage);
        }
    }

    /**
     * Constructor for server.
     * @param portNumber The designated port.
     */
    public myftpserver(int portNumber)
    {
        try
        {
            server = new ServerSocket(portNumber);              //Creates server socket
            acceptMoreConnections = true;                       //Continue to accept connections
            getClientConnections ();                            //Listen for clients
        }
        catch (IOException e)
        {
            e.printStackTrace ();
            printToServer ("Oops! Something went wrong while creating the server.");
        }
        finally
        {
            try
            {
                server.close();                                 //Close the server
            }
            catch (IOException e)
            {
                e.printStackTrace ();
                printToServer ("Oops! Something went wrong while attempting to close the server.");
            }
            acceptMoreConnections = false;
        }


    }

    /**
     * Listens for clients and creates a new thread to handle each client.
     */
    private void getClientConnections()
    {
        while (acceptMoreConnections)
        {
            try
            {
                client = server.accept();                       //Accept client
                new Thread(new ServerThread(client)).start();   //Create a new thread to handle the client
            }
            catch (IOException e)
            {
                e.printStackTrace ();
                printToServer ("Oops! Something went wrong while connecting a client.");
            }
        }
    }

    /**
     * Thread that handles a particular client.
     */
    public class ServerThread implements Runnable
    {
        //Instance var's
        private Socket clientSocket;                            //The client socket
        private boolean loggedIn;                               //Client's log in status
        private boolean quit;                                   //Client's connection status
        private String userName;                                //Client's user name
        private String password;                                //Client's password
        private String rootDirectory;                           //Client's root directory
        private String currentDirectory;                        //Client's current directory
        private StringTokenizer tokenizer;
        private InetAddress transferAddress;                    //Address for transfer
        private int transferPort;                               //Port for transfer
        private BufferedReader input;                           //Input of client
        private PrintWriter output;                             //Output of client

        /**
         * Thread constructor.
         * @param clientSocket The client's socket.
         */
        public ServerThread(Socket clientSocket)
        {
            this.clientSocket = clientSocket;                   //Initialize instance variables
            loggedIn = false;
            quit = false;
            rootDirectory = "";
            currentDirectory = "";
            tokenizer = null;
            transferAddress = null;
            transferPort = 0;
        }

        @Override
        /**
         * Running function of thread; handles all of client's requests, until client disconnects.
         */
        public void run()
        {

            try
            {
                //Set up input and output streams
                input = new BufferedReader (new InputStreamReader (clientSocket.getInputStream ()));
                output = new PrintWriter (clientSocket.getOutputStream (), true);

                printToClient (CODE_220);

                while (!quit)
                {
                    String enteredText = input.readLine ();     //Get input
                    printToServer(enteredText);
                    tokenizer = new StringTokenizer (enteredText);
                    String command = getNextToken ();

                    if (command.equals ("USER"))                //If 'user' ...
                    {
                        userName = getNextToken ();

                        printToClient (CODE_331);

                    }
                    else if (command.equals("PASS"))            //If 'password' ...
                    {
                        password = getNextToken ();

                        if (validLogin())
                        {
                            login();
                            printToClient (CODE_230);
                        }
                        else
                        {
                            printToClient (CODE_430);
                        }
                    }
                    else if (command.equals ("XMKD"))           //If 'mkdir' ...
                    {
                        if (loggedIn)
                        {
                            makeDirectories (getNextToken ());
                        }
                        else
                        {
                            printToClient (CODE_530);
                        }
                    }
                    else if (command.equals("CWD"))             //If 'cd' ...
                    {
                        if (loggedIn)
                        {
                            changeDirectory (getNextToken ());
                        }
                        else
                        {
                            printToClient (CODE_530);
                        }
                    }
                    else if (command.equals("XPWD"))            //If 'pwd' ...
                    {
                        if (loggedIn)
                        {
                            printToClient (CODE_212 + "\n" + currentDirectory.replace (rootDirectory, "~"));
                        }
                        else
                        {
                            printToClient (CODE_530);
                        }
                    }
                    else if (command.equals ("EPRT"))           //If 'get' or 'put' with 'localhost' ...
                    {
                        if (loggedIn)
                        {
                            setupEprtTransfer(getNextToken());
                        }
                        else
                        {
                            printToClient (CODE_530);
                        }
                    }
                    else if (command.equals("PORT"))            //If 'get' or 'put' with IP address ...
                    {
                        //printToClient (CODE_200);
                        if (loggedIn)
                        {
                            setupPortTransfer(getNextToken());
                        }
                        else
                        {
                            printToClient(CODE_530);
                        }
                    }
                    else if (command.equals("RETR"))            //If 'get' ...
                    {
                        if (loggedIn)
                        {
                            doRetrTransfer (getNextToken ());
                        }
                        else
                        {
                            printToClient (CODE_530);
                        }
                    }
                    else if (command.equals ("STOR"))           //If 'put' ...
                    {
                        if (loggedIn)
                        {
                            doStorTransfer (getNextToken ());
                        }
                        else
                        {
                            printToClient (CODE_530);
                        }
                    }
                    else if (command.equals ("DELE"))           //If 'delete' ...
                    {
                        if (loggedIn)
                        {
                            deleteFile (getNextToken ());
                        }
                        else
                        {
                            printToClient (CODE_530);
                        }
                    }
                    else if (command.equals("QUIT"))            //If 'quit' ...
                    {
                        logout();
                        printToClient (CODE_231);
                    }
                    else
                    {
                        printToServer ("Attempted command: '" + command + "'.");
                        printToClient (CODE_502);
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace ();
                printToServer ("Oops! Something went wrong when setting up the input/output for client.");
            }
        }

        /**
         * Utility method: Gets the next token.
         * @return The next token associated with the StringTokenizer.
         */
        private String getNextToken()
        {
            return tokenizer.nextToken ();
        }

        /**
         * Utility method: Prints a given message to the client.
         * @param message The message to be printed.
         */
        private void printToClient (String message)
        {
            output.println(message);
        }

        /**
         * Checks whether the user name and password are a valid combination.
         * @return True on correct combination; false otherwise.
         */
        private boolean validLogin ()
        {
            return userName.equals (password);
        }

        /**
         * Utility method: Logs the user into the server.
         */
        private void login()
        {
            loggedIn = true;
            rootDirectory = System.getProperty ("user.dir");
            currentDirectory = System.getProperty ("user.dir");
            printToServer("User '" +userName + "' successfully connected to server." );
        }

        /**
         * Utility method: Gets the full path given a sub-path.
         * @param subpath The sub-path of which the full path is to be determined.
         * @return The complete absolute path.
         */
        private String getFullPath (String subpath)
        {
            return currentDirectory + "\\" + subpath;
        }

        /**
         * Utility method: Makes the directories given a relative path.
         * @param relativePath The directories to make.
         * @return True if the directories were successfully created; false otherwise.
         */
        private boolean makeDirectories (String relativePath)
        {
            String fullPath = getFullPath (relativePath);
            File directories = new File(fullPath);

        if (directories.mkdirs ())                              //If directories successful created ...
            {
                printToClient (CODE_257 + "'" + relativePath + "' created.");
                printToServer("Directory '" + relativePath + "' created.");
                return true;
            }
            else
            {
                printToClient (CODE_450);
                return false;
            }
        }

        /**
         * Utility method: Changes the current directory.
         * @param relativePath Directory to change to.
         */
        private void changeDirectory (String relativePath)
        {
            String copyCurrentDirectory = currentDirectory;
            String[] subpaths = relativePath.split ("\\\\");    //Split the string into sub-directories
            boolean success = true;

            for (String path : subpaths)
            {
                if (path.equals (".."))                         //If going backwards ...
                {
                    if (!goPrevDirectory ())                    //If failure
                    {
                        success = false;
                        break;                                  //Break and do not change directory
                    }
                }
                else
                {
                    if (!goFwrdDirectory (path))                //If going forwards ...
                    {
                        success = false;
                        break;
                    }
                }
            }

            if (!success)                                       //If failure, then do not change any directory
            {
                File oldCurrentDirectory = new File (copyCurrentDirectory);
                doChangeDir (oldCurrentDirectory);
                printToClient (CODE_450);
            }
            else
            {
                printToClient (CODE_250);
            }
            printToServer("Current Directory is ... '" + currentDirectory + "'.");
        }

        /**
         * Utility method: Back tracks one level of directories.
         * @return True on successful back track; false otherwise.
         */
        private boolean goPrevDirectory ()
        {
            String [] currentDirectoryParts = currentDirectory.split ("\\\\");
            String prevDirectoryPath = "";
            for (int i = 0; i < currentDirectoryParts.length - 1; i++)
            {
                prevDirectoryPath += currentDirectoryParts[i] + "\\";
            }
            prevDirectoryPath = prevDirectoryPath.substring(0, prevDirectoryPath.length() - 1);

            if (prevDirectoryPath.compareTo (rootDirectory) < 0) //If going further back than root ...
            {
                return false;
            }
            else
            {
                File prevDirectory = new File (prevDirectoryPath);
                if (doChangeDir (prevDirectory))
                {
                    //currentDirectory = prevDirectoryPath;

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        /**
         * Utility method: Navigates forward to a given directory.
         * @param dir The directory to be changed to.
         * @return True on successful directory change; false otherwise.
         */
        private boolean goFwrdDirectory (String dir)
        {
            String fullPathToDirectory = getFullPath (dir);
            File directory = new File (fullPathToDirectory);
        if (directory.isDirectory ())                           //If the directory exists
            {
                if (doChangeDir (directory))                    //Attempt to change
                {
                    //currentDirectory = fullPathToDirectory;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }

        /**
         * Utility method: Actually does the changing of the client's directory.
         * @param directory The directory to be changed to.
         * @return True on successful directory change; false otherwise.
         */
        private boolean doChangeDir (File directory)
        {
            currentDirectory = directory.getAbsolutePath();
            return System.setProperty ("user.dir", directory.getAbsolutePath ()) != null;
        }

        /**
         * Parses a String that contains the transfer information, like "|2|::1|6499|".
         * @param transferInfo The String containing the transfer information.
         * @return An array containing the transfer information; the second index holds the IP version (IPv4, or IPv6),
         * the third index holds the transfer IP address, and the fourth is the TCP port.
         */
        private String [] parseEprtTransferInfo(String transferInfo)
        {
            return transferInfo.split ("\\|");
        }

        /**
         * Parses a String that contains the transfer information, like "127,0,0,1,19,164".
         * @param transferInfo The String containing the transfer information.
         * @return An array containing the transfer information; the first index holds the IP address, and the
         * second index holds the TCP port.
         */
        private String [] parsePortTransferInfo(String transferInfo)
        {
            return transferInfo.split("\\,");
        }

        /**
         * Utility method: Sets up the transfer for localhost clients.
         * @param transInfo The String containing the transfer information.
         * @return True on success of set up; false otherwise.
         */
        private boolean setupEprtTransfer(String transInfo)
        {
            String [] transferInfo = parseEprtTransferInfo(transInfo);
            try
            {
                transferAddress = InetAddress.getByName (transferInfo[2]);      /* CHECK 'transferInfo' INDEX */
                transferPort = Integer.parseInt (transferInfo[3]);
                printToClient (CODE_200);   //CODE_150?
                return true;
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace ();
                printToClient (CODE_451);
                return false;
            }
        }

        /**
         * Utility method: Sets up the transfer for IP address connected clients.
         * @param transInfo
         * @return
         */
        private boolean setupPortTransfer(String transInfo)
        {
            String [] transferInfo = parsePortTransferInfo (transInfo);
            try
            {
                transferAddress = InetAddress.getByName (
                        transferInfo[0] + "." + transferInfo[1] + "." + transferInfo[2] + "." + transferInfo[3]);
                transferPort = Integer.parseInt(transferInfo[4]) * 256 + Integer.parseInt(transferInfo[5]);
                printToClient (CODE_200);   //CODE_150?
                return true;
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace ();
                printToClient (CODE_451);
                return false;
            }
        }

        /**
         * Utility method: Does the transfer of a 'get' command.
         * @param fileName The file to be received by the client.
         * @return True on successful transfer; false otherwise.
         */
        private boolean doRetrTransfer (String fileName)
        {
            try
            {
                                                                //Set up data connection
                Socket transferSocket = new Socket(transferAddress, transferPort);
                File transferFile = new File(getFullPath (fileName));

                if (transferFile.exists())                      //If valid file
                {
                    printToClient(CODE_125);
                                                                //Set up input and output stream for transfer
                    BufferedInputStream inputStream =
                            new BufferedInputStream(new FileInputStream(transferFile));
                    BufferedOutputStream outputStream =
                            new BufferedOutputStream(transferSocket.getOutputStream());

                    byte[] buffer = new byte[4096];             //Make transfer
                    int c = 0;
                    while ((c = inputStream.read(buffer)) >= 0)
                    {
                        outputStream.write(buffer, 0, c);
                    }
                    outputStream.flush();                       //Close data connection and streams
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                    transferSocket.close();
                    printToClient(CODE_226);
                    printToServer("File '" + fileName + "' transferred.");
                    return true;
                }
                else
                {
                    printToClient (CODE_550);
                    return false;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace ();
                printToClient (CODE_425);
                return false;
            }
        }

        /**
         * Utility method: Does the transfer of a 'put' command.
         * @param fileName The file to be received by the client.
         * @return True on successful transfer; false otherwise.
         */
        private boolean doStorTransfer (String fileName)
        {
            try
            {
                                                                //Set up data connection
                Socket transferSocket = new Socket (transferAddress, transferPort);
                File transferFile = new File (getFullPath (fileName));

                if (transferFile.createNewFile ())              //If new file successfully created
                {
                    printToClient(CODE_125);
                                                                //Set up input and output streams
                    InputStream inputStream = transferSocket.getInputStream ();
                    FileOutputStream outputStream = new FileOutputStream (getFullPath (fileName));

                    byte [] buffer = new byte[4096];            //Make transfer
                    int c = 0;
                    while ((c = inputStream.read (buffer)) >= 0)
                    {
                        outputStream.write (buffer, 0, c);
                    }
                    outputStream.flush();                       //Close data connection and streams
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                    transferSocket.close();
                    printToClient(CODE_226);
                    printToServer("File '" + fileName + "' successfully received.");
                    return true;
                }
                else
                {
                    printToClient (CODE_550);
                    return false;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace ();
                printToClient (CODE_425);
                return false;
            }

        }

        /**
         * Utility method: Deletes a file specified by client.
         * @param fileName The file to be received by the client.
         * @return True on successful deletion; false otherwise.
         */
        private boolean deleteFile ( String fileName)
        {
            printToServer (getFullPath (fileName));
            File fileToDelete = new File (getFullPath (fileName));

            if (fileToDelete.exists ())
            {
                try
                {
                    Files.delete(fileToDelete.toPath());
                    printToClient (CODE_250);
                    printToServer("File '" + fileName + "' successfully deleted.");
                    return true;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    printToClient (CODE_450);
                    return false;
                }
            }
            else
            {
                printToClient (CODE_550);
                return false;
            }
        }

        /**
         * Utility method: Logs user out of the server.
         */
        private void logout()
        {
            //currentDirectory = rootDirectory;
            printToServer("User '" + userName + "' successfully logged out.");
            quit = true;
            loggedIn = false;
        }
    }

    /**
     * Utility method: Checks whether an array's length is of a certain size.
     * @param arr The array to be compared.
     * @param correctSize The correct length of the array.
     * @param <E> A type of the array.
     * @return True if the array's length matches the 'correctSize' parameter; false otherwise.
     */
    private static <E> boolean correctLengthArray (E [] arr, int correctSize)
    {
        return arr.length == correctSize;
    }

    /**
     * Prints a given message to the server.
     * @param message The message to be printed.
     */
    private static void printToServer (String message)
    {
        System.out.println (message);
    }

    public static final String usage = "Usage: \njava myftpserver port-number";

    //List of FTP Server Return Codes
    //1xx - Positive Preliminary Reply
    public static final String CODE_125 = "125 Data connection already open; transfer starting.";
    public static final String CODE_150 = "150 File status okay; about to open data connection.";

    //2xx - Positive Completion Reply
    public static final String CODE_200 = "200 The requested action has been successfully completed.";
    public static final String CODE_202 = "202 Command not implemented, superfluous at this site.";
    public static final String CODE_212 = "212 Directory status.";
    public static final String CODE_220 = "220 Service ready for new user";
    public static final String CODE_226 = "226 Closing data connection. Requested file action successful.";
    public static final String CODE_230 = "230 User logged in, proceed. Logged out if appropriate.";
    public static final String CODE_231 = "231 User logged out; service terminated.";
    public static final String CODE_250 = "250 Requested file action okay, completed.";
    public static final String CODE_257 = "257 ";

    //3xx - Positive Intermediate Reply
    public static final String CODE_331 = "331 User name okay, need password.";

    //4xx - Transient Negative Completion Reply
    public static final String CODE_425 = "425 Can't open data connection.";
    public static final String CODE_426 = "426 Connection closed; transfer aborted";
    public static final String CODE_430 = "430 Invalid user name or password";
    public static final String CODE_450 = "450 Requested file action not taken";
    public static final String CODE_451 = "451 Requested action aborted. Local error in processing.";
    public static final String CODE_452 = "452 Requested action not taken. Insufficient storage space in system, or file unavailable.";

    //5xx - Permanent Negative Completion Reply
    public static final String CODE_501 = "501 Syntax error in parameters or arguments";
    public static final String CODE_502 = "502 Command not implemented.";
    public static final String CODE_530 = "530 Not logged in";
    public static final String CODE_550 = "550 Requested action not taken. File unavailable (e.g., file not found, no access, etc.)";
    public static final String CODE_553 = "553 Requested action not take. File name not allowed.";
}

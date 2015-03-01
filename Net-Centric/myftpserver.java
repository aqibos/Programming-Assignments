import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * Created by Aqib Shah.
 */
public class myftpserver
{
    //Instance var's
    private ServerSocket server = null;
    private Socket client = null;
    private boolean acceptMoreConnections;

    public static void main (String [] args)
    {
        if (correctLengthArray (args, 1))
        {
            new myftpserver(Integer.parseInt(args[0]));
        }
        else
        {
            System.out.println (usage);
        }
    }

    public myftpserver(int portNumber)
    {
        try
        {
            server = new ServerSocket(portNumber);
            acceptMoreConnections = true;
            getClientConnections ();
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
                server.close();
            }
            catch (IOException e)
            {
                e.printStackTrace ();
                printToServer ("Oops! Something went wrong while attempting to close the server.");
            }
            acceptMoreConnections = false;
        }


    }

    private void getClientConnections()
    {
        while (acceptMoreConnections)
        {
            try
            {
                client = server.accept();
                new Thread(new ServerThread(client)).start();
            }
            catch (IOException e)
            {
                e.printStackTrace ();
                printToServer ("Oops! Something went wrong while connecting a client.");
            }
        }
    }

    public class ServerThread implements Runnable
    {
        private Socket clientSocket;

        private boolean loggedIn;
        private boolean quit;
        private String userName;
        private String password;
        private String rootDirectory;
        private String currentDirectory;
        private StringTokenizer tokenizer;
        private InetAddress transferAddress;
        private int transferPort;
        private BufferedReader input;
        private PrintWriter output;

        public ServerThread(Socket clientSocket)
        {
            this.clientSocket = clientSocket;

            loggedIn = false;
            quit = false;
            rootDirectory = "";
            currentDirectory = "";
            tokenizer = null;
            transferAddress = null;
            transferPort = 0;
        }

        @Override
        public void run()
        {

            try
            {
                input = new BufferedReader (new InputStreamReader (clientSocket.getInputStream ()));
                output = new PrintWriter (clientSocket.getOutputStream (), true);

                printToClient (CODE_220);

                while (!quit)
                {
                    String enteredText = input.readLine ();
                    tokenizer = new StringTokenizer (enteredText);
                    String command = getNextToken ();

                    if (command.equals ("USER"))
                    {
                        userName = getNextToken ();

                        printToClient (CODE_331);

                    }
                    else if (command.equals("PASS"))
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
                    else if (command.equals ("XMKD"))
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
                    else if (command.equals("CWD"))
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
                    else if (command.equals("XPWD"))
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
                    else if (command.equals ("EPRT"))
                    {
                        if (loggedIn)
                        {
                            setupTransfer (getNextToken ());
                        }
                        else
                        {
                            printToClient (CODE_530);
                        }
                    }
                    else if (command.equals("RETR"))
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
                    else if (command.equals ("STOR"))
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
                    else if (command.equals ("DELE"))
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
                    else if (command.equals("QUIT"))
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

        private String getNextToken()
        {
            return tokenizer.nextToken ();
        }

        private void printToClient (String message)
        {
            output.println(message);
        }

        private boolean validLogin ()
        {
            return userName.equals (password);
        }

        private void login()
        {
            loggedIn = true;
            rootDirectory = System.getProperty ("user.dir");
            currentDirectory = System.getProperty ("user.dir");
        }

        private String getFullPath (String subpath)
        {
            return currentDirectory + "\\" + subpath;
        }

        private boolean makeDirectories (String relativePath)
        {
            String fullPath = getFullPath (relativePath);
            File directories = new File(fullPath);

            if (directories.mkdirs ())
            {
                printToClient (CODE_257 + "'" + relativePath + "' created.");
                return true;
            }
            else
            {
                printToClient (CODE_450);
                return false;
            }
        }

        private void changeDirectory (String relativePath)
        {
            String[] subpaths = relativePath.split ("\\\\");

            for (String path : subpaths)
            {
                if (path.equals (".."))
                {
                    goPrevDirectory ();
                }
                else
                {
                    goFwrdDirectory (path);
                }
            }
        }

        private void goPrevDirectory ()
        {
            String [] rootDirectoryParts = rootDirectory.split ("\\\\");
            String prevDirectoryPath = "";
            for (int i = 0; i < rootDirectoryParts.length - 1; i++)
            {
                prevDirectoryPath += rootDirectoryParts[i];
            }
            if (prevDirectoryPath.compareTo (rootDirectory) < 0)
            {
                printToClient (CODE_450);
            }
            else
            {
                File prevDirectory = new File (prevDirectoryPath);
                if (doChangeDir (prevDirectory))
                {
                    printToClient (CODE_250);
                }
                else
                {
                    printToClient (CODE_450);
                }
            }
        }

        private void goFwrdDirectory (String dir)
        {
            String fullPathToDirectory = getFullPath (dir);
            File directory = new File (fullPathToDirectory);
            if (directory.isDirectory ())
            {
                if (doChangeDir (directory))
                {
                    currentDirectory = fullPathToDirectory;
                    printToClient (CODE_250);
                }
                else
                {
                    printToClient (CODE_450);
                }
            }
            else
            {
                printToClient (CODE_550);
            }
        }

        private boolean doChangeDir (File directory)
        {
            return System.setProperty ("user.dir", directory.getAbsolutePath ()) != null;
        }

        private String [] parseTransferInfo(String transferInfo)
        {
            return transferInfo.split ("\\|");
        }

        private boolean setupTransfer(String transInfo)
        {
            String [] transferInfo = parseTransferInfo (transInfo);
            try
            {
                transferAddress = InetAddress.getByName (transferInfo[2]);      /* CHECK 'transferInfo' INDEX */
                transferPort = Integer.parseInt (transferInfo[3]);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace ();
                printToClient (CODE_451);
                return false;
            }
            printToClient (CODE_200);   //CODE_150?
            return true;
        }

        private boolean doRetrTransfer (String fileName)
        {
            try
            {
                printToClient (CODE_125);
                Socket transferSocket = new Socket(transferAddress, transferPort);
                File transferFile = new File(getFullPath (fileName));

                BufferedInputStream inputStream =
                        new BufferedInputStream (new FileInputStream (transferFile));
                BufferedOutputStream outputStream =
                        new BufferedOutputStream (transferSocket.getOutputStream ());

                byte [] buffer = new byte[4096];
                int c = 0;
                while ((c = inputStream.read (buffer)) >= 0)
                {
                    outputStream.write (buffer, 0, c);
                }
                outputStream.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace ();
                printToClient (CODE_425);
                return false;
            }
            printToClient (CODE_226);
            return true;
        }

        private boolean doStorTransfer (String fileName)
        {
            try
            {
                printToClient (CODE_125);
                Socket transferSocket = new Socket (transferAddress, transferPort);
                File transferFile = new File (getFullPath (fileName));

                if (transferFile.createNewFile ())
                {
                    InputStream inputStream = transferSocket.getInputStream ();
                    FileOutputStream outputStream = new FileOutputStream (getFullPath (fileName));

                    byte [] buffer = new byte[4096];
                    int c = 0;
                    while ((c = inputStream.read (buffer)) >= 0)
                    {
                        outputStream.write (buffer, 0, c);
                    }
                    outputStream.flush();
                }
                else
                {
                    printToClient (CODE_450);           /* FIX CODE */
                    return false;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace ();
                printToClient (CODE_425);
                return false;
            }
            printToClient (CODE_226);
            return true;
        }

        private boolean deleteFile ( String fileName)
        {
            File fileToDelete = new File (getFullPath (fileName));

            if (fileToDelete.exists ())
            {
                if (fileToDelete.delete ())
                {
                    printToClient (CODE_250);
                }
                else
                {
                    printToClient (CODE_450);
                    return false;
                }
            }
            else
            {
                printToClient (CODE_550);
                return false;
            }
            return true;
        }

        private void logout()
        {
            //currentDirectory = rootDirectory;
            quit = true;
            loggedIn = false;
        }
    }

    private static <E> boolean correctLengthArray (E [] arr, int correctSize)
    {
        return arr.length == correctSize;
    }

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

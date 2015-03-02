package cop3530;

/**
 * Exception class used for when objects are deleted from an already empty
 * queue.
 * @author Aqib Shah
 */
public class UnderflowException extends RuntimeException
{
    /**
     * Default constructor - no message.
     */
    public UnderflowException ( )
    {}
    
    /**
     * Constructor with a message.
     * @param message message to be displayed.
     */
    public UnderflowException ( String message )
    {
        super ( message ) ;
    }
}

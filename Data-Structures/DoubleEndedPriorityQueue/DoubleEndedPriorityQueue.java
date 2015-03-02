package cop3530;

import java.util.Comparator;

/**
 * Represents a Double Ended Priority Queue.
 * @author Aqib Shah
 * @param <AnyType> Generic Type.
 */
public interface DoubleEndedPriorityQueue< AnyType > 
{
    void makeEmpty( ) ;             //Makes the queue empty.
    void add ( AnyType x ) ;        //Adds a value to the queue.
    AnyType deleteMin( ) ;          //Deletes minimum value.
    AnyType deleteMax( ) ;          //Deletes maximum value.
    AnyType findMin( ) ;            //Returns minimum.
    AnyType findMax( ) ;            //Returns maximum.
    boolean isEmpty( ) ;            //Determines whether empty or not.
}
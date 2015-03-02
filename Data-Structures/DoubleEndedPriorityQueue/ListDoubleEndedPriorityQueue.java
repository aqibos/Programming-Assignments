package cop3530;

import java.util.Comparator;

/**
 * Represents a Double Ended Priority Queue as a linked list.
 * @author Aqib Shah
 */
public class ListDoubleEndedPriorityQueue< AnyType > 
                                implements DoubleEndedPriorityQueue < AnyType >
{
    //--------------------------------------------------------------------------
    /**
     * Represents the nodes of the linked list.
     * @param <AnyType> the value of the node.
     */
    private static class Node < AnyType > 
    {
        private AnyType data ;
        Node < AnyType > prev ;
        Node < AnyType > next ;
        
        /**
         * Constructs a node with a previous and next node along with the 
         * given data.
         * @param d the value.
         * @param p the previous node.
         * @param n the next node.
         */
        public Node ( AnyType d , Node < AnyType > p , Node < AnyType > n )
        {
            data = d ;
            prev = p ;
            next = n ;
        }
    }
    //--------------------------------------------------------------------------

    private Comparator < ? super AnyType > cmp ;
    private Node < AnyType > first ;   
    private Node < AnyType > last ;     
    
    /**
     * Constructor with null comparator.
     */
    public ListDoubleEndedPriorityQueue( )
    {
        this ( null ) ;
    }
    
    /**
     * Constructor with given comparator.
     * @param c the specified comparator.
     */
    public ListDoubleEndedPriorityQueue( Comparator<? super AnyType> c ) 
    {
        first = null ;
        last = null ;
        cmp = c ;
    }
    
    /**
     * Compares two values.
     * @param a the first value.
     * @param b the second value.
     * @return whether one is larger than the other, or vice-versa, or equal.
     */
    private int myCompare( AnyType a , AnyType b )
    {
        if ( cmp == null )
            return ( ( Comparable ) a ).compareTo ( b ) ; 
        
        else
            return cmp.compare( a ,  b ) ;
    }
    
    /**
     * Clears the contents of the tree.
     */
    public void makeEmpty() 
    {
        first = null ; last = null ; 
    }

    /**
     * Adds a value to the list.
     * @param x the value to be added.
     */
    public void add( AnyType x ) 
    {
        Node temp = new Node ( x , null , null ) ;
        
        if ( last == null && first == null )        //Empty
            first = last = temp ;
        else
        {
            Node head = first ;                     //Not Empty
            int compareResult = myCompare( x , ( AnyType ) first.data ) ;
            
            if ( compareResult <= 0 )    //Before first node
            {
                temp.next = first ;     //Note: temp.prev already null
                first.prev = temp ; first = temp ;
            }
            else                         //After first node 
            {
                while ( head.next != null )
                {
                    Node current = head.next ;
                    AnyType currentData = ( AnyType ) current.data ;
                    
                    if ( myCompare ( x , currentData ) <= 0 )
                    {
                        head.next = temp ; temp.prev = head ;
                        current.prev = temp ; temp.next = current ;
                        return ;
                    } 
                    head = head.next ;
                }
                last.next = new Node ( x , last , null ) ;
                last = last.next ;
            }
        }
    }

    /**
     * Deletes the minimum value on the list.
     * @return the lowest value in the list.
     * @throws UnderflowException if the list empty prior to deletion.
     */
    public AnyType deleteMin( ) throws UnderflowException
    {
        if ( first == null || last == null )
        {
            throw new UnderflowException( ) ;
        }
        AnyType data = first.data ;
        if ( first.next != null )   //More than one node
        {
            first = first.next ; first.prev = null ;
        }
        else                        //Only one node
        {
            first = null ; last = null ;
        }
        return data ;
        
    }

    /**
     * Deletes the maximum value on the list.
     * @return the maximum value on the list.
     * @throws UnderflowException if the list empty prior to deletion.
     */
    public AnyType deleteMax( )  throws UnderflowException
    {
        if ( first == null && last == null )
        {
            throw new UnderflowException( ) ;
        }
        AnyType data = last.data ;
        if ( last.prev != null )
        {
            last = last.prev ;
            last.next = null ;
        }
        else
        {
            last = null ; first = null ;
        }
        
        return data ;

    }

    /**
     * Finds the lowest value on the list and returns it.
     * @return the lowest value.
     */
    public AnyType findMin( ) 
    {
        return first.data ;
    }

    /**
     * Finds the maximum value on the list and returns it.
     * @return the maximum value.
     */
    public AnyType findMax( ) 
    {
        return last.data ;
    }
    
    /**
     * Checks whether the list empty or not.
     * @return whether the list is empty.
     */
    public boolean isEmpty( ) 
    {
        return ((first == null) && (last == null)) ;
    }
    
    /**
     * Constructs a string representation of the list.
     * @return a complete string of list.
     */
    public String toString() 
    {
        StringBuilder sb = new StringBuilder( ) ;
        sb.append( "[  " ) ;
        toString ( first , sb ) ;
        sb.setLength( sb.length() - 2 ) ;
        sb.append( "  ]" ) ;
        return new String ( sb ) ;
    }
    
    /**
     * Continues to build the string.
     * @param t the current node on the list.
     * @param sb the StringBuilder that will be used to make the String.
     */
    private void toString( Node <AnyType> t , StringBuilder sb)
    {
        Node head = first ;
        while ( head != null )
        {
            sb.append( head.data ) ;
            sb.append( ", " ) ;
            head = head.next ;
        }
    }
    
    /*    
    public void printList ( ) 
    {
        System.out.print( "Forwards : " ) ;
        Node head = first ;
        System.out.print(" null <-- ");
        while ( head != null ) 
        {
            System.out.print( "[ " + head.data + " ] --> " ) ;
            head = head.next ;
        }
        System.out.println( " null " ) ;
        
        System.out.print( "Backwards: " ) ;
        Node tail = last ; 
        System.out.print(" null <-- ");
        while ( tail != null )
        {
            System.out.print( "[ " + tail.data + " ] --> ") ;
            tail = tail.prev ;
        }
        System.out.println( " null " ) ;
                
        System.out.println( "----------------------------------------------" ) ;
    }
    */
}
//------------------------------------------------------------------------------
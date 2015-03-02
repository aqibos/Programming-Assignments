package cop3530;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a Double Ended Priority Queue as a binary tree.
 * @author Aqib Shah
 * @param <AnyType> Generic type.
 */
public class TreeDoubleEndedPriorityQueue< AnyType > implements 
        DoubleEndedPriorityQueue < AnyType > 
{
    
    //--------------------------------------------------------------------------
    /**
     * Represents individual nodes on the tree.
     * @param <AnyType> 
     */
    private static class Node < AnyType >
    {
        private Node < AnyType > left ;             //Left child
        private Node < AnyType > right ;            //Right child
        private ListNode < AnyType > items ;        //Duplicates
        
        /**
         * A linked-list representation of duplicate values on the tree.
         * @param <AnyType> 
         */
        private static class ListNode < AnyType >
        {
            private AnyType data ;                  //Value
            private ListNode < AnyType > next ;     //Pointer to next duplicate
            
            public ListNode( AnyType d , ListNode < AnyType > n )
            {
                data = d ;                          //Initialize instance var's
                next = n ;
            }
        }
        
        /**
         * Constructs a node with an initial value.
         * @param data value of the node.
         */
        public Node ( AnyType data )
        {
            left = right = null ;
            items = new ListNode< AnyType > ( data , null ) ;
        }
    }
   //--------------------------------------------------------------------------- 
    
    private Comparator < ? super AnyType > cmp ;        //Comparator
    private Node < AnyType > root ;                     //Root node
    
    /**
     * Constructs an binary tree with a null comparator.
     */
    public TreeDoubleEndedPriorityQueue ( )
    {
        this ( null ) ;
    }
    
    /**
     * Constructs a binary tree with the given comparator.
     * @param c the comparator.
     */
    public TreeDoubleEndedPriorityQueue ( Comparator <? super AnyType > c )
    {
        root = null ; cmp = c ; 
    }
    
    /**
     * Compares two values.
     * @param a first value.
     * @param b second value.
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
     * Clears the contents of a tree.
     */
    public void makeEmpty( ) 
    {
        root = null ;
    }
    
    /**
     * Adds a value to the binary tree.
     * @param x value to be added.
     */
    public void add ( AnyType x ) 
    {
        Node temp = new Node ( x ) ;
        if ( root == null )
            root = temp ;
        else
            add ( root , temp ) ;
    }
    
    /**
     * Recursively finds the correct position for a given value and adds it 
     * to the tree.
     * @param src the node being compared.
     * @param t the node with the value being added.
     */
    private void add ( Node < AnyType > src , Node < AnyType > t )
    {
        int compResult = myCompare ( t.items.data , src.items.data ) ;

        if ( compResult < 0 )
            if ( src.left == null )
                src.left = t ;
            else
                add( src.left , t ) ;
        else if ( compResult >  0 )
            if ( src.right == null )
                src.right = t ;
            else
                add( src.right , t ) ;
        else    //compResult == 0
        {
            Node.ListNode temp = src.items ;
            src.items = t.items ;
            src.items.next = temp ;
        }
    }
    
    /**
     * Deletes the node that represents the minimum.
     * @return the minimum value.
     * @throws UnderflowException if called for an empty tree.
     */
    public AnyType deleteMin( ) throws UnderflowException
    {
        if ( root == null )
            throw new UnderflowException( ) ;
        if ( root.left == null )    //root is smallest
        {
            AnyType x = ( AnyType ) root.items.data ;
            
            if ( root.items.next == null )          //Only one copy of root
                root = root.right ;            //If no right, then root = null
            else if ( root.items.next != null )     //More than one copy
                root.items = root.items.next ;
            
            return x ;
        }
        else
            return ( AnyType ) deleteMin( root ) ;  //Recursive method
    }
    
    /**
     * Recursively finds the minimum and deletes it from the tree.
     * @param src the node being compared.
     * @return the value being deleted.
     */
    private AnyType deleteMin( Node< AnyType > src )
    {
        if ( src.left.left != null )            //If not at left-most node
            return deleteMin( src.left ) ;      //Continue to traverse...
        else
        {
            AnyType data = ( AnyType ) src.left.items.data ;    //Save data
            if ( src.left.items.next == null )  //If no dups
            {
                if ( src.left.right == null  )  //If no right subtree
                    src.left = null ;           //Remove entirely
                else
                    src.left = src.left.right ; //Make it right subtree 
            }
            else                                //Yes, duplicates
                src.left.items = src.left.items.next ;  //Remove one.
            
            return data ;
        }
    }
    
    /**
     * Deletes the node that represents the maximum.
     * @return the maximum value.
     * @throws UnderflowException if called for an empty tree.
     */
    public AnyType deleteMax( ) throws UnderflowException
    {
        if ( root == null )
            throw new UnderflowException( ) ;
        if ( root.right == null )    //root is largest
        {
            AnyType x = ( AnyType ) root.items.data ;
            
            if ( root.items.next == null )          //Only one copy of root
                root = root.left ;            //If no left, then root = null
            else if ( root.items.next != null )     //More than one copy
                root.items = root.items.next ;      //Delete a copy.
            
            return x ;
        }
        else
            return ( AnyType ) deleteMax( root ) ;  
        
    }
    
    /**
     * Recursively finds the maximum and deletes it from the tree.
     * @param src the node being compared.
     * @return the value being deleted.
     */
    private AnyType deleteMax( Node < AnyType > src )
    {
        if ( src.right.right != null )
            return deleteMax( src.right ) ;
        else
        {
            AnyType data = ( AnyType ) src.right.items.data ;
            if ( src.right.items.next == null )     //If no duplicates
            {
                if ( src.right.left == null  )      //If no left subtree
                    src.right = null ;              //Remove entirely
                else
                    src.right = src.right.left ;    //Make it left subtree
            }
            else
                src.right.items = src.right.items.next ;    //Yes duplicate
                                                            //Remove one
            return data ;
        }
    }
    
    /**
     * Finds the value that is the minimum.
     * @return the minimum value.
     */
    public AnyType findMin( ) 
    {
        Node temp = root ;
        if ( temp == null )
        {
            throw new UnderflowException( ) ;
        }
        while ( temp.left != null )     //Continue to go left,
        {
            temp = temp.left ;
        }
        return ( AnyType ) temp.items.data ;    //return left most node.
    }
    
    /**
     * Finds the value that is the maximum.
     * @return the maximum value.
     */
    public AnyType findMax( )   
    {
        Node temp = root ;
        while ( temp.right != null ) //Continue to go right,
        {
            temp = temp.right ;
        }
        return ( AnyType ) temp.items.data ;    //return right most node.
    }
    
    /**
     * Checks whether the tree is empty.
     * @return whether tree is empty or not.
     */
    public boolean isEmpty( ) 
    {
        return root == null ;
    }

    /**
     * Provides a string representation of the tree.
     * @return a a printable string.
     */
    public String toString() 
    {
        StringBuilder sb = new StringBuilder() ;
        sb.append( "[  " ) ;
        toString ( root , sb ) ;
        sb.setLength( sb.length() - 2 ) ;
        sb.append( "  ]" ) ;
        return new String ( sb ) ;
    }
    
    /**
     * Private routine to build a string to represent the tree.
     * @param t the current node being observed.
     * @param sb a StringBuilder object that will be used to construct the 
     * String.
     */
    private void toString( Node < AnyType > t , StringBuilder sb )
    {  
        if ( t == null )
            return ;
        
        toString ( t.left , sb ) ;
        sb.append ( t.items.data ) ;
        sb.append (", ") ;
        Node.ListNode temp = t.items ;
        while ( temp.next != null )
        {
            temp = temp.next ;
            sb.append( temp.data ) ;
            sb.append ( ", " ) ;
        }
        toString ( t.right , sb ) ;
    }
    
    /*    
    public void printTree( )
    {
        printTree ( root ) ;
        System.out.println();   
    }
    
    private void printTree( Node < AnyType > t )
    {
        if ( t == null )
            return ;
        
        printTree ( t.left ) ;
        System.out.print( t.items.data  + " ");
        Node.ListNode temp = t.items ;
        while ( temp.next != null )
        {
            temp = temp.next ;
            System.out.print( temp.data + " ") ;
        }
        
        printTree ( t.right ) ;
    }
    */
}

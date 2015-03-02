package cop3530;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
/**
 *
 * File: MyHashMap.java
 *
 * The purpose of this class is to implement a hash map.
 *
 * I affirm that this program is entirely my own work and none of it is the
 * work of any other person. 
 * @author Aqib
 * @param <KeyType> A generic key.
 * @param <ValueType> A generic value.
 */
public class MyHashMap<KeyType,ValueType> 
        implements Iterable<Map.Entry<KeyType,ValueType>>
{
    private HashFunction<KeyType> hash1;                //Hash function 1
    private HashFunction<KeyType> hash2;                //Hash function 2
    private Node<KeyType, ValueType> [] arr = null;     //Hash Array
    private int theSize;                                //Number of items
    private static final int DEFAULT_ARR_SIZE = 11 ;    //Array size
    
    /**
     * Constructor that creates Hash map with two separate hash functions.
     * @param h1 first hash function.
     * @param h2 second hash function.
     */
    public MyHashMap( HashFunction<KeyType> h1, HashFunction<KeyType> h2 )
    {
        hash1 = h1;            //initialize instance var's
        hash2 = h2;
        doClear( );            //private routine--sets arr and theSize var's
    }
    
    /**
     * Get method the size of the hash map.
     * @return the number of elements in the hash map.
     */
    public int size( )
    {
        return theSize;
    }
    
    /**
     * Clears the hash map. Resets the size.
     */
    public void clear( )
    {
        doClear( );
    }
    
    /**
     * Clears the array; resets size and array.
     */
    private void doClear( )
    {
        theSize = 0;
        arr = new Node[ DEFAULT_ARR_SIZE ];
    }
    
    /**
     * Adds a key to the hash map. If the key has already been added previously, 
     * then the previous value is replaced with the new.
     * @param k the key.
     * @param v the value.
     * @return the value being overwritten. If no value is being overwritten, 
     * null is returned.
     */
    public ValueType put( KeyType k, ValueType v )
    {
        if ( theSize > (arr.length) ) //If the number of items are getting too 
            rehash();                 //high then, rehash with a bigger array.
        
        int hash1Result = myHash1( k );     //Find both hash codes.
        int hash2Result = myHash2( k );
        Node contains1 = listContains( k , hash1Result ); //Check both indeces.
        Node contains2 = listContains( k , hash2Result );
        
        if ( contains1 == null && contains2 == null)//If neither contains
        {
            theSize++;          //Increment the size
            //List 1 is smaller or equal
            if ( indexSize( hash1Result ) <= indexSize( hash2Result ))      
                arr[ hash1Result ] = new Node( k , v , arr[ hash1Result ]);
            else    //List 2 is smaller
                arr[ hash2Result ] = new Node( k , v , arr[ hash2Result ]);
            return null;
        }
        else if ( contains1 != null && contains2 == null )  //list 1 contains
        {
            ValueType val = ( ValueType ) contains1.value;
            contains1.value = v;
            return val;
        }
        else                                                //list 2 contains
        {
            ValueType val = ( ValueType ) contains2.value;
            contains2.value = v;
            return val;
        }
    }
    
    /**
     * Generates a hash code using the first hash function.
     * @param k key that the hash code is being generated for.
     * @return a hash code.
     */
    private int myHash1( KeyType k ) 
    {
        return Math.abs( hash1.hashCode( k ) % arr.length );
    }
    
    /**
     * Generates hash code using the second hash function.
     * @param k key that the hash code is being generated for.
     * @return a hash code.
     */
    private int myHash2( KeyType k )
    {
        return Math.abs( hash2.hashCode( k ) % arr.length );
    }
    
    /**
     * Determines if an index in the hash table contains a specific key, k.
     * @param k the key in question.
     * @param index the index being searched.
     * @return the Node containing the key, or null if not found.
     */
    private Node listContains( KeyType k, int index )
    {    
        Node current = arr[ index ];                //Get the first Node
        while ( current != null )                   //While not end of list
        {
            KeyType k1 = (KeyType) current.key;     //Get current key
            if ( k.equals( k1 ) )                   //Compare with search
                return current;                     //Exit, if found
            
            current = current.next;                 //Otherwise, continue
        }
        return null;                                //Return null, if not found
    }
    
    /**
     * Private routine that enlarges the array. 
     */
    private void rehash()
    {
        MyHashMap<KeyType, ValueType> tempHash
                = new MyHashMap<>( hash1, hash2 );
        tempHash.arr = new Node[ arr.length * 4 ];
        
        int index = 0;
        while ( index < arr.length )
        {
            Node curr = arr[ index ];
            {
                while ( curr != null )
                {
                    tempHash.put( (KeyType) curr.key, (ValueType) curr.value );
                    curr = curr.next;
                }
            }
            index++;
        }
        
        arr = tempHash.arr ;
        tempHash = null ;
    }
    
    /**
     * Determines the size (number of items) in the list at a given index.
     * @param index the index of the hash table.
     * @return the number of items 
     */
    private int indexSize( int index )
    {
        int countNodes = 0;
        Node current = arr[ index ];
        while ( current != null )
        {
            current = current.next;
            countNodes++;
        }
        
        return countNodes;
    }
    
    /**
     * Removes a specific key from the hash table.
     * @param k the key of the item being removed.
     * @return true if the item is removed, else false.
     */
    public boolean remove( KeyType k )
    {
        //int min = findMinHashCode( k );
        int hash1Code = myHash1( k );
        int hash2Code = myHash2( k );
        Node contains1 = listContains( k, hash1Code );
        Node contains2 = listContains( k, hash2Code );
        
        if ( contains1 == null && contains2 == null )
        { return false; }
        else if ( contains1 != null & contains2 == null )   //list 1 contains
        {
            theSize--;
            Node current = arr[hash1Code];
            if ( current == contains1 )         //first node
            {
               arr[ hash1Code ] = current.next;
                return true;
            }
            while ( current.next != contains1 ) //nodes after the first
            {
                current = current.next;
            }
            current.next = contains1.next;
            return true;
        }
        else    //list 2 Contains
        {
            theSize--;
            Node current = arr[hash2Code];
            if ( current == contains2 )         //first node
            {
                arr[ hash2Code ] = current.next;
                return true;
            }
            while ( current.next != contains2 ) //nodes after the first
            {
                current = current.next;
            }
            current.next = contains2.next;
            return true;
        }
    }
    
    /**
     * Computes two hash codes and returns the one that has the smaller list.
     * @param k the key.
     * @return the hash code with the smaller list.
     */
    private int findMinHashCode( KeyType k )
    {
        int hash1Result = myHash1( k );
        int hash2Result = myHash2( k );
        int list1Size = indexSize( hash1Result );
        int list2Size = indexSize( hash2Result );
        
        if ( list1Size <= list2Size )
            return hash1Result;
        else
            return hash2Result;
    }
    
    /**
     * Returns the value of a given key.
     * @param k the key.
     * @return the value.
     */
    public ValueType get( KeyType k )
    {
        int min = findMinHashCode( k );
        Node contains = listContains( k , min );
        if ( contains != null )
            return (ValueType) contains.value;
        else
            return null;
    }
    
    /**
     * Represents the hash table as a String.
     * @return the hash table.
     */
    public String toString( )
    {
        StringBuilder sb = new StringBuilder() ;
        sb.append( "[ " );
        int index = 0;
        while ( index < arr.length )
        {
            Node current = arr[ index ];
            while ( current != null )
            {
                sb.append( current );
                sb.append( " " );
                current = current.next;
                
            }
            sb.append(" ]");
            index++;
            sb.append("\n");
            sb.append("[ ");
        }
        sb.append(" ]");
        return new String( sb );
    }
    
    /**
     * An iterator that allows iteration through the hash table.
     * @return an Iterator.
     */
    public Iterator<Map.Entry<KeyType, ValueType>> iterator( )
    {
        return new Iterator<Map.Entry<KeyType, ValueType>>( )
        {
            Node<KeyType, ValueType> current;   // current node
            int listNum;                        // current list #
            
            public boolean hasNext( )
            {
                return current != null;
            }
            
            public Map.Entry<KeyType, ValueType> next( )
            {
                final Node<KeyType, ValueType> theCurrent = current;
                
                Map.Entry<KeyType, ValueType> nextItem 
                        = new Map.Entry<KeyType, ValueType>( )
                {
                    public KeyType getKey()
                    {
                        return theCurrent.key;
                    }

                    public ValueType getValue()
                    {
                        return theCurrent.value;
                    }

                    public ValueType setValue(ValueType value)
                    {
                        ValueType val = theCurrent.value;
                        theCurrent.value = value;
                        return val;
                        //throw UnsupportedOperationException();
                    }
                };
                 
                current = current.next;
                
                if( current == null )
                {
                    listNum++;
                    advanceToNewList( );
                }
                
                return nextItem;       
            }
            
            private void advanceToNewList( )
            {
                while( listNum < arr.length && arr[ listNum ] == null )
                    listNum++;
                
                if( listNum != arr.length )  // current is already null
                    current = arr[ listNum ];
            }
            
            {
                advanceToNewList( );
            }
        };
        
    }
    
    /**
     * Node class that comprises of a key and a value.
     * @param <KeyType> represents a key.
     * @param <ValueType> represents a value.
     */
    private static class Node<KeyType, ValueType>
    {
        KeyType key;
        ValueType value;
        Node<KeyType, ValueType> next ;
        
        Node( KeyType k, ValueType v, Node<KeyType, ValueType> n )
        {
            key = k; value = v; next = n; 
        }
        
        public String toString( )
        {
            return key + "=" +  value; 
        }
    }
    
    /**
     * Returns an array with a distribution of the hash table.
     * @return the distribution array.
     */
    public int [ ] getLengths()
    {
        int [ ] count = new int [ 20 ];
        Arrays.fill( count, 0 );
        for( int i = 0; i < arr.length; i++ )
        {
            int s = indexSize( i );
            count[ s ] = count[ s ] + 1; 
        }
        return count;
    }
}

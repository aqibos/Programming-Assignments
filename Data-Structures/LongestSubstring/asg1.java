import java.io.File;
import java.io.FileNotFoundException ;
import java.util.Scanner;
import java.util.Arrays ;

class TheString implements Comparable<TheString>
{
    private String original ;       //stores the entire String
    private int beginIndex ;        //represents where the substring begins
    private int length ;            //represents where the substring ends 
    
    /**
     * Constructs a TheString object from a String object.
     * @param aString the source String.
     */
    public TheString( String aString ) 
    {
        original = aString ;                //initialize instance var's
        beginIndex = 0 ;
        length = aString.length() ;
    }
    
    /**
     * Creates a substring from a given index to the end of the string.
     * @param startIndex the index of where the substring begins
     * @return a TheString object with the new beginning index
     */
    public TheString substring( int startIndex )
    {
        TheString temporary ;
        temporary = new TheString ( original ) ; //create a temporary TheString
        temporary.beginIndex = startIndex ;      //change beginIndex to argument
        temporary.length = original.length() ;   //change the length
        return  temporary ;
    }
    
    /**
     * Returns a TheString object as a String.
     * @return a TheString object represented as a String object. 
     */
    @Override
    public String toString()
    {
        //create String substring using beginning index
        return original.substring( beginIndex ) ;   
    }
    
    /**
     * Alternative toString method that takes two parameters.
     * @param begin the beginning of the String.
     * @param length the number of chars in the String.
     * @return the desired String.
     */
    public String toString(int begin, int length)
    {
        String temp = original.toString() ;
        
        if ( length > 30 )
        {
            return temp.substring( begin , begin + 30 ) + " ... " ;
        }
        else
        {
            return temp.substring( begin , length ) ;
        }
    }
    
    /**
     * Returns a char at a specific index.
     * @param index the position of the char in the TheString
     * @return a char that is at the specified position
     */
    public char charAt( int index )
    {
        return original.charAt( index ) ;
    }

    /**
     * Compares one TheString to another .
     * @param that  the second TheString being compared.
     * @return -1 if the second TheString is greater, 0 
     * if they are both equal, and 1 if the first TheString 
     * is greater.
     */
    @Override
    public int compareTo(TheString that)
    {
        int retValue ;  //value to be returned
        
        int thisStart = this.beginIndex ;
        int thatStart = that.beginIndex ;
        
        //if first char of "this" is before the first char of "that"
        if ( this.charAt ( thisStart ) < that.charAt (thatStart ) )
        {
            return - 1 ;
        }
        //otherwise...if first char of "this" is after the first char of "that"
        else if ( this.charAt ( thisStart ) > that.charAt ( thatStart ) )
        {
            return 1 ;
        }
        else    //first char's are equal
        {
            //while the subsequent chars are equal and the end of either 
            //TheString is reached...
            while (thisStart < this.length - 1 && thatStart < that.length - 1 
                    && this.charAt( thisStart ) == that.charAt( thatStart ) )
            {
                //continue to match the chars...
                thisStart++ ; thatStart++ ;
            }
            //if the first char that is not matching of "this" is less than 
            //corresponding char of "that", then ...
            if ( this.charAt ( thisStart ) < that.charAt ( thatStart ) )
            {
                 retValue = - 1 ;       //return - 1 
            }
            //otherwise, if the converse is true, then return 1
            else if ( this.charAt ( thisStart ) > that.charAt ( thatStart ) )
            {
                return 1 ;
             }
            //otherwise, the end of both TheStrings are reached, 
            //return 0 (they must be equal)
            else if ( thisStart == this.length - 1 
                    && thatStart == that.length - 1 )
            {
                return 0 ;
            }
            //otherwise if "this" finished before "that", 
            //then it is smaller. Thus, return - 1 
            else if ( thisStart == this.length - 1 
                    && thatStart != that.length - 1 )
            {
                return -1 ;
            }
            //otherwise, if "this" finished after "that", then it is larger. 
            //Thus, return 1
            else 
            //if ( thisStart != this.length -1  && thatStart == that.length - 1)
            {
                return 1 ;
            }
        }
        return retValue ;
    }
    
    /**
     * Get method for length. 
     * @return the length of the TheString.
     */
    public int getLength() 
    {
        return length ;
    }
    
    /**
     * Get method for beginning index.
     * @return the beginning index of the TheString.
     */
    public int getBeginIndex()
    {
        return beginIndex ;
    }
    
}


//-----------------------------------------------------------------------------


public class asg1
{
    private static int fileOneSize = 0 ;
    //stores where end of one file and beginning of second file occurs 
    
    /**
     * Finds the longest common prefix from two strings.
     * @param s0 the first String
     * @param s1 the second String
     * @param fileSize a int that represents a boundary that divides the string
     * into two parts, so that the longest phrase is not within the same
     * file.
     * @return the number of chars in the longest common prefix in both strings.
     */
    public static int longestPrefix( TheString s0, TheString s1 , int fileSize)
    {
        int s0Start =  s0.getBeginIndex() ;
        int s1Start = s1.getBeginIndex() ;
        int count = 0 ;
        
        while( s0Start < s0.getLength() - 1 && s1Start < s1.getLength() - 1
                && s0.charAt ( s0Start ) == s1.charAt( s1Start )
                && ((s0.getBeginIndex() < fileSize 
                        && s1.getBeginIndex() > fileSize )
                || (s0.getBeginIndex() > fileSize 
                        && s1.getBeginIndex() < fileSize ) ) ) 
        {
            s0Start++ ; s1Start++ ; count++ ;
        }
        return count ;
    }
    
    /**
     * Creates one compiled String from two files.
     * @param fileOne the first file.
     * @param fileTwo the second file.
     * @return the combined String.
     * @throws FileNotFoundException 
     */
    public static String compileFiles( String fileOne, String fileTwo ) 
            throws FileNotFoundException
    {
        Scanner fileReader = new Scanner( new File ( fileOne ) ) ;
        
        StringBuilder sb = new StringBuilder() ;
        
        while ( fileReader.hasNext() )
        {
            String s0 = fileReader.next() ;
            sb.append( s0 ) ;
            sb.append( " " ) ;
        }
        sb.setLength( sb.length() - 1  );    //remove extra space char
        sb.append( '@' ) ; 
        String temp = new String ( sb ) ;
        fileOneSize = temp.length()  - 1 ;     
        
        fileReader = new Scanner( new File ( fileTwo ) ) ;
        
        while ( fileReader.hasNext() )
        {
            String s0 = fileReader.next() ;
            sb.append( s0 ) ;
            sb.append(' ') ;
        }
        
        sb.setLength( sb.length() - 1 ) ;    //remove extra space
        String str = new String ( sb ) ;
        
        return str ;
    }
    
    public static void main(String [] args)
    {   
        try
        {
            System.out.println( "Files : " + args[ 0 ] 
                    + ", " + args[ 1 ] ) ;
            String str = compileFiles( args[ 0 ] , args[ 1 ] ) ;
            
            TheString compiledString = new TheString( str ) ;
        
            TheString [] suffixes = new TheString[ str.length() ] ;
        
            long start, end, elapsed;
            start = System.currentTimeMillis( );
            System.out.println("\nComputing substring ... ") ;
            for ( int i = 0 ; i < suffixes.length ; ++i )
            {
                suffixes[ i ] = compiledString.substring( i ) ;
            }
        
            System.out.println("\nFinished substring ... ") ;
        
            System.out.println( "\nStarting sort ... " ) ;
            Arrays.sort( suffixes ) ;
            System.out.println( "\nFinished sort ... " ) ;
        
            System.out.println("\nFinding least common prefix ( LCP ) ... ");
        
            int [] LCP = new int [ str.length() ] ;
            for ( int i = 1 ; i < str.length() ; i++ )
            {
                LCP[ i ] = longestPrefix(suffixes[ i ] , 
                    suffixes[ i - 1 ], fileOneSize ) ;
            }
        
            System.out.println("\nFound maximum LCP ... \n");
        
            int maxLCPIndex = 0 ;
            for ( int i = 1 ; i < LCP.length ; i++ )
            {
                if ( LCP[ i ] > LCP[ maxLCPIndex ] )
                {
                    maxLCPIndex = i ;
                }
            }
        
            end = System.currentTimeMillis( ) ;
        
            System.out.println( "Index of maximum LCP = " + maxLCPIndex ) ;
            System.out.println( "Length of max LCP = " + LCP[ maxLCPIndex ] ) ;
        
            int beginIndex = suffixes[ maxLCPIndex ].getBeginIndex() ;
            System.out.println( "LCP begins at : "  + beginIndex ) ;
            
            TheString temp = suffixes[ maxLCPIndex ] ;
            String str2 = temp.toString( beginIndex , LCP[ maxLCPIndex ] ) ;
            System.out.println( "\nThe pattern is: " + str2 ) ;
        
            elapsed = end - start ;
            System.out.println( "\nTime elapsed: " + elapsed + "ms." ) ;
        }
        catch ( FileNotFoundException error ) 
        {
            System.out.println("Sorry, the file(s) were not found. "
                    + "\nPlease restart the program. ") ;
        }
    }
}
 
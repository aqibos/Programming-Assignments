import java.io.File ;
import java.io.FileNotFoundException ;
import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

//------------------------------------------------------------------------------

/**
 * Represents a position with a row and a column. 
 * @author Aqib Shah
 */
interface Position
{
    int getRow() ;                          //row of the position
    int getColumn() ;                       //row of the column
    List< Position > getNeighbors() ;       //list of all adjacent positions
}

//------------------------------------------------------------------------------
/**
 * A solver for the game Boggle.
 * @author Aqib Shah
 */
class Boggle
{
    //--------------------------------------------------------------------------
    //Represents a coordinate ( row , column )
    private class aPosition implements Position
    {
        int row ;                           //instance var's
        int column ;
        String value ;
    
        public aPosition( int aRow , int aCol )
        {
            row = aRow ;
            column = aCol ;
        }
        
        public List< Position > getNeighbors() 
        {
            int bottom ;        //below 
            int top ;           //above
            int right ;         //right-side
            int left ;          //left side
            List< Position > neighbors  = new ArrayList<>() ;
            //get row below
            top = ( this.row == 0 ) ? 0 : ( this.row - 1 ) ; 
            //get row above
            bottom = ( row == numOfRows - 1 ) ? row : (  row + 1 ) ; 
            //get column to left
            right = ( column == numOfCols - 1 ) ? column : ( column + 1 ) ; 
            //get column to right
            left = ( column == 0 ) ? 0 : ( column - 1 ) ;                   
            
             //Start from top and go down
            for ( int r = top ; r <= bottom ; ++r )     
            {
                //Start from left and go right
                for ( int c = left ; c <= right ; ++c ) 
                {
                    //If not same position, then add as a neighbor
                    if ( r != row || c != column )     
                    {
                        neighbors.add( new aPosition ( r , c ) ) ;
                    }
                }
            }
            return neighbors ;
        }
        
        /**
         * Get method for a specific spot on the board.
         * @return the char at the location
         */
        public char getLetter()
        {
            return board[ row ][ column ] ;
        }
    
        /**
         * Get method for a row.
         * @return the row in which the position is located.
         */
        public int getRow() 
        {
            return row ;
        }
    
        /**
         * Get method for a column.
         * @return the column in which the position is located. 
         */
        public int getColumn()
        {
            return column ;
        }
        
        /**
         * Represents the position as an ordered pair.
         * @return a String representing the position.
         */
        public String toString()
        {
            return "( " + row + ", " + column + " )" ;
        }
    
        /**
         * Compares to positions to check whether they are equal.
         * @param other the second position.
         * @return whether they are equal (true) or not (false).
         */
        @Override
        public boolean equals( Object other ) 
        {
            if ( ! ( other instanceof aPosition ) )
            {
                return false ;
            }
            else
            {
                aPosition temp = ( aPosition ) other ;
                return ( row == temp.row ) && ( column == temp.column ) ;
            }
        }
    }
    // End of aPosition class . . . 
    //--------------------------------------------------------------------------
    
    private char [][] board ;                //instance var's
    private int numOfRows ;
    private int numOfCols ;
    private String [] dictionarys = new String [ 1000 ] ;
    
    /**
     * Creates a Boggle board given a proper text file. 
     * @param file the file containing the letters.
     * @throws FileNotFoundException text file not found.
     * @throws IncorrectPuzzleFormatException text file not proper format.
     * @throws DictionaryException dictionary not found.
     */
    public Boggle( String file ) 
            throws FileNotFoundException, IncorrectPuzzleFormatException, 
            DictionaryException
    {
        ArrayList< String > puzzle = new ArrayList<>() ;
        Scanner fileReader = new Scanner ( new File ( file ) ) ;
                        
        while ( fileReader.hasNext() )
        {
            String line = fileReader.next() ;
            puzzle.add( line ) ;    //Store all the lines of the puzzle
        } 
        
        boolean validPuzzle = false ;
        for ( int i = 0 ; i < puzzle.size() - 1 ; i++ )
        {
            validPuzzle = ( puzzle.get( i ).length()    //check whether all
                    == puzzle.get( i + 1 ).length() ) ; //lines are of equal
            if ( validPuzzle == false )                 //length.
            {
                throw new IncorrectPuzzleFormatException() ;
            }
        }
        
        if ( puzzle.size() == 0 )
            throw new IncorrectPuzzleFormatException() ;
        
        numOfRows = puzzle.size() ;   //If valid puzzle, initialize instance v's
        numOfCols = puzzle.get( 0 ).length() ; 
        board = new char [ numOfRows ][ numOfCols ] ;
        
        for ( int i = 0 ; i < numOfRows ; ++i )
        {
            String line = puzzle.get( i ) ;
                
            for ( int j = 0 ; j < numOfCols ; ++j )
            {
                board[ i ][ j ]  = line.charAt( j ) ;   //set-up board
            }
        }
        
        //----------------- SET UP DICTIONARY ----------------------------------
        int words = 0 ; 
        try
        {
            Scanner dictScanner = new Scanner ( new File ( "dict.txt" ) ) ;
            
            while ( dictScanner.hasNext() )
            {
                if ( words > dictionarys.length - 1 )
                {
                    String [] temp = new String [ dictionarys.length * 2 ] ;
                    System.arraycopy( dictionarys , 0 , temp , 0 , words ) ;
                    dictionarys = temp ;
                }
                String tempWord = dictScanner.next() ;
                if ( tempWord.length() > 2 )   //dont store 1 or 2 letter words
                    dictionarys[ words++ ] = tempWord ; 
            }
            String [] temp = new String [ words ] ;
            System.arraycopy( dictionarys , 0 , temp , 0 , words ) ;
            dictionarys = temp ;    //delete any empty array spaces
            Arrays.sort( dictionarys ) ; 
            
        } 
        catch ( FileNotFoundException e )
        {
            throw new DictionaryException() ;
        }
        //-------------- DONE SETTING DICTIONARY -------------------------------
    }
    
    /**
     * Returns a letter based on the given row and column.
     * @param row the row at which the letter is located.
     * @param col the column at with the letter is located.
     * @return the letter at the given position.
     */
    public char getLetter( int row , int col )
    {
        return board[ row ][ col ] ;
    }
    
    /**
     * Solves the puzzle.
     * @return Map with a word as the key and a list of positions as the
     * value for each key
     */
    public Map< String , List < aPosition > > solve() 
    {
        Map< String, List < aPosition > > answers = new TreeMap() ;
        List path = new ArrayList() ; 
        
        for ( int i = 0 ; i < numOfRows ; i++ )    // for each char in row
        {
            for ( int j = 0 ; j < numOfCols ; j++ ) //for each char in col
            {
                solve( new aPosition( i , j ) , "" , path , answers ) ; //solve
            }
        }
        return answers ;
    }
    /**
     * Solves Boggle recursively.
     * @param thisPos: current position
     * @param charSequence: current character sequence
     * @param path: current path 
     * @param results: list with all answer found
     */
    private void solve( aPosition thisPos , String charSequence , 
            List path , Map results ) 
    {
        String temporary = charSequence + thisPos.getLetter() ;
        //temporarily add current letter and look for String with temporary
        int found = Arrays.binarySearch( dictionarys, temporary ) ;             
        
        if ( path.contains( thisPos ) )  //If already added, then skip
        {
            return ;
        }
        if ( found < 0 )        //Item not found (not exact match)
        {
            int inverse = (-1 * found) - 1 ;
            if ( inverse == dictionarys.length )
            {
                return ;
            }
            String wordPrefix = dictionarys[ inverse ] ;             
            if ( wordPrefix.indexOf( temporary ) == 0 )     //contains prefix   
            {
                charSequence = temporary ;  //If prefix, then add the char
                path.add( thisPos ) ;       //Add current position to path 
                List neighbors = thisPos.getNeighbors() ; 
                for ( int i = 0 ; i < neighbors.size() ; i++ )
                {
                    aPosition temp = ( aPosition ) neighbors.get( i ) ;
                    solve( temp , charSequence , path ,  results ) ; //Recursive
                }                                           //call
            }
            else               //Otherwise, not on list at all...
            {
                return ; 
            }   
        }
        if ( found >= 0 )      //Exact item found           
        {
            charSequence = temporary ;      //add the char to sequence
            path.add( thisPos ) ;           //add the position to path
            
            List currentPath = new ArrayList<>();   //make a copy of path
            for ( int i = 0 ; i < path.size() ; i++ )
            {
                currentPath.add( path.get ( i ) ) ; 
            }
            results.put( charSequence , currentPath ) ;//put into map
            
            List neighbors = thisPos.getNeighbors() ;       //recursively do for 
            for ( int i = 0 ; i < neighbors.size() ; i++ )  //neighbors
            {
                aPosition temp = ( aPosition ) neighbors.get( i ) ;
                solve( temp , charSequence , path ,  results ) ;
            }
        }
        path.remove( path.size() - 1 ) ;    //backtracking
    }
    
    /**
     * Calculates total number of points in a map -- hidden.
     * @return the total number of points.
     */
    private int calculateTotalPoints( Map answers )
    {
        int cumulativePoints = 0 ;
        int currentPoints = 0 ;
        Map < String , List < aPosition > > results = answers ;
        for ( Map.Entry< String, List< aPosition > > entry 
                : results.entrySet() )
        {
            String key = entry.getKey() ;
            currentPoints = calculateIndividualPoints ( key ) ;
            cumulativePoints = cumulativePoints + currentPoints ;
        }
        return cumulativePoints ;
    }
    
    /**
    * Calculate the number of points for each word, also known as the key
    * @return the individual points each key is worth.
    */
    private int calculateIndividualPoints( String key )
    {
        int currentPoints ;
    
        if ( key.length() >= 9 )
        {
            currentPoints = 15 ;
        }
        else if ( key.length() >= 8 )
        {
            currentPoints = 10 ;
        }
        else if ( key.length() >= 7 )
        {
            currentPoints = 6 ;
        }
        else if ( key.length() >= 6 )
        {
            currentPoints = 4 ;
        }
        else if ( key.length() >= 5 )
        {
            currentPoints = 3 ;
        }
        else if ( key.length() >= 4 )
        {
            currentPoints = 2 ;
        }
        else if ( key.length() >= 3 ) 
        {
            currentPoints = 1 ;
        }
        else
        {
            currentPoints = 0 ;
        } 
        return currentPoints ;
    }
    
    /**
     * Creates a String representation of the board.
     * @return the board as a String.
     */
    public String toString()
    {
        String sBoard = "  " ;
        int x = 0 ;
        while ( x < numOfCols )
        {
            if ( x > 9 )
                sBoard = sBoard + "  " + x ;
            else
                sBoard = sBoard + "   " + x ;
            x++ ;
        }
        sBoard = sBoard + "\n" ;
        
        x = 0 ;
        for (int i = 0 ; i < numOfRows ; i++ )
        {
            if ( x > 9 )
                sBoard += x ;
            else
                sBoard += " " + x ;
            for ( int j = 0 ; j < numOfCols ; j++ )
            {
                sBoard = sBoard + "   " + board[ i ][ j ] ;
            }
            sBoard = sBoard + "\n" ;
            x++ ;
        }
        return sBoard ;
    }
    
    
    /**
     * Creates a String with each word along with its path and the amount of 
     * points it is worth. 
     * @param answers a Map containing the words as a key and a list of
     * positions as a value. 
     * @return a String with of all of the possible solutions, how they can
     * be found and the number of points they are worth.
     */
    public String printAnswers( Map answers ) 
    {
        String print = "" ;
        if ( answers.size() >= 200 )
        {
            int count3 = 0 ;        int points3 = 0 ;
            int count4 = 0 ;        int points4 = 0 ;
            int count5 = 0 ;        int points5 = 0 ;
            int count6 = 0 ;        int points6 = 0 ;
            int count7 = 0 ;        int points7 = 0 ;
            
            Map < String , List < aPosition > > results = answers ;
            for (Map.Entry< String , 
                    List < aPosition > > entry : results.entrySet() )
            {
                String key = entry.getKey() ;
                List tempPath = entry.getValue() ;
                
                if ( key.length() >= 8 )
                {
                    print = print + key + ", " + " path = " + tempPath ;
                    print = print + ", " + calculateIndividualPoints( key ) 
                            + " points. \n" ;
                }
                else if ( key.length() >= 7 )
                {
                    count7++ ;
                    points7+= 6 ;
                }
                else if ( key.length() >= 6 )
                {
                    count6++ ;
                    points6+= 4 ;
                }
                else if ( key.length() >= 5 )
                {
                    count5++ ;
                    points5+= 3 ;
                }
                else if ( key.length() >= 4 )
                {
                    count4++ ;
                    points4+= 2 ;
                }
                else //if ( key.length() >= 3 )
                {
                    count3++ ;
                    points3+= 1 ;
                }
            }
            print = print + "Words of length 7: " + count7 
                        + ", worth " + points7 + " points."
                    + "\nWords of length 6: " + count6 
                        + ", worth " + points6 + " points."
                    + "\nWords of length 5: " + count5 
                        + ", worth " + points5 + " points."
                    + "\nWords of length 4: " + count4 
                        + ", worth " + points4 + " points."
                    + "\nWords of length 3: " + count3 
                        + ", worth " + points3 + " points.\n" ;
            
            print = print + "\nTOTAL POINTS: " 
                    + calculateTotalPoints ( results ) ;
        }
        else
        {
            Map < String , List < aPosition > > results = answers ;
            for (Map.Entry< String , 
                    List < aPosition > > entry : results.entrySet() )
            {
                String key = entry.getKey() ;
                List tempPath = entry.getValue() ;
                    print = print + key + ", " + " path = " + tempPath ;
                    print = print + ", worth " 
                            + calculateIndividualPoints( key ) 
                            + " points." ;
                    print = print + "\n" ;
            }

            print = print + "\nTOTAL POINTS: " 
                    + calculateTotalPoints( results ) ;   
        }
        return print ;
    }
}
//------------------------------------------------------------------------------

/**
 * Exception for a puzzle having incorrect dimensions.
 * @author Aqib Shah
 */
class IncorrectPuzzleFormatException extends Exception
{
    /**
     * Constructor for exception.
     */
    public IncorrectPuzzleFormatException()
    {}
    
    /**
     * Creates an exception with a specific message.
     * @param message the message to be displayed.
     */
    public IncorrectPuzzleFormatException( String message )
    {
        super( message ) ;
    }
}

/**
 * Exception for when a dictionary file is not found.
 * @author Aqib Shah
 */
class DictionaryException extends Exception
{
    /**
     * Constructor for exception.
     */
    public DictionaryException()
    {}
    
    /**
     * Creates an exception with a specific message.
     * @param message the message to be displayed.
     */
    public DictionaryException( String message )
    {
        super( message ) ;
    }
}

//------------------------------------------------------------------------------
/**
 *  Driver class for Boggle class.
 * @author Aqib Shah
 */
public class asg2 
{
    public static void main( String [] args )
    {
        try 
        {   
            String file = "puz2.txt" ;
            System.out.println("\nFile: " + file + "\n") ;
            Boggle board = new Boggle( file ) ;

            String sBoard = board.toString() ;
            System.out.println( sBoard ) ;

            Map answers = board.solve() ;
            
            String results = board.printAnswers( answers ) ;
            System.out.println(results);
            System.out.println("Number of Words: " + answers.size() ) ;
        }
        catch( FileNotFoundException e )
        {
            System.err.println( "Puzzle file not found!" ) ;
        }
        catch( IncorrectPuzzleFormatException e )
        {
            System.err.println( "Not valid board parameters or the puzzle"
                    + " file is empty." ) ; 
        }
        catch ( DictionaryException e )
        {
            System.err.println( "No dictionary found." ) ;
        }
    }
}

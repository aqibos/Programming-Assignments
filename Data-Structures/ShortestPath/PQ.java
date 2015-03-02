import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * A class that uses a priority queue to compute the shortest path from the 
 * starting point ( 0 , 0 ) to the ending point (the lower right corner) of a 
 * grid. The shortest path includes the lowest sum of each individual cell
 * that was visited. The path from the starting point to the end point that 
 * contains the largest minimum can also be computed.
 * 
 * @author Aqib Shah
 */
public class PQ
{
    /**
     * A class to represent individual cells in the grid.
     */
    private class Square
    {
        int row ;       //CURRENT ROW
        int col ;       //CURRENT COLUMN
        int dist ;      //"WEIGHT" of the square
        Square prev ;   //Prevous square visited
                
        /*
        
        Sample Grid :
        
       r\c 0    1    2    3
         |-------------------|
       0 | 0  | 14 | 15 | 82 | 
         |-------------------|          
       1 | 61 | 17 | 28 | 94 | 
         |-------------------|
       2 | 11 | 54 | 10 | 28 |
         |-------------------|
       3 | 12 | 43 | 77 | 62 |
         |-------------------|
       4 | 40 | 13 | 14 | 0  |
         |-------------------|
         */
        
        /**
         * Constructor that sets a square with the given row and column.
         * @param r the row
         * @param c the column
         */
        public Square( int r , int c )
        {
            row = r ;           //Initialize instance var's
            col = c ;
            dist = INFINITY ;
            prev = null ;
        }
        
        /**
         * Sets the distance and previous square to new values/objects.
         * @param newDistance the updated distance
         * @param prevSquare the updated previous square
         */
        public void setDistance ( int newDistance , Square prevSquare )
        {
            dist = newDistance ;
            prev = prevSquare ;
        }
        
        /**
         * Get method for distance.
         * @return the distance.
         */
        public int getDistance()
        {
            return dist ;
        }
        
        /**
         * Get method for cost.
         * @return the cost.
         */
        public int getCost() 
        {
            return matrix [ row ][ col ] ;
        }
        
        /**
         * Sets the cost to a new value.
         * @param newCost the new cost.
         */
        public void setCost( int newCost )
        {
            matrix [ row ][ col ] = newCost ;
        }
        
        /**
         * Gets the previous square.
         * @return the previous square.
         */
        public Square getPrev()
        {
            return prev ;
        }
        
        /**
         * Creates a String representation of a Square.
         * @return the information of the square.
         */
        public String toString()
        {
            return "( " + row + " , " + col + " ) cell is " 
                    + matrix[ row ][ col ] ;
        }
        
        /**
         * Finds all adjacent squares.
         * @return a list of all of the adjacent squares.
         */
        public List < Square > getAdjacents()
        {
            int lowRow = ( row == 0 ) ? 0 : row - 1 ;
            int lowCol = ( col == 0 ) ? 0 : col - 1 ;
            int highRow = ( row == numRows - 1 ) ? row : row + 1 ;
            int highCol = ( col == numCols - 1 ) ? col : col + 1 ;
            
            List < Square > neighbors = new ArrayList <> () ;
            
            for ( int r = lowRow ; r <= highRow ; r++ )
                for ( int c = lowCol ; c <= highCol ; c++ )
                    if ( c != col || r != row )
                        neighbors.add( squares[ r ][ c ] ) ;    
            
            return neighbors ; 
        }
    }   //END OF SQUARE CLASS
    
    
    private Square [][] squares ;
    private int [][] matrix ;
    private int numRows ;
    private int numCols ;
    public static final int INFINITY = Integer.MAX_VALUE / 3 ;
    public final Square UPPER_LEFT ;
    public final Square LOWER_RIGHT ;
      
    /**
     * Constructs a grid from a properly formatted file.
     * @param file the name of the file as a String.
     * @throws FileNotFoundException if the file name is not found.
     * @throws IncorrectPuzzleFormatException if the file is not properly 
     * formatted.
     */
    public PQ( String file ) 
            throws FileNotFoundException , IncorrectPuzzleFormatException
    {
        //--------------------------- READ INPUT -------------------------------
        Scanner fileScan = new Scanner( new File ( file ) ) ;
        List<String> puzzle = new ArrayList<>() ;
        
        while ( fileScan.hasNext() )
        {
            puzzle.add( fileScan.nextLine() ) ;     //Store lines in list
        } 
        
        if ( puzzle.isEmpty() )   //Empty puzzle
        {
            System.out.println("Empty file.");
            throw new IncorrectPuzzleFormatException() ; 
        }
        
        int count1 = 0 ;
        int count2 = 0 ;
        for ( int i = 0 ; i < puzzle.size() - 1 ; i++ )
        {
            Scanner lineScan = new Scanner( puzzle.get( i ) ) ; //current line
            while ( lineScan.hasNext() )
            {
                int current = lineScan.nextInt() ;
                count2++ ;              //find num of ints in current line
            }
            if ( i == 0 )               //if first line
                count1 = count2 ;
                
            //Puzzle NOT in proper format ( ie. not a rectangle )
            if ( count1 != count2 )     //compare num of ints with previous
            {
                System.out.println("Puzzle does not have correct dimensions.");
                throw new IncorrectPuzzleFormatException() ;
            }

            count1 = count2 ;           //save current
            count2 = 0 ;                //reset 
        }
        
        
        numRows = puzzle.size() ;                   //Initialize instance var's
        numCols = count1 ;
        
        matrix = new int [ numRows ][ numCols ] ;   
        
        for ( int i = 0 ; i < numRows ; i++ )       //store ints in board
        {
            String line = puzzle.get( i ) ;
            //System.out.println( line ) ;
            fileScan = new Scanner ( line ) ;
            
            for ( int j = 0 ; j < numCols ; j++ )
            {
                matrix [ i ][ j ] = fileScan.nextInt() ;
            }
        }
        //-------------------- FINISH READING FILE -----------------------------
        squares = new Square [ numRows ][ numCols ] ;
        
        for ( int r = 0 ; r < numRows ; r++ )
            for ( int c = 0 ; c < numCols ; c++ )
                squares [ r ][ c ] = new Square ( r , c ) ;
        
        UPPER_LEFT = squares [ 0 ][ 0 ] ;
        LOWER_RIGHT = squares [ numRows - 1 ][ numCols - 1 ] ;
        
    }
    
    
    /**
     * Finds the shortest path from top left to bottom right corner.
     * @param s the starting point.
     */
    public void computeShortestPath ( Square s ) //Starting point
    {
        for ( int r = 0 ; r < numRows ; r++ ) 
            for ( int c = 0 ; c < numCols ; c++ )
                squares [ r ][ c ].setDistance( INFINITY , null); 
  
            
        PriorityQueue < Square > pq = new PriorityQueue <> 
        ( ( a , b ) -> a.getDistance() - b.getDistance() ) ;
        
        s.setDistance( 0 , null ) ;
        
        pq.add( s ) ; 
        while ( !pq.isEmpty() )
        {
            Square v = pq.remove() ;
            for ( Square w : v.getAdjacents() )
            {
                if ( w.getDistance() == INFINITY )
                {
                    w.setDistance( v.getDistance() 
                            + w.getCost() , v ) ;
                    pq.add( w ) ;
                }
            }
        }
    }
    
    /**
     * Computes the bottle neck path from the starting point to the end.
     * @param s the starting square.
     */
    public void computeBottleNeckPath( Square s )
    {
        //Initialize all squares' distances to zero
        for ( int r = 0 ; r < numRows ; r++ )
            for ( int c = 0 ; c < numCols ; c++ )
                squares [ r ][ c ].setDistance( 0 , null ) ;
        
        //Create priority queue; sorts by maximum
        PriorityQueue < Square > pq = new PriorityQueue <> 
        ( ( a , b ) -> b.getDistance() - a.getDistance() ) ;
        
        //Set START point ( 0 , 0 ) to INFINITY
        s.setDistance( INFINITY , null ) ;
        int oldLowerCost = LOWER_RIGHT.getCost() ;  //Save cost of END point
        LOWER_RIGHT.setCost(INFINITY);              //Set END point to INFINITY
        
        pq.add( s ) ;               //Add the START point to the priority queue
        while ( !pq.isEmpty() )     //If priority queue is NOT empty...
        {
            Square v = pq.remove() ;//Get the max
            for ( Square w : v.getAdjacents() ) //Find it's neighbors...
            {
                if ( w.getDistance() == 0 )     //If not changed yet...
                {
                    //Change distance to minimum between itself and neighbor
                    w.setDistance( Math.min( v.getDistance() , w.getCost() ),
                            v ) ;
                    pq.add( w ) ;   //Add neighbor to priority queue
                }
            }
        }
            LOWER_RIGHT.setCost(oldLowerCost) ; //Return original cost of END
    }
    
    /**
     * Prints the path to the given square, s.
     * @param s the square of which the path is to be printed.
     */
    public void printPath ( Square s )
    {
        ArrayList < String > print = createPath ( s ) ;
        if ( print.size() > 20 )
        {
            for ( int i = 0 ; i < 10 ; i++ )
            {
                if ( i == 0 )
                {
                    String current = print.get(i) ;
                    current = current.replace( "is 0" , "is START" );
                    System.out.println( current );
                }
                else
                    System.out.println( print.get( i ) ) ;
            }
            System.out.println(" ... " ) ;
            for ( int i = (print.size() - 10) ; i < print.size() ; i++ )
            {
                if ( i == print.size() - 1 )
                {
                    String current = print.get(i) ;
                    current = current.replace( "is 0" , "is END" );
                    System.out.println( current );
                }
                else
                    System.out.println( print.get(i) ) ;
            }   
        }
        else
        {
            for ( int i = 0 ; i < print.size() ; i++ )
            {
                if ( i == 0 )
                {
                    String current = print.get(i) ;
                    current = current.replace( "is 0" , "is START" );
                    System.out.println( current );
                }
                else if ( i == print.size() - 1 )
                {
                    String current = print.get(i) ;
                    current = current.replace( "is 0" , "is END" );
                    System.out.println( current );
                }
                else
                    System.out.println( print.get(i) ) ;
            }
        }
    }
    
    /**
     * Creates a path and stores it into an ArrayList.
     * @param s the square of which the path is to be determined.
     * @return a list of the path.
     */
    private ArrayList createPath( Square s )
    {
        ArrayList < String > print = new ArrayList <>() ;
        
        while ( s != null )
        {
            print.add( s.toString() ) ;
            s = s.prev ;
        }
        
        ArrayList < String > temp = new ArrayList <>() ;
        for ( int i = ( print.size() - 1 ) ; i >= 0 ; i-- )
            temp.add( print.get( i ) ) ;
        
        print = temp ;
 
        return print ;
    }
    
    /**
     * Creates a string representation of the board.
     * @return the string with the board.
     */
    public String toString() 
    {
        StringBuilder sb = new StringBuilder() ;
        for ( int r = 0 ; r < numRows ; r++ )
        {
            for ( int c = 0 ; c < numCols ; c++ )
            {
                sb.append( matrix [ r ][ c ] ) ;
                sb.append(" ") ;
            }
            sb.append("\n") ;
        }
        return new String( sb ) ;
    }
    
    //--------------------------- MAIN -----------------------------------------
    
    public static void main ( String [] args )
    {
        try 
        {
            
            //System.out.println( "File - " + args[0] ) ; 
            //String file = "puzzle2.txt" ;
            System.out.println( "File - " + args[0]);
            PQ g1 = new PQ ( args[0] ) ;
            
            //System.out.println( g1.toString() );
            System.out.println( "\nComputing shortest path ... " ) ;
            g1.computeShortestPath( g1.UPPER_LEFT ) ;
            
            g1.printPath( g1.LOWER_RIGHT ) ;
            System.out.println( "Total cost is " + 
                    g1.LOWER_RIGHT.getDistance() ) ; 
            
            System.out.println("\n");
            System.out.println( "Computing bottle neck path ..." ) ; 
            g1.computeBottleNeckPath( g1.UPPER_LEFT ) ;
            
            g1.printPath( g1.LOWER_RIGHT ) ;
            System.out.println( "All cells support " + 
                    g1.LOWER_RIGHT.getDistance() ) ; 
        } 
        catch (FileNotFoundException ex) 
        {
            System.out.println("File not found.") ;
        } 
        catch (IncorrectPuzzleFormatException ex) 
        {
            System.out.println("Please run the program again after "
                    + "fixing the file" ) ;
        }
    } 
    //-------------------------- END OF MAIN -----------------------------------

    
}   //END OF GRID CLASS


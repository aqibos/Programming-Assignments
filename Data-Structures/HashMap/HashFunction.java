/**
 *
 * File: HashFunction.java
 *
 * The purpose of this class is to provide a Hash function interface.
 */

package cop3530 ;

public interface HashFunction<AnyType>
{
    int hashCode(AnyType x) ;
}
package rubikscube;

/**
 * A corner piece on a rubiks cube.
 * @author Cathal
 *
 */
public class Corner extends Piece
{
	/**
	 * Create a corner piece of a rubiks cube.	
	 * (Precondition: sides.length == 3)
	 * @param sides the array of sides for faces -> destination and current
	 */
	public Corner(Side[] sides)
	{
		super(sides);
	}
	
	/**
	 * Create a corner piece of a rubiks cube.
	 * (Precondition: sides.length == currentSides.length == 3)
	 * @param sides the destination sides of the faces
	 * @param currentSides the current sides of the faces
	 */
	public Corner(Side[] sides, Side[] currentSides)
	{
		super(sides, currentSides);
	}
}

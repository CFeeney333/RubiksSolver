package rubikscube;

public class Edge extends Piece
{
	/**
	 * Create an edge piece of a rubiks cube.	
	 * (Precondition: sides.length == 2)
	 * @param sides the array of sides for faces -> destination and current
	 */
	public Edge(Side[] sides)
	{
		super(sides);
	}
	
	/**
	 * Create an edge piece of a rubiks cube.
	 * (Precondition: sides.length == currentSides.length == 2)
	 * @param sides the destination sides of the faces
	 * @param currentSides the current sides of the faces
	 */
	public Edge(Side[] sides, Side[] currentSides)
	{
		super(sides, currentSides);
	}
	
	/**
	 * Get the face of the edge that doesn't have a given destination side.
	 * (Precondition: one face has that destination side)
	 * @param side the destination the face must not have
	 * @return the first face that doesn't have that destination side
	 */
	public Face getOtherFace(Side side)
	{
		for (Face face : this.getFaces())
		{
			if (!(face.getDestinationSide() == side))
			{
				return face;
			}
		}
		return null;
	}
}

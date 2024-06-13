package rubikscube;

import java.util.HashMap;

/**
 * A piece has an array of faces.
 * @author Cathal
 *
 */
public abstract class Piece
{
	private Face[] faces;
	
	/**
	 * Create a piece with an array of sides to make faces.
	 * The amount of faces is equal to the length of sides.
	 * The sides given are also the current sides of the faces.
	 * @param sides the sides to give the faces -> destination and current
	 */
	public Piece(Side[] sides)
	{
		this.faces = new Face[sides.length];
		for (int i = 0; i < sides.length; i++)
		{
			this.faces[i] = new Face(sides[i]);
		}
	}
	
	/**
	 * Create a piece with two arrays of sides, the first being the destination
	 * sides of the faces and the second being the current sides of the faces.
	 * (Precondition: sides.length == currentSides.length)
	 * @param sides the destination sides to give the faces
	 * @param currentSides the current sides i.e. the sides the faces are on
	 
	 */
	public Piece(Side[] sides, Side[] currentSides)
	{
		this.faces = new Face[sides.length];
		for (int i= 0; i < sides.length; i++)
		{
			this.faces[i] = new Face(sides[i], currentSides[i]);
		}
	}
	
	/**
	 * Get an array of the faces of the piece.
	 * @return an array of the piece's faces
	 */
	public Face[] getFaces()
	{
		return this.faces;
	}
	
	/**
	 * Is the piece in its correct position.
	 * @return true if all faces are at their destinations
	 */
	public boolean isAtCorrectPosition()
	{
		for (Face face : this.faces)
		{
			if (!face.isAtCorrectSide()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Does a face of this piece have a face with current side side?
	 * @param side the sides to search for in its faces' current sides
	 * @return true if a face has a current side side
	 */
	public boolean hasFaceWithCurrentSide(Side side)
	{
		for (Face face : this.faces)
		{
			if (face.getCurrentSide() == side) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasFaceWithDestinationSide(Side side)
	{
		for (Face face : this.faces)
		{
			if (face.getDestinationSide() == side)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Rotate the piece in accordance with rotation data.
	 * @param sideMap the map of sides showing which sides change to which
	 */
	public void rotate(HashMap<Side, Side> sideMap)
	{
		for (Face face : this.faces)
		{	
			if (sideMap.containsKey(face.getCurrentSide()))
			{
				face.changeCurrentSide(sideMap.get(face.getCurrentSide()));
			}
		}
	}
	
	/**
	 * Get the face with a given destination side.
	 * (Precondition: hasFaceWithDestinationSide() == true)
	 * @param side the destination side the face must have
	 * @return the face with the destination side
	 */
	public Face getFaceWithDestinationSide(Side side)
	{
		for (Face face : this.faces)
		{
			if (face.getDestinationSide() == side)
			{
				return face;
			}
		} 
		return null;
	}
	
	/**
	 * Get the face with a given current side.
	 * (Precondition: hasFaceWithCurrentSide() == true)
	 * @param side the current side the face must have
	 * @return the face with the current side
	 */
	public Face getFaceWithCurrentSide(Side side)
	{
		for (Face face : this.faces) 
		{
			if (face.getCurrentSide() == side)
			{
				return face;
			}
		}
		return null;
	}
	
	public String toString()
	{
		String state = "{";
		for (Face face : this.faces)
		{
			state += face.toString();
			if (face != this.faces[this.faces.length-1]) {
				state += ", ";
			}
		}
		state += "}";
		return state;
	}
}

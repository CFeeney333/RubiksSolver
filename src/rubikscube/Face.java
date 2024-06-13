package rubikscube;

/**
 * A face of a rubiks cube has a destination side and a current side.
 * @author Cathal
 *
 */
public class Face 
{
	private Side destination;
	private Side current;
	
	/**
	 * Create a face with a given destination and current side.
	 * @param side the destination side of the face
	 * @param currentSide the current side of the face
	 */
	public Face(Side destinationSide, Side currentSide)
	{
		this.destination = destinationSide;
		this.current = currentSide;
	}
	
	/**
	 * Create a face with a given destination side. The current side is set
	 * to the destination side also.
	 * @param side the destination and current side of the face
	 */
	public Face(Side side)
	{
		this.destination = side;
		this.current = side;
	}
	
	/**
	 * Get the side that the face is currently on.
	 * @return the current side of the face
	 */
	public Side getCurrentSide()
	{
		return this.current;
	}
	
	/**
	 * Get the side that the face belongs on.
	 * @return the destination side of the face
	 */
	public Side getDestinationSide()
	{
		return this.destination;
	}
	
	/**
	 * Is the current side of the face the side that the face belongs?
	 * @return true if current side is equal to the destination side
	 */
	public boolean isAtCorrectSide()
	{
		return this.destination == this.current;
	}
	
	/**
	 * Change the current side of the face.
	 * @param newSide the side to change the face to.
	 */
	public void changeCurrentSide(Side newSide)
	{
		this.current = newSide;
	}
	
	public String toString()
	{
		return  "{destination:" + this.getDestinationSide() + 
				", current:" + this.getCurrentSide() + "}";
	}
}

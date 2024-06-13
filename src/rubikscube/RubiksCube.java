package rubikscube;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * A rubiks cube has 8 corners and 12 edges.
 * The sides of the rubiks cube can rotate.
 * @author Cathal
 *
 */
public class RubiksCube
{
	private Corner[] corners;
	private Edge[] edges;
	
	public static final Side[][] CORNER_POSITIONS = 
		{{Side.TOP,    Side.FRONT, Side.LEFT},
		 {Side.TOP,    Side.FRONT, Side.RIGHT},
		 {Side.TOP,    Side.BACK,  Side.LEFT},
		 {Side.TOP,    Side.BACK,  Side.RIGHT},
		 {Side.BOTTOM, Side.FRONT, Side.LEFT},
		 {Side.BOTTOM, Side.FRONT, Side.RIGHT},
		 {Side.BOTTOM, Side.BACK,  Side.LEFT},
		 {Side.BOTTOM, Side.BACK,  Side.RIGHT}
		 };
	public static final Side[][] EDGE_POSITIONS =
		{{Side.TOP,    Side.FRONT},
		 {Side.TOP,    Side.BACK},
		 {Side.TOP,    Side.LEFT},
		 {Side.TOP,    Side.RIGHT},
		 {Side.BOTTOM, Side.FRONT},
		 {Side.BOTTOM, Side.BACK},
		 {Side.BOTTOM, Side.LEFT},
		 {Side.BOTTOM, Side.RIGHT},
		 {Side.FRONT,  Side.LEFT},
		 {Side.FRONT,  Side.RIGHT},
		 {Side.BACK,   Side.LEFT},
		 {Side.BACK,   Side.RIGHT}
		};
	public static final HashMap<Side, HashMap<Side,Side>> ROTATION_DATA =
			new HashMap<Side, HashMap<Side, Side>>();
	
	// create map rotation data
	{
	HashMap<Side, Side> topMap = new HashMap<>();
	topMap.put(Side.FRONT, Side.LEFT);
	topMap.put(Side.LEFT, Side.BACK);
	topMap.put(Side.BACK, Side.RIGHT);
	topMap.put(Side.RIGHT, Side.FRONT);
	topMap.put(Side.TOP, Side.TOP);
	topMap.put(Side.BOTTOM, Side.BOTTOM);
	ROTATION_DATA.put(Side.TOP, topMap);

	HashMap<Side, Side> bottomMap = new HashMap<>();
	bottomMap.put(Side.FRONT, Side.RIGHT);
	bottomMap.put(Side.RIGHT, Side.BACK);
	bottomMap.put(Side.BACK, Side.LEFT);
	bottomMap.put(Side.LEFT, Side.FRONT);
	bottomMap.put(Side.BOTTOM, Side.BOTTOM);
	bottomMap.put(Side.TOP, Side.TOP);
	ROTATION_DATA.put(Side.BOTTOM, bottomMap);

	HashMap<Side, Side> rightMap = new HashMap<>();
	rightMap.put(Side.TOP, Side.BACK);
	rightMap.put(Side.BACK, Side.BOTTOM);
	rightMap.put(Side.BOTTOM, Side.FRONT);
	rightMap.put(Side.FRONT, Side.TOP);
	rightMap.put(Side.RIGHT, Side.RIGHT);
	rightMap.put(Side.LEFT, Side.LEFT);
	ROTATION_DATA.put(Side.RIGHT, rightMap);

	HashMap<Side, Side> leftMap = new HashMap<>();
	leftMap.put(Side.TOP, Side.FRONT);
	leftMap.put(Side.FRONT, Side.BOTTOM);
	leftMap.put(Side.BOTTOM, Side.BACK);
	leftMap.put(Side.BACK, Side.TOP);
	leftMap.put(Side.LEFT, Side.LEFT);
	leftMap.put(Side.RIGHT, Side.RIGHT);
	ROTATION_DATA.put(Side.LEFT, leftMap);

	HashMap<Side, Side> frontMap = new HashMap<>();
	frontMap.put(Side.TOP, Side.RIGHT);
	frontMap.put(Side.RIGHT, Side.BOTTOM);
	frontMap.put(Side.BOTTOM, Side.LEFT);
	frontMap.put(Side.LEFT, Side.TOP);
	frontMap.put(Side.FRONT, Side.FRONT);
	frontMap.put(Side.BACK, Side.BACK);
	ROTATION_DATA.put(Side.FRONT, frontMap);

	HashMap<Side, Side> backMap = new HashMap<>();
	backMap.put(Side.TOP, Side.LEFT);
	backMap.put(Side.LEFT, Side.BOTTOM);
	backMap.put(Side.BOTTOM, Side.RIGHT);
	backMap.put(Side.RIGHT, Side.TOP);
	backMap.put(Side.BACK, Side.BACK);
	backMap.put(Side.FRONT, Side.FRONT);
	ROTATION_DATA.put(Side.BACK, backMap); 
	}
	
	/**
	 * Create a rubiks cube in a solved state.
	 */
	public RubiksCube()
	{
		this.edges = new Edge[12];
		this.corners = new Corner[8];
		for (int i=0; i<EDGE_POSITIONS.length; i++)
		{
			this.edges[i] = new Edge(EDGE_POSITIONS[i]);
		}
		for (int i=0; i<CORNER_POSITIONS.length; i++)
		{
			this.corners[i] = new Corner(CORNER_POSITIONS[i]);
		}
	}
	
	/**
	 * Create a rubiks cube by giving it a file of the positions of faces.
	 * Not yet implemented properly!!!
	 * @param positionFile the file with positions in it
	 * @throws FileNotFoundException
	 */
	public RubiksCube(File positionFile) throws FileNotFoundException
	{
		Scanner in = new Scanner(positionFile);
		this.edges = new Edge[12];
		this.corners = new Corner[8];
		in.close();
		
	}
	
	/**
	 * Create a rubiks cube by giving it a hash map of the positions.
	 * @param positions the hash map of positions
	 * (Precondition: edges has 12 items and corners has 8 items)
	 */
	public RubiksCube(HashMap<Integer, HashMap<String, Side[]>> edges,
			HashMap<Integer, HashMap<String, Side[]>> corners)
	{
		this.edges = new Edge[12];
		this.corners = new Corner[8];
		
		for (int keyInt : edges.keySet())
		{
			HashMap<String, Side[]> edgeRepr = edges.get(keyInt);
			this.edges[keyInt] = new Edge(edgeRepr.get("destination"),
					edgeRepr.get("current"));
		}
		
		for (int keyInt : corners.keySet())
		{
			HashMap<String, Side[]> cornerRepr = corners.get(keyInt);
			this.corners[keyInt] = new Corner(cornerRepr.get("destination"),
					cornerRepr.get("current"));
		}
	}
	
	/**
	 * Rotate a side of the rubiks cube through 90 degrees clockwise.
	 * @param side the side of the cube to rotate
	 */
	public void rotate(Side side)
	{
		for (Edge edge : this.edges)
		{
			if (edge.hasFaceWithCurrentSide(side))
			{
				edge.rotate(ROTATION_DATA.get(side));
			}
		}
		for (Corner corner : this.corners)
		{
			if (corner.hasFaceWithCurrentSide(side))
			{
				corner.rotate(ROTATION_DATA.get(side));
			}
		}
	}
	
	/**
	 * Rotate a side of the rubiks cube through 90 degrees clockwise a given 
	 * amount of times.
	 * @param side the side of the rubiks cube to rotate
	 * @param amount the amount of times to rotate it 90 degrees clockwise
	 */
	public void rotate(Side side, int amount)
	{
		for (int i=0; i<amount; i++)
		{
			this.rotate(side);
		}
	}
	
	/**
	 * Get an array of all the edges in the cube with a given destination side.
	 * @param side the side the edges must have to be included in the array
	 * @return an array of edges
	 */
	public Edge[] getEdgesWithDestinationSide(Side side)
	{
		ArrayList<Edge> withSide = new ArrayList<>();
		for (Edge edge : this.edges)
		{
			if (edge.hasFaceWithDestinationSide(side))
			{
				withSide.add(edge);
			}
		}
		Edge[] toReturn = new Edge[withSide.size()];
		for (int i=0; i<withSide.size(); i++)
		{
			toReturn[i] = withSide.get(i);
		}
		return toReturn;
	}
	
	/**
	 * Get an array of all the corners in the cube with a 
	 * given destination side.
	 * @param side the side the corners must have to be included in the array
	 * @return an array of corners
	 */
	public Corner[] getCornersWithDestinationSide(Side side)
	{
		ArrayList<Corner> withSide = new ArrayList<>();
		for (Corner corner : this.corners)
		{
			if (corner.hasFaceWithDestinationSide(side))
			{
				withSide.add(corner);
			}
		}
		Corner[] toReturn = new Corner[withSide.size()];
		for (int i=0; i<withSide.size(); i++)
		{
			toReturn[i] = withSide.get(i);
		}
		return toReturn;
	}
	
	/**
	 * Get an array of all the edges in the cube without a given 
	 * destination side.
	 * @param side the side the edges must not have to be included in the array
	 * @return an array of edges
	 */
	public Edge[] getEdgesWithoutDestinationSide(Side side)
	{
		ArrayList<Edge> withoutSide = new ArrayList<>();
		for (Edge edge : this.edges)
		{
			if (!edge.hasFaceWithDestinationSide(side))
			{
				withoutSide.add(edge);
			}
		}
		Edge[] toReturn = new Edge[withoutSide.size()];
		for (int i=0; i<withoutSide.size(); i++)
		{
			toReturn[i] = withoutSide.get(i);
		}
		return toReturn;
	}
	
	/**
	 * Get an array of all the corners in the cube without a
	 * given destination side.
	 * @param side the side the corners must not have to be included
	 * in the array
	 * @return an array of corners
	 */
	public Corner[] getCornersWithoutDestinationSide(Side side)
	{
		ArrayList<Corner> withoutSide = new ArrayList<>();
		for (Corner corner : this.corners)
		{
			if (!corner.hasFaceWithDestinationSide(side))
			{
				withoutSide.add(corner);
			}
		}
		Corner[] toReturn = new Corner[withoutSide.size()];
		for (int i=0; i<withoutSide.size(); i++)
		{
			toReturn[i] = withoutSide.get(i);
		}
		return toReturn;
	}
		
	/**
	 * Rotate random sides, with a given amount of rotations.
	 * @param amount the amount of rotations
	 */
	public void randomize(int amount)
	{
		Random generator = new Random();
		Side[] sides = new Side[6];
		sides[0] = Side.TOP;
		sides[1] = Side.BOTTOM;
		sides[2] = Side.RIGHT;
		sides[3] = Side.LEFT;
		sides[4] = Side.FRONT;
		sides[5] = Side.BACK;
		for (int i=0; i<amount; i++)
		{
			Side side = sides[generator.nextInt(5)];
			rotate(side);
			System.out.println(side);
		}
	}
	
	public String toString()
	{
		String state = "{\n";
		for (Edge edge : this.edges)
		{
			state += edge.toString();
			state += ",\n";
		}
		for (Corner corner : this.corners)
		{
			state += corner.toString();
			if (corner != this.corners[this.corners.length-1])
			{
				state += ",\n";
			} 
		}
	state += "\n}";
	return state;
	}
}

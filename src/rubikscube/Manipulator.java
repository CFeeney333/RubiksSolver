package rubikscube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Manipulates a single rubiks cube.
 * @author Cathal
 *
 */
public class Manipulator
{
	private RubiksCube rubiksCube;
	private ArrayList<Move> solution;
	private final HashMap<Side, String> colourMap = new HashMap<>();
	
	public Manipulator()
	{
		this.rubiksCube = new RubiksCube();
		this.solution = new ArrayList<>();
		colourMap.put(Side.FRONT, "red");
		colourMap.put(Side.BACK, "orange");
		colourMap.put(Side.RIGHT, "blue");
		colourMap.put(Side.LEFT, "green");
		colourMap.put(Side.TOP, "white");
		colourMap.put(Side.BOTTOM, "yellow");
	}
	
	/**
	 * A move represents a side with a certain amount of clockwise rotation.
	 * 0 -> no rotation
	 * 1 -> 90 degrees clockwise
	 * 2 -> 180 degrees
	 * 3 -> 90 degrees anti-clockwise
	 * Amount will always be one of these numbers as whenever a value is added 
	 * to it, the total is divided by four, and the remainder is the new amount.
	 * @author Cathal
	 *
	 */
	private class Move
	{
		private int amount;
		private Side side;
		
		private Move(Side side, int amount)
		{
			this.side = side;
			this.amount = 0;
			this.addMove(amount);
		}
		
		private Side getSide()
		{
			return this.side;
		}
		
		private void addMove(int amount)
		{
			this.amount += amount;
			this.amount = this.amount % 4;
		}
		
		private int getAmount()
		{
			return this.amount;
		}
	}
	
	/**
	 * Get the side on the right relative to a given side.
	 * (Precondition: side != (TOP || BOTTOM)
	 * @param side the side on the 'front'
	 * @return the side to the right of the given side
	 */
	public Side getSideOnRight(Side side)
	{
		Side right;
		switch (side)
		{
		case BACK:
			right = Side.LEFT;
			break;
		case BOTTOM:
			right = null;
			break;
		case FRONT:
			right = Side.RIGHT;
			break;
		case LEFT:
			right = Side.FRONT;
			break;
		case RIGHT:
			right = Side.BACK;
			break;
		case TOP:
			right = null;
			break;
		default:
			right = null;
			break;
		}
		return right;
	}
	
	/**
	 * Get the side on the left relative to a given side.
	 * (Precondition: side != (TOP || BOTTOM)
	 * @param side the side on the 'front'
	 * @return the side to the left of the given side
	 */
	public Side getSideOnLeft(Side side)
	{
		Side left;
		switch (side)
		{
		case BACK:
			left = Side.RIGHT;
			break;
		case BOTTOM:
			left = null;
			break;
		case FRONT:
			left = Side.LEFT;
			break;
		case LEFT:
			left = Side.BACK;
			break;
		case RIGHT:
			left = Side.FRONT;
			break;
		case TOP:
			left = null;
			break;
		default:
			left = null;
			break;
		}
		return left;
	}
	
	/**
	 * Get the side opposite a given side.
	 * @param side the side whose opposite will be returned
	 * @return the opposite side of the given side
	 */
	public Side getOppositeSide(Side side)
	{
		Side opposite;
		switch (side)
		{
		case BACK:
			opposite = Side.FRONT;
			break;
		case BOTTOM:
			opposite = Side.TOP;
			break;
		case FRONT:
			opposite = Side.BACK;
			break;
		case LEFT:
			opposite = Side.RIGHT;
			break;
		case RIGHT:
			opposite = Side.LEFT;
			break;
		case TOP:
			opposite = Side.BOTTOM;
			break;
		default:
			opposite = null;
			break;
		}
		return opposite;
	}
	
	/**
	 * Parse the rubiks cube xml file
	 * @param file the file object to parse
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws XPathExpressionException 
	 * @throws NumberFormatException 
	 */
	private HashMap<String, HashMap<Integer, HashMap<String, Side[]>>> parseXML(Document doc)
			throws ParserConfigurationException, SAXException, IOException, NumberFormatException, XPathExpressionException
	{
		HashMap<String, HashMap<Integer, HashMap<String, Side[]>>> toReturn = 
				new HashMap<String, HashMap<Integer, HashMap<String, Side[]>>>();
		
		HashMap<String, Side> sideMap = new HashMap<>();
		sideMap.put("t", Side.TOP);
		sideMap.put("o", Side.BOTTOM);
		sideMap.put("r", Side.RIGHT);
		sideMap.put("l", Side.LEFT);
		sideMap.put("a", Side.BACK);
		sideMap.put("f", Side.FRONT);
		
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//		Document doc = builder.parse(file);
//		
		XPathFactory xpFactory = XPathFactory.newInstance();
		XPath path = xpFactory.newXPath();
		
		HashMap<Integer, HashMap<String, Side[]>> edges = new HashMap<>();
		HashMap<Integer, HashMap<String, Side[]>> corners = new HashMap<>();
		
		int edgeIndex = 0;
		int cornerIndex = 0;
		
		for (int i=0; i<Integer.parseInt(path.evaluate("count(/rubikscube/*)", doc)); i++)
		{
			int pathIndex = i + 1;
			String piecePath = "rubikscube/piece["+pathIndex+"]";
			
			if (path.evaluate(piecePath+"/type", doc).equals("edge"))
			{
				HashMap<String, Side[]> newEdge = new HashMap<>();
				
				Side[] destination = new Side[2];
				Side[] current = new Side[2];
				String destinationString = path.evaluate(piecePath+"/destination", doc);
				String currentString = path.evaluate(piecePath+"/current", doc);
				
				for (int index=0; index<2; index++)
				{
					destination[index] = 
							sideMap.get(destinationString.substring(index, index+1));
					current[index] = 
							sideMap.get(currentString.substring(index, index+1));
				}
				newEdge.put("destination", destination);
				newEdge.put("current", current);
				edges.put(edgeIndex, newEdge);
				edgeIndex ++;
			}
			
			if (path.evaluate(piecePath+"/type", doc).equals("corner"))
			{
				HashMap<String, Side[]> newCorner = new HashMap<>();
				
				Side[] destination = new Side[3];
				Side[] current = new Side[3];
				String destinationString = path.evaluate(piecePath+"/destination", doc);
				String currentString = path.evaluate(piecePath+"/current", doc);

				for (int index=0; index<3; index++)
				{
					destination[index] = 
							sideMap.get(destinationString.substring(index, index+1));
					current[index] = 
							sideMap.get(currentString.substring(index, index+1));
				}
				newCorner.put("destination", destination);
				newCorner.put("current", current);
				corners.put(cornerIndex, newCorner);
				cornerIndex ++;
			}
		}
		toReturn.put("edges", edges);
		toReturn.put("corners", corners);
		return toReturn;
		
	}
	
	/**
	 * Solve the rubiks cube and generate instructions which can be accessed by
	 * the method getInstruction string.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws XPathExpressionException 
	 * @throws NumberFormatException 
	 */
	public void solve(Document doc) throws NumberFormatException, XPathExpressionException, ParserConfigurationException, SAXException, IOException
	{
//		File f = new File(docfileName);
		
		HashMap<String, HashMap<Integer, HashMap<String, Side[]>>> parsed = this.parseXML(doc);
		// extract the edge and corner data
		HashMap<Integer, HashMap<String, Side[]>> edges = parsed.get("edges");
		HashMap<Integer, HashMap<String, Side[]>> corners = parsed.get("corners");
		
		this.rubiksCube = new RubiksCube(edges, corners);
		
		this.solution.clear();
		stageOne();
		stageTwo();
		stageThree();
		stageFour();
		this.stageFive();
		this.stageSix();
		this.stageSeven();
		System.out.println(this.rubiksCube);
	}
	
	/**
	 * Get the solution from the solved rubiks cube.
	 * @return a string representation of the solution
	 */
	public String getSolutionString()
	{
		String solutionString = "";
		for (Move move : this.solution)
		{
			int amount = move.getAmount();
			if (amount != 0)
			{
				String colour = this.colourMap.get(move.getSide());
				if (amount == 1) 
				{
					solutionString += "Rotate " + colour + " 90 degrees clockwise\n";
				}
				else if (amount == 2)
				{
					solutionString += "Rotate " + colour + " 180 degrees\n";
				}
				else // has to be 3
				{
					solutionString += "Rotate " + colour + " 90 degrees anti-clockwise\n";
				}
			}
		}
		return solutionString;
	}
	
	/**
	 * Calls rotate on the rubiks cube. It also adds an instruction to the 
	 * instruction string.
	 * @param side the side to rotate
	 * @param amount the amount of times to rotate it 90 degrees clockwise
	 */
	private void rotate(Side side, int amount)
	{
		this.rubiksCube.rotate(side, amount);
		if (this.solution.size() >= 1)
		{
			Move lastMove = this.solution.get(this.solution.size() - 1);
			if (lastMove.getSide() == side)
			{
				lastMove.addMove(amount);
				return;
			}
		}
		this.solution.add(new Move(side, amount));
	}
	
	/**
	 * Give the object a new rubiks cube.
	 * Set the instruction string to empty again.
	 * @param rc the new rubiks cube
	 */
	public void changeRubiksCube(RubiksCube rc)
	{
		this.rubiksCube = rc;
		this.solution.clear();
	}
	
	// solver algorithms:
	/** 
	 * Sort out the four edges on top into proper places.
	 */
	private void stageOne()
	{
		Edge[] edges = this.rubiksCube.getEdgesWithDestinationSide(Side.TOP);
		for (Edge edge : edges)
		{
			if (edge.isAtCorrectPosition()) continue;
			
			Face topFace = null;
			Face otherFace = null;
			for (Face face : edge.getFaces())
			{
				if (face.getDestinationSide() == Side.TOP) topFace = face;
				else otherFace = face;
			}
			
			if (topFace.isAtCorrectSide() && !otherFace.isAtCorrectSide())
				stageOneCaseOne(topFace, otherFace);
			else if (!edge.hasFaceWithCurrentSide(Side.TOP) &&
					!edge.hasFaceWithCurrentSide(Side.BOTTOM))
			{
				Side sideOfTop = topFace.getCurrentSide();
				if (this.getSideOnLeft(sideOfTop) == otherFace.getCurrentSide())
					stageOneCaseTwo(topFace, otherFace);
				else if (this.getSideOnRight(sideOfTop) ==
						otherFace.getCurrentSide())
					stageOneCaseThree(topFace, otherFace);
			}
			else if (otherFace.getCurrentSide() == Side.BOTTOM)
			{
				stageOneCaseFour(topFace, otherFace);
			}
			else if (topFace.getCurrentSide() == Side.BOTTOM)
			{
				stageOneFinalCase(topFace, otherFace);
			}
			else if (otherFace.getCurrentSide() == Side.TOP)
			{
				stageOneCaseFive(topFace, otherFace);
			}
		}
	}
	
	private void stageOneCaseOne(Face topFace, Face otherFace)
	{
		rotate(getSideOnRight(otherFace.getCurrentSide()), 1);
		rotate(getSideOnLeft(otherFace.getCurrentSide()), 3);
		rotate(otherFace.getCurrentSide(), 2);
		rotate(getSideOnRight(otherFace.getCurrentSide()), 3);
		rotate(getSideOnLeft(otherFace.getCurrentSide()), 1);
		stageOneFinalCase(topFace, otherFace);
	}
	
	private void stageOneCaseTwo(Face topFace, Face otherFace)
	{
		Side side = otherFace.getCurrentSide();
		rotate(side, 1);
		rotate(Side.BOTTOM, 1);
		rotate(side, 3);
		stageOneFinalCase(topFace, otherFace);
	}
	
	private void stageOneCaseThree(Face topFace, Face otherFace)
	{
		Side side = otherFace.getCurrentSide();
		rotate(side, 3);
		rotate(Side.BOTTOM, 1);
		rotate(side, 1);
		stageOneFinalCase(topFace, otherFace);
	}
	
	private void stageOneCaseFour(Face topFace, Face otherFace)
	{
		Side side1 = topFace.getCurrentSide();
		rotate(side1, 1);
		Side side2 = otherFace.getCurrentSide();
		rotate(side2, 1);
		rotate(Side.BOTTOM, 3);
		rotate(side2, 3);
		rotate(side1, 3);
		stageOneFinalCase(topFace, otherFace);
	}
	
	private void stageOneCaseFive(Face topFace, Face otherFace)
	{
		Side right = getSideOnRight(topFace.getCurrentSide());
		Side left = getSideOnLeft(topFace.getCurrentSide());
		rotate(right, 1);
		rotate(left, 3);
		rotate(topFace.getCurrentSide(), 1);
		rotate(right, 3);
		rotate(left, 1);
		stageOneFinalCase(topFace, otherFace);
	}
	
	private void stageOneFinalCase(Face topFace, Face otherFace)
	{
		assert topFace.getCurrentSide() == Side.BOTTOM;
		while (otherFace.getCurrentSide() != otherFace.getDestinationSide())
		{
			rotate(Side.BOTTOM, 1);
		}
		rotate(otherFace.getCurrentSide(), 2);
	}
	
	
	private void stageTwo()
	{
		for (Corner corner : rubiksCube.getCornersWithDestinationSide(Side.TOP))
		{
			if (corner.isAtCorrectPosition()) continue;
			
			Face topFace = null;
			Face otherFace1 = null;
			Face otherFace2 = null;
			for (Face face : corner.getFaces())
			{
				if (face.getDestinationSide() == Side.TOP) topFace = face;
				else
				{
					if (otherFace1 == null)
					{
						otherFace1 = face;
					} 
					else 
					{
						otherFace2 = face;
					}
				}
			}
			
			if (topFace.isAtCorrectSide() && !(otherFace1.isAtCorrectSide()))
				stageTwoCaseOne(topFace, otherFace1, otherFace2);
			
			else if ((otherFace1.getCurrentSide() == Side.TOP) ||
					otherFace2.getCurrentSide() == Side.TOP)
				stageTwoCaseTwo(topFace, otherFace1, otherFace2);
			
			else if (topFace.getCurrentSide() == Side.BOTTOM)
				stageTwoCaseThree(topFace, otherFace1, otherFace2);
			
			else if ((otherFace1.getCurrentSide() == Side.BOTTOM) ||
					(otherFace2.getCurrentSide() == Side.BOTTOM))
			{
				Side front = topFace.getCurrentSide();
				if ((getSideOnRight(front) == otherFace1.getCurrentSide()) ||
						(getSideOnRight(front) == otherFace2.getCurrentSide()))
					stageTwoFinalCaseRight(topFace, otherFace1, otherFace2);
				else if ((getSideOnLeft(front) == otherFace1.getCurrentSide()) ||
						(getSideOnLeft(front) == otherFace2.getCurrentSide()))
					stageTwoFinalCaseLeft(topFace, otherFace1, otherFace2);
			}
		}
	}
	
	private void stageTwoCaseOne(Face topFace, Face otherFace1, Face otherFace2)
	{
		Side front = otherFace1.getCurrentSide();
		Side right = null;
		if (getSideOnRight(front) == otherFace2.getCurrentSide())
		{
			right = otherFace2.getCurrentSide();
		}
		else
		{
			front = otherFace2.getCurrentSide();
			right = otherFace1.getCurrentSide();
		}
		rotate(right, 3);
		rotate(Side.BOTTOM, 3);
		rotate(right, 1);
		stageTwoFinalCaseRight(topFace, otherFace1, otherFace2);
	}
	
	private void stageTwoCaseTwo(Face topFace, Face otherFace1, Face otherFace2)
	{
		Side front = topFace.getCurrentSide();
		if ((getSideOnRight(front) == otherFace1.getCurrentSide()) ||
				(getSideOnRight(front) == otherFace2.getCurrentSide()))
		{
			rotate(front, 1);
			rotate(Side.BOTTOM, 1);
			rotate(front, 3);
			stageTwoFinalCaseRight(topFace, otherFace1, otherFace2);
		}
		else if ((getSideOnLeft(front) == otherFace1.getCurrentSide()) ||
				(getSideOnLeft(front) == otherFace2.getCurrentSide()))
		{
			rotate(front, 3);
			rotate(Side.BOTTOM, 3);
			rotate(front, 1);
			stageTwoFinalCaseLeft(topFace, otherFace1, otherFace2);
		}
	}
	
	private void stageTwoCaseThree(Face topFace, Face otherFace1, 
			Face otherFace2)
	{
		Side side2 = otherFace2.getDestinationSide();
		while (otherFace1.getCurrentSide() != side2)
		{
			rotate(Side.BOTTOM, 1);
		}
		
		Side front = otherFace1.getCurrentSide();
		Side left = null;
		if (getSideOnLeft(front) == otherFace2.getCurrentSide())
		{
			left = otherFace2.getCurrentSide();
		}
		else
		{
			front = otherFace2.getCurrentSide();
			left = otherFace1.getCurrentSide();
		}
		
		rotate(left, 1);
		rotate(Side.BOTTOM, 2);
		rotate(left, 3);
		stageTwoFinalCaseRight(topFace, otherFace1, otherFace2);
	}
	
	private void stageTwoFinalCaseRight(Face topFace, Face otherFace1, 
			Face otherFace2)
	{
		Face bottomFace = null;
		if (otherFace1.getCurrentSide() == Side.BOTTOM) bottomFace = otherFace1;
		else bottomFace = otherFace2;
		while (bottomFace.getDestinationSide() != getSideOnRight(topFace.getCurrentSide()))
		{
			rotate(Side.BOTTOM, 1);
		}
		Side front = topFace.getCurrentSide();
		Side back = getOppositeSide(front);
		rotate(back, 3);
		rotate(Side.BOTTOM, 1);
		rotate(back, 1);
	}
	
	private void stageTwoFinalCaseLeft(Face topFace, Face otherFace1,
			Face otherFace2)
	{
		Face bottomFace = null;
		if (otherFace1.getCurrentSide() == Side.BOTTOM) bottomFace = otherFace1;
		else bottomFace = otherFace2;
		while (bottomFace.getDestinationSide() != getSideOnLeft(topFace.getCurrentSide()))
		{
			rotate(Side.BOTTOM, 1);
		}
		Side front = topFace.getCurrentSide();
		Side back = getOppositeSide(front);
		rotate(back, 1);
		rotate(Side.BOTTOM, 3);
		rotate(back, 3);
	}
	
	
	/**
	 * Sort out the four edges between the top and the bottom.
	 */
	private void stageThree()
	{
		ArrayList<Edge> edges = new ArrayList<>();
		for (Edge edge : rubiksCube.getEdgesWithoutDestinationSide(Side.TOP))
		{
			if (!edge.hasFaceWithDestinationSide(Side.BOTTOM))
				edges.add(edge);
		}
		Edge[] edgesToUse = new Edge[4];
		for (int i=0; i<edges.size(); i++)
		{
			edgesToUse[i] = edges.get(i);
		}
		for (Edge edge : edgesToUse)
		{
			if (edge.isAtCorrectPosition()) continue;
			
			Face face1 = edge.getFaces()[0];
			Face face2 = edge.getFaces()[1];
			
			if (!(face1.getCurrentSide() == Side.BOTTOM) && 
					!(face2.getCurrentSide() == Side.BOTTOM))
			{
				stageThreeCaseOne(face1, face2);
			}
			else if (face1.getCurrentSide() == Side.BOTTOM ||
					face2.getCurrentSide() == Side.BOTTOM)
			{
				stageThreeFinalCase(face1, face2);
			}
		}
	}
	
	private void stageThreeCaseOne(Face face1, Face face2)
	{
		Side front = face1.getCurrentSide();
		Side right = null;
		if (face2.getCurrentSide() == getSideOnRight(front))
		{
			right = face2.getCurrentSide();
		}
		else 
		{
			front = face2.getCurrentSide();
			right = face1.getCurrentSide();
		}
		rotate(right, 3);
		rotate(Side.BOTTOM, 1);
		rotate(right, 1);
		rotate(Side.BOTTOM, 1);
		rotate(front, 1);
		rotate(Side.BOTTOM, 3);
		rotate(front, 3);
		stageThreeFinalCase(face1, face2);
	}
	
	private void stageThreeFinalCase(Face face1, Face face2)
	{
		Face bottomFace = null;
		Face otherFace = null;
		if (face1.getCurrentSide() == Side.BOTTOM)
		{
			bottomFace = face1;
			otherFace = face2;
		}
		else 
		{
			bottomFace = face2;
			otherFace = face1;
		}
		while (otherFace.getCurrentSide() !=
				getOppositeSide(bottomFace.getDestinationSide()))
		{
			rotate(Side.BOTTOM, 1);
		}
		Side front = otherFace.getDestinationSide();
		if (otherFace.getCurrentSide() == getSideOnRight(front))
		{
			stageThreeFinalCaseRight(bottomFace, otherFace);
		}
		else if (otherFace.getCurrentSide() == getSideOnLeft(front))
		{
			stageThreeFinalCaseLeft(bottomFace, otherFace);
		}
	}
	
	private void stageThreeFinalCaseRight(Face bottomFace, Face otherFace)
	{
		rotate(bottomFace.getDestinationSide(), 1);
		rotate(Side.BOTTOM, 3);
		rotate(bottomFace.getDestinationSide(), 3);
		rotate(Side.BOTTOM, 3);
		rotate(otherFace.getDestinationSide(), 3);
		rotate(Side.BOTTOM, 1);
		rotate(otherFace.getDestinationSide(), 1);
	}
	
	private void stageThreeFinalCaseLeft(Face bottomFace, Face otherFace)
	{
		rotate(bottomFace.getDestinationSide(), 3);
		rotate(Side.BOTTOM, 1);
		rotate(bottomFace.getDestinationSide(), 1);
		rotate(Side.BOTTOM, 1);
		rotate(otherFace.getDestinationSide(), 1);
		rotate(Side.BOTTOM, 3);
		rotate(otherFace.getDestinationSide(), 3);
	}
	
	
	private Edge[] stageFourGetEdgesAtDest(Edge[] edges)
	{
		ArrayList<Edge> edgesAtDest = new ArrayList<>();
		for (Edge edge : edges)
		{
			if (edge.getFaceWithDestinationSide(Side.BOTTOM).isAtCorrectSide())
			{
				edgesAtDest.add(edge);
			}
		}
		Edge[] toReturn = new Edge[edgesAtDest.size()];
		for (int i = 0; i < edgesAtDest.size(); i++)
		{
			toReturn[i] = edgesAtDest.get(i);
		}
		return toReturn;
	}
	
	private void stageFour()
	{
		Edge[] edges = rubiksCube.getEdgesWithDestinationSide(Side.BOTTOM);
		Edge[] atDest = stageFourGetEdgesAtDest(edges);
		
		if (atDest.length == 0)
		{
			stageFourSequenceOne();
			atDest = stageFourGetEdgesAtDest(edges);
		}
		if ((atDest.length == 2) &&
				(atDest[0].getOtherFace(Side.BOTTOM).getCurrentSide() ==
				getOppositeSide(atDest[1].getOtherFace(Side.BOTTOM).getCurrentSide())))
		{
			stageFourSequenceOne();
			atDest = stageFourGetEdgesAtDest(edges);
		}
		if ((atDest.length == 2) &&
			((	atDest[0].getOtherFace(Side.BOTTOM).getCurrentSide() ==
				getSideOnRight(atDest[1].getOtherFace(Side.BOTTOM).getCurrentSide())) ||
			 ( atDest[1].getOtherFace(Side.BOTTOM).getCurrentSide() ==
				getSideOnRight(atDest[0].getOtherFace(Side.BOTTOM).getCurrentSide())))	
			)
		{
			stageFourSequenceTwo(atDest[0], atDest[1]);
		}
	}
	
	private void stageFourSequenceOne()
	{
		rotate(Side.FRONT, 1);
		rotate(Side.LEFT, 1);
		rotate(Side.BOTTOM, 3);
		rotate(Side.LEFT, 3);
		rotate(Side.FRONT, 3);
		rotate(Side.RIGHT, 3);
		rotate(Side.BOTTOM, 1);
		rotate(Side.RIGHT, 1);
	}
	
	private void stageFourSequenceTwo(Edge edge1, Edge edge2)
	{
		Face otherFace1 = edge1.getOtherFace(Side.BOTTOM);
		
		Face otherFace2 = edge2.getOtherFace(Side.BOTTOM);
		
		Face backFace = otherFace1;
		
		if (getSideOnRight(otherFace2.getCurrentSide()) == backFace.getCurrentSide())
		{
			backFace = otherFace2;
		}
		
		while (backFace.getCurrentSide() != Side.BACK)
		{
			rotate(Side.BOTTOM, 1);
		}
		
		stageFourSequenceOne();
	}
	
	/**
	 * Make the four bottom corners have the faces with destination bottom at 
	 * the bottom.
	 */
	private void stageFive()
	{
		int amountWithBottomAtDest = 0;
		for (Corner corner : this.rubiksCube.getCornersWithDestinationSide(Side.BOTTOM))
		{
			if (corner.getFaceWithDestinationSide(Side.BOTTOM).isAtCorrectSide())
			{
				amountWithBottomAtDest ++;
			}
		}
		
		if (amountWithBottomAtDest == 4)
		{
			return; // complete
		}
		
		if (amountWithBottomAtDest == 2)
		{
			// there are three possibilities
			ArrayList<Corner> notAtHome = new ArrayList<>();
			for (Corner corner : this.rubiksCube.getCornersWithDestinationSide(Side.BOTTOM))
			{
				if (!corner.getFaceWithDestinationSide(Side.BOTTOM).isAtCorrectSide())
				{
					notAtHome.add(corner);
				}
			}
			Corner notAtHome1 = notAtHome.get(0);
			Side notAtHome1Current = notAtHome1.getFaceWithDestinationSide(Side.BOTTOM).getCurrentSide();
			Corner notAtHome2 = notAtHome.get(1);
			Side notAtHome2Current = notAtHome2.getFaceWithDestinationSide(Side.BOTTOM).getCurrentSide();
			
			if (notAtHome1Current == notAtHome2Current) // the sides are the same
			{
				// case 2
				Side front = notAtHome1Current;
				this.stageFiveSequenceTwo(this.getSideOnRight(front), Side.BOTTOM);
				this.rotate(Side.BOTTOM, 3);
				this.stageFiveSequenceOne(this.getSideOnRight(front), Side.BOTTOM);
			}
			
			else if (this.getOppositeSide(notAtHome1Current) == notAtHome2Current) // the sides are opposite
			{
				// case 5
				Side front = null;
				if (notAtHome1.hasFaceWithCurrentSide(this.getSideOnLeft(notAtHome1Current)))
				{
					front = notAtHome1Current;
				}
				else 
				{
					front = notAtHome2Current;
				}
				this.stageFiveSequenceTwo(this.getSideOnRight(front), Side.BOTTOM);
				this.rotate(Side.BOTTOM, 1);
				this.stageFiveSequenceOne(this.getSideOnRight(front), Side.BOTTOM);
			}
			
			else // the sides are adjacent
			{
				// case 1
				Side front = null;
				if (this.getSideOnLeft(notAtHome1Current) == notAtHome2Current)
				{
					front = notAtHome2Current;
				}
				else
				{
					front = notAtHome1Current;
				}
				this.stageFiveSequenceTwo(this.getSideOnRight(front), Side.BOTTOM);
				this.rotate(Side.BOTTOM, 2);
				this.stageFiveSequenceOne(this.getSideOnRight(front), Side.BOTTOM);
			}
		}
		
		if (amountWithBottomAtDest == 1)
		{
			// there are two possibilities
			// get the 3 corners
			ArrayList<Corner> corners = new ArrayList<>();
			for (Corner corner : this.rubiksCube.getCornersWithDestinationSide(Side.BOTTOM))
			{
				if (!corner.getFaceWithDestinationSide(Side.BOTTOM).isAtCorrectSide())
				{
					corners.add(corner);
				}
			}
			Corner firstCorner = corners.get(0);
			Side first = firstCorner.getFaceWithDestinationSide(Side.BOTTOM).getCurrentSide();
			Corner secondCorner = corners.get(1);
			Side second = secondCorner.getFaceWithDestinationSide(Side.BOTTOM).getCurrentSide();
			Corner thirdCorner = corners.get(2);
			Side third = thirdCorner.getFaceWithDestinationSide(Side.BOTTOM).getCurrentSide();
			Side front = null;
			
			if (!(this.getOppositeSide(first) == second || this.getOppositeSide(first) == third))
			{
				front = first;
			}
			else if (!(this.getOppositeSide(second) == first || this.getOppositeSide(second) == third))
			{
				front = second;
			}
			else 
			{
				front = third;
			}
			
			Corner frontPiece = null;
			for (Corner corner : corners)
			{
				if (corner.getFaceWithDestinationSide(Side.BOTTOM).getCurrentSide() == front)
				{
					frontPiece = corner;
				}
			}
			
			if (frontPiece.hasFaceWithCurrentSide(this.getSideOnLeft(front)))
			{
				this.stageFiveSequenceOne(this.getSideOnRight(front), Side.BOTTOM);
			}
			else 
			{
				this.stageFiveSequenceThree(this.getSideOnLeft(front), Side.BOTTOM);
			}
		}
		
		if (amountWithBottomAtDest == 0)
		{
			// there are two possibilities
			Set<Side> sidesWithBottomFaces = new HashSet<>();
			for (Corner corner : this.rubiksCube.getCornersWithDestinationSide(Side.BOTTOM))
			{
				sidesWithBottomFaces.add(corner.getFaceWithDestinationSide(Side.BOTTOM).getCurrentSide());
			}
			
			if (sidesWithBottomFaces.size() == 2)
			{
				// case 4
				Side front = (Side) sidesWithBottomFaces.toArray()[0];
				this.stageFiveSequenceOne(this.getSideOnRight(front), Side.BOTTOM);
				this.stageFiveSequenceOne(this.getSideOnRight(front), Side.BOTTOM);
			}
			else   // otherwise three sides are occupied
			{
				// case 6
				ArrayList<Corner> corners = new ArrayList<>();
				for (Corner corner : this.rubiksCube.getCornersWithDestinationSide(Side.BOTTOM))
				{
					corners.add(corner);
				}
				
				ArrayList<Side> usedSides = new ArrayList<>();
				for (Corner corner : corners)
				{
					usedSides.add(corner.getFaceWithDestinationSide(Side.BOTTOM).getCurrentSide());
				}
				
				Side hasTwo = null;
				Side sideOne = usedSides.get(0);
				Side sideTwo = usedSides.get(1);
				Side sideThree = usedSides.get(2);
				Side sideFour = usedSides.get(3);
				if (sideOne == sideTwo || sideOne == sideThree ||sideOne == sideFour)
				{
					hasTwo = sideOne;
				}
				else if (sideTwo == sideThree || sideTwo == sideFour)
				{
					hasTwo = sideTwo;
				}
				else if (sideThree == sideFour)
				{
					hasTwo = sideThree;
				}
				Side front = this.getSideOnLeft(hasTwo);
				
				this.stageFiveSequenceTwo(this.getSideOnRight(front), Side.BOTTOM);
				this.rotate(Side.BOTTOM, 2);
				this.stageFiveSequenceTwo(this.getSideOnRight(front), Side.BOTTOM);
				this.rotate(Side.BOTTOM, 3);
				this.stageFiveSequenceOne(this.getSideOnRight(front), Side.BOTTOM);
			}
		}
	}
	
	private void stageFiveSequenceOne(Side right, Side bottom)
	{
		this.rotate(right, 3);
		this.rotate(bottom, 2);
		this.rotate(right, 1);
		this.rotate(bottom, 1);
		this.rotate(right, 3);
		this.rotate(bottom, 1);
		this.rotate(right, 1);
	}
	
	private void stageFiveSequenceTwo(Side right, Side bottom)
	{
		this.rotate(right, 3);
		this.rotate(bottom, 3);
		this.rotate(right, 1);
		this.rotate(bottom, 3);
		this.rotate(right, 3);
		this.rotate(bottom, 2);
		this.rotate(right, 1);
	}
	
	private void stageFiveSequenceThree(Side left, Side bottom)
	{
		this.rotate(left, 1);
		this.rotate(bottom, 2);
		this.rotate(left, 3);
		this.rotate(bottom, 3);
		this.rotate(left, 1);
		this.rotate(bottom, 3);
		this.rotate(left, 3);
	}
	
	/**
	 * Put the corners with destination bottom in their correct places
	 */
	private void stageSix()
	{
		ArrayList<Corner> corners = new ArrayList<>();
		for (Corner corner : this.rubiksCube.getCornersWithDestinationSide(Side.BOTTOM))
		{
			corners.add(corner);
		}
		
		// rotate until at least TWO corners at their destinations
		int amountAtDestination = 0;
		do
		{
			this.rotate(Side.BOTTOM, 1);
			amountAtDestination = 0;
			for (Corner corner : corners)
			{
				if (corner.isAtCorrectPosition())
				{
					amountAtDestination ++;
				}
			}
		} while(amountAtDestination < 2);
		
		// there will always be exactly 4 or two if at least one is (sampleCorner is).
		if (amountAtDestination == 4)
		{
			return; // we are done this stage
		}
		else // there has to be 2
		{
			// get the two corners
			ArrayList<Corner> atHome = new ArrayList<>();
			for (Corner c : corners)
			{
				if (c.isAtCorrectPosition())
				{
					atHome.add(c);
				}
			}
			
			// create a set of all the sides occupied (except the bottom)
			// if two corners share a common side, we won't get a repeat and the
			// length will be 3 not 4
			Set<Side> sidesOccupied = new HashSet<>();
			for (Corner corner : atHome)
			{
				for (Face face : corner.getFaces())
				{
					if (face.getCurrentSide() != Side.BOTTOM)
					{
						sidesOccupied.add(face.getCurrentSide());
					}
				}
			}
			// if the two corners don't share a common side (EXCEPT BOTTOM)
			// i.e. if the length of sidesOccupied is exactly 4
			if (sidesOccupied.toArray().length == 4)
			{
				this.stageSixSequenceOne(Side.LEFT, Side.RIGHT, Side.BOTTOM);
			}
			
			// if the two corners do share a common side (EXCEPT BOTTOM)
			// i.e. if the length of sidesOccupied is exactly 3
			else // it has to be 3
			{
				// get the front
				Side[] fourSides = {Side.FRONT, Side.BACK, Side.LEFT, Side.RIGHT};
				Side left = null;
				for (Side side : fourSides)
				{
					if (!sidesOccupied.contains(side))
					{
						left = this.getOppositeSide(side);
					}
				}
				
				this.stageSixSequenceTwo(left,
						this.getOppositeSide(left), Side.BOTTOM);
			}
		}	
	}
	
	private void stageSixSequenceOne(Side left, Side right, Side bottom)
	{
		this.rotate(left, 1);
		this.rotate(bottom, 1);
		this.rotate(left, 3);
		this.rotate(bottom, 3);
		this.rotate(right, 3);
		this.rotate(bottom, 3);
		this.rotate(right, 1);
		this.rotate(bottom, 3);
		this.rotate(right, 3);
		this.rotate(left, 1);
		this.rotate(bottom, 2);
		this.rotate(right, 1);
		this.rotate(left, 3);
		this.rotate(bottom, 3);
		this.rotate(left, 1);
		this.rotate(bottom, 3);
		this.rotate(left, 3);
		this.rotate(bottom, 3);
		this.rotate(right, 3);
		this.rotate(bottom, 1);
		this.rotate(right, 1);
		this.rotate(bottom, 1);
		
		this.stageFiveSequenceOne(right, bottom);
		this.stageFiveSequenceOne(right, bottom);
	}
	
	private void stageSixSequenceTwo(Side left, Side right, Side bottom)
	{
		this.rotate(left, 1);
		this.rotate(bottom, 1);
		this.rotate(left, 3);
		this.rotate(bottom, 3);
		this.rotate(right, 3);
		this.rotate(bottom, 3);
		this.rotate(right, 1);
		this.rotate(bottom, 3);
		this.rotate(right, 3);
		this.rotate(left, 1);
		this.rotate(bottom, 3);
		this.rotate(right, 1);
		this.rotate(left, 3);
		this.rotate(bottom, 3);
		this.rotate(left, 1);
		this.rotate(bottom, 3);
		this.rotate(left, 3);
		this.rotate(bottom, 3);
		this.rotate(right, 3);
		this.rotate(bottom, 1);
		this.rotate(right, 1);
		this.rotate(bottom, 1);
		
		this.stageFiveSequenceTwo(right, bottom);
		this.rotate(bottom, 3);
		this.stageFiveSequenceOne(right, bottom);
	}
	
	private void stageSeven()
	{
		Corner randomCorner = this.rubiksCube.getCornersWithDestinationSide(Side.BOTTOM)[0];
		while (!randomCorner.isAtCorrectPosition())
		{
			this.rotate(Side.BOTTOM, 1);
		}
		
		Edge[] bottomEdges = this.rubiksCube.getEdgesWithDestinationSide(Side.BOTTOM);
		int atDestinationCount = 0;
		
		for (Edge edge : bottomEdges)
		{
			if (edge.isAtCorrectPosition())
			{
				atDestinationCount ++;
			}
		}
		
		// first possibility
		if (atDestinationCount == 0)
		{
			Edge randomEdge = bottomEdges[0];
			Face sideFace = null;
			for (Face face : randomEdge.getFaces())
			{
				if (face.getDestinationSide() != Side.BOTTOM)
				{
					sideFace = face;
				}
			}
			
			// (any) face is opposite its destination side (corners in position)
			if (sideFace.getDestinationSide() == this.getOppositeSide(sideFace.getCurrentSide()))
			{
				this.stageSevenSequenceOne(Side.LEFT, Side.RIGHT, Side.TOP, Side.BOTTOM);
			}
			else // (any) face is not opposite its destination side (its beside it)
			{
				Side front = null;
				if (sideFace.getDestinationSide() == this.getSideOnRight(sideFace.getCurrentSide()))
				{
					front = sideFace.getCurrentSide();
				}
				else
				{
					front = this.getSideOnRight(sideFace.getCurrentSide());
				}
				this.stageSevenSequenceTwo(this.getSideOnLeft(front), this.getSideOnRight(front), front, this.getOppositeSide(front));
			}
		}
		
		else if (atDestinationCount == 1)
		{
			Edge atDestination = null;
			Face onFront = null;
			Side front = null;
			
			for (Edge e : bottomEdges)
			{
				if (e.isAtCorrectPosition())
				{
					atDestination = e;
				}
			}
			
			onFront = atDestination.getOtherFace(Side.BOTTOM);
			front = onFront.getCurrentSide();
			
			Edge leftEdge = null;
			Face onLeft = null;
			Side left = this.getSideOnLeft(front);
			for (Edge e : bottomEdges)
			{
				if (e.hasFaceWithCurrentSide(left))
				{
					leftEdge = e;
				}
			}
			onLeft = leftEdge.getFaceWithCurrentSide(left);
			
			// if the face to the left of the front (completed) needs to go to the
			// opposite of front
			if (onLeft.getDestinationSide() == this.getOppositeSide(front))
			{
				// make the completed (front) the right
				Side newRight = front;
				Side newLeft = this.getOppositeSide(newRight);
				Side newFront = this.getSideOnLeft(newRight);
				this.stageSevenLeftSequence(newRight, newLeft, newFront);
			}
			else // its the face to the right of completed side that needs to go 
				// to the opposite side of the completed side
			{
				// make the completed side the left
				Side newLeft = front;
				Side newRight = this.getOppositeSide(newLeft);
				Side newFront = this.getSideOnRight(newLeft);
				this.stageSevenRightSequence(newRight, newLeft, newFront);
			}
		}
	}
	
	private void stageSevenSequenceOne(Side left, Side right, Side top,
			Side bottom)
	{
		for (int i=0; i<2; i++)
		{
			this.rotate(right, 2);
			this.rotate(left, 2);
			this.rotate(top, 2);
			this.rotate(right, 2);
			this.rotate(left, 2);
			this.rotate(bottom, 1);
		}
		this.rotate(bottom, 2);
	}
	
	private void stageSevenSequenceTwo(Side left, Side right, Side front, 
			Side back)
	{
		this.rotate(right, 1);
		this.rotate(left, 3);
		this.rotate(front, 1);
		this.rotate(right, 2);
		this.rotate(left, 2);
		this.rotate(back, 1);
		this.rotate(right, 2);
		this.rotate(left, 2);
		this.rotate(front, 1);
		this.rotate(right, 1);
		this.rotate(left, 3);
		this.rotate(Side.TOP, 2);
		this.rotate(right, 2);
		this.rotate(left, 2);
		this.rotate(Side.BOTTOM, 3);
	}
	
	private void stageSevenLeftSequence(Side right, Side left, Side front)
	{
		this.rotate(right, 1);
		this.rotate(left, 3);
		this.rotate(front, 1);
		this.rotate(right, 3);
		this.rotate(left, 1);
		this.rotate(Side.BOTTOM, 1);
		this.rotate(right, 1);
		this.rotate(left, 3);
		this.rotate(front, 2);
		this.rotate(right, 3);
		this.rotate(left, 1);
		this.rotate(Side.BOTTOM, 1);
		this.rotate(right, 1);
		this.rotate(left, 3);
		this.rotate(front, 1);
		this.rotate(right, 3);
		this.rotate(left, 1);
		this.rotate(Side.BOTTOM, 2);
	}
	
	private void stageSevenRightSequence(Side right, Side left, Side front)
	{
		this.rotate(left, 3);
		this.rotate(right, 1);
		this.rotate(front, 3);
		this.rotate(right, 3);
		this.rotate(left, 1);
		this.rotate(Side.BOTTOM, 3);
		this.rotate(right, 1);
		this.rotate(left, 3);
		this.rotate(front, 2);
		this.rotate(right, 3);
		this.rotate(left, 1);
		this.rotate(Side.BOTTOM, 3);
		this.rotate(right, 1);
		this.rotate(left, 3);
		this.rotate(front, 3);
		this.rotate(right, 3);
		this.rotate(left, 1);
		this.rotate(Side.BOTTOM, 2);
	}
}

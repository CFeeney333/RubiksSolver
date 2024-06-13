package mvcbase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import observerpattern.Publisher;
import rubikscube.Manipulator;

public class RubiksSolverModel extends Publisher
{
	private ArrayList<Command> availableCommands;
	private State currentState;
	private String currentInstruction;
	private String currentHelp;
	private String[] setArray;
	private int setArrayIndex;
	private String solution;
	private Manipulator manipulator;
	private Document doc;
	
	private final String[] SET_STRINGS = {"wrb", "wr", "wrg", "wb", "wg", "wob",
			"wo", "wog", "rb", "rg", "ob", "og", "yrb", "yr", "yrg", "yb", "yg", 
			"yob", "yo", "yog"};
	private final String START_INSTRUCTION = "Welcome to the Rubiks Cube Solver "
			+ "program!";
	private final String SET_INSTRUCTION = "Enter the colours of the Rubiks Cube"
			+ " pieces in correct order:\nW = white, Y = yellow, R = red, "
			+ "O = orange, B = blue, G = green";
	private final String CANTSOLVE_INSTRUCTION = "Sorry, the Rubiks Cube cannot "
			+ "be solved.\nYou must have entered the positions wrong. :(";
	private final String SOLVED_INSTRUCTION = "Solved!";
	private final String UNSOLVED_INSTRUCTION = "Rubiks Cube is set!";
	
	private final String START_HELP = "Set -> Set the state of the rubiks "
			+ "cube so that it can be solved.\r\n" 
			+ "Exit -> Exit the program.";
	private final String SET_HELP = "-Each prompt specifies a position of a"
			+ " corner or edge piece of a rubiks cube.\r\n" 
			+ "-For example W R B is the corner that is on the white, red and blue"
			+ " sides.\r\n" 
			+ "-The colour of the side is specified by the colour of the piece in "
			+ "the center.\r\n" 
			+ "-The prompt specifies the position of the piece; you are asked to "
			+ "put in the \r\n" 
			+ "colours of the pieces.\r\n" 
			+ "-These must be in the order that the prompt appears.\r\n" 
			+ "-For example, first give the colour that is on the white side, then"
			+ " the red, \r\n" 
			+ "	and so on.\r\n" 
			+ "-If a mistake is made the rubiks cube might not be able"
			+ " to solve.\r\n" 
			+ "Undo -> Undo the last piece set.\r\n" 
			+ "Exit -> Exit the program.\r\n";
	private final String CANTSOLVE_HELP = "Reset -> Reset the rubiks cube.\r\n"
			+ "Exit -> Exit the program.";
	private final String SOLVED_HELP = "Solution -> Display a dialog with the "
			+ "solution\r\n"
			+ "Reset -> Reset the Rubiks Cube\r\n"
			+ "Exit -> Exit the program\r\n";
	private final String UNSOLVED_HELP = "Solve -> Solve the rubiks cube.\r\n"
			+ "Reset -> reset the rubiks cube.\r\n"
			+ "Exit -> exit the program.\r\n";
	
	private final String DEFAULT_PROMPT = "->";
	
	public RubiksSolverModel()
	{
		this.manipulator = new Manipulator();
		
		this.setArray = new String[20];
		this.setArrayIndex = 0;
		
		this.solution = "";
		
		this.availableCommands = new ArrayList<>();
		availableCommands.add(Command.SET);
		availableCommands.add(Command.HELP);
		availableCommands.add(Command.EXIT);
		this.changeState(null);
		
		this.currentHelp = this.START_HELP;
		this.currentInstruction = this.START_INSTRUCTION;
	}
	
	private final void changeState(State newState)
	{
		this.currentState = newState;
		this.notifyObservers();
	}
	
	public final Command[] getAvailableCommands()
	{
		Command[] commands = new Command[this.availableCommands.size()];
		for (int i = 0; i < this.availableCommands.size(); i++)
		{
			commands[i] = this.availableCommands.get(i);
		}
		return commands;
	}
	
	public final State getCurrentState()
	{
		return this.currentState;
	}
	
	/**
	 * Using the current private set index, it changes the value of the 
	 * corresponding index of the private state list. If all of the data is set,
	 * the state is changed to UNSOLVED.
	 * @param data the string of new data
	 * (Precondition: data.equals([a value in this.getSetStrings()]))
	 * @throws ParserConfigurationException 
	 */
	public void setData(String data) throws ParserConfigurationException
	{
		this.setArray[this.setArrayIndex] = data;
		this.setArrayIndex ++;
		
		if (this.setArrayIndex == 20)
		{
			this.availableCommands.clear();
			this.availableCommands.add(Command.SOLVE);
			this.availableCommands.add(Command.RESET);
			this.availableCommands.add(Command.HELP);
			this.availableCommands.add(Command.EXIT);
			
			this.currentHelp = this.UNSOLVED_HELP;
			this.currentInstruction = this.UNSOLVED_INSTRUCTION;
			
			this.createXMLStateFile();
			
			this.changeState(State.UNSOLVED);
			return;
		}
		this.changeState(State.SET); // so that view's onUpdate is called and gets the new prompt
	}

	public void doStart()
	{
		System.out.println("Doing start");
		this.availableCommands.clear();
		this.availableCommands.add(Command.SET);
		this.availableCommands.add(Command.HELP);
		this.availableCommands.add(Command.EXIT);

		this.currentHelp = this.START_HELP;
		this.currentInstruction = this.START_INSTRUCTION;
		
		this.changeState(State.START);
	}

	public void doHelp()
	{
		System.out.println("Doing Help");
	}

	public void doSet()
	{
		System.out.println("Doing set");
		this.availableCommands.clear();
		this.availableCommands.add(Command.HELP);
		this.availableCommands.add(Command.UNDO);
		this.availableCommands.add(Command.EXIT);
		
		this.currentHelp = this.SET_HELP;
		this.currentInstruction = this.SET_INSTRUCTION;
		
		this.changeState(State.SET);
	}

	public void doExit()
	{
		System.out.println("doing exit");
		this.changeState(State.END);
	}

	public void doReset()
	{
		System.out.println("doing reset");
		for (int i=0; i<this.setArray.length; i++)
		{
			this.setArray[i] = null;
		}
		this.setArrayIndex = 0;
		this.solution = "";
		this.doSet();
	}

	public void doSolution()
	{
		// TODO get the solution from the rubiks cube
		System.out.println("doing solution");
		this.solution = this.manipulator.getSolutionString();
		File f = new File("C:\\Users\\Cathal\\Documents\\solution");
		try
		{
			PrintWriter pw = new PrintWriter(f);
			pw.write(this.solution);
			pw.close();
		} catch (FileNotFoundException e)
		{
			System.out.println("Could not find solution file to write to.");
			e.printStackTrace();
		}
	}

	public void doUndo()
	{
		if (this.setArrayIndex > 0)
		{
		this.setArrayIndex --;
		this.setArray[this.setArrayIndex] = null;
		this.changeState(State.SET); // so that the view's onUpdate is called and gets the new prompt
		System.out.println("doing undo");
		}
	}

	public void doSolve() throws Exception
	{
		// TODO implement this method properly with rc.
//		Random generator = new Random();
//		int canSolve = generator.nextInt(2);
//		if (canSolve == 0)
//		{
//			// the rubiks cube can't be solved:
//			this.availableCommands.clear();
//			this.availableCommands.add(Command.RESET);
//			this.availableCommands.add(Command.HELP);
//			this.availableCommands.add(Command.EXIT);
//			
//			this.currentHelp = this.CANTSOLVE_HELP;
//			this.currentInstruction = this.CANTSOLVE_INSTRUCTION;
//			
//			this.changeState(State.CANTSOLVE);
//		}
		
//		this.manipulator.solve("C:\\Users\\Cathal\\Documents\\doc");
		this.manipulator.solve(doc);
		// the rubiks cube can be solved:
		this.availableCommands.clear();
		this.availableCommands.add(Command.SOLUTION);
		this.availableCommands.add(Command.RESET);
		this.availableCommands.add(Command.HELP);
		this.availableCommands.add(Command.EXIT);
			
		this.currentHelp = this.SOLVED_HELP;
		this.currentInstruction = this.SOLVED_INSTRUCTION;	
			
		this.changeState(State.SOLVED);
		
		System.out.println("doing solve");
	}
	
	public String getCurrentInstruction()
	{
		return this.currentInstruction;
	}
	
	public String getCurrentHelp()
	{
		return this.currentHelp;
	}
	
	public final String[] getSetStrings()
	{
		return this.SET_STRINGS;
	}
	
	public final String getSolution()
	{
		return this.solution;
	}
	
	public final String getPrompt()
	{
		if (this.currentState == State.SET)
		{
			return this.SET_STRINGS[this.setArrayIndex];
		}
		else 
		{
			return this.DEFAULT_PROMPT;
		}
	}
	
	private void createXMLStateFile() throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		this.doc = builder.newDocument();
		Element rubiksCubeElement = doc.createElement("rubikscube");
		
		// create piece elements
		HashMap<String, String> map = new HashMap<>();
		map.put("wrb", "tfr");
		map.put("wr", "tf");
		map.put("wrg", "tfl");
		map.put("wb", "tr");
		map.put("wg", "tl");
		map.put("wob", "tar");
		map.put("wo", "ta");
		map.put("wog", "tal");
		map.put("rb", "fr");
		map.put("rg", "fl");
		map.put("ob", "ar");
		map.put("og", "al");
		map.put("yrb", "ofr");
		map.put("yr", "of");
		map.put("yrg", "ofl");
		map.put("yb", "or");
		map.put("yg", "ol");
		map.put("yob", "oar");
		map.put("yo", "oa");
		map.put("yog", "oal");
		
		HashMap<String, String> map2 = new HashMap<>();
		map2.put("w", "t");
		map2.put("y", "o");
		map2.put("r", "f");
		map2.put("o", "a");
		map2.put("b", "r");
		map2.put("g", "l");
		
		for (int i=0; i<this.setArray.length; i++)
		{
			Element pieceElement = doc.createElement("piece");
			Element typeElement = doc.createElement("type");
			Element destinationElement = doc.createElement("destination");
			Element currentElement = doc.createElement("current");
			
			if (this.SET_STRINGS[i].length() == 2)
			{
				Text typeNode = doc.createTextNode("edge");
				typeElement.appendChild(typeNode);
				
				String destinationString = "";
				String currentString = "";
				for (int index=0; index<2; index++)
				{
					destinationString += map2.get(this.setArray[i].substring(index, index+1));
					currentString += map2.get(this.SET_STRINGS[i].substring(index, index+1));
				}
				Text destinationNode = doc.createTextNode(destinationString);
				Text currentNode = doc.createTextNode(currentString);
				
				destinationElement.appendChild(destinationNode);
				currentElement.appendChild(currentNode);
			}
			else
			{
				Text textNode = doc.createTextNode("corner");
				typeElement.appendChild(textNode);
				
				String destinationString = "";
				String currentString = "";
				for (int index=0; index<3; index++)
				{
					destinationString += map2.get(this.setArray[i].substring(index, index+1));
					currentString += map2.get(this.SET_STRINGS[i].substring(index, index+1));
				}
				Text destinationNode = doc.createTextNode(destinationString);
				Text currentNode = doc.createTextNode(currentString);
				
				destinationElement.appendChild(destinationNode);
				currentElement.appendChild(currentNode);
			}
			
//			String destinationKey = "";
//			String currentKey = "";
						
//			Text textNode = doc.createTextNode(map.get(key));
//			destinationElement.appendChild(textNode);
			
//			key = this.setArray[i];
//			textNode = doc.createTextNode(map.get(key));
//			currentElement.appendChild(textNode);
			
			pieceElement.appendChild(typeElement);
			pieceElement.appendChild(destinationElement);
			pieceElement.appendChild(currentElement);
			
			rubiksCubeElement.appendChild(pieceElement);	
		}
		doc.appendChild(rubiksCubeElement);
		
//		DOMImplementation impl = doc.getImplementation();
//		DOMImplementationLS implLS = 
//				(DOMImplementationLS) impl.getFeature("LS", "3.0");
//		LSSerializer ser = implLS.createLSSerializer();
//		
//		try
//		{
//			File f = new File(fileName);
//			f.setWritable(true);
//			PrintWriter pw;
//			pw = new PrintWriter(f);
//			pw.write(ser.writeToString(doc));
//			pw.close();
//		} catch (FileNotFoundException e)
//		{
//			System.out.println("Didn't find file " + fileName);
//			e.printStackTrace();
//		}
		
	}
}

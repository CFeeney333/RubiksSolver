package mvcbase;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import observerpattern.Observer;

/**
 * A Rubiks Command Line View is to be created by a RubiksCLController. This 
 * defines the view part of the model view controller strategy pattern. It 
 * interacts with the user, displays info and handles events. This frame has
 * an instruction at the top, a text field area with a prompt, and a submit 
 * button to submit the command in the text field. It's panel implements the
 * observer pattern and is an observer of the model.
 * @author Cathal
 *
 */
@SuppressWarnings("serial")
public class RubiksCLView extends JFrame
{
	private RubiksCLPanel panel;
	
	/**
	 * Creates a frame to represent the view.
	 * @param controller the controller strategy for the view
	 * @param model the model that the view observes
	 */
	public RubiksCLView(RubiksCLController controller, RubiksSolverModel model)
	{
		super();
		this.panel = new RubiksCLPanel(controller, model);
		this.add(this.panel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Enable or disable the submit button. 
	 * @param enable true enables, false disables
	 */
	public void enableButton(boolean enable)
	{
		this.panel.setEnabled(enable);
	}
	
	/**
	 * Set the text in the text field to an empty string.
	 */
	public void clearTextField()
	{
		this.panel.clearTextField();
	}
	
	/**
	 * Show warning message dialog box with "Invalid command".
	 */
	public void showInvalidCommandDialog()
	{
		this.panel.showInvalidCommandDialog();
	}
	
	/**
	 * Show a dialog with the state sensitive help message.
	 */
	public void showHelpDialog()
	{
		this.panel.showHelpDialog();
	}
	
	/**
	 * Show a dialog with the message "Not yet implemented"
	 */
	public void showNotImplementedDialog()
	{
		this.panel.showNotImplementedDialog();
	}
	
	/**
	 * Show a dialog with the solution obtained from the model.
	 * Model.doSolution() must be called for the proper solution to be obtained.
	 */
	public void showSolutionDialog()
	{
		this.panel.showSolutionDialog();
	}
	
	/**
	 * This panel implements the observer pattern. It is the working part of the
	 * view. Contains methods for managing the view.
	 * @author Cathal
	 *
	 */
	private class RubiksCLPanel extends JPanel implements Observer
	{
		private RubiksCLController controller;
		private RubiksSolverModel model;
		
		private JButton submitButton;
		private JLabel instruction;
		private JTextField inputField;
		
		/**
		 * Create a Rubiks Command Line Panel. Only to be used by the rubiks 
		 * command line view. 
		 * @param controller the controller strategy to be used
		 * @param model the rubiks solver model that publishes updates
		 */
		public RubiksCLPanel(RubiksCLController controller,
				RubiksSolverModel model)
		{
			this.controller = controller;
			this.model = model;
			// set as an observer of the model
			this.model.subscribe(this);
			
			this.submitButton = new JButton("Submit");
			this.instruction = new JLabel(this.model.getCurrentInstruction());
			this.inputField = new JTextField();
			
			this.setLayout(new GridLayout(8, 3));
			this.add(this.instruction);
			this.add(this.inputField);
			this.add(this.submitButton);
			
			ActionListener listener = new SubmitButtonListener();
			submitButton.addActionListener(listener);
		}
		
		/**
		 * Go through the available commands in the model. If the command is 
		 * equal to the input (case-insensitive), or if the first letter of the 
		 * input corresponds to the first letter of a command, then doCommand is 
		 * called on the controller. If it is not a command, the parser then 
		 * checks to see if the state is SET. If it is, it checks to see if the 
		 * input is equal to a string in the model's set_string array 
		 * (case-insensitive). If it is one of these it calls doSet on the 
		 * controller. If it is equal to nothing useful calls doInvalidCommand on
		 * the controller.
		 * @param input the string input to parse.
		 */
		private void parseInput(String input)
		{	
			if (input.length() == 0)
			{
				this.controller.doInvalidCommand();
				return;
			}
			
			for (Command command : this.model.getAvailableCommands())
			{
			
				if (input.equalsIgnoreCase(command.toString()) || 
						input.substring(0, 1).equalsIgnoreCase(
								command.toString().substring(0, 1)))
				{
					this.controller.doCommand(command);
					return;
				}
			}
			
			if (this.model.getCurrentState() == State.SET)
			{
				for (String setString : this.model.getSetStrings())
				{
					ArrayList<Character> newList = new ArrayList<>();
					ArrayList<Character> setList = new ArrayList<>();
					for (int i=0; i<setString.length(); i++) {setList.add(setString.charAt(i));}
					for (int i=0; i<input.length(); i++) {newList.add(input.charAt(i));}
					if (setList.containsAll(newList))
					{
						this.controller.doSet(input);
						return;
					}
				}
			}
			this.controller.doInvalidCommand();
		}
		
		class SubmitButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				parseInput(inputField.getText());
			}
		}
		
		@Override
		public void onUpdate()
		{
			if (this.model.getCurrentState() == State.END)
			{
				this.controller.doEnd();
			}
			
			String commands = "";
			for (Command c : this.model.getAvailableCommands())
			{
				commands += c + "  ";
			}
			this.submitButton.setEnabled(true);
			this.instruction.setText(this.model.getCurrentInstruction()
					+ " Available commands are: " + commands
					+ "  " + this.model.getPrompt());
		}
		
		public void clearTextField()
		{
			this.inputField.setText("");
		}
		
		public void showHelpDialog()
		{
			String helpString = this.model.getCurrentHelp();
			JOptionPane.showConfirmDialog(this, helpString, "Rubiks Solver Help", 
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
		}
		
		public void showInvalidCommandDialog()
		{
			JOptionPane.showConfirmDialog(this, "Invalid command!", "Warning",
					JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}
		
		public void showNotImplementedDialog()
		{
			JOptionPane.showConfirmDialog(this, "Not yet implemented", "Warning",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
		}
		
		public void showSolutionDialog()
		{
			JOptionPane.showConfirmDialog(this, this.model.getSolution(), 
					"Solution", JOptionPane.DEFAULT_OPTION, 
					JOptionPane.INFORMATION_MESSAGE);
			System.out.println(this.model.getSolution());
		}
	}
}

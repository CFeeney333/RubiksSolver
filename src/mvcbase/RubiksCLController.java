package mvcbase;

import javax.xml.parsers.ParserConfigurationException;

public class RubiksCLController
{
	private RubiksSolverModel model;
	private RubiksCLView view;
	
	public RubiksCLController(RubiksSolverModel model)
	{
		this.model = model;
		this.view = new RubiksCLView(this, this.model);
		
		this.view.setSize(1000, 500);
		this.view.setTitle("Rubiks Command Line Solver");
		this.view.setVisible(true);
	}
	
	public void doInvalidCommand()
	{
		this.view.showInvalidCommandDialog();
		this.view.clearTextField();
	}
	
	public void doSet(String setString)
	{
		try
		{
			this.model.setData(setString);
		} 
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.view.clearTextField();
	}
	
	public void doCommand(Command c)
	{
		this.view.enableButton(false);
		switch (c)
		{
			case START:
				this.model.doStart();
				break;
			case EXIT:
				this.model.doExit();
				break;
			case HELP:
				this.view.showHelpDialog();
				break;
			case RESET:
				this.model.doReset();
				break;
			case SET:
				this.model.doSet();
				break;
			case SOLUTION:
				this.model.doSolution();
				this.view.showSolutionDialog();
				break;
			case SOLVE:
				try
				{
					this.model.doSolve();
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case UNDO:
				this.model.doUndo();
				break;
			default:
				break;
		}
		this.view.clearTextField();
	}
	
	public void doEnd()
	{
		this.view.showNotImplementedDialog();
	}
	
}

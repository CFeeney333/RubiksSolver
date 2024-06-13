import mvcbase.Command;
import mvcbase.RubiksCLController;
import mvcbase.RubiksSolverModel;

public class RubiksCLSolver
{
	
	public static void main(String[] args)
	{
		RubiksSolverModel model = new RubiksSolverModel();
		RubiksCLController c = new RubiksCLController(model);
		c.doCommand(Command.START);
	}
}

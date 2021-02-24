import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
/**
 * Class containing GUI: board + buttons
 */
public class GUI extends JScrollPane implements ActionListener {
	private static final long serialVersionUID = 1L;
	private Board board;
	private SolverBase solver;
	private JButton clear;
	private JButton solverSingleMove;
	private JButton solverTrySolve;
	private JFrame frame;
	private boolean running = false;


	public GUI(JFrame jf) {
		frame = jf;
	}

	/**
	 * @param container to which GUI and board is added
	 */
	public void initialize(Container container) {
		container.setLayout(new BorderLayout());
		container.setSize(new Dimension(1024, 768));

		JPanel buttonPanel = new JPanel();

		clear = new JButton("Restart");
		clear.setActionCommand("clear");
		clear.setToolTipText("Starts new game");
		clear.addActionListener(this);

		solverSingleMove = new JButton("Make move");
		solverSingleMove.setActionCommand("solverSingleMove");
		solverSingleMove.setToolTipText("Makes a move, using the connected solver");
		solverSingleMove.addActionListener(this);

		solverTrySolve = new JButton("Solve");
		solverTrySolve.setActionCommand("solverTrySolve");
		solverTrySolve.setToolTipText("Use the connected solver to try and make as many certain moves as possible, up to 100");
		solverTrySolve.addActionListener(this);
		
		buttonPanel.add(clear);
		buttonPanel.add(solverSingleMove);
		buttonPanel.add(solverTrySolve);

		board = new Board();
		solver = new SolverBase(board);
		JScrollPane	scroll = new JScrollPane(board, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		container.add(scroll, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * handles clicking on each button
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("clear")) {
			board.clear();
			frame.setTitle("Cellular Automata Toolbox");
		}else if(command.equals("solverSingleMove")){
			solver.makeMove();
		}else if(command.equals("solverTrySolve")){
			solver.solve();
		}


	}

}

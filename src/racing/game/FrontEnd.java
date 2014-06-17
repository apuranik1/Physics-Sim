package racing.game;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JLabel;

import com.sun.awt.AWTUtilities;

import racing.BasicGame;
import racing.networking.NetServer;
import engine.GameEngine;

public class FrontEnd {
	private Frame frame;
	private static final String INSTRUCTIONS = "";
	private static FrontEnd fe;
	private final Dialog pframe = new Dialog((Frame) null);

	public static FrontEnd getFrontEnd() {
		if (fe == null)
			fe = new FrontEnd();
		return fe;
	}

	private FrontEnd() {
		frame = new Frame("Racing");
		frame.setLayout(new GridLayout(0, 1));
		frame.setPreferredSize(new Dimension(190, 140));
		((GridLayout) (frame.getLayout())).setHgap(5);
		((GridLayout) (frame.getLayout())).setVgap(5);
		Button newGame = new Button("New Game");
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				typeSelect();
			}
		});
		Button instructions = new Button("Instructions");
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showInstructions();
			}
		});
		Button quit = new Button("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		// handle quit button
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});
		frame.add(newGame);
		frame.add(instructions);
		frame.add(quit);
		frame.pack();
		// Center frame
		frame.setLocation(
				frame.getToolkit().getScreenSize().width / 2 - frame.getWidth()
						/ 2, frame.getToolkit().getScreenSize().height / 2
						- frame.getHeight() / 2);
		frame.setVisible(true);
	}

	public void showPopup(String msg) {
		pframe.removeAll();
		pframe.setAlwaysOnTop(true);
		if (!pframe.isVisible())
			pframe.setUndecorated(true);
		JLabel label = new JLabel(msg);
		label.setFont(new Font("sansserif", Font.BOLD, 30));
		label.setForeground(Color.RED);
		AWTUtilities.setWindowOpaque(pframe, false);
		pframe.add(label);
		label.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)
					pframe.dispose();
			}

			public void mouseReleased(MouseEvent arg0) {
			}
		});
		pframe.pack();
		pframe.setLocation(
				pframe.getToolkit().getScreenSize().width / 2
						- pframe.getWidth() / 2,
				pframe.getToolkit().getScreenSize().height / 2
						- pframe.getHeight() / 2);
		pframe.toFront();
		pframe.setVisible(true);
	}
	
	public void hidePopup() {
		pframe.setVisible(false);
	}

	private void typeSelect() {
		final Frame iframe = new Frame("Game Type Select");
		iframe.setLayout(new GridLayout(0, 1));
		iframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				iframe.dispose();
			}
		});
		((GridLayout) (iframe.getLayout())).setHgap(5);
		((GridLayout) (iframe.getLayout())).setVgap(5);
		Button single = new Button("Singleplayer");
		single.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					iframe.dispose();
					new NetServer(8888, 5);
					new BasicGame("localhost");
					GameEngine ge = GameEngine.getGameEngine();
					ge.beginGame();
				} catch (IOException e1) {
					showPopup("Error: " + e1.getMessage());
				}
			}
		});
		Button multiServer = new Button("Multiplayer Server");
		multiServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iframe.dispose();
				new NetServer(8888, 60);
			}
		});
		Button multiClient = new Button("Multiplayer Client");
		multiClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iframe.dispose();
				getIP();
			}
		});
		iframe.add(single);
		iframe.add(multiServer);
		iframe.add(multiClient);
		iframe.pack();
		iframe.setLocation(
				iframe.getToolkit().getScreenSize().width / 2
						- iframe.getWidth() / 2,
				iframe.getToolkit().getScreenSize().height / 2
						- iframe.getHeight() / 2);
		iframe.setVisible(true);
	}

	private void getIP() {
		final Frame iframe = new Frame("IP Address");
		iframe.setLayout(new GridLayout(1, 0));
		iframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				iframe.dispose();
			}
		});
		((GridLayout) (iframe.getLayout())).setHgap(5);
		((GridLayout) (iframe.getLayout())).setVgap(5);
		iframe.add(new Label("IP Address: "));
		final TextField field = new TextField("");
		field.setColumns(15);
		iframe.add(field);
		final Button enter = new Button("Enter");
		enter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					iframe.dispose();
					new BasicGame(field.getText());
					GameEngine ge = GameEngine.getGameEngine();
					ge.beginGame();
				} catch (Exception e1) {
					showPopup("Error: " + e1.getMessage());
				}
			}
		});
		field.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					enter.dispatchEvent(new ActionEvent((Object) enter,
							ActionEvent.ACTION_PERFORMED, ""));
			}

			public void keyReleased(KeyEvent e) {
			}
		});
		iframe.add(enter);
		iframe.pack();
		iframe.setLocation(
				iframe.getToolkit().getScreenSize().width / 2
						- iframe.getWidth() / 2,
				iframe.getToolkit().getScreenSize().height / 2
						- iframe.getHeight() / 2);
		iframe.setVisible(true);
	}

	private void showInstructions() {
		final Frame iframe = new Frame("Instructions");
		iframe.setLayout(new FlowLayout());
		Button cancel = new Button("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iframe.dispose();
			}
		});
		TextArea ta = new TextArea(INSTRUCTIONS);
		ta.setEditable(false);
		ta.setPreferredSize(new Dimension(300, 200));
		// handle quit button
		iframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				iframe.dispose();
			}
		});
		iframe.add(ta);
		iframe.add(cancel);
		iframe.pack();
		// center frame
		iframe.setLocation(
				iframe.getToolkit().getScreenSize().width / 2
						- iframe.getWidth() / 2,
				iframe.getToolkit().getScreenSize().height / 2
						- iframe.getHeight() / 2);
		iframe.setVisible(true);
	}
	
	public void gameWon() {
		GameEngine.getGameEngine().conclude("You win! :)");
	}
	
	public void gameLost() {
		GameEngine.getGameEngine().conclude("You lose. :(");
	}

	public static void main(String[] args) {
		FrontEnd.getFrontEnd();
	}
}

package racing.game;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class Menu extends JPanel implements ActionListener{
	private JFrame frame;
	private JButton single;
	private JButton multiClient;
	private JButton multiServer;
	private Manager manager;
	public Menu(Manager manager){
		this.manager=manager;
		//---Buttons---
		single=new JButton("SinglePlayer");
		single.addActionListener(this);
		add(single);
		multiClient=new JButton("Multiplayer Connect");
		multiClient.addActionListener(this);
		add(multiClient);
		multiServer=new JButton("Multiplayer Server");
		multiServer.addActionListener(this);
		add(multiServer);
		//---Frame---
		frame=new JFrame("Kart");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(this);
		frame.setPreferredSize(new Dimension(350, 350));
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
	}
//		JPanel inputPanel=new JPanel(new GridLayout(0,4));
//		inputPanel.add(new JLabel("IP: "));
//		inputPanel.add(new JTextField());
//		inputPanel.add(new JLabel("Port: "));
//		inputPanel.add(new JTextField());
//		JButton enter=new JButton("Enter");
//		enter.addActionListener(this);
//		enter.setActionCommand("Client Info Enter");
//		inputPanel.add(enter);
//		JDialog inputDialog=new JDialog();
//		inputDialog.setTitle("Input Client Info");
//		inputDialog.setContentPane(inputPanel);
//		inputDialog.pack();
//		inputDialog.setVisible(true);
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(single))manager.newSingle();
	}
	
}

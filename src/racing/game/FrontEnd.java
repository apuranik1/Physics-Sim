package racing.game;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import racing.BasicGame;
import engine.GameEngine;
public class FrontEnd {
	private Frame frame;
	private static final String INSTRUCTIONS="";
	public FrontEnd(){
		frame=new Frame("Racing");
		frame.setLayout(new GridLayout(0,1));
		((GridLayout) (frame.getLayout())).setHgap(5);
		((GridLayout) (frame.getLayout())).setVgap(5);
		Button newGame=new Button("New Game");
		newGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try {
					new BasicGame();
					GameEngine.getGameEngine().beginGame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Button instructions=new Button("Instructions");
		instructions.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				showInstructions();
			}
		});
		//handle quit button
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
			}
		});
		frame.add(newGame);
		frame.add(instructions);
		frame.pack();
		//Center frame
		frame.setLocation(frame.getToolkit().getScreenSize().width/2-frame.getWidth()/2, frame.getToolkit().getScreenSize().height/2-frame.getHeight()/2);
		frame.setVisible(true);
	}
	private void showPopup(String msg){
		final Window window=new Window(frame);
		window.setLayout(new GridLayout(2,0));
		Label label=new Label(msg);
		label.setFont(new Font("sansserif",Font.PLAIN,20));
		window.add(label);
		Button close=new Button("Close");
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				window.setVisible(false);
			}
		});
		window.add(close);
		window.pack();
		window.setLocation(window.getToolkit().getScreenSize().width/2-window.getWidth()/2, window.getToolkit().getScreenSize().height/2-window.getHeight()/2);
		window.setVisible(true);
	}
	private void showInstructions(){
		final Frame iframe=new Frame("Instructions");
		iframe.setLayout(new FlowLayout());
		Button cancel=new Button("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				iframe.setVisible(false);
			}
		});
		TextArea ta=new TextArea(INSTRUCTIONS);
		ta.setEditable(false);
		ta.setPreferredSize(new Dimension(300,200));
		//handle quit button
		iframe.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent windowEvent){
	            iframe.setVisible(false);
			}
		});
		iframe.add(ta);
		iframe.add(cancel);
		iframe.pack();
		//center frame
		iframe.setLocation(iframe.getToolkit().getScreenSize().width/2-iframe.getWidth()/2, iframe.getToolkit().getScreenSize().height/2-iframe.getHeight()/2);
		iframe.setVisible(true);
		showPopup("Test Popup");
	}
	public static void main(String[] args){
		new FrontEnd();
	}
}

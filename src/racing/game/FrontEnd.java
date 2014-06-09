package racing.game;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
public class FrontEnd {
	private Frame frame;
	private Button newGame;
	private Button instructions;
	private Panel panel;
	private static final String INSTRUCTIONS="";
	public FrontEnd(){
		frame=new Frame("Racing");
		panel=new Panel(new FlowLayout());
		frame.setSize(new Dimension(200,300));
		//Center frame
		frame.setLocation(frame.getToolkit().getScreenSize().width/2-frame.getWidth()/2, frame.getToolkit().getScreenSize().height/2-frame.getHeight()/2);
		newGame=new Button("New Game");
		newGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		panel.add(newGame);
		instructions=new Button("Instructions");
		instructions.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				showInstructions();
			}
		});
		panel.add(instructions);
		//handle quit button
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
			}
		});
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	private void showInstructions(){
		Frame iframe=new Frame("Instructions");
		Panel ipanel=new Panel(new FlowLayout());
		iframe.setLocation(frame.getToolkit().getScreenSize().width/2-frame.getWidth()/2, frame.getToolkit().getScreenSize().height/2-frame.getHeight()/2);
		ipanel.add(new TextArea(INSTRUCTIONS));
		iframe.add(ipanel);
		//handle quit button
		iframe.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
			}
		});
		iframe.setVisible(true);
	}
	public static void main(String[] args){
		new FrontEnd();
	}
}

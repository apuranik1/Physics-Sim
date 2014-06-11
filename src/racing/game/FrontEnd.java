package racing.game;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
					GameManager.initGame(0);
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
	}
	public static void main(String[] args){
		new FrontEnd();
	}
}

package racing.game;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import com.sun.awt.AWTUtilities;
import racing.BasicGame;
import engine.GameEngine;
public class FrontEnd {
	private Frame frame;
	private static final String INSTRUCTIONS="";
	private static FrontEnd fe;
	public static FrontEnd getFrontEnd(){
		if(fe==null)fe=new FrontEnd();
		return fe;
	}
	private FrontEnd(){
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
					showPopup("Test Popup");
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
		showPopup("Test Popup");
	}
	public void showPopup(String msg){
		final Frame pframe=new Frame();
		pframe.setUndecorated(true);
		JLabel label=new JLabel(msg);
		label.setFont(new Font("sansserif",Font.PLAIN,25));
		AWTUtilities.setWindowOpaque(pframe, false);
		pframe.add(label);
		label.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {
			}
			public void mouseEntered(MouseEvent arg0) {
			}
			public void mouseExited(MouseEvent arg0) {
			}
			public void mousePressed(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON1)pframe.setVisible(false);
			}
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		pframe.pack();
		pframe.setLocation(pframe.getToolkit().getScreenSize().width/2-pframe.getWidth()/2, pframe.getToolkit().getScreenSize().height/2-pframe.getHeight()/2);
		pframe.setVisible(true);
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
		FrontEnd.getFrontEnd();
	}
}

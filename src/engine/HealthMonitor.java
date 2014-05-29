package engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class HealthMonitor extends JFrame {
	private JTextArea jta;
	public HealthMonitor() {
		super("Game Engine Monitor");
		jta = new JTextArea();
		
		new Timer(200, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GameEngine engine = GameEngine.getGameEngine();
					int objects = engine.getSize();
					int treeSize = engine.treeSize();
					double duplicationPercentage = ((double) treeSize - objects) * 100 / objects;
					int treeDepth = engine.treeDepth() + 1;
					double reqDepth = Math.log(Math.ceil(objects / 16d)) / Math.log(8) + 1;
					double overshoot = (treeDepth / reqDepth - 1) * 100;
					int rendered = engine.lastRendered();
					jta.setText(
							"Objects loaded: " + objects + "\n" +
							"Octree size: " + treeSize + "\n" +
							"Duplication percentage: " + duplicationPercentage +"%\n" +
							"Octree depth: " + treeDepth + "\n" +
							"Depth overshoot: " +  overshoot + "%\n" +
							"Culling status: " + rendered +"/"+objects+"\n" +
							"FPS: " + engine.getFPS()
					);
				}
				catch(Exception ex) {
					jta.setText("Poor timing...");
				}
			}
		}).start();
		setSize(500,500);
		add(jta);
		setVisible(true);
	}
}

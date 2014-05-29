package engine.processors;

import com.jogamp.newt.event.KeyEvent;

import engine.EventProcessor;

public class DefaultExitProcessor extends EventProcessor {
	public boolean keyPressed(int keyCode) {
		if (keyCode == KeyEvent.VK_ESCAPE)
			System.exit(0);
		return false;
	}
}
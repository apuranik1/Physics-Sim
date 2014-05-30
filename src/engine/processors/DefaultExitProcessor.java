package engine.processors;

import java.util.Set;

import com.jogamp.newt.event.KeyEvent;

import engine.EventProcessor;

public class DefaultExitProcessor extends EventProcessor {
	public void keysPressed(Set<Short> keys) {
		if (keys.contains(KeyEvent.VK_ESCAPE))
			System.exit(0);
	}
}
package engine.processors;

import java.awt.event.KeyEvent;
import java.util.Set;


import engine.EventProcessor;
import engine.GameEngine;

public class DefaultExitProcessor extends EventProcessor {
	public void keysPressed(Set<Integer> keys) {
		if (keys.contains(KeyEvent.VK_ESCAPE))
			GameEngine.getGameEngine().exit();
	}
}
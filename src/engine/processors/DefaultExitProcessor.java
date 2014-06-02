package engine.processors;

import java.util.Set;

import com.jogamp.newt.event.KeyEvent;

import engine.EventProcessor;
import engine.GameEngine;

public class DefaultExitProcessor extends EventProcessor {
	public void keysPressed(Set<Short> keys) {
		if (keys.contains(KeyEvent.VK_ESCAPE))
			GameEngine.getGameEngine().exit();
	}
}
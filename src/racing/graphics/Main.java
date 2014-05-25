package racing.graphics;

import racing.physics.Vector3D;

public class Main {
	public static void main(String[] args) throws Exception {
		Object3D obj = Object3D.load("/Users/michael/Desktop/monkey.obj");
		obj.motion.setAccel(new Vector3D(0,0,0));
		GameEngine.getGameEngine().addObject(obj);
		new RenderEngine("Game");
	}
}

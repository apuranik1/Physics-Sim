package racing.graphics;

public class Main {
	public static void main(String[] args) throws Exception {
		GameEngine.getGameEngine().addObject(Object3D.load("/Users/michael/Desktop/monkey.obj"));
		new RenderEngine("Game");
	}
}

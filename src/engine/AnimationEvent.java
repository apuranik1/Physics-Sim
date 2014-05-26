package engine;

public abstract class AnimationEvent implements Comparable<AnimationEvent> {
	protected double execTime;

	public AnimationEvent(double offsetTime) {
		this(System.nanoTime() / 1000000000d, offsetTime);
	}

	private AnimationEvent(double baseTime, double offsetTime) {
		this.execTime = baseTime + offsetTime;
	}

	public double executionTime() {
		return execTime;
	}

	public int compareTo(AnimationEvent other) {
		if (executionTime() < executionTime())
			return -1;
		if (executionTime() == executionTime())
			return 0;
		return 1;
	}

	public abstract void animate();

	public boolean prepareNext() {
		return false;
	}
}

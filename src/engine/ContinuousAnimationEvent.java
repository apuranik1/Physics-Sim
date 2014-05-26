package engine;

public abstract class ContinuousAnimationEvent extends AnimationEvent implements
		Cloneable {
	private double period;

	public ContinuousAnimationEvent(double startTime, double period) {
		super(startTime);
		this.period = period;
	}

	public final boolean prepareNext() {
		execTime += period;
		return true;
	}
}

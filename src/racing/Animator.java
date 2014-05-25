package racing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

public class Animator {
	private static Animator animator;
	private PriorityQueue<AnimationEvent> animationQueue;

	private Animator() {
		animationQueue = new PriorityQueue<AnimationEvent>();
	}

	public void registerEvent(AnimationEvent event) {
		animationQueue.offer(event);
	}

	public void registerEvents(AnimationEvent[] events) {
		for (AnimationEvent event : events)
			registerEvent(event);
	}

	public void registerEvents(Collection<? extends AnimationEvent> events) {
		animationQueue.addAll(events);
	}

	public ArrayList<AnimationEvent> retrieve(double time) {
		ArrayList<AnimationEvent> events = new ArrayList<AnimationEvent>();
		while (!animationQueue.isEmpty()
				&& animationQueue.peek().executionTime() <= time) {
			AnimationEvent event = animationQueue.poll();
			events.add(event);
			if(event.prepareNext()) {
				animationQueue.offer(event);
			}
		}
		return events;
	}

	public static Animator getAnimator() {
		if (animator == null)
			animator = new Animator();
		return animator;
	}
}

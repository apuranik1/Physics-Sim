package racing;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;

import engine.animation.AnimationEvent;
import engine.animation.Animator;
import engine.graphics.Object3D;
import engine.physics.Motion;
import engine.physics.PhysicsSpec;
import engine.physics.Quaternion;
import engine.physics.Vector2D;
import engine.physics.Vector3D;

public class Cart extends Object3D implements Serializable {
	private static final transient CarForces	CAR_FORCES	= new CarForces(40, 1.33);

	private int									lap;
	private Vector3D							force;
	private double								thrustBoost;
	transient private int						framesSinceCollide;
	transient private int						framesSinceBoost;
	transient private boolean					aligning;

	private double								handling	= 0.035;
	private double								turnVeloc;

	private Item								item;

	public Cart(Vector3D[] vertices, Vector3D[] normals, Vector2D[] textureCoords, Color[] colors, Motion motion) {
		super(vertices, normals, textureCoords, colors, motion, new PhysicsSpec(false, false, true, true, 50));
		force = new Vector3D(0, 0, 0);
		thrustBoost = 1;
		aligning = false;
		lap = 0;
		item = Item.NONE;
	}

	public Cart clone() {
		Cart that = (Cart) super.clone();
		return that;
	}

	public Cart(String from) throws IOException {
		super(from);
		System.out.println(motion);
		force = new Vector3D(0, 0, 0);
		setRotation(new Quaternion(new Vector3D(0, 0, 1), 0));
		setSpec(new PhysicsSpec(false, false, true, true, 50));
		setAcceleration(Vector3D.gravity);
		aligning = false;
		lap = 0;
		item = Item.NONE;
	}

	public void incrementLap() {
		lap++;
	}

	public int getLap() {
		return lap;
	}

	public void resetLap() {
		lap = 0;
	}

	public void setForce(Vector3D force) {
		this.force = force;
	}

	public void turnLeft() {
		turnVeloc = handling;
	}

	public void turnRight() {
		turnVeloc = -handling;
	}

	public void setTurnVeloc(double veloc) {
		this.turnVeloc = veloc;
	}

	public void boost(double thrustProportion) {
		thrustBoost = thrustProportion;
		framesSinceBoost = 0;
	}

	public void align(Vector3D upVec) {
		if (aligning) {
			System.out.println("Can't do that now");
			return;
		}
		Quaternion rot = getRotation();
		Vector3D currUp = rot.toMatrix().multiply(new Vector3D(0, 1, 0));
		Vector3D axis = currUp.cross(upVec);
		if (axis.isZero(1e-8))
			return;
		aligning = true;
		double angle = 0.1 * Math.atan2(axis.magnitude(), currUp.dot(upVec));
		System.out.println("align angle: " + angle);
		final Quaternion change = new Quaternion(axis, angle);
		for (int i = 0; i < 10; i++) {
			Animator.getAnimator().registerEvent(new AnimationEvent(0.02 * i) {
				@Override
				public void animate() {
					Cart.this.setRotation(change.multiply(Cart.this.getRotation()));

				}
			});
		}
		Animator.getAnimator().registerEvent(new AnimationEvent(0.5) {
			public void animate() {
				Cart.this.aligning = false;
				System.out.println("Releasing align lock");
			}
		});
	}

	public void updateImpl(long nanos) {
		boolean grounded = framesSinceCollide <= 10;
		// System.out.println("frames since collide: " + framesSinceCollide);
		Vector3D appliedForce = !grounded ? Vector3D.origin : framesSinceBoost > 60 ? force : force.multiply(thrustBoost);
		CAR_FORCES.updateAccel(motion, appliedForce, getSpec().getMass(), grounded, true, true);
		Vector3D forward = getRotation().toMatrix().multiply(new Vector3D(0, 0, 1));
		double dPos = getVelocity().project(forward);
		// System.out.println("dPos = " + dPos);
		if (grounded)
			uncheckedSetRotation(getRotation().multiply(new Quaternion(new Vector3D(0, 1, 0), turnVeloc * dPos * nanos / 1e9)));
		super.updateImpl(nanos);
		framesSinceCollide++;
		framesSinceBoost++;
	}

	public void specialCollide(Object3D other) {
		if (other.getSpec().isCollidable())
			framesSinceCollide = 0;
		if (!(getPosition().magnitude() < 1000000)) {
			System.out.println("Where am I, exactly?");
			System.out.println("About here: " + getPosition());
		}
	}

	public Vector3D getForce() {
		return force;
	}

	public double getThrustBoost() {
		return thrustBoost;
	}

	public void setThrustBoost(double thrust) {
		thrustBoost = thrust;
	}

	public double getHandling() {
		return handling;
	}

	public void setHandling(double handling) {
		this.handling = handling;
	}

	public double getTurnVeloc() {
		return turnVeloc;
	}

	public void useItem() {
		switch (item) {
			case NONE:
				break;
			case MUSHROOM:
				boost(5);
				break;
			case SUPER_MUSHROOM:
				boost(10);
				break;
			case ULTRA_STEER:
				handling = .1;
				Animator.getAnimator().registerEvent(new AnimationEvent(10d) {
					
					@Override
					public void animate() {
						handling = .035;
					}
				});
				break;
			case QUICKSTOPS:
				getSpec().setMass(1);
				Animator.getAnimator().registerEvent(new AnimationEvent(10d) {
					
					@Override
					public void animate() {
						getSpec().setMass(50);
					}
				});
				break;
		}
		item = Item.NONE;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}

	public enum Item {
		NONE("None"), MUSHROOM("Mushroom"), SUPER_MUSHROOM("Super Mushroom"), ULTRA_STEER("Ultra Steering"), QUICKSTOPS("Weightless Stops");

		private String	name;

		private Item(String st) {
			this.name = st;
		}
		
		public String getName() {
			return name;
		}
		
		public static Item random() {
			Item[] items =  Item.values();
			return items[(int) (1 + Math.random() * (items.length-1))];
		}
	}
}

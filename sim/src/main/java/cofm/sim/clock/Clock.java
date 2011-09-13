package cofm.sim.clock;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import cofm.sim.pool.Pool;

public class Clock {

	Logger logger = Logger.getLogger(Clock.class);
	
	public static final int DELAY = 2000;  // 2s
	
	private Pool pool;
	private Timer timer;
	private int turn;
	
	public Clock(Pool pool) {
		this.pool = pool;
		timer = new Timer();
		turn = 1;
	}
	
	public void start() {
		
		timer.schedule(new TimerTask(){

			public void run() {
				tick();
			}
			
		}, DELAY);
	}
	
	public void stop() {
		timer.cancel();
	}
	
	private void tick() {
		logger.info("=== TURN " + turn + " ===");
		turn++;
		pool.evolve();
		logger.info(pool.toString());
		if (!pool.endAfterLastEvolve()) {
			start();
		} else {
			stop();
			logger.info("=== " + pool.getEndCondition().toString() + " ===");
		}
	}
}

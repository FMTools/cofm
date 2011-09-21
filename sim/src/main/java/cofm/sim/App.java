package cofm.sim;

import cofm.sim.clock.Clock;
import cofm.sim.pool.Pool;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String CFG_FILE = "./target/classes/sim";
	
    public static void main( String[] args )
    {
        SimConfigReader reader = new SimConfigReader();      
        Pool pool = reader.initEnvironment(CFG_FILE);
        
        Clock clock = new Clock(pool);
//        clock.start();
        clock.startWithoutDelay();
    }
}

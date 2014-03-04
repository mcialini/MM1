/*************************************************************************
 *  M/M/1 Controller, by Matt Cialini on 2/20/2014
 * 
 * 	A Controller class used to simulate the behavior of an M/M/1 system.
 *
 *************************************************************************/
package mm1;

import java.util.*;
import java.util.Random;
import java.io.*;

public class Controller {
	
    public static final int BIRTH = 0; // represents arrival
    public static final int DEATH = 1; // represents departure
       
    
    /* CLASS VARIABLES */
    LinkedList<Event> schedule;     // storing the queue for the system
    double clock;					// current time of sitslation		
    double endTime;					// end time of sitslation
    double nextArrival;				// the time of the next scheduled arrival to the system
    double nextDeparture;			// the time of the next scheduled departure from the system
    double lambda;					// the rate of arrivals to the system
    double ts;						// the mean service time
    ArrayList<Double> checkpoints;	// list of monitoring checkpoint times
    int numChecks;					// number of checkpoints 
    
    double Tq,Tw,Ts;
    int w, q;
    int requests, serviced;
    
    
    /* INITIALIZE A CONTROLLER OBJECT */
    public Controller(double lambda, double ts, double endTime) {
        this.lambda = lambda;
        this.ts = ts;
        this.endTime = endTime;
        
        /* Pre-generate a list of monitoring events for ease */
        checkpoints = new ArrayList<Double>();      
        double n = 0;
        while (n < 2*endTime) {
        	numChecks++;
        	n += exponential(lambda);
        	checkpoints.add(n);
        }
        
        
        clock = 0;
        schedule = new LinkedList<Event>();
        nextArrival = exponential(lambda);
        nextDeparture = Double.POSITIVE_INFINITY;  // no departures initially scheduled
        
        Tq = 0; Tw = 0; Ts = 0;
        w= 0; q = 0;
        requests = 0; serviced = 0;
    }
  
    /* Determines whether to queue the object up or begin serving it */
    public void birthHandler(double time) {
        if (schedule.isEmpty()) { // if the queue is empty, schedule a departure
            scheduleDeath(time);
        } 
        else { // queue isn't empty, schedule a birth
            schedule.add(new Event(time,BIRTH));
        }
        nextArrival += exponential(lambda); // arrange the next arrival
    }
    
    
    /* Removes the death event from the beginning of the queue that has been serviced
     * and determine if there is another object to service next
     */
    public void deathHandler() {
    	schedule.remove(); // pop the completed request out of the queue 
    	if (!schedule.isEmpty()) { // if there is a pending request, modify its contents to be a death event 
    		Event next = schedule.remove();
    		scheduleDeath(next.getTime());     		        		
    	} else{ 
    		nextDeparture = Double.POSITIVE_INFINITY; // no pending requests, no pending deaths
    		nextArrival += exponential(lambda);
    	}
    }
    
    /* Convenient function to plan out the death of an object
     * and also collect some statistics about its time in the 
     * system.
     */
    public void scheduleDeath(double arrivalTime) {
    	nextDeparture = clock + exponential(1/ts);
        schedule.addFirst(new Event(nextDeparture,DEATH));
        serviced++;
        Tq += (nextDeparture - arrivalTime);
        Tw += (clock - arrivalTime);
        Ts += (nextDeparture - clock);
                
    }
    
    /* Observes the queue at exponentially random intervals and
     * notes w and q.
     */
    public void monitorHandler(PrintWriter out) {
    	int cur_q = schedule.size();
    	int cur_w = (cur_q > 0) ? (schedule.size() - 1) : 0;
    	w += cur_w;
    	q += cur_q;
    	checkpoints.remove(0);
    	out.println("checkpoint: " + clock);
    	out.println("\tnum waiting (w): " + cur_w);
    	
    }

    /* HELPER METHODS */
    public static double exponential(double lambda) {
        Random r = new Random();
        double x = Math.log(1-r.nextDouble())/(-lambda);
        return x;
    }
    

    /* MAIN SIMULATION FUNCTION */
    public void run(PrintWriter out) {
    	while (clock < 2*endTime) {
    		if (checkpoints.get(0) < nextArrival && checkpoints.get(0) < nextDeparture) {
    			clock = checkpoints.get(0);
        		out.println("\tmonitoring at " + clock);
    			monitorHandler(out); 			
    		}
    		else if (nextArrival <= nextDeparture) { // an object has arrived to the system
            	clock = nextArrival;
        		out.println("\tarrival at " + clock);
            	birthHandler(nextArrival); 
            	requests++;
            }
            else {	// element has been serviced, remove it from queue
            	clock = nextDeparture;
        		out.println("\tdeparture at " + clock);
            	deathHandler();
            }           
    	}
    	
    	printStats(out);
    	out.close();
    }
    
    public void printStats(PrintWriter out) {
    	out.println("\nSTATISTICS OF RUN");
    	out.println("w = " + w/numChecks);
    	out.println("q = " + q/numChecks);
    	out.println("Tw = " + Tw/requests);
    	out.println("Tq = " + Tq/serviced);
    	out.println("Ts = " + Ts/serviced);

    	System.out.println("STATISTICS OF RUN");
    	System.out.println("requests: " + requests);
    	System.out.println("w = " + w/numChecks);
    	System.out.println("q = " + q/numChecks);
    	System.out.println("Tw = " + Tw/requests);
    	System.out.println("Tq = " + Tq/serviced);
    	System.out.println("Ts = " + Ts/serviced);
    }
}
This is a simple M/M/1 queue simulator consisting of three classes--Simulator.java, which runs based on hard-coded inputs, Event.java, which is a wrapper class for any event passing through the system, and Controller.java, which does all of the heavy lifting.

Given an arrival rate lambda, a mean service time Ts, and a run time, the controller will generate arrival events to the system based on a Poisson process--meaning that each arrival time is set as the previous arrival time plus some exponentially distributed random variable with input lambda. When the server is not busy, it changes the birth event in the front of the queue into a death event, with a departure time set at the current time + some exponential centered at 1/Ts, or mu. Additionally, a monitoring event which gathers statistics about the system will be called based on an exponential distribution with some input rate.

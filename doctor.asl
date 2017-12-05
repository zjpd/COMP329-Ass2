/*
 * Doctor agent first begin to initiate the search action 
 * and receive the message from the scout agent.
 * 
 * Doctor agent also should present the relevant message on the screen.
 * 
 * Victims are subjected to:
 * 	Red 	-- 	critical
 * 	Blue 	-- 	serious
 * 	Green 	--	 minor
 * 
 * Doctor should tell scout where to find the victim and whether it is the highest priority victim.
 */

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

/* Print the message and tell scout to get the accurate location of itself */
+!start : true 
	<- 	.print("Doctor start!!!");
		.send(scout, achieve, get_location).

/* Receiving from scout that a victim has been found */
+receiveVictim(V, X, Y) 
	: V<4					//Color ID of the victim, 0-red, 1-blue, 2-green, 3-no victim
	<- 	if(V==0) {.print(A, " found the critical victim at the point (",X,",",Y,")")};
		if(V==1) {.print(A, " found the serious victim at the point (",X,",",Y,")")};
		if(V==2) {.print(A, " found the minor victim at the point (",X,",",Y,")")};
		if(V==3) {.print(A, " did not found the victim at the point (",X,",",Y,")")};
		!helpVictim.

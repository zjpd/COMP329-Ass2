/*
 * Communicate with the doctor agent to get the order and communicate with the robot via socket.
 * 
 */

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start 
	: 	true 
	<- 	.print("Scout Ready!").

+!get_location[source(A)]
	: 	true
	<-	.print(A, "Receiving from ",A, " to get the location on the map!");
		!start_location.

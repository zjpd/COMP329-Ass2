import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Main {
	
	public static GraphicsLCD lcd = LocalEV3.get().getGraphicsLCD();
	
	public static void main(String args[]) {
		PilotControl pilot = new PilotControl();
		lcd.setFont(Font.getSmallFont());
		
		/* Wait for button press and begin running behaviours */
		Behavior stop = new stopRobot();
		
		lcd.drawString("Press any button to start", 0, 0, 0);
		Button.waitForAnyPress();
		Behavior mainBehavior = new ActionImpl(pilot);
		Behavior[] bArray = {stop, mainBehavior};
		Arbitrator arbitrator = new Arbitrator(bArray);
		arbitrator.go();
		
		pilot.closeRobot();
		Button.waitForAnyPress();
	}
	
}

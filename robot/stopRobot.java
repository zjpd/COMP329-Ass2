import lejos.robotics.subsumption.Behavior;
import lejos.hardware.Button;
public class stopRobot implements Behavior {

	public stopRobot(){}
	
	//this class takes control when escape button is pressed. helps to exit the program swiftly
	public boolean takeControl() {
		if(Button.ESCAPE.isDown()) {
			System.exit(0);
			return true;
		}else {
			return false;
		}
	}

	public void action() {}
	
	public void suppress() {}	
	
}

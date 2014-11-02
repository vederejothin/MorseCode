import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.SensorChangeEvent;
import com.phidgets.event.SensorChangeListener;

class MorseCode implements SensorChangeListener {
	public long start = 0, stop = 0, counter = 0; // Class variables to hold the time duration of each button press and between simultaneous button press.
	public String myChar = "", myString = ""; // Class variables to hold the individual letters and the complete sentence
	public Map<String, String> codeMap; // Map to hold the encoding for each signal value
	
	public static void main(String args[]) throws Exception {
		/*
		 * Instantiate the object and call the method to execute the the program
		 */
		MorseCode mc = new MorseCode();
		mc.myCodeGenerator();
	}
	
	public void myCodeGenerator() throws PhidgetException {
		// Store the encoded value and the corresponding decoded value of the morse code characters in the codeMap
		codeMap = new HashMap<String, String>();
		codeMap.put(".-", "A");
        codeMap.put("-...", "B");
        codeMap.put("-.-.", "C");
        codeMap.put("-..", "D");
        codeMap.put(".", "E");
        codeMap.put("..-.", "F");
        codeMap.put("--.", "G");
        codeMap.put("....", "H");
        codeMap.put("..", "I");
        codeMap.put(".---", "J");
        codeMap.put("-.-", "K");
        codeMap.put(".-..", "L");
        codeMap.put("--", "M");
        codeMap.put("-.", "N");
        codeMap.put("---", "O");
        codeMap.put(".--.", "P");
        codeMap.put("--.-", "Q");
        codeMap.put(".-.", "R");
        codeMap.put("...", "S");
        codeMap.put("-", "T");
        codeMap.put("..-", "U");
        codeMap.put("...-", "V");
        codeMap.put(".--", "W");
        codeMap.put("-..-", "X");
        codeMap.put("-.--", "Y");
        codeMap.put("--..", "Z");
		codeMap.put(".----", "1");
		codeMap.put("..---", "2");
		codeMap.put("...--", "3");
		codeMap.put("....-", "4");
		codeMap.put(".....", "5");
		codeMap.put("-....", "6");
		codeMap.put("--...", "7");
		codeMap.put("---..", "8");
		codeMap.put("----.", "9");
		codeMap.put("-----", "0");
		codeMap.put("...---...", "SOS");
		codeMap.put("_", " ");
		
		// Initialize the phidget
		InterfaceKitPhidget device = new InterfaceKitPhidget();
		device.openAny();
		device.waitForAttachment();	
		
		// Add a sensor change listener to this phidget. The listener is the current instance of the class
		device.addSensorChangeListener(this);
		
		// Initialize scanner and wait for input from user
		Scanner inp = new Scanner(System.in);
		System.out.println("\nStart your Morse Code input.....\n\nPress the sensor for a short time (not more than 0.3 seconds) to represent a \".\"\n\nPress the sensor for a moderate amount of time (0.3 to 1 second) to represent a \"-\"\n\nPress the sensor for a long time (1 to 2 seconds) to represent a blank space\n\nAt the end of every encoded word or blank space, wait for 3 seconds.\n\nAfter you are done with your input, please wait 5 seconds and press the sensor button once to view the decoded string.");
		inp.nextInt();
		device.close();
		inp.close();
	}
	
	/*
	 * Method to be called every time the sensor value is changed
	 */
	public void sensorChanged(SensorChangeEvent ae) {
		/*
		 *  If it has been 3 seconds since the last change in sensed value, get decoded character of the input 
		 *  from the codeMap and reset all class variables except myString 
		 */
		if(counter != 0 && System.currentTimeMillis() - counter > 3000) {
			String mapValue = codeMap.get(myChar);
			myString += mapValue != null ? codeMap.get(myChar) : "";
			
			/* 
			 * If it has been 5 seconds since the last change in sensed value, consider that the user has ceased giving input, 
			 * display the decoded string and reset the string for next input
			 */
			if(System.currentTimeMillis() - counter > 5000) {
				System.out.println(myString);
				myString = "";
			}
			
			// Reset all class variables to start with new character
			myChar = "";
			start = 0;
			stop = 0;			
			counter = 0;
		} else {
			// If the start time is 0, consider this as the first input
			if(start == 0) {
				// The threshold for sensed value to be considered is 20. If the value is greater, consider it as the input and start keeping track of time
				if(ae.getValue() >= 20) {
					start = System.currentTimeMillis();
				}
			} else {
				// If the start time is not 0 and stop time is 0 and the sensor value goes below the threshold, consider that the user released the sensor.
				if(start != 0 && stop == 0 && ae.getValue() < 20) {
					stop = System.currentTimeMillis();
					counter = stop; // Keep track of the stop time to decide if the input should be decoded and displayed
					
					/*
					 *  1 unit if the user input lasted less than 0.3 seconds
					 *  3 units if the user input lasted more than 0.3 seconds but less than 1 second
					 *  A blank space if the user input lasted more than 1 second but less than 2 seconds
					 */
					if(stop - start > 0 && stop - start <= 300) {
						myChar += ".";
					} else if(stop - start > 300 && stop - start <= 1000) {
						myChar += "-";
					} else if(stop - start > 1000 && stop - start <= 2000) {
						myChar += "_";
					}
					
					// Reset the start and stop times for the user to continue with input
					start = 0;
					stop = 0;
				}
			}
		}
	}
}
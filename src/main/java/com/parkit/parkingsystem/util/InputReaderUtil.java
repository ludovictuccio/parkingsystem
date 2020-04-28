package com.parkit.parkingsystem.util;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputReaderUtil {

    private Scanner scan = new Scanner(System.in);
    private static final Logger logger = LogManager.getLogger("InputReaderUtil");
    private static final String ERROR_MESSAGE = "Error while reading user input from Shell";

    // Setter for the InputStream Scanner used for unit tests
    public void setScan(Scanner scan) {
	this.scan = scan;
    }

    // The entry must be between 1 and 3
    public int readSelection() {
	try {
	    int input = Integer.parseInt(scan.nextLine());

	    if (input > 0 && input < 4) {
		return input;
	    } else {
		logger.error(ERROR_MESSAGE);
		return -1;
	    }
	} catch (IllegalArgumentException e) {
	    logger.error(ERROR_MESSAGE, e);
	    logger.error("Error reading input. Please enter valid number for proceeding further");
	    return -1;
	}
    }

    // Registration must be between 4 and 7 characters
    public String readVehicleRegistrationNumber() {
	try {
	    String vehicleRegNumber = scan.nextLine();

	    if (vehicleRegNumber == null || vehicleRegNumber.trim().length() < 4 || vehicleRegNumber.trim().length() > 7
		    || !vehicleRegNumber.matches("^[0-9a-zA-Z]*$")) {
		throw new IllegalArgumentException("Invalid input provided");
	    }
	    return vehicleRegNumber;
	} catch (IllegalArgumentException e) {
	    logger.error(ERROR_MESSAGE, e);
	    logger.error("Error reading input. Please enter a valid string for vehicle registration number");
	    throw e;
	}
    }
}
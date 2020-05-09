package com.parkit.parkingsystem.util;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to read int keyboard inputs, including the choice of
 * options in the main menu and the vehicle registration numbers.
 * 
 * @author Ludovic Tuccio
 */
public class InputReaderUtil {
    /**
     * A Scanner initialization in order to read the inputs users.
     */
    private Scanner scan = new Scanner((System.in), "UTF-8");
    /**
     * InputReaderUtil logger.
     */
    private static final Logger logger = LogManager.getLogger("InputReaderUtil");
    /**
     * Error message to display in catch blocks.
     */
    private static final String ERROR_MESSAGE = "Error while reading user input from Shell.";

    /**
     * Setter of the inputReaderUtil scanner, used as InputStream Scanner for unit
     * tests in InputReaderUtilTest.class.
     * 
     * @param scan InputReaderUtil Scanner
     */
    public void setScan(Scanner scan) {
	this.scan = scan;
    }

    /**
     * This method is used to read int keyboard inputs in main menu. The entry must
     * be 1, 2 or 3. Other input is incorrect selection.
     *
     * @return an int 1,2 or 3 for valid input or -1 if invalid input
     */
    public int readSelection() {
	try {
	    int input = Integer.parseInt(scan.nextLine());

	    if (input > 0 && input < 4) {
		return input;
	    } else {
		return -1;
	    }
	} catch (IllegalArgumentException e) {
	    logger.error(ERROR_MESSAGE + "\r\nPlease enter valid number for proceeding further");
	    return -1;
	}
    }

    /**
     * This method is used to read vehicle registration number inputs. The entry
     * must be between 4 and 7 characters. Other input is incorrect selection.
     *
     * @return String, the vehicle registration number or IllegalArgumentException
     */
    public String readVehicleRegistrationNumber() {
	try {
	    String vehicleRegNumber = scan.nextLine();

	    if (vehicleRegNumber == null || vehicleRegNumber.trim().length() < 4 || vehicleRegNumber.trim().length() > 7
		    || !vehicleRegNumber.matches("^[0-9a-zA-Z]*$")) {
		throw new IllegalArgumentException("Invalid input provided");
	    }
	    return vehicleRegNumber;
	} catch (IllegalArgumentException e) {
	    logger.error(ERROR_MESSAGE + "\r\nPlease enter a valid string for vehicle registration number");
	    throw e;
	}
    }
}
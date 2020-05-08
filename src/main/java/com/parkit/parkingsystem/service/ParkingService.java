package com.parkit.parkingsystem.service;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * This class is used to manage parking entry and exit process.
 * 
 * @author Ludovic Tuccio
 */
public class ParkingService {
    /**
     * Create a FareCalculatorService object.
     */
    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();
    /**
     * InputReaderUtil object.
     */
    private InputReaderUtil inputReaderUtil;
    /**
     * ParkingSpotDAO object.
     */
    private ParkingSpotDAO parkingSpotDAO;
    /**
     * TicketDAO object.
     */
    private TicketDAO ticketDAO;
    /**
     * ParkingService logger.
     */
    private static final Logger logger = LogManager.getLogger("ParkingService");
    /**
     * Formats the time for a careful display
     */
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    /**
     * Defines visits threshold to be a regular user
     */
    private static final int VISITS_THRESHOLD_REGULAR_USER = 1;

    /**
     * Class constructor.
     * 
     * @param inputReaderUtil
     * @param parkingSpotDAO
     * @param ticketDAO
     */
    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
	this.inputReaderUtil = inputReaderUtil;
	this.parkingSpotDAO = parkingSpotDAO;
	this.ticketDAO = ticketDAO;
    }

    /**
     * This method manages incoming vehicle.
     */
    public void processIncomingVehicle() {
	try {
	    ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
	    if (parkingSpot != null && parkingSpot.getId() > 0) {
		String vehicleRegNumber = getVehicleRegNumber();

		// Check if a vehicle already has an entry ticket and check if his out-ticket is
		// null to know if he's already parked
		Ticket parkedVehicle = ticketDAO.getTicket(vehicleRegNumber);
		// While a vehicle has already parked and didn't get out-ticket
		while (parkedVehicle != null && parkedVehicle.getOutTime() == null) {
		    logger.error(
			    "INVALID ENTRY \r\nThis registration is already occupied at a parking space. \r\nEnter a valid registration (or other character to exit and return to menu).");
		    vehicleRegNumber = getVehicleRegNumber();
		    parkedVehicle = ticketDAO.getTicket(vehicleRegNumber);
		}
		parkingSpot.setAvailable(false);
		parkingSpotDAO.updateParking(parkingSpot);
		LocalDateTime inTime = LocalDateTime.now();
		String inTimeFormatter = inTime.format(formatter);
		Ticket ticket = new Ticket();
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setPrice(0);
		ticket.setInTime(inTime);
		ticket.setOutTime(null);
		ticketDAO.saveTicket(ticket);
		int numberVisitsUser = ticketDAO.checkNumberVisitsUser(ticket.getVehicleRegNumber());

		// Check if a user is considered regular with a fixed number of visits by
		// checking its total number of tickets
		if (numberVisitsUser >= VISITS_THRESHOLD_REGULAR_USER) {
		    logger.info("As regular user, you will benefit from a {}% discount on your final fare", 5);
		}

		logger.info("Generated Ticket and saved in DB");
		logger.info("Please park your vehicle in spot number: {}", parkingSpot.getId());
		logger.info("Recorded in-time for vehicle number: {} is: {}", vehicleRegNumber, inTimeFormatter);
	    } else {
		logger.error("Sorry the parking is full. No more places are available.");
	    }
	} catch (Exception e) {
	    logger.error("Unable to process incoming vehicle");
	}
    }

    /**
     * This method calls readVehicleRegistrationNumber method from
     * InputReaderUtil.class, in order that the user enters his valid vehicle
     * registration number
     * 
     * @return String, the vehicle registration number
     */
    private String getVehicleRegNumber() {
	logger.info("Please type the vehicle registration number and press enter key");
	return inputReaderUtil.readVehicleRegistrationNumber();
    }

    /**
     * This method check if there is any available parking spot for incoming users.
     * 
     * @return parkingSpot, the available parking spot
     */
    public ParkingSpot getNextParkingNumberIfAvailable() {
	int parkingNumber = 0;
	ParkingSpot parkingSpot = null;
	try {
	    ParkingType parkingType = getVehicleType();
	    parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
	    if (parkingNumber > 0) {
		parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
	    } else {
		throw new SQLException("Error fetching parking number from DB. Parking slots might be full");
	    }
	} catch (IllegalArgumentException ie) {
	    logger.error("Error parsing user input for type of vehicle", ie);
	} catch (Exception e) {
	    logger.error("Error fetching next available parking slot", e);
	}
	return parkingSpot;
    }

    /**
     * This method asks the user what type of vehicle he wants to park (car or bike)
     * during incoming process.
     * 
     * @return enumeration ParkingType, the selected vehicle type
     * 
     */
    private ParkingType getVehicleType() {
	logger.info("Please select vehicle type from menu");
	logger.info("1 CAR");
	logger.info("2 BIKE");
	int input = inputReaderUtil.readSelection();
	switch (input) {
	case 1: {
	    return ParkingType.CAR;
	}
	case 2: {
	    return ParkingType.BIKE;
	}
	default: {
	    logger.error("Incorrect input provided");
	    throw new IllegalArgumentException("Entered input is invalid");
	}
	}
    }

    /**
     * This method manages exiting vehicle.
     */
    public void processExitingVehicle() {
	try {
	    String vehicleRegNumber = getVehicleRegNumber();
	    Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

	    if (ticket != null) {
		// For to not recover a vehicle left with a ticket already paid
		while (ticket != null && ticket.getOutTime() != null) {
		    logger.error(
			    "INVALID ENTRY \r\nThis registration has already exited the parking.\r\nEnter a valid registration (or other character to exit and return to menu).");
		    vehicleRegNumber = getVehicleRegNumber();
		    ticket = ticketDAO.getTicket(vehicleRegNumber);
		}
	    }

	    LocalDateTime inTime = ticket.getInTime();
	    String inTimeFormatter = inTime.format(formatter);
	    LocalDateTime outTime = LocalDateTime.now();
	    String outTimeFormatter = outTime.format(formatter);
	    ticket.setOutTime(outTime);
	    int numberVisitsUser = ticketDAO.checkNumberVisitsUser(ticket.getVehicleRegNumber());

	    if (ticket.getOutTime().isBefore(ticket.getInTime().plusMinutes(30))) {
		fareCalculatorService.calculateFreeFareForLessThanThirtyMinutes(ticket);

	    } else if (numberVisitsUser >= VISITS_THRESHOLD_REGULAR_USER) {
		fareCalculatorService.calculateFareForRegularClient(ticket);
		logger.info("As regular user you benefit from a {}% discount", 5);

	    } else {
		fareCalculatorService.calculateFare(ticket);
	    }

	    if (ticketDAO.updateTicket(ticket)) {
		ParkingSpot parkingSpot = ticket.getParkingSpot();
		parkingSpot.setAvailable(true);
		parkingSpotDAO.updateParking(parkingSpot);
		DecimalFormat arroundPrice = new DecimalFormat("#0.00 €");
		String finalTicketPrice = arroundPrice.format(ticket.getPrice());
		logger.info("Recorded in-time : {}", inTimeFormatter);
		logger.info("Please pay the parking fare: {}", finalTicketPrice);
		logger.info("Recorded out-time for vehicle number: {} is: {}", ticket.getVehicleRegNumber(),
			outTimeFormatter);
	    } else {
		logger.error("Unable to update ticket information. Error occurred");
	    }
	} catch (Exception e) {
	    logger.error("Unable to process exiting vehicle");
	}
    }
}
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

public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");
    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();
    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private TicketDAO ticketDAO;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
	this.inputReaderUtil = inputReaderUtil;
	this.parkingSpotDAO = parkingSpotDAO;
	this.ticketDAO = ticketDAO;
    }

    public void processIncomingVehicle() {
	try {
	    ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
	    if (parkingSpot != null && parkingSpot.getId() > 0) {
		String vehicleRegNumber = getVehicleRegNumber();

		// Verify if a vehicle is already parked
		while (ticketDAO.checkIfVehicleIsAlreadyParked(vehicleRegNumber) == 1) {
		    logger.error(
			    "INVALID ENTRY. This registration is already occupied at a parking space. Please enter a valid registration.");
		    vehicleRegNumber = getVehicleRegNumber();
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

		if (ticketDAO.getTotalNumberOfTicketsIssuedPerVehicle(ticket.getVehicleRegNumber()) > 0) {
		    logger.info("As regular user, you will benefit from a {}% discount on your final fare", 5);
		}
		logger.info("Generated Ticket and saved in DB");
		logger.info("Please park your vehicle in spot number: {}", parkingSpot.getId());
		logger.info("Recorded in-time for vehicle number: {} is: {}", vehicleRegNumber, inTimeFormatter);
	    } else {
		logger.error("Sorry the parking is full. No more places are available.");
	    }
	} catch (Exception e) {
	    logger.error("Unable to process incoming vehicle", e);
	}
    }

    private String getVehicleRegNumber() {
	logger.info("Please type the vehicle registration number and press enter key");
	return inputReaderUtil.readVehicleRegistrationNumber();
    }

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

    public void processExitingVehicle() {
	try {
	    String vehicleRegNumber = getVehicleRegNumber();
	    Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
	    LocalDateTime outTime = LocalDateTime.now();
	    String outTimeFormatter = outTime.format(formatter);
	    ticket.setOutTime(outTime);
	    int totalNomberOfVehicleTickets = ticketDAO
		    .getTotalNumberOfTicketsIssuedPerVehicle(ticket.getVehicleRegNumber());

	    if (totalNomberOfVehicleTickets > 0
		    && ticket.getOutTime().isAfter(ticket.getInTime().plusMinutes(29).plusSeconds(59))) {
		fareCalculatorService.calculateFareForRegularClient(ticket);
		logger.info("As regular user you benefit from a {}% discount", 5);
	    } else if (ticket.getOutTime().isBefore(ticket.getInTime().plusMinutes(30))) {
		fareCalculatorService.calculateFreeFareForLessThanThirtyMinutes(ticket);
	    } else {
		fareCalculatorService.calculateFare(ticket);
	    }

	    if (ticketDAO.updateTicket(ticket)) {
		ParkingSpot parkingSpot = ticket.getParkingSpot();
		parkingSpot.setAvailable(true);
		parkingSpotDAO.updateParking(parkingSpot);
		DecimalFormat arroundPrice = new DecimalFormat("#0.00€");
		logger.info("Please pay the parking fare: {}", arroundPrice.format(ticket.getPrice()));
		logger.info("Recorded out-time for vehicle number:{} is:{}", ticket.getVehicleRegNumber(),
			outTimeFormatter);
	    } else {
		logger.error("Unable to update ticket information. Error occurred");
	    }
	} catch (Exception e) {
	    logger.error("Unable to process exiting vehicle", e);
	}
    }
}
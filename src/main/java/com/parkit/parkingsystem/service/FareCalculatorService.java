package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.LocalDateTime;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * This class calculates the tickets price during exiting process
 * 
 * @author Ludovic Tuccio
 */
public class FareCalculatorService {

    /**
     * This method check errors during the exit parking process
     * 
     * @param ticket check NullPointerException
     */
    public void checkErrorsWhenVehiculeIsExitingParking(Ticket ticket) {

	if ((ticket.getOutTime() == null)) {
	    throw new NullPointerException("Out time provided is incorrect null pointer exception");
	}
	if (ticket.getOutTime().compareTo(ticket.getInTime()) <= 0) {
	    throw new NullPointerException("Out time provided is incorrect:" + ticket.getOutTime().toString());
	}
    }

    /**
     * This method calculate ticket fare
     * 
     * @param ticket fare
     */
    public void calculateFare(Ticket ticket, boolean isRegularUser) {
	double vehicleRatePerHour = 0;
	switch (ticket.getParkingSpot().getParkingType()) {
	case CAR: {
	    vehicleRatePerHour = Fare.CAR_RATE_PER_HOUR;
	    break;
	}
	case BIKE: {
	    vehicleRatePerHour = Fare.BIKE_RATE_PER_HOUR;
	    break;
	}
	default:
	    throw new IllegalArgumentException("Unkown Parking Type");
	}

	LocalDateTime inHour = ticket.getInTime();
	LocalDateTime outHour = ticket.getOutTime();
	Duration durationOfTicket = Duration.between(inHour, outHour);
	LocalDateTime thirtyMinutes = inHour.plusMinutes(30); // Less than 30 minutes is free
	LocalDateTime threeQuartersOfAnHour = inHour.plusMinutes(45); // 3/4 hour threshold
	LocalDateTime hourThreshold = inHour.plusHours(1);// Hours thresholds after ticket receipt

	if (outHour.isBefore(thirtyMinutes)) {
	    ticket.setPrice(0); // Less than 30 minutes

	} else if ((outHour.isBefore(threeQuartersOfAnHour) || outHour.isEqual(threeQuartersOfAnHour))
		&& (outHour.isAfter(thirtyMinutes) || outHour.isEqual(thirtyMinutes))) {
	    ticket.setPrice(0.75 * vehicleRatePerHour); // Between 30 minutes and 3/4 of an hour

	} else if ((outHour.isBefore(hourThreshold) || outHour.isEqual(hourThreshold))
		&& outHour.isAfter(threeQuartersOfAnHour)) {
	    ticket.setPrice(vehicleRatePerHour); // Between 3/4 of an hour and 1 hour

	} else if (outHour.isAfter(hourThreshold)) {
	    ticket.setPrice(durationOfTicket.toHours() * vehicleRatePerHour); // Parking time more than an hour

	} else {
	    throw new IllegalArgumentException(
		    "An error was occured. Please try again in a few moments or contact our technical support.");
	}

	if (isRegularUser) {
	    ticket.setPrice(0.95 * ticket.getPrice()); // Set the ticket price with a 5% discount for regular users
	}
    }
}
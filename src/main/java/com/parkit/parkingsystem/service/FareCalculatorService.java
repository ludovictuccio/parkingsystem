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
    public void calculateFare(Ticket ticket) {

	checkErrorsWhenVehiculeIsExitingParking(ticket);
	LocalDateTime inHour = ticket.getInTime();
	LocalDateTime outHour = ticket.getOutTime();
	Duration durationOfTicket = Duration.between(inHour, outHour);
	LocalDateTime thirtyMinutes = inHour.plusMinutes(30); // Less than 30 minutes is free
	LocalDateTime threeQuartersOfAnHour = inHour.plusMinutes(45); // 3/4 hour threshold
	LocalDateTime hourThreshold = inHour.plusHours(1);// Hours thresholds after ticket receipt

	// (outHour < thirtyMinutes)
	boolean testLessThanThirtyMinutes = outHour.isBefore(thirtyMinutes);

	// (outHour <= threeQuartersOfAnHour) && (outHour >= thirtyMinutes)
	boolean testBetweenThirtyMinutesAndThreeQuartersOfAnHour = (outHour.isBefore(threeQuartersOfAnHour)
		|| outHour.isEqual(threeQuartersOfAnHour))
		&& (outHour.isAfter(thirtyMinutes) || outHour.isEqual(thirtyMinutes));

	// (outHour <= oneHour) && (outHour > threeQuartersOfAnHour)
	boolean testBetweenThreeQuartersOfAnHourAndOneHour = (outHour.isBefore(hourThreshold)
		|| outHour.isEqual(hourThreshold)) && outHour.isAfter(threeQuartersOfAnHour);

	// (outHour > hourThreshold) - This boolean will test every hour parked
	boolean testMoreThanAnHour = outHour.isAfter(hourThreshold);

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

	if (testLessThanThirtyMinutes) {
	    ticket.setPrice(0);

	} else if (testBetweenThirtyMinutesAndThreeQuartersOfAnHour) {
	    ticket.setPrice(0.75 * vehicleRatePerHour);

	} else if (testBetweenThreeQuartersOfAnHourAndOneHour) {
	    ticket.setPrice(vehicleRatePerHour);

	} else if (testMoreThanAnHour) {
	    ticket.setPrice(durationOfTicket.toHours() * vehicleRatePerHour);

	} else {
	    throw new IllegalArgumentException(
		    "An error was occured. Please try again in a few moments or contact our technical support.");
	}
    }

    /**
     * This method set at 0 the parking ticket fare for a period less than 30
     * minutes
     * 
     * @param ticket free fare for short period
     */
    public void calculateFreeFareForLessThanThirtyMinutes(Ticket ticket) {
	checkErrorsWhenVehiculeIsExitingParking(ticket);
	calculateFare(ticket);
	ticket.setPrice(0);
    }

    /**
     * This method set the ticket price with a 5% discount for regular users with a
     * predefined visit threshold
     * 
     * @param ticket fare for regular user
     */
    public void calculateFareForRegularClient(Ticket ticket) {
	checkErrorsWhenVehiculeIsExitingParking(ticket);
	calculateFare(ticket);
	ticket.setPrice(0.95 * ticket.getPrice());
    }
}
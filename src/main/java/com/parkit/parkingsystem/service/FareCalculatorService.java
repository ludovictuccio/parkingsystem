package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.LocalDateTime;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * This class calculates the price of tickets
 * 
 * @author Ludovic Tuccio
 */
public class FareCalculatorService {

    /**
     * This method check errors during the exit parking process
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
     */
    public void calculateFare(Ticket ticket) {

	checkErrorsWhenVehiculeIsExitingParking(ticket);
	LocalDateTime inHour = ticket.getInTime();
	LocalDateTime outHour = ticket.getOutTime();
	Duration durationOfTicket = Duration.between(inHour, outHour);
	LocalDateTime afterHour = inHour.plusHours(1);// Hours thresholds after receipt of the ticket
	LocalDateTime afterThreeQuartersOfHour = inHour.plusMinutes(45); // 3/4 hour threshold
	LocalDateTime thirtyMinutes = inHour.plusMinutes(30); // Less than 30 minutes is free
	// outHour <= afterHour && outHour > afterQuartersOfHour
	boolean testBetweenThreeQuartersOfAnHourAndHours = (outHour.isBefore(afterHour) || outHour.isEqual(afterHour))
		&& outHour.isAfter(afterThreeQuartersOfHour);
	// outHour <= afterQuartersOfHour
	boolean testLessThreeQuartersOfAnHour = outHour.isBefore(afterThreeQuartersOfHour)
		|| outHour.isEqual(afterThreeQuartersOfHour);
	// outHour > afterHour
	boolean testMoreHour = outHour.isAfter(afterHour);
	// outHour < thirtyMinutes
	boolean testLessThirtyMinutes = outHour.isBefore(thirtyMinutes);

	switch (ticket.getParkingSpot().getParkingType()) {
	case CAR: {

	    if (testBetweenThreeQuartersOfAnHourAndHours) {
		ticket.setPrice(Fare.CAR_RATE_PER_HOUR);

	    } else if (testLessThreeQuartersOfAnHour) {
		ticket.setPrice(0.75 * Fare.CAR_RATE_PER_HOUR);

	    } else if (testMoreHour) {
		ticket.setPrice(durationOfTicket.toHours() * Fare.CAR_RATE_PER_HOUR);

	    } else if (testLessThirtyMinutes) {
		ticket.setPrice(0);
	    }
	    break;
	}
	case BIKE: {

	    if (testBetweenThreeQuartersOfAnHourAndHours) {
		ticket.setPrice(Fare.BIKE_RATE_PER_HOUR);

	    } else if (testLessThreeQuartersOfAnHour) {
		ticket.setPrice(0.75 * Fare.BIKE_RATE_PER_HOUR);

	    } else if (testMoreHour) {
		ticket.setPrice(durationOfTicket.toHours() * Fare.BIKE_RATE_PER_HOUR);

	    } else if (testLessThirtyMinutes) {
		ticket.setPrice(0);
	    }
	    break;
	}
	default:
	    throw new IllegalArgumentException("Unkown Parking Type");
	}
    }

    /**
     * This method set at 0 the parking fare for a period less than 30 minutes
     */
    public void calculateFreeFareForLessThanThirtyMinutes(Ticket ticket) {
	checkErrorsWhenVehiculeIsExitingParking(ticket);
	calculateFare(ticket);
	ticket.setPrice(0);
    }

    /**
     * This method set the ticket price with a 5% discount for users who have
     * already come to the parking
     */
    public void calculateFareForRegularClient(Ticket ticket) {
	checkErrorsWhenVehiculeIsExitingParking(ticket);
	calculateFare(ticket);
	ticket.setPrice(0.95 * ticket.getPrice());
    }
}
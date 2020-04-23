package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.LocalDateTime;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void errorWhenVehiculeLeave(Ticket ticket) {

	if ((ticket.getOutTime() == null)) {
	    throw new NullPointerException("Out time provided is incorrect null pointer exception");
	}
	if (ticket.getOutTime().compareTo(ticket.getInTime()) < 0) {
	    throw new NullPointerException("Out time provided is incorrect:" + ticket.getOutTime().toString());
	}
    }

    public void calculateFareForRegularClient(Ticket ticket) {

	errorWhenVehiculeLeave(ticket);

	calculateFare(ticket);

	ticket.setPrice(0.95 * ticket.getPrice());

    }

    public void calculateFare(Ticket ticket) {

	errorWhenVehiculeLeave(ticket);

	LocalDateTime inHour = ticket.getInTime();
	LocalDateTime outHour = ticket.getOutTime();
	Duration durationOfTicket = Duration.between(inHour, outHour);

	LocalDateTime afterHour = inHour.plusHours(1);// Hours thresholds after receipt of the ticket
	LocalDateTime afterThreeQuartersOfHour = inHour.plusMinutes(45);

	// outHour <= afterHour && outHour > afterQuartersOfHour
	Boolean testBetweenThreeQuartersAndHours = (outHour.isBefore(afterHour) || outHour.isEqual(afterHour))
		&& outHour.isAfter(afterThreeQuartersOfHour);

	// outHour <= afterQuartersOfHour
	Boolean testLessThreeQuarters = outHour.isBefore(afterThreeQuartersOfHour)
		|| outHour.isEqual(afterThreeQuartersOfHour);

	// outHour > afterHour
	Boolean testMoreHour = outHour.isAfter(afterHour);

	switch (ticket.getParkingSpot().getParkingType()) {
	case CAR: {

	    if (testBetweenThreeQuartersAndHours) {
		ticket.setPrice(1 * Fare.CAR_RATE_PER_HOUR);

	    } else if (testLessThreeQuarters) {
		ticket.setPrice(0.75 * Fare.CAR_RATE_PER_HOUR);

	    } else if (testMoreHour) {
		ticket.setPrice(durationOfTicket.toHours() * Fare.CAR_RATE_PER_HOUR);
	    }
	    break;

	}
	case BIKE: {

	    if (testBetweenThreeQuartersAndHours) {
		ticket.setPrice(1 * Fare.BIKE_RATE_PER_HOUR);

	    } else if (testLessThreeQuarters) {
		ticket.setPrice(0.75 * Fare.BIKE_RATE_PER_HOUR);

	    } else if (testMoreHour) {
		ticket.setPrice(durationOfTicket.toHours() * Fare.BIKE_RATE_PER_HOUR);
	    }
	    break;
	}
	default:
	    throw new IllegalArgumentException("Unkown Parking Type");
	}
    }
}
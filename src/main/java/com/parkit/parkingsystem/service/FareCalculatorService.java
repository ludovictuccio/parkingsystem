package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.LocalDateTime;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void checkErrorsWhenVehiculeIsExitingParking(Ticket ticket) {

	if ((ticket.getOutTime() == null)) {
	    throw new NullPointerException("Out time provided is incorrect null pointer exception");
	}
	if (ticket.getOutTime().compareTo(ticket.getInTime()) < 0) {
	    throw new NullPointerException("Out time provided is incorrect:" + ticket.getOutTime().toString());
	}
    }

    public void calculateFare(Ticket ticket) {

	checkErrorsWhenVehiculeIsExitingParking(ticket);
	LocalDateTime inHour = ticket.getInTime();
	LocalDateTime outHour = ticket.getOutTime();
	Duration durationOfTicket = Duration.between(inHour, outHour);
	LocalDateTime afterHour = inHour.plusHours(1);// Hours thresholds after receipt of the ticket
	LocalDateTime afterThreeQuartersOfHour = inHour.plusMinutes(45); // Three-quarters of an hour threshold
	// outHour <= afterHour && outHour > afterQuartersOfHour
	boolean testBetweenThreeQuartersOfAnHourAndHours = (outHour.isBefore(afterHour) || outHour.isEqual(afterHour))
		&& outHour.isAfter(afterThreeQuartersOfHour);
	// outHour <= afterQuartersOfHour
	boolean testLessThreeQuartersOfAnHour = outHour.isBefore(afterThreeQuartersOfHour)
		|| outHour.isEqual(afterThreeQuartersOfHour);
	// outHour > afterHour
	boolean testMoreHour = outHour.isAfter(afterHour);

//	do {
//	if (testBetweenThreeQuartersOfAnHourAndHours) {
//	    ticket.setPrice(Fare.CAR_RATE_PER_HOUR);
//	    ticket.setPrice(Fare.BIKE_RATE_PER_HOUR);
//	} else if (testLessThreeQuartersOfAnHour) {
//	    ticket.setPrice(0.75 * Fare.CAR_RATE_PER_HOUR);
//	    ticket.setPrice(0.75 * Fare.BIKE_RATE_PER_HOUR);
//
//	} else if (testMoreHour) {
//	    ticket.setPrice(durationOfTicket.toHours() * Fare.CAR_RATE_PER_HOUR);
//	    ticket.setPrice(durationOfTicket.toHours() * Fare.BIKE_RATE_PER_HOUR);
//	}
//
//	}while(ticket.getParkingSpot().getParkingType() : CAR);

	switch (ticket.getParkingSpot().getParkingType()) {
	case CAR: {

	    if (testBetweenThreeQuartersOfAnHourAndHours) {
		ticket.setPrice(Fare.CAR_RATE_PER_HOUR);

	    } else if (testLessThreeQuartersOfAnHour) {
		ticket.setPrice(0.75 * Fare.CAR_RATE_PER_HOUR);

	    } else if (testMoreHour) {
		ticket.setPrice(durationOfTicket.toHours() * Fare.CAR_RATE_PER_HOUR);
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
	    }
	    break;
	}
	default:
	    throw new IllegalArgumentException("Unkown Parking Type");
	}
    }

    public void calculateFareForRegularClient(Ticket ticket) {

	checkErrorsWhenVehiculeIsExitingParking(ticket);
	calculateFare(ticket);
	ticket.setPrice(0.95 * ticket.getPrice());
    }
}
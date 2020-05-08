package com.parkit.parkingsystem.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;

/**
 * The goal of this test is to test the good functioning of the ParkingSpotDAO
 * with the DB. The established order is :
 * 
 * 1 - Car 2 - Car 3 - Car 4 - Bike 5 - Bike
 * 
 * This test should use the methods getNextAvailableSlot() & updateParking().
 * 
 * @author Ludovic Tuccio
 *
 */
public class ParkingSpotDAOTest {

    private ParkingSpotDAO parkingSpotDAO;

    @BeforeEach
    public void setUpPerTest() {
	parkingSpotDAO = new ParkingSpotDAO();
    }

    @Test
    @Tag("CAR")
    @DisplayName("CAR - All car parking slots available - Return slot number 1")
    public void givenEntryCar_whenAllCarParkingSlotsAreAvailable_thenReturnTheFirstSlot() {

	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
	parkingSpotDAO.updateParking(parkingSpot);
	int availableSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

	parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

	assertThat(availableSlot).isEqualTo(1);
    }

    @Test
    @Tag("CAR")
    @DisplayName("CAR - Car parking slots 1 & 2 unavailable - Return slot number 3")
    public void givenEntryCar_whenCarParkingSlotsOneAndTwoAreUnavailable_thenReturnSlotThree() {

	ParkingSpot parkingSpot1 = new ParkingSpot(1, ParkingType.CAR, false);
	parkingSpotDAO.updateParking(parkingSpot1);
	ParkingSpot parkingSpot2 = new ParkingSpot(2, ParkingType.CAR, false);
	parkingSpotDAO.updateParking(parkingSpot2);
	ParkingSpot parkingSpot3 = new ParkingSpot(3, ParkingType.CAR, true);
	parkingSpotDAO.updateParking(parkingSpot3);
	int availableSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

	parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

	assertThat(availableSlot).isEqualTo(3);
    }

    @Test
    @Tag("BIKE")
    @DisplayName("BIKE - All bike parking slots available - Return slot number 4")
    public void givenEntryBike_whenAllBikeParkingSlotsAreAvailable_thenReturnSlotFour() {

	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, true);
	parkingSpotDAO.updateParking(parkingSpot);
	int availableSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);

	parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);

	assertThat(availableSlot).isEqualTo(4);
    }

    @Test
    @Tag("NULL")
    @DisplayName("Null ParkingType - Return -1")
    public void givenNullParkingType_whenGetNextAvailableSlot_thenReturnMinusOne() {

	ParkingSpot parkingSpot = new ParkingSpot(1, null, true);
	parkingSpotDAO.updateParking(parkingSpot);
	int availableSlot = parkingSpotDAO.getNextAvailableSlot(null);

	parkingSpotDAO.getNextAvailableSlot(null);

	assertThat(availableSlot).isEqualTo(-1);
    }
}
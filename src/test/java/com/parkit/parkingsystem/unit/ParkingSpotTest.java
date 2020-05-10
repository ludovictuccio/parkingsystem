package com.parkit.parkingsystem.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotTest {

    private ParkingSpot parkingSpot;

    @Test
    @Tag("CAR")
    @DisplayName("CAR - parking slot number 3 available")
    public void givenEntryCar_whenParkingSlotThreeIsAvailable_thenReturnCorrectValues() {

	parkingSpot = new ParkingSpot(3, ParkingType.CAR, true);

	assertThat(parkingSpot.getId()).isEqualTo(3);
	assertThat(parkingSpot.getParkingType()).isEqualTo(ParkingType.CAR);
	assertThat(parkingSpot.isAvailable()).isTrue();
    }

    @Test
    @Tag("BIKE")
    @DisplayName("BIKE - parking slot number 2 unavailable")
    public void givenEntryBike_whenParkingSlotTwoIsUnavailable_thenReturnCorrectValues() {

	parkingSpot = new ParkingSpot(2, ParkingType.BIKE, false);

	assertThat(parkingSpot.getId()).isEqualTo(2);
	assertThat(parkingSpot.getParkingType()).isEqualTo(ParkingType.BIKE);
	assertThat(parkingSpot.isAvailable()).isFalse();
    }

    @Test
    @Tag("NULL")
    @DisplayName("Null ParkingType ")
    public void givenEntry_whenNullParkingType_thenReturnCorrectValues() {

	parkingSpot = new ParkingSpot(1, null, false);

	assertThat(parkingSpot.getParkingType()).isEqualTo(null);
	assertThat(parkingSpot.isAvailable()).isFalse();
    }
}
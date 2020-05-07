package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * This class permits the storage and retrieving values from the MySQL database
 * parking table.
 */
public class ParkingSpot {
    /**
     * The parking number.
     */
    private int number;
    /**
     * ParkingType that can be used by cars or bikes.
     */
    private ParkingType parkingType;
    /**
     * The parking availability.
     */
    private boolean isAvailable;

    /**
     * Class constructor.
     * 
     * @param number      the unique identifier of a parking spot
     * @param parkingType the parking type (car or bike)
     * @param isAvailable will define if a parking spot is available or not
     */
    public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
	this.number = number;
	this.parkingType = parkingType;
	this.isAvailable = isAvailable;
    }

    /**
     * Getter of parkingSpot number.
     * 
     * @return int number the id parkingSpot
     */
    public int getId() {
	return number;
    }

    /**
     * Setter of parkingSpot number.
     * 
     * @param number the id parkingSpot
     */
    public void setId(int number) {
	this.number = number;
    }

    /**
     * Getter of the parkingType.
     * 
     * @return a ParkingType, CAR or BIKE
     */
    public ParkingType getParkingType() {
	return parkingType;
    }

    /**
     * Setter of the parkingType.
     * 
     * @param parkingType, CAR or BIKE
     */
    public void setParkingType(ParkingType parkingType) {
	this.parkingType = parkingType;
    }

    /**
     * Getter of parkingSpot isAvailable.
     * 
     * @return boolean which will define if a parking spot is available or not
     */
    public boolean isAvailable() {
	return isAvailable;
    }

    /**
     * Setter of parkingSpot isAvailable.
     * 
     * @param available true if a parking spot is available or false if unavailable
     */
    public void setAvailable(boolean available) {
	isAvailable = available;
    }

    /**
     * Boolean equals method.
     * 
     * @param o an equals Object
     * @return boolean, true if number has the same value of the attribute number or
     *         false if not
     */
    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o == null || getClass() != o.getClass())
	    return false;
	ParkingSpot that = (ParkingSpot) o;
	return number == that.number;
    }

    /**
     * int hashCode method.
     * 
     * @return number hashCode
     */
    @Override
    public int hashCode() {
	return number;
    }
}

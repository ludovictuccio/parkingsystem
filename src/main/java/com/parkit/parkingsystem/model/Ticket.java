package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;

/**
 * This class is used to store ticket data.
 */
public class Ticket {
    /**
     * The unique ticket identifier.
     */
    private int id;
    /**
     * The parking spot number allocated to the vehicle.
     */
    private ParkingSpot parkingSpot;
    /**
     * The vehicle registration number.
     */
    private String vehicleRegNumber;
    /**
     * The price to pay during exiting process depending on parking time.
     */
    private double price;
    /**
     * The parking entry time.
     */
    private LocalDateTime inTime;
    /**
     * The parking exit time.
     */
    private LocalDateTime outTime;

    /**
     * Getter of id ticket.
     * 
     * @return int id ticket
     */
    public int getId() {
	return id;
    }

    /**
     * Setter of id ticket.
     * 
     * @param id, the id ticket
     */
    public void setId(int id) {
	this.id = id;
    }

    /**
     * Getter of parkingSpot ticket.
     * 
     * @return parkingSpot
     */
    public ParkingSpot getParkingSpot() {
	return parkingSpot;
    }

    /**
     * Setter of parkingSpot ticket.
     * 
     * @param parkingSpot
     */
    public void setParkingSpot(ParkingSpot parkingSpot) {
	this.parkingSpot = parkingSpot;
    }

    /**
     * Getter of vehicle registration number ticket.
     * 
     * @return String vehicleRegNumber, the vehicle registration number
     */
    public String getVehicleRegNumber() {
	return vehicleRegNumber;
    }

    /**
     * Setter of vehicle registration number ticket.
     * 
     * @param vehicleRegNumber, the vehicle registration number to set
     */
    public void setVehicleRegNumber(String vehicleRegNumber) {
	this.vehicleRegNumber = vehicleRegNumber;
    }

    /**
     * Getter of ticket price.
     * 
     * @return double price, the price to pay during exiting process
     */
    public double getPrice() {
	return Math.round(price * 100.0) / 100.0;
    }

    /**
     * Setter of ticket price.
     * 
     * @param price the price to set on ticket
     */
    public void setPrice(double price) {
	this.price = price;
    }

    /**
     * Getter of ticket inTime.
     * 
     * @return inTime the ticket entry time
     */
    public LocalDateTime getInTime() {
	return inTime;
    }

    /**
     * Setter of ticket inTime.
     * 
     * @param inTime, the LocalDateTime of ticket entry time
     */
    public void setInTime(LocalDateTime inTime) {
	this.inTime = inTime;
    }

    /**
     * Getter of ticket outTime.
     * 
     * @return outTime the ticket exit time
     */
    public LocalDateTime getOutTime() {
	return outTime;
    }

    /**
     * Setter of ticket outTime.
     * 
     * @param outTime, the LocalDateTime of ticket exit time
     */
    public void setOutTime(LocalDateTime outTime) {
	this.outTime = outTime;
    }
}

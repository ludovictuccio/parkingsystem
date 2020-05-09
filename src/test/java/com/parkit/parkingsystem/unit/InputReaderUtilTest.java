package com.parkit.parkingsystem.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.util.InputReaderUtil;

public class InputReaderUtilTest {

    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() {
	inputReaderUtil = new InputReaderUtil();
    }

    @Tag("ReadSelection")
    @Test
    @DisplayName("Valid user selection")
    public void readSelection_whenEntryIsValid_returnNumberEntry() {
	// 1,2 & 3 are valid entry
	String input = "1";
	InputStream inputStream = new ByteArrayInputStream((input).getBytes(Charset.forName("UTF-8")));
	Scanner scan = new Scanner(inputStream);
	inputReaderUtil.setScan(scan);
	assertThat(1).isEqualTo(inputReaderUtil.readSelection());
    }

    @Tag("ReadSelection")
    @Test
    @DisplayName("Invalid user selection (number)")
    public void readSelection_whenEntryIsNotValidWithNumber_returnMinusOne() {
	// Bad entry - number
	String input = "4";
	InputStream inputStream = new ByteArrayInputStream((input).getBytes(Charset.forName("UTF-8")));
	Scanner scan = new Scanner(inputStream);
	inputReaderUtil.setScan(scan);
	assertThat(-1).isEqualTo(inputReaderUtil.readSelection());
    }

    @Tag("ReadSelection")
    @Test
    @DisplayName("Invalid user selection (letter)")
    public void readSelection_whenEntryIsNotValidWithLetter_returnMinusOne() {
	// Bad entry - letter
	String input = "p";
	InputStream inputStream = new ByteArrayInputStream((input).getBytes(Charset.forName("UTF-8")));
	Scanner scan = new Scanner(inputStream);
	inputReaderUtil.setScan(scan);
	assertThat(-1).isEqualTo(inputReaderUtil.readSelection());
    }

    @Tag("ReadVehicleRegistrationNumber")
    @Test
    @DisplayName("Valid vehicle registration number")

    public void readVehicleRegistrationNumber_whenEntryIsValid_returnVehicleRegNumber() {
	String vehicleRegNumber = "CL009WB";
	InputStream inputStream = new ByteArrayInputStream((vehicleRegNumber).getBytes(Charset.forName("UTF-8")));
	Scanner scan = new Scanner(inputStream);
	inputReaderUtil.setScan(scan);
	inputReaderUtil.readVehicleRegistrationNumber();
	assertThat(vehicleRegNumber).hasSizeGreaterThan(3).hasSizeLessThan(8);
    }

    @Tag("ReadVehicleRegistrationNumber")
    @Test
    @DisplayName("Exception - Invalid vehicle registration number ")
    public void readVehicleRegistrationNumber_whenEntryIsNotValid_returnIllegalArgumentException() {
	String vehicleRegNumber = "CD";
	InputStream inputStream = new ByteArrayInputStream((vehicleRegNumber).getBytes(Charset.forName("UTF-8")));
	Scanner scan = new Scanner(inputStream);
	inputReaderUtil.setScan(scan);
	assertThatIllegalArgumentException().isThrownBy(() -> {
	    inputReaderUtil.readVehicleRegistrationNumber();
	});
    }
}
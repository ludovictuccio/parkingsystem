package com.parkit.parkingsystem.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.service.InteractiveShell;

public class InteractiveShellTest {

    @Test
    @DisplayName("loadInterface - Return out input possibility when load interface")
    public void givenReadingApplication_whenLoadInterface_thenReturnOutInputPossibility() throws IOException {
	ByteArrayOutputStream readMenu = new ByteArrayOutputStream();
	String userInput = "3"; // Only entry 3 is allowed, because of 3 input only interact with loadInterface
				// method
	InputStream userInputStream = new ByteArrayInputStream((userInput).getBytes(Charset.forName("UTF-8")));
	System.setOut(new PrintStream(readMenu));
	System.setIn(userInputStream);
	userInputStream.close();

	InteractiveShell.loadInterface();

	assertThat(readMenu.toString())
		.contains("Please select an option. Simply enter the number to choose an action");
	assertThat(readMenu.toString()).contains("1 New Vehicle Entering - Allocate Parking Space");
	assertThat(readMenu.toString()).contains("2 Vehicle Exiting - Generate Ticket Price");
	assertThat(readMenu.toString()).contains("3 Shutdown System");
    }
}
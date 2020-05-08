package com.parkit.parkingsystem.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;

public class DataBaseConfigTest {

    private DataBaseConfig dataBaseConfig;

    @BeforeEach
    public void setUpPerTest() {
	dataBaseConfig = new DataBaseConfig();
    }

    @Test
    @Tag("Connection")
    @DisplayName("Get Connection")
    public void givenDataBase_whenGetConnection_thenConnectionShouldBeEstablished()
	    throws ClassNotFoundException, SQLException {

	Connection con;

	con = dataBaseConfig.getConnection();

	assertThat(con).isNotNull();
    }

    @Test
    @Tag("Connection")
    @DisplayName("Connection closed")
    public void givenDataBase_whenCloseConnection_thenConnectionShouldBeClosed()
	    throws ClassNotFoundException, SQLException {

	Connection con;
	con = dataBaseConfig.getConnection();

	dataBaseConfig.closeConnection(con);

	assertThat(con.isClosed()).isTrue();
    }

    @Test
    @Tag("PreparedStatement")
    @DisplayName("PreparedStatement closed")
    public void givenDataBaseConnection_whenClosePreparedStatement_thenPreparedStatementShouldBeClosed()
	    throws ClassNotFoundException, SQLException {

	Connection con;
	con = dataBaseConfig.getConnection();
	PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);

	dataBaseConfig.closePreparedStatement(ps);

	assertThat(ps.isClosed()).isTrue();
    }

    @Test
    @Tag("ResultSet")
    @DisplayName("ResultSet closed")
    public void givenDataBaseConnection_whenCloseResultSet_thenResultSetShouldBeClosed()
	    throws ClassNotFoundException, SQLException {

	Connection con;
	con = dataBaseConfig.getConnection();
	ResultSet rs = null;
	PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
	ps.setString(1, "CAR");
	rs = ps.executeQuery();

	dataBaseConfig.closeResultSet(rs);

	assertThat(rs.isClosed()).isTrue();
    }
}
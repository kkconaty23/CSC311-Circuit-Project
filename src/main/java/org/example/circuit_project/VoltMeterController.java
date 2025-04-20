package org.example.circuit_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class VoltMeterController {

    enum Mode {
        VOLTS,
        AMPS,
        RESISTANCE
    }


    // Basic Readings for meter
    @FXML
    private double voltage;
    @FXML
    private double amps;
    @FXML
    private double resistance;

    // The setting of the meter (type of measurement)
    @FXML
    private Mode mode;

    @FXML
    private String display;

    // need leads for positive and negative (red and black)

    public VoltMeterController() {
        this.amps = 0;
        this.resistance = 0;
        this.voltage = 0;
        this.mode = Mode.VOLTS;
    }

    // Button to change mode
    @FXML
    public void switchModeOnClick(ActionEvent event) {
        System.out.println(event);
    }

    // Calculations of the display values using Ohms law (V = IR)
    // Ensure value of reading is changed when the mode is changed
    public String displayReading() {
        String reading = switch (mode) {
            case AMPS -> (voltage / resistance) + "A";
            case VOLTS -> (amps * resistance) + "V";
            case RESISTANCE -> (voltage / amps) + "Ω";
            default -> throw new IllegalArgumentException("ERROR");
        };
        this.display = reading;
        return reading;
    }




    // Getters and Setters

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getAmps() {
        return amps;
    }

    public void setAmps(double amps) {
        this.amps = amps;
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}

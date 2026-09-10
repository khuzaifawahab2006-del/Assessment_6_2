package com.example;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class TrafficViolationSystemTest {

    @Test
    public void testMultipleVehicleRegistration() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle1 =
                new TrafficViolationSystem.Vehicle(
                        "TN01AA1111",
                        "Arun",
                        "9000000001",
                        TrafficViolationSystem.VehicleType.CAR);

        TrafficViolationSystem.Vehicle vehicle2 =
                new TrafficViolationSystem.Vehicle(
                        "TN02BB2222",
                        "Kumar",
                        "9000000002",
                        TrafficViolationSystem.VehicleType.BIKE);

        system.registerVehicle(vehicle1);
        system.registerVehicle(vehicle2);

        assertEquals(
                2,
                system.getRegisteredVehicleCount());
    }

    @Test
    public void testOverSpeedingFine() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "TN01AA1111",
                        "Arun",
                        "9000000001",
                        TrafficViolationSystem.VehicleType.CAR);

        system.registerVehicle(vehicle);

        TrafficViolationSystem.Violation violation =
                new TrafficViolationSystem.Violation(
                        "TN01AA1111",
                        TrafficViolationSystem.ViolationType.OVER_SPEEDING,
                        "Main Road",
                        LocalDateTime.of(
                                2026, 9, 10, 10, 0),
                        75,
                        50);

        TrafficViolationSystem.EChallan challan =
                system.recordViolation(violation);

        assertEquals(
                2000.0,
                challan.getFineAmount(),
                0.01);
    }

    @Test
    public void testSignalViolationFine() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "TN01CC3333",
                        "Ravi",
                        "9000000003",
                        TrafficViolationSystem.VehicleType.CAR);

        system.registerVehicle(vehicle);

        TrafficViolationSystem.Violation violation =
                new TrafficViolationSystem.Violation(
                        "TN01CC3333",
                        TrafficViolationSystem.ViolationType.SIGNAL_VIOLATION,
                        "Signal Junction",
                        LocalDateTime.of(
                                2026, 9, 10, 11, 0),
                        40,
                        50);

        TrafficViolationSystem.EChallan challan =
                system.recordViolation(violation);

        assertEquals(
                1500.0,
                challan.getFineAmount(),
                0.01);
    }

    @Test
    public void testIllegalParkingFine() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "TN01DD4444",
                        "Sameer",
                        "9000000004",
                        TrafficViolationSystem.VehicleType.BIKE);

        system.registerVehicle(vehicle);

        TrafficViolationSystem.Violation violation =
                new TrafficViolationSystem.Violation(
                        "TN01DD4444",
                        TrafficViolationSystem.ViolationType.ILLEGAL_PARKING,
                        "Market Road",
                        LocalDateTime.of(
                                2026, 9, 10, 12, 0),
                        0,
                        40);

        TrafficViolationSystem.EChallan challan =
                system.recordViolation(violation);

        assertEquals(
                1000.0,
                challan.getFineAmount(),
                0.01);
    }

    @Test
    public void testRepeatedViolationGetsHigherPenalty() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "TN01EE5555",
                        "Rahul",
                        "9000000005",
                        TrafficViolationSystem.VehicleType.CAR);

        system.registerVehicle(vehicle);

        TrafficViolationSystem.Violation first =
                new TrafficViolationSystem.Violation(
                        "TN01EE5555",
                        TrafficViolationSystem.ViolationType.SIGNAL_VIOLATION,
                        "Junction A",
                        LocalDateTime.of(
                                2026, 9, 10, 9, 0),
                        40,
                        50);

        TrafficViolationSystem.Violation second =
                new TrafficViolationSystem.Violation(
                        "TN01EE5555",
                        TrafficViolationSystem.ViolationType.SIGNAL_VIOLATION,
                        "Junction B",
                        LocalDateTime.of(
                                2026, 9, 11, 9, 0),
                        40,
                        50);

        TrafficViolationSystem.EChallan firstChallan =
                system.recordViolation(first);

        TrafficViolationSystem.EChallan secondChallan =
                system.recordViolation(second);

        assertEquals(
                1500.0,
                firstChallan.getFineAmount(),
                0.01);

        assertEquals(
                1875.0,
                secondChallan.getFineAmount(),
                0.01);
    }

    @Test
    public void testDuplicateViolationIsRejected() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "TN01FF6666",
                        "Vijay",
                        "9000000006",
                        TrafficViolationSystem.VehicleType.CAR);

        system.registerVehicle(vehicle);

        LocalDateTime time =
                LocalDateTime.of(
                        2026, 9, 10, 13, 0);

        TrafficViolationSystem.Violation violation1 =
                new TrafficViolationSystem.Violation(
                        "TN01FF6666",
                        TrafficViolationSystem.ViolationType.ILLEGAL_PARKING,
                        "Market Road",
                        time,
                        0,
                        40);

        TrafficViolationSystem.Violation violation2 =
                new TrafficViolationSystem.Violation(
                        "TN01FF6666",
                        TrafficViolationSystem.ViolationType.ILLEGAL_PARKING,
                        "Market Road",
                        time,
                        0,
                        40);

        system.recordViolation(violation1);

        try {

            system.recordViolation(violation2);

            fail("Expected DuplicateChallanException");

        } catch (
                TrafficViolationSystem.DuplicateChallanException e) {

            assertEquals(
                    "Duplicate violation event detected.",
                    e.getMessage());
        }
    }

    @Test
    public void testChallanPayment() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "TN01GG7777",
                        "Manoj",
                        "9000000007",
                        TrafficViolationSystem.VehicleType.BIKE);

        system.registerVehicle(vehicle);

        TrafficViolationSystem.Violation violation =
                new TrafficViolationSystem.Violation(
                        "TN01GG7777",
                        TrafficViolationSystem.ViolationType.ILLEGAL_PARKING,
                        "Street A",
                        LocalDateTime.of(
                                2026, 9, 10, 14, 0),
                        0,
                        40);

        TrafficViolationSystem.EChallan challan =
                system.recordViolation(violation);

        assertEquals(
                TrafficViolationSystem.PaymentStatus.UNPAID,
                challan.getPaymentStatus());

        system.payChallan(
                challan.getChallanId());

        assertEquals(
                TrafficViolationSystem.PaymentStatus.PAID,
                challan.getPaymentStatus());
    }

    @Test
    public void testOutstandingFine() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "TN01HH8888",
                        "Suresh",
                        "9000000008",
                        TrafficViolationSystem.VehicleType.CAR);

        system.registerVehicle(vehicle);

        TrafficViolationSystem.Violation violation1 =
                new TrafficViolationSystem.Violation(
                        "TN01HH8888",
                        TrafficViolationSystem.ViolationType.SIGNAL_VIOLATION,
                        "Junction A",
                        LocalDateTime.of(
                                2026, 9, 10, 15, 0),
                        40,
                        50);

        TrafficViolationSystem.Violation violation2 =
                new TrafficViolationSystem.Violation(
                        "TN01HH8888",
                        TrafficViolationSystem.ViolationType.ILLEGAL_PARKING,
                        "Street B",
                        LocalDateTime.of(
                                2026, 9, 10, 16, 0),
                        0,
                        40);

        TrafficViolationSystem.EChallan challan1 =
                system.recordViolation(violation1);

        system.recordViolation(violation2);

        system.payChallan(
                challan1.getChallanId());

        assertEquals(
                1000.0,
                system.calculateOutstandingFine(
                        "TN01HH8888"),
                0.01);
    }

    @Test
    public void testVehicleClassification() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "TN01II9999",
                        "Ajay",
                        "9000000009",
                        TrafficViolationSystem.VehicleType.CAR);

        system.registerVehicle(vehicle);

        assertEquals(
                "CLEAN",
                system.classifyVehicle(
                        "TN01II9999"));

        TrafficViolationSystem.Violation violation =
                new TrafficViolationSystem.Violation(
                        "TN01II9999",
                        TrafficViolationSystem.ViolationType.ILLEGAL_PARKING,
                        "Road A",
                        LocalDateTime.of(
                                2026, 9, 10, 17, 0),
                        0,
                        40);

        system.recordViolation(violation);

        assertEquals(
                "LOW RISK",
                system.classifyVehicle(
                        "TN01II9999"));
    }

    @Test
    public void testInvalidVehicleInformation() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "",
                        "Rahul",
                        "9000000010",
                        TrafficViolationSystem.VehicleType.CAR);

        try {

            system.registerVehicle(vehicle);

            fail("Expected InvalidVehicleException");

        } catch (
                TrafficViolationSystem.InvalidVehicleException e) {

            assertEquals(
                    "Vehicle number is required.",
                    e.getMessage());
        }
    }

    @Test
    public void testInvalidViolationInformation() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "TN01JJ1010",
                        "Karthik",
                        "9000000011",
                        TrafficViolationSystem.VehicleType.CAR);

        system.registerVehicle(vehicle);

        TrafficViolationSystem.Violation violation =
                new TrafficViolationSystem.Violation(
                        "TN01JJ1010",
                        TrafficViolationSystem.ViolationType.OVER_SPEEDING,
                        "Road A",
                        LocalDateTime.of(
                                2026, 9, 10, 18, 0),
                        -20,
                        50);

        try {

            system.recordViolation(violation);

            fail("Expected InvalidViolationException");

        } catch (
                TrafficViolationSystem.InvalidViolationException e) {

            assertEquals(
                    "Speed cannot be negative.",
                    e.getMessage());
        }
    }

    @Test
    public void testMultipleFailureScenarios() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        // Failure 1: Invalid vehicle
        try {

            TrafficViolationSystem.Vehicle invalidVehicle =
                    new TrafficViolationSystem.Vehicle(
                            "",
                            "",
                            "",
                            null);

            system.registerVehicle(invalidVehicle);

            fail("Invalid vehicle should fail");

        } catch (
                TrafficViolationSystem.InvalidVehicleException e) {

            assertTrue(true);
        }

        // Failure 2: Unregistered vehicle
        try {

            TrafficViolationSystem.Violation violation =
                    new TrafficViolationSystem.Violation(
                            "UNKNOWN",
                            TrafficViolationSystem.ViolationType.SIGNAL_VIOLATION,
                            "Junction",
                            LocalDateTime.of(
                                    2026, 9, 10, 19, 0),
                            40,
                            50);

            system.recordViolation(violation);

            fail("Unregistered vehicle should fail");

        } catch (
                TrafficViolationSystem.InvalidVehicleException e) {

            assertTrue(true);
        }

        // Failure 3: Invalid permitted speed
        try {

            TrafficViolationSystem.Vehicle vehicle =
                    new TrafficViolationSystem.Vehicle(
                            "TN01KK2020",
                            "Naveen",
                            "9000000012",
                            TrafficViolationSystem.VehicleType.CAR);

            system.registerVehicle(vehicle);

            TrafficViolationSystem.Violation violation =
                    new TrafficViolationSystem.Violation(
                            "TN01KK2020",
                            TrafficViolationSystem.ViolationType.OVER_SPEEDING,
                            "Road",
                            LocalDateTime.of(
                                    2026, 9, 10, 20, 0),
                            60,
                            0);

            system.recordViolation(violation);

            fail("Invalid permitted speed should fail");

        } catch (
                TrafficViolationSystem.InvalidViolationException e) {

            assertTrue(true);
        }
    }

    @Test
    public void testPayingAlreadyPaidChallanFails() {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        TrafficViolationSystem.Vehicle vehicle =
                new TrafficViolationSystem.Vehicle(
                        "TN01LL3030",
                        "Prakash",
                        "9000000013",
                        TrafficViolationSystem.VehicleType.CAR);

        system.registerVehicle(vehicle);

        TrafficViolationSystem.Violation violation =
                new TrafficViolationSystem.Violation(
                        "TN01LL3030",
                        TrafficViolationSystem.ViolationType.SIGNAL_VIOLATION,
                        "Junction",
                        LocalDateTime.of(
                                2026, 9, 10, 21, 0),
                        40,
                        50);

        TrafficViolationSystem.EChallan challan =
                system.recordViolation(violation);

        system.payChallan(
                challan.getChallanId());

        try {

            system.payChallan(
                    challan.getChallanId());

            fail("Second payment should fail");

        } catch (
                TrafficViolationSystem.PaymentException e) {

            assertEquals(
                    "Challan has already been paid.",
                    e.getMessage());
        }
    }
}

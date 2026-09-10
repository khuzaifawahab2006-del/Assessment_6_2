package com.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrafficViolationSystem {

    // Types of vehicles
    public enum VehicleType {
        CAR,
        BIKE,
        BUS,
        TRUCK
    }

    // Types of violations
    public enum ViolationType {
        OVER_SPEEDING,
        SIGNAL_VIOLATION,
        ILLEGAL_PARKING
    }

    // Payment status
    public enum PaymentStatus {
        UNPAID,
        PAID
    }

    // Vehicle details
    public static class Vehicle {

        private final String vehicleNumber;
        private final String ownerName;
        private final String ownerPhone;
        private final VehicleType vehicleType;

        public Vehicle(
                String vehicleNumber,
                String ownerName,
                String ownerPhone,
                VehicleType vehicleType) {

            this.vehicleNumber = vehicleNumber;
            this.ownerName = ownerName;
            this.ownerPhone = ownerPhone;
            this.vehicleType = vehicleType;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public String getOwnerPhone() {
            return ownerPhone;
        }

        public VehicleType getVehicleType() {
            return vehicleType;
        }
    }

    // Violation details
    public static class Violation {

        private final String vehicleNumber;
        private final ViolationType violationType;
        private final String location;
        private final LocalDateTime timestamp;
        private final double speed;
        private final double permittedSpeed;

        public Violation(
                String vehicleNumber,
                ViolationType violationType,
                String location,
                LocalDateTime timestamp,
                double speed,
                double permittedSpeed) {

            this.vehicleNumber = vehicleNumber;
            this.violationType = violationType;
            this.location = location;
            this.timestamp = timestamp;
            this.speed = speed;
            this.permittedSpeed = permittedSpeed;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public ViolationType getViolationType() {
            return violationType;
        }

        public String getLocation() {
            return location;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public double getSpeed() {
            return speed;
        }

        public double getPermittedSpeed() {
            return permittedSpeed;
        }
    }

    // Electronic challan
    public static class EChallan {

        private final String challanId;
        private final Vehicle vehicle;
        private final Violation violation;
        private final double fineAmount;
        private PaymentStatus paymentStatus;

        public EChallan(
                String challanId,
                Vehicle vehicle,
                Violation violation,
                double fineAmount) {

            this.challanId = challanId;
            this.vehicle = vehicle;
            this.violation = violation;
            this.fineAmount = fineAmount;
            this.paymentStatus = PaymentStatus.UNPAID;
        }

        public String getChallanId() {
            return challanId;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public Violation getViolation() {
            return violation;
        }

        public double getFineAmount() {
            return fineAmount;
        }

        public PaymentStatus getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
        }
    }

    // Custom exception for invalid vehicle information
    public static class InvalidVehicleException
            extends RuntimeException {

        public InvalidVehicleException(String message) {
            super(message);
        }
    }

    // Custom exception for invalid violation information
    public static class InvalidViolationException
            extends RuntimeException {

        public InvalidViolationException(String message) {
            super(message);
        }
    }

    // Custom exception for duplicate challans
    public static class DuplicateChallanException
            extends RuntimeException {

        public DuplicateChallanException(String message) {
            super(message);
        }
    }

    // Custom exception for payment errors
    public static class PaymentException
            extends RuntimeException {

        public PaymentException(String message) {
            super(message);
        }
    }

    private final Map<String, Vehicle> vehicles =
            new HashMap<>();

    private final List<EChallan> challans =
            new ArrayList<>();

    // Counter for generating unique challan IDs
    private int challanCounter = 1000;

    // Register a vehicle
    public void registerVehicle(Vehicle vehicle) {

        validateVehicle(vehicle);

        if (vehicles.containsKey(vehicle.getVehicleNumber())) {

            throw new InvalidVehicleException(
                    "Vehicle is already registered: "
                            + vehicle.getVehicleNumber());
        }

        vehicles.put(
                vehicle.getVehicleNumber(),
                vehicle);
    }

    // Validate vehicle information
    private void validateVehicle(Vehicle vehicle) {

        if (vehicle == null) {
            throw new InvalidVehicleException(
                    "Vehicle information cannot be null.");
        }

        if (vehicle.getVehicleNumber() == null ||
                vehicle.getVehicleNumber().trim().isEmpty()) {

            throw new InvalidVehicleException(
                    "Vehicle number is required.");
        }

        if (vehicle.getOwnerName() == null ||
                vehicle.getOwnerName().trim().isEmpty()) {

            throw new InvalidVehicleException(
                    "Owner name is required.");
        }

        if (vehicle.getOwnerPhone() == null ||
                vehicle.getOwnerPhone().trim().isEmpty()) {

            throw new InvalidVehicleException(
                    "Owner phone number is required.");
        }

        if (vehicle.getVehicleType() == null) {

            throw new InvalidVehicleException(
                    "Vehicle type is required.");
        }
    }

    // Record a traffic violation and generate an e-challan
    public EChallan recordViolation(Violation violation) {

        validateViolation(violation);

        Vehicle vehicle =
                vehicles.get(violation.getVehicleNumber());

        if (vehicle == null) {

            throw new InvalidVehicleException(
                    "Vehicle is not registered: "
                            + violation.getVehicleNumber());
        }

        // Prevent duplicate challan for the same event
        if (isDuplicateViolation(violation)) {

            throw new DuplicateChallanException(
                    "Duplicate violation event detected.");
        }

        int previousViolations =
                countPreviousViolations(
                        violation.getVehicleNumber(),
                        violation.getViolationType());

        double fine =
                calculateFine(
                        violation,
                        previousViolations);

        String challanId =
                "ECH-" + (++challanCounter);

        EChallan challan =
                new EChallan(
                        challanId,
                        vehicle,
                        violation,
                        fine);

        challans.add(challan);

        return challan;
    }

    // Validate violation information
    private void validateViolation(Violation violation) {

        if (violation == null) {

            throw new InvalidViolationException(
                    "Violation information cannot be null.");
        }

        if (violation.getVehicleNumber() == null ||
                violation.getVehicleNumber().trim().isEmpty()) {

            throw new InvalidViolationException(
                    "Vehicle number is required.");
        }

        if (violation.getViolationType() == null) {

            throw new InvalidViolationException(
                    "Violation type is required.");
        }

        if (violation.getLocation() == null ||
                violation.getLocation().trim().isEmpty()) {

            throw new InvalidViolationException(
                    "Violation location is required.");
        }

        if (violation.getTimestamp() == null) {

            throw new InvalidViolationException(
                    "Violation timestamp is required.");
        }

        if (violation.getSpeed() < 0) {

            throw new InvalidViolationException(
                    "Speed cannot be negative.");
        }

        if (violation.getPermittedSpeed() <= 0) {

            throw new InvalidViolationException(
                    "Permitted speed must be greater than zero.");
        }
    }

    // Check whether the same violation event already exists
    private boolean isDuplicateViolation(
            Violation violation) {

        for (EChallan challan : challans) {

            Violation oldViolation =
                    challan.getViolation();

            if (oldViolation.getVehicleNumber()
                    .equals(violation.getVehicleNumber())
                    && oldViolation.getViolationType()
                    == violation.getViolationType()
                    && oldViolation.getLocation()
                    .equalsIgnoreCase(violation.getLocation())
                    && oldViolation.getTimestamp()
                    .equals(violation.getTimestamp())) {

                return true;
            }
        }

        return false;
    }

    // Count previous violations of the same type
    private int countPreviousViolations(
            String vehicleNumber,
            ViolationType violationType) {

        int count = 0;

        for (EChallan challan : challans) {

            Violation violation =
                    challan.getViolation();

            if (violation.getVehicleNumber()
                    .equals(vehicleNumber)
                    && violation.getViolationType()
                    == violationType) {

                count++;
            }
        }

        return count;
    }

    // Calculate fine according to violation severity
    public double calculateFine(
            Violation violation,
            int previousViolations) {

        double baseFine;

        switch (violation.getViolationType()) {

            case OVER_SPEEDING:

                double excess =
                        violation.getSpeed()
                                - violation.getPermittedSpeed();

                if (excess <= 0) {
                    throw new InvalidViolationException(
                            "Vehicle is not over-speeding.");
                }

                if (excess <= 10) {
                    baseFine = 500;
                } else if (excess <= 20) {
                    baseFine = 1000;
                } else {
                    baseFine = 2000;
                }

                break;

            case SIGNAL_VIOLATION:

                baseFine = 1500;
                break;

            case ILLEGAL_PARKING:

                baseFine = 1000;
                break;

            default:

                throw new InvalidViolationException(
                        "Unknown violation type.");
        }

        // 25% additional penalty for each previous
        // violation of the same type
        double repeatedPenalty =
                baseFine * 0.25 * previousViolations;

        return baseFine + repeatedPenalty;
    }

    // Pay an e-challan
    public void payChallan(String challanId) {

        EChallan challan =
                findChallan(challanId);

        if (challan.getPaymentStatus()
                == PaymentStatus.PAID) {

            throw new PaymentException(
                    "Challan has already been paid.");
        }

        challan.setPaymentStatus(
                PaymentStatus.PAID);
    }

    // Find challan using challan ID
    public EChallan findChallan(
            String challanId) {

        for (EChallan challan : challans) {

            if (challan.getChallanId()
                    .equals(challanId)) {

                return challan;
            }
        }

        throw new PaymentException(
                "Challan not found: " + challanId);
    }

    // Calculate total outstanding fines
    public double calculateOutstandingFine(
            String vehicleNumber) {

        double total = 0;

        for (EChallan challan : challans) {

            if (challan.getVehicle()
                    .getVehicleNumber()
                    .equals(vehicleNumber)
                    && challan.getPaymentStatus()
                    == PaymentStatus.UNPAID) {

                total += challan.getFineAmount();
            }
        }

        return total;
    }

    // Calculate total fines for all vehicles
    public double calculateTotalOutstandingFines() {

        double total = 0;

        for (EChallan challan : challans) {

            if (challan.getPaymentStatus()
                    == PaymentStatus.UNPAID) {

                total += challan.getFineAmount();
            }
        }

        return total;
    }

    // Classify vehicle based on violation history
    public String classifyVehicle(
            String vehicleNumber) {

        int count = 0;

        for (EChallan challan : challans) {

            if (challan.getVehicle()
                    .getVehicleNumber()
                    .equals(vehicleNumber)) {

                count++;
            }
        }

        if (count == 0) {
            return "CLEAN";
        } else if (count <= 2) {
            return "LOW RISK";
        } else if (count <= 4) {
            return "HIGH RISK";
        } else {
            return "REPEAT OFFENDER";
        }
    }

    // Get complete violation history
    public List<EChallan> getViolationHistory(
            String vehicleNumber) {

        List<EChallan> history =
                new ArrayList<>();

        for (EChallan challan : challans) {

            if (challan.getVehicle()
                    .getVehicleNumber()
                    .equals(vehicleNumber)) {

                history.add(challan);
            }
        }

        return Collections.unmodifiableList(history);
    }

    // Get number of registered vehicles
    public int getRegisteredVehicleCount() {
        return vehicles.size();
    }

    // Get total challans
    public int getTotalChallanCount() {
        return challans.size();
    }

    // Display challan details
    public String getChallanDetails(
            String challanId) {

        EChallan challan =
                findChallan(challanId);

        Vehicle vehicle =
                challan.getVehicle();

        Violation violation =
                challan.getViolation();

        return "E-Challan ID: "
                + challan.getChallanId()
                + "\nVehicle Number: "
                + vehicle.getVehicleNumber()
                + "\nOwner Name: "
                + vehicle.getOwnerName()
                + "\nVehicle Type: "
                + vehicle.getVehicleType()
                + "\nViolation Type: "
                + violation.getViolationType()
                + "\nLocation: "
                + violation.getLocation()
                + "\nTimestamp: "
                + violation.getTimestamp()
                + "\nSpeed: "
                + violation.getSpeed()
                + "\nPermitted Speed: "
                + violation.getPermittedSpeed()
                + "\nFine Amount: ₹"
                + challan.getFineAmount()
                + "\nPayment Status: "
                + challan.getPaymentStatus();
    }

    // Demonstration
    public static void main(String[] args) {

        TrafficViolationSystem system =
                new TrafficViolationSystem();

        Vehicle vehicle =
                new Vehicle(
                        "TN01AB1234",
                        "Rahul Kumar",
                        "9876543210",
                        VehicleType.CAR);

        system.registerVehicle(vehicle);

        Violation violation =
                new Violation(
                        "TN01AB1234",
                        ViolationType.OVER_SPEEDING,
                        "Katpadi Road",
                        LocalDateTime.of(
                                2026, 9, 10, 10, 30),
                        75,
                        50);

        EChallan challan =
                system.recordViolation(violation);

        System.out.println(
                system.getChallanDetails(
                        challan.getChallanId()));

        System.out.println(
                "\nOutstanding Fine: ₹"
                        + system.calculateOutstandingFine(
                                "TN01AB1234"));

        system.payChallan(
                challan.getChallanId());

        System.out.println(
                "\nPayment Status: "
                        + challan.getPaymentStatus());
    }
}

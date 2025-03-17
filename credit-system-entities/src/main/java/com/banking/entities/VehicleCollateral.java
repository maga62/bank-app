package com.banking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

/**
 * Araç teminatı sınıfı.
 * Kredi teminatı olarak gösterilen araçları temsil eder.
 */
@Entity
@Table(name = "vehicle_collaterals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class VehicleCollateral extends Collateral {
    
    @Column(name = "vehicle_type")
    private String vehicleType; // Car, Truck, Motorcycle, etc.
    
    @Column(name = "make")
    private String make;
    
    @Column(name = "model")
    private String model;
    
    @Column(name = "year")
    private Integer year;
    
    @Column(name = "license_plate")
    private String licensePlate;
    
    @Column(name = "chassis_number")
    private String chassisNumber;
    
    @Column(name = "engine_number")
    private String engineNumber;
    
    @Column(name = "registration_number")
    private String registrationNumber;
    
    @Column(name = "registration_date")
    private LocalDate registrationDate;
    
    @Column(name = "color")
    private String color;
    
    @Column(name = "mileage")
    private Integer mileage;
    
    @Column(name = "fuel_type")
    private String fuelType;
    
    @Column(name = "transmission_type")
    private String transmissionType;
    
    @Column(name = "insurance_policy_number")
    private String insurancePolicyNumber;
    
    @Column(name = "insurance_company")
    private String insuranceCompany;
    
    @Column(name = "insurance_expiry_date")
    private LocalDate insuranceExpiryDate;
} 
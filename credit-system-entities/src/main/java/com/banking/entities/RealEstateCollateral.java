package com.banking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

/**
 * Gayrimenkul teminatı sınıfı.
 * Kredi teminatı olarak gösterilen gayrimenkulleri temsil eder.
 */
@Entity
@Table(name = "real_estate_collaterals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class RealEstateCollateral extends Collateral {
    
    @Column(name = "property_type")
    private String propertyType; // Apartment, House, Land, Commercial, etc.
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "district")
    private String district;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "land_registry_number")
    private String landRegistryNumber;
    
    @Column(name = "deed_number")
    private String deedNumber;
    
    @Column(name = "block_number")
    private String blockNumber;
    
    @Column(name = "parcel_number")
    private String parcelNumber;
    
    @Column(name = "floor_number")
    private String floorNumber;
    
    @Column(name = "apartment_number")
    private String apartmentNumber;
    
    @Column(name = "area_square_meters")
    private Double areaSquareMeters;
    
    @Column(name = "construction_year")
    private Integer constructionYear;
    
    @Column(name = "has_mortgage")
    private Boolean hasMortgage;
    
    @Column(name = "mortgage_details")
    private String mortgageDetails;
} 
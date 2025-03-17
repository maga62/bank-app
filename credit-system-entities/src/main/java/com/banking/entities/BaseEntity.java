package com.banking.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@SQLRestriction("deleted_date IS NULL")
public abstract class BaseEntity<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private T id;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
} 
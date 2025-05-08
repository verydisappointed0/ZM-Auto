package com.adminpanel.zmauto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a payment in the system.
 */
@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(nullable = false)
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, CASH, BANK_TRANSFER
    
    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED
    
    @Column
    private String transactionId;
    
    @Column
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime paymentDate;
    
    // Default constructor required by JPA
    public Payment() {
        this.paymentDate = LocalDateTime.now();
    }
    
    public Payment(Reservation reservation, Double amount, String paymentMethod, 
                  String status, String transactionId, String notes) {
        this.reservation = reservation;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionId = transactionId;
        this.notes = notes;
        this.paymentDate = LocalDateTime.now();
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", reservation=" + (reservation != null ? reservation.getId() : "null") +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
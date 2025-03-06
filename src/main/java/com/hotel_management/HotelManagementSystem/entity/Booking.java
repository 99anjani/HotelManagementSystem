package com.hotel_management.HotelManagementSystem.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import java.time.LocalDate;

@Data
@Entity
@Table(name="bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "check in date is required")
    private LocalDate checkInDate;

    @Future(message = "checkout date must be in future")
    private LocalDate checkOutDate;

    @Min(value=1, message = "number of adult must not be less than 1")
    private int numOfAdults;

    @Min(value=0, message = "number of children must not be negative")
    private int numOfChildren;

    private int totalNumOfGuest;
    private String bookingConfirmationCode;;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public void calculateTotalNumOfGuest(){
        this.totalNumOfGuest = this.numOfAdults + this.numOfChildren;
    }

    public void setNumOfAdults(int numOfAdults) {
        this.numOfAdults = numOfAdults;
        calculateTotalNumOfGuest();
    }

    public void setNumOfChildren(int numOfChildren) {
        this.numOfChildren = numOfChildren;
        calculateTotalNumOfGuest();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numOfAdults=" + numOfAdults +
                ", numOfChildren=" + numOfChildren +
                ", totalNumOfGuest=" + totalNumOfGuest +
                ", bookingInformationCode='" + bookingConfirmationCode + '\'' +
                ", user=" + user +
                ", room=" + room +
                '}';
    }

}

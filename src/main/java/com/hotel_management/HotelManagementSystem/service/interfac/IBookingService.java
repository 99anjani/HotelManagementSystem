package com.hotel_management.HotelManagementSystem.service.interfac;


import com.hotel_management.HotelManagementSystem.dto.Response;
import com.hotel_management.HotelManagementSystem.entity.Booking;

public interface IBookingService {

    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);
    Response findBookingByConfirmationCode(String ConfirmationCode);
    Response getAllBooking();
    Response cancelBooking(Long bookingId);

}

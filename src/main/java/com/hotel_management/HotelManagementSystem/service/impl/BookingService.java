package com.hotel_management.HotelManagementSystem.service.impl;

import com.hotel_management.HotelManagementSystem.dto.BookingDTO;
import com.hotel_management.HotelManagementSystem.dto.Response;
import com.hotel_management.HotelManagementSystem.entity.Booking;
import com.hotel_management.HotelManagementSystem.entity.Room;
import com.hotel_management.HotelManagementSystem.entity.User;
import com.hotel_management.HotelManagementSystem.exception.OurException;
import com.hotel_management.HotelManagementSystem.repo.BookingRepository;
import com.hotel_management.HotelManagementSystem.repo.RoomRepository;
import com.hotel_management.HotelManagementSystem.repo.UserRepository;
import com.hotel_management.HotelManagementSystem.service.interfac.IBookingService;
import com.hotel_management.HotelManagementSystem.service.interfac.IRoomService;
import com.hotel_management.HotelManagementSystem.utils.utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private IRoomService roomService;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;


    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        // Check if the room is available by ensuring there is no overlap with existing bookings
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                                //Check if the booking request's check-in date matches an existing booking's check-in date
                                bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                        // Check if the booking request's check-out date is before an existing booking's check-out date
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())

                                        // Check if the booking request's check-in date is within the existing booking's duration
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))

                                        // Check if the booking request's check-in date is before the existing booking's check-in date
                                        // and check-out date is the same as the existing booking's check-out date
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))

                                        // Check if the booking request's check-in date is before the existing booking's check-in date
                                        // and the booking request's check-out date is within the existing booking's duration
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                        // Check if the booking request's check-in date matches the existing booking's check-out date
                                        // and the check-out date matches the existing booking's check-in date
                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                        // Check if the booking request's check-in date matches the existing booking's check-out date
                                        // and the check-out date matches the booking request's check-in date
                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }
    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {

        Response response=new Response();
        try{
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalArgumentException("Check in date must come after check out date");
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User Not Found"));

            List <Booking> existingBookings=room.getBookings();
            if (!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new OurException("Room not Available for selected date range");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode = utils.generateRandomConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            bookingRepository.save(bookingRequest);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Saving a booking: " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {

        Response response = new Response();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new OurException("Booking Not Found"));
            BookingDTO bookingDTO = utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBooking(bookingDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Finding a booking: " + e.getMessage());

        }
        return response;
    }


    @Override
    public Response getAllBooking() {
        Response response = new Response();

        try {
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDTO> bookingDTOList = utils.mapBooKingListEntityToBookingListDTO(bookingList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingList(bookingDTOList);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Getting all bookings: " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();

        try {
            bookingRepository.findById(bookingId).orElseThrow(() -> new OurException("Booking Does Not Exist"));
            bookingRepository.deleteById(bookingId);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Cancelling a booking: " + e.getMessage());

        }
        return response;
    }
}


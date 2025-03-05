package com.hotel_management.HotelManagementSystem.utils;

import com.hotel_management.HotelManagementSystem.dto.BookingDTO;
import com.hotel_management.HotelManagementSystem.dto.RoomDTO;
import com.hotel_management.HotelManagementSystem.dto.UserDTO;
import com.hotel_management.HotelManagementSystem.entity.Booking;
import com.hotel_management.HotelManagementSystem.entity.Room;
import com.hotel_management.HotelManagementSystem.entity.User;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

public class utils {
    private static final String ALPHANUMERIC_STRING="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom(); // A cryptographically secure random number generator

    // generates a random alphanumeric string
    public static String generateRandomConfirmationCode(int length){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<length;i++){
            int randomIndex=secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            char randomChar=ALPHANUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    //Entity to DTO Mapping
    public static UserDTO mapUserEntityToUserDTO(User user){
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

    public static RoomDTO mapRoomEntityToRoomDTO(Room room) {
        RoomDTO roomDTO = new RoomDTO();

        roomDTO.setId(room.getId());
        roomDTO.setRoomType(room.getRoomType());
        roomDTO.setRoomPrice(room.getRoomPrice());
        roomDTO.setRoomPhotoURL(room.getRoomPhotoURL());
        roomDTO.setRoomDescription(room.getRoomDescription());
        return roomDTO;
    }

    public static BookingDTO mapBookingEntityToBookingDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();

        bookingDTO.setId(booking.getId());
        bookingDTO.setCheckInDate(booking.getCheckInDate());
        bookingDTO.setCheckOutDate(booking.getCheckOutDate());
        bookingDTO.setNumOfAdult(booking.getNumOfAdults());
        bookingDTO.setNumOfChildren(booking.getNumOfChildren());
        bookingDTO.setTotalNumOfGuest(booking.getTotalNumOfGuest());
        bookingDTO.setBookingConfirmationCode(booking.getBookingConfirmationCode());
        return bookingDTO;
    }

    public static RoomDTO mapRoomEntityToRoomDTOPlusBookings(Room room){
        RoomDTO roomDTO=new RoomDTO();

        roomDTO.setId(room.getId());
        roomDTO.setRoomType(room.getRoomType());
        roomDTO.setRoomPrice(room.getRoomPrice());
        roomDTO.setRoomPhotoURL(room.getRoomPhotoURL());
        roomDTO.setRoomDescription(room.getRoomDescription());

        // Ensure the room has bookings before processing
        if(room.getBookings()!=null){
            roomDTO.setBooking(room.getBookings()
                    .stream()// Convert the list into a Stream for transformation
                    .map(utils::mapBookingEntityToBookingDTO)// Each Booking entity convert to BookingDTO
                    .collect(Collectors.toList())
            );
        }
        return roomDTO;
    }

    public static UserDTO mapUserEntityToUserDTOPlusUserBookingAndRoom(User user){
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRole(user.getRole());

        if(!user.getBookings().isEmpty()){
            userDTO.setBookings(user.getBookings()
                    .stream()
                    .map(booking -> mapBookingEntityToBookingDTOPlusBookingRooms(booking,false))
                    .collect(Collectors.toList())
            );
        }
        return userDTO;
    }

    public static BookingDTO mapBookingEntityToBookingDTOPlusBookingRooms(Booking booking,Boolean mapUser){
        BookingDTO bookingDTO = new BookingDTO();

        bookingDTO.setId(booking.getId());
        bookingDTO.setCheckInDate(booking.getCheckInDate());
        bookingDTO.setCheckOutDate(booking.getCheckOutDate());
        bookingDTO.setNumOfAdult(booking.getNumOfAdults());
        bookingDTO.setNumOfChildren(booking.getNumOfChildren());
        bookingDTO.setTotalNumOfGuest(booking.getTotalNumOfGuest());
        bookingDTO.setBookingConfirmationCode(booking.getBookingConfirmationCode());

        if(mapUser){
            bookingDTO.setUser(utils.mapUserEntityToUserDTO(booking.getUser()));
        }

        if (booking.getUser() != null){
            RoomDTO roomDTO = new RoomDTO();

            roomDTO.setId(booking.getRoom().getId());
            roomDTO.setRoomType(booking.getRoom().getRoomType());
            roomDTO.setRoomPrice(booking.getRoom().getRoomPrice());
            roomDTO.setRoomPhotoURL(booking.getRoom().getRoomPhotoURL());
            roomDTO.setRoomDescription(booking.getRoom().getRoomDescription());
            bookingDTO.setRoom(roomDTO);
        }
        return bookingDTO;
    }

    //public static List<UserDTO>

}

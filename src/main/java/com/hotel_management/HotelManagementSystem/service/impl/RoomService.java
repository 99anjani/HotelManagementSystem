package com.hotel_management.HotelManagementSystem.service.impl;

import com.hotel_management.HotelManagementSystem.dto.Response;
import com.hotel_management.HotelManagementSystem.dto.RoomDTO;
import com.hotel_management.HotelManagementSystem.entity.Room;
import com.hotel_management.HotelManagementSystem.exception.OurException;
import com.hotel_management.HotelManagementSystem.repo.BookingRepository;
import com.hotel_management.HotelManagementSystem.repo.RoomRepository;
import com.hotel_management.HotelManagementSystem.service.interfac.IRoomService;
import com.hotel_management.HotelManagementSystem.utils.utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService implements IRoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();
        try {
//          String imageUrl = awsS3Service.saveImageToS3(photo);
            Room room = new Room();
//          room.setRoomPhotoUrl(imageUrl);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setRoomDescription(description);
            Room savedRoom = roomRepository.save(room);
            RoomDTO roomDTO = utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomType() {
        return roomRepository.findDistinctByRoomTypes();
    }

    @Override
    public Response getAllRooms() {

        Response response =new Response();
        try{
            List<Room> roomList=roomRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
            List<RoomDTO> roomDTOList = utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;

    }

    @Override
    public Response deleteRoom(Long roomId) {
        return null;
    }

    @Override
    public Response updateRoom(Long roomId, MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();

        try {
//            String imageUrl = null;
//            if (photo != null && !photo.isEmpty()) {
//                imageUrl = awsS3Service.saveImageToS3(photo);
//            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice);
            if (description != null) room.setRoomDescription(description);
//            if (imageUrl != null) room.setRoomPhotoUrl(imageUrl);

            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = utils.mapRoomEntityToRoomDTO(updatedRoom);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(Long roomID) {
        Response response = new Response();

        try {
            roomRepository.findById(roomID).orElseThrow(() -> new OurException("Room Not Found"));
            roomRepository.deleteById(roomID);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response=new Response();

        try{
            List<Room> availableRooms = roomRepository.findAvailableRoomsByDatesAndTypes(checkInDate,checkOutDate,roomType);
            List<RoomDTO> roomDTOList = utils.mapRoomListEntityToRoomListDTO(availableRooms);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAvailableRooms() {
        Response response = new Response();

        try {
            List<Room> roomList=roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoomList(roomDTOList);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }
}

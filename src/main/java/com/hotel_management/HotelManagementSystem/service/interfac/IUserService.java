package com.hotel_management.HotelManagementSystem.service.interfac;

import com.hotel_management.HotelManagementSystem.dto.LoginRequest;
import com.hotel_management.HotelManagementSystem.dto.Response;
import com.hotel_management.HotelManagementSystem.entity.User;

public interface IUserService {
    Response register(User user);
    Response login(LoginRequest loginRequest);
    Response getAllUser();
    Response getUserBookingHistory(String userId);
    Response deleteUser(String userId);
    Response getUserById(String userId);
    Response getMyInfo(String email);

}

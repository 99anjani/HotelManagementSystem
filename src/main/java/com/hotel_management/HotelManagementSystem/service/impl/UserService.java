package com.hotel_management.HotelManagementSystem.service.impl;


import com.hotel_management.HotelManagementSystem.dto.LoginRequest;
import com.hotel_management.HotelManagementSystem.dto.Response;
import com.hotel_management.HotelManagementSystem.dto.UserDTO;
import com.hotel_management.HotelManagementSystem.entity.User;
import com.hotel_management.HotelManagementSystem.exception.OurException;
import com.hotel_management.HotelManagementSystem.repo.UserRepository;
import com.hotel_management.HotelManagementSystem.service.interfac.IUserService;
import com.hotel_management.HotelManagementSystem.utils.JWTUtils;
import com.hotel_management.HotelManagementSystem.utils.utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils; // Handles JSON Web Token (JWT) creation and validation

    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public Response register(User user) {

        Response response = new Response();
        try{
            if(user.getRole()==null || user.getRole().isBlank()){
                user.setRole("USER");
            }
            if(userRepository.existsByEmail(user.getEmail())){
                throw new OurException(user.getEmail() + "Already Exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser=userRepository.save(user);
            UserDTO userDTO= utils.mapUserEntityToUserDTO(savedUser);
            response.setStatusCode(200);
            response.setUser(userDTO);
        }
        catch (OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error occurred during user registration"+ e.getMessage());
        }

        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
            var user= userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()->new OurException("User not found"));

            var token = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 Days");
            response.setMessage("Successfully");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error Occurred During USer Login " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllUser() {
        Response response=new Response();
        try {
            List<User> userList=userRepository.findAll();
            List<UserDTO> userDTOList=utils.mapUserListEntityToUserListDTO(userList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUserList(userDTOList);

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response response=new Response();
        try {
            User user= userRepository.findById(Long.valueOf(userId)).orElseThrow(()->new OurException("User Not Found"));
            UserDTO userDTO=utils.mapUserEntityToUserDTOPlusUserBookingAndRoom(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteUser(String userId) {
        Response response=new Response();
        try {
            userRepository.findById(Long.valueOf(userId)).orElseThrow(()->new OurException("User Not Found"));
            userRepository.deleteById(Long.valueOf(userId));
            response.setStatusCode(200);
            response.setMessage("successful");


        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response=new Response();
        try {
            User user= userRepository.findById(Long.valueOf(userId)).orElseThrow(()->new OurException("User Not Found"));
            UserDTO userDTO=utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response=new Response();
        try {
            User user= userRepository.findByEmail(email).orElseThrow(()->new OurException("User Not Found"));
            UserDTO userDTO=utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        }catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }
}

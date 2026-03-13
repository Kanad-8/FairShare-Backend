package com.example.FairShare.controller;

import com.example.FairShare.dto.userDTO.UserRequestDTO;
import com.example.FairShare.dto.userDTO.UserResponseDTO;
import com.example.FairShare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserService userService;

//    @PostMapping
//    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
//        UserResponseDTO response = userService.createUser(request);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getUserById() {
        UserResponseDTO response = userService.getUserById();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> responses = userService.getAllUsers();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByGroupId(@PathVariable Long groupId) {
        List<UserResponseDTO> responses = userService.getUsersByGroupId(groupId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO response = userService.updateUser(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }

}

package com.example.FairShare.controller;

import com.example.FairShare.dto.groupDTO.GroupRequestDTO;
import com.example.FairShare.dto.groupDTO.GroupResponseDTO;
import com.example.FairShare.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/group")
@RestController
public class GroupController {
    private final GroupService groupService;

    // 1. Create a new group
    @PostMapping
    public ResponseEntity<GroupResponseDTO> createGroup(@Valid @RequestBody GroupRequestDTO requestDTO) {
        GroupResponseDTO response = groupService.createGroup(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Get a specific group by ID
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDTO> getGroupById(@PathVariable Long id) {
        GroupResponseDTO response = groupService.getGroupById(id);
        return ResponseEntity.ok(response);
    }

    // 3. Get all groups
    @GetMapping
    public ResponseEntity<List<GroupResponseDTO>> getAllGroups() {
        List<GroupResponseDTO> responses = groupService.getAllGroups();
        return ResponseEntity.ok(responses);
    }

    // 4. Update a group's name
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDTO> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody GroupRequestDTO requestDTO) {
        GroupResponseDTO response = groupService.updateGroup(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 5. Delete a group
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    // 6. Add users to an existing group
    // Accepts a JSON array of User IDs in the request body: [1, 2, 5]
    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupResponseDTO> addUsersToGroup(
            @PathVariable Long groupId,
            @RequestBody List<Long> userIds) {
        GroupResponseDTO response = groupService.addUserToGroup(userIds, groupId);
        return ResponseEntity.ok(response);
    }

}

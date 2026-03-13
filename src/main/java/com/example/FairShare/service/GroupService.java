package com.example.FairShare.service;

import com.example.FairShare.dto.groupDTO.GroupRequestDTO;
import com.example.FairShare.dto.groupDTO.GroupResponseDTO;
import com.example.FairShare.model.Group;
import com.example.FairShare.model.User;
import com.example.FairShare.repository.GroupRepository;
import com.example.FairShare.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    @Transactional
    public GroupResponseDTO createGroup(GroupRequestDTO requestDTO) {
        Group group = new Group();
        group.setName(requestDTO.name());
        List<Long> userIdList = requestDTO.member();
        List<User> memberList = userRepository.findAllById(userIdList);

        if(userIdList.size() != memberList.size()){
            throw new IllegalArgumentException("One or more userId are invalid");
        }

        for(User user:memberList){
            group.addUser(user);
        }

        Group savedGroup = groupRepository.save(group);

        return mapToResponseDTO(savedGroup);
    }

    @Transactional(readOnly = true)
    public GroupResponseDTO getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + id));

        return mapToResponseDTO(group);
    }

    @Transactional(readOnly = true)
    public List<GroupResponseDTO> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupResponseDTO updateGroup(Long id, GroupRequestDTO requestDTO) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + id));

        group.setName(requestDTO.name());
        Group updatedGroup = groupRepository.save(group);

        return mapToResponseDTO(updatedGroup);
    }

    @Transactional
    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new EntityNotFoundException("Group not found with id: " + id);
        }
        groupRepository.deleteById(id);
    }

    @Transactional
    public GroupResponseDTO addUserToGroup(List<Long> userIdList,Long groupId){
        List<User> users = userRepository.findAllById(userIdList);

        if(userIdList.size() != users.size()){
            throw new IllegalArgumentException("One or more users are invalid");
        }

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found"));

        for(User user:users){
            if(!group.getMembers().contains(user)) {
                group.addUser(user);
            }
        }

        return mapToResponseDTO(group);

    }

    private GroupResponseDTO mapToResponseDTO(Group group) {
        List<Long> memberIds = group.getMembers() != null
                ? group.getMembers().stream()
                .map(User::getUserId)
                .toList() // .toList() is the modern Java 16+ way, replaces Collectors.toList()
                : List.of();

        return new GroupResponseDTO(
                group.getGroupId(),
                group.getName(),
                memberIds,
                group.getCreatedAt()
        );
    }
}


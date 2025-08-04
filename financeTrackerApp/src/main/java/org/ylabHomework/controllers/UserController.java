package org.ylabHomework.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ylabHomework.DTOs.MessageResponseDTO;
import org.ylabHomework.DTOs.userDTOs.GetAllUsersResponseDTO;
import org.ylabHomework.DTOs.userDTOs.UpdateUserRequestDTO;
import org.ylabHomework.DTOs.userDTOs.UpdateUserResponseDTO;
import org.ylabHomework.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<UpdateUserResponseDTO> updateUser(@Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO,
                                                            @PathVariable Long userId) {
        UpdateUserResponseDTO userResponseDTO = userService.updateUser(updateUserRequestDTO, userId);
        return ResponseEntity.ok(userResponseDTO);

    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/block/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponseDTO> blockUser(@PathVariable Long userId) {
        userService.blockUser(userId);
        return ResponseEntity.ok(new MessageResponseDTO("User " + userId + " blocked successfully."));

    }

    @PatchMapping("/unblock/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponseDTO> unblockUser(@PathVariable Long userId) {
        userService.unblockUser(userId);
        return ResponseEntity.ok(new MessageResponseDTO("User " + userId + " unblocked successfully."));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GetAllUsersResponseDTO> getAllUsers(){
        GetAllUsersResponseDTO usersResponseDTO = userService.getAllUsers();
        return ResponseEntity.ok(usersResponseDTO);
    }
}

package org.ylabHomework.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.GoalRequestDTO;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.GoalResponseDTO;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.UpdateGoalRequestDTO;
import org.ylabHomework.serviceClasses.springConfigs.security.UserDetailsImpl;
import org.ylabHomework.services.GoalService;

@RestController
@RequestMapping("/api/v1/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GoalResponseDTO> getUserGoal(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        GoalResponseDTO goalResponseDTO = goalService.getUserGoal(currentUser.getId());
        return ResponseEntity.ok(goalResponseDTO);
    }


    @PatchMapping("/set")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GoalResponseDTO> setUserGoal(@RequestBody @Valid GoalRequestDTO goalRequestDTO,
                                                       @AuthenticationPrincipal UserDetailsImpl currentUser) {
        GoalResponseDTO goalResponseDTO = goalService.setUserGoal(currentUser.getId(), goalRequestDTO);
        return ResponseEntity.ok(goalResponseDTO);
    }

    @PatchMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GoalResponseDTO> updateUserGoal(@RequestBody @Valid UpdateGoalRequestDTO updateRequestDTO,
                                                       @AuthenticationPrincipal UserDetailsImpl currentUser) {
        GoalResponseDTO goalResponseDTO = goalService.updateUserGoal(currentUser.getId(), updateRequestDTO);
        return ResponseEntity.ok(goalResponseDTO);
    }

    @PatchMapping("/reset")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> resetUserGoal(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        goalService.resetUserGoal(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> clearGoalTransactions(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        goalService.clearGoalTransactions(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}

package org.ylabHomework.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ylabHomework.DTOs.ErrorResponse;
import org.ylabHomework.DTOs.MessageResponseDTO;
import org.ylabHomework.DTOs.userDTOs.CreateUserRequestDTO;
import org.ylabHomework.DTOs.userDTOs.CreateUserResponseDTO;
import org.ylabHomework.DTOs.userDTOs.LoginRequestDTO;
import org.ylabHomework.DTOs.userDTOs.LoginResponseDTO;
import org.ylabHomework.serviceClasses.springConfigs.security.JWTCore;
import org.ylabHomework.services.TokenService;
import org.ylabHomework.services.UserService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final JWTCore jwtCore;

    @PostMapping("/signup")
    public ResponseEntity<CreateUserResponseDTO> signup(@RequestBody @Valid CreateUserRequestDTO dto) {
        CreateUserResponseDTO userResponseDTO = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        Authentication authentication;
        authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        String jwt = jwtCore.generateToken(authentication);
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(jwt);
        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            tokenService.blacklistToken(jwt);

            return ResponseEntity.ok(new MessageResponseDTO("Logout successful"));
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Incorrect logout request", LocalDateTime.now()));
    }
}
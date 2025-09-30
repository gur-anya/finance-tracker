package org.ylabHomework.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anyaTasks.DTOs.Event;
import org.anyaTasks.DTOs.UserRegisteredEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.DTOs.userDTOs.*;
import org.ylabHomework.mappers.userMappers.CreateUserMapper;
import org.ylabHomework.mappers.userMappers.GetAllUsersMapper;
import org.ylabHomework.mappers.userMappers.UpdateUserMapper;
import org.ylabHomework.mappers.userMappers.UserMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.customExceptions.EmailAlreadyExistsException;
import org.ylabHomework.serviceClasses.customExceptions.EmptyValueException;
import org.ylabHomework.serviceClasses.customExceptions.NoUserActivenessUpdateException;
import org.ylabHomework.serviceClasses.customExceptions.UserNotFoundException;
import org.ylabHomework.serviceClasses.enums.BudgetNotificationStatus;
import org.ylabHomework.serviceClasses.enums.RoleEnum;
import org.ylabHomework.serviceClasses.security.UserDetailsImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с сущностью User.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 02.08.2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CreateUserMapper createUserMapper;
    private final UpdateUserMapper updateUserMapper;
    private final GetAllUsersMapper getAllUsersMapper;
    private final KafkaProducer kafkaProducer;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail).orElseThrow(UserNotFoundException::new);
        return UserDetailsImpl.build(user);
    }

    @Cacheable(cacheNames = "allUsers")
    public GetAllUsersResponseDTO getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return getAllUsersMapper.toDTO(users);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.toDTO(user);
    }

    @CacheEvict(cacheNames = "allUsers", allEntries = true)
    public CreateUserResponseDTO createUser(CreateUserRequestDTO userRequestDTO) {
        String normalizedEmail = normalizeEmail(userRequestDTO.getEmail());
        Optional<User> foundUser = userRepository.findByEmail(normalizedEmail);
        if (foundUser.isPresent()) {
            throw new EmailAlreadyExistsException();
        }
        User newUser = createUserMapper.toModel(userRequestDTO);
        String rawPassword = newUser.getPassword();
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setRole(RoleEnum.USER);
        newUser.setActive(true);
        newUser.setBudgetLimit(BigDecimal.ZERO);
        newUser.setGoalSum(BigDecimal.ZERO);
        newUser.setBudgetNotificationStatus(BudgetNotificationStatus.NOT_NOTIFIED);
        userRepository.save(newUser);
        UserDTO userDTO = userMapper.toDTO(newUser);
        publishInKafka(userDTO.getId(), userDTO.getName(), userDTO.getEmail());
        return new CreateUserResponseDTO(userDTO);
    }

    @Transactional
    @CacheEvict(cacheNames = "allUsers", allEntries = true)
    public UpdateUserResponseDTO updateUser(UpdateUserRequestDTO updateUserRequestDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (updateUserRequestDTO.getName() != null) {
            if (updateUserRequestDTO.getName().trim().isEmpty()) {
                throw new EmptyValueException("name");
            }
            user.setName(updateUserRequestDTO.getName());
        }
        if (updateUserRequestDTO.getNewPassword() != null && updateUserRequestDTO.getOldPassword() != null) {
            if (updateUserRequestDTO.getNewPassword().trim().isEmpty()) {
                throw new EmptyValueException("new password");
            }
            if (!passwordEncoder.matches(updateUserRequestDTO.getOldPassword(), user.getPassword())) {
                throw new BadCredentialsException("wrong old password");
            }
            user.setPassword(passwordEncoder.encode(updateUserRequestDTO.getNewPassword()));
        }
        return updateUserMapper.toDTO(user);

    }

    @Transactional
    @CacheEvict(cacheNames = "allUsers", allEntries = true)
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(user.getId());
    }

    @Transactional
    @CacheEvict(cacheNames = "allUsers", allEntries = true)
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (!user.isActive()) {
            throw new NoUserActivenessUpdateException(userId, true);
        }
        user.setActive(false);
    }

    @Transactional
    @CacheEvict(cacheNames = "allUsers", allEntries = true)
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (user.isActive()) {
            throw new NoUserActivenessUpdateException(userId, false);
        }
        user.setActive(true);
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.toLowerCase().trim();
    }

    private void publishInKafka(Long userId, String username, String email){
        Event<UserRegisteredEvent> event = new Event<>(UUID.randomUUID(), "user.successful-registration", Instant.now());
        UserRegisteredEvent userEventData = new UserRegisteredEvent(userId, username, email);
        event.setEventData(userEventData);
        try {
            kafkaProducer.publish("user.successful-registration", userId.toString(), event);
        } catch (Exception e) {
            log.error("CRITICAL: User {} was saved to DB, but failed to publish registration event to Kafka.", userId, e);
            // (!): Transactional Outbox
        }

    }
}


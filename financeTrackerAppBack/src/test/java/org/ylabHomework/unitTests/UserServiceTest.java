package org.ylabHomework.unitTests;

import org.anyaTasks.DTOs.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.ylabHomework.DTOs.userDTOs.CreateUserRequestDTO;
import org.ylabHomework.DTOs.userDTOs.UpdateUserRequestDTO;
import org.ylabHomework.DTOs.userDTOs.UpdateUserResponseDTO;
import org.ylabHomework.DTOs.userDTOs.UserDTO;
import org.ylabHomework.mappers.userMappers.CreateUserMapper;
import org.ylabHomework.mappers.userMappers.UpdateUserMapper;
import org.ylabHomework.mappers.userMappers.UserMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.customExceptions.EmptyValueException;
import org.ylabHomework.serviceClasses.customExceptions.UserNotFoundException;
import org.ylabHomework.serviceClasses.enums.RoleEnum;
import org.ylabHomework.services.KafkaProducer;
import org.ylabHomework.services.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UpdateUserMapper updateUserMapper;
    @Mock
    private CreateUserMapper createUserMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    KafkaTemplate<String, Event<?>> kafkaTemplate;
    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void updateUserName_WhenOnlyNameIsProvided_shouldPass() {
        Long userId = 1L;
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
        requestDTO.setName("NewUserName");
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("OldUserName");
        existingUser.setPassword("encodedOldPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        when(updateUserMapper.toDTO(any(User.class))).thenAnswer(invocation -> {
            User userToMap = invocation.getArgument(0);
            UpdateUserResponseDTO response = new UpdateUserResponseDTO();
            response.setUser(new UserDTO(
                userToMap.getId(),
                userToMap.getName(),
                userToMap.getEmail(),
                userToMap.getRole(),
                userToMap.isActive()
            ));
            return response;
        });

        UpdateUserResponseDTO actualResponse = userService.updateUser(requestDTO, userId);


        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getUser().getName()).isEqualTo("NewUserName");

        verify(updateUserMapper).toDTO(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getName()).isEqualTo("NewUserName");
        assertThat(capturedUser.getPassword()).isEqualTo("encodedOldPassword");
    }

    @Test
    void shouldUpdateUserPassword_WhenCorrectOldPasswordIsProvided() {
        Long userId = 1L;
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
        requestDTO.setOldPassword("plainOldPassword");
        requestDTO.setNewPassword("plainNewPassword");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("ExistingName");
        existingUser.setPassword("encodedOldPassword");

        UpdateUserResponseDTO expectedResponse = new UpdateUserResponseDTO();
        expectedResponse.setUser(new UserDTO(userId, "ExistingName", "foo@email.com", RoleEnum.USER, true));


        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("plainOldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("plainNewPassword")).thenReturn("encodedNewPassword");
        when(updateUserMapper.toDTO(any(User.class))).thenReturn(expectedResponse);

        UpdateUserResponseDTO actualResponse = userService.updateUser(requestDTO, userId);
        assertThat(actualResponse).isNotNull();


        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches("plainOldPassword", "encodedOldPassword");
        verify(passwordEncoder).encode("plainNewPassword");

        verify(updateUserMapper).toDTO(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(capturedUser.getName()).isEqualTo("ExistingName");
    }

    @Test
    void updateUser_shouldThrowEmptyValueException_whenNewNameIsBlank() {
        Long userId = 1L;
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
        requestDTO.setName("   ");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(EmptyValueException.class, () -> userService.updateUser(requestDTO, userId));
    }

    @Test
    void updateUser_shouldThrowEmptyValueException_whenNewPasswordIsBlank() {
        Long userId = 1L;
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
        requestDTO.setOldPassword("oldPass");
        requestDTO.setNewPassword("   ");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(EmptyValueException.class, () -> userService.updateUser(requestDTO, userId));
    }

    @Test
    public void updateUser_shouldThrowBadCredentialsException() {
        Long userId = 1L;
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
        requestDTO.setOldPassword("incorrectOldPassword");
        requestDTO.setNewPassword("plainNewPassword");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("ExistingName");
        existingUser.setPassword("encodedOldPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("incorrectOldPassword", "encodedOldPassword")).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> userService.updateUser(requestDTO, userId));
    }

    @Test
    public void updateUser_shouldThrowUserNotFoundException() {
        Long userId = 1L;
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
        requestDTO.setOldPassword("plainOldPassword");
        requestDTO.setNewPassword("plainNewPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(requestDTO, userId));
    }

    @Test
    public void updateUser_shouldPropagateException_whenRepositoryThrowsException() {
        Long userId = 1L;
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
        requestDTO.setOldPassword("plainOldPassword");
        requestDTO.setNewPassword("plainNewPassword");

        when(userRepository.findById(anyLong())).thenThrow(new DataAccessException("Database connection failed") {
        });
        assertThrows(DataAccessException.class, () -> userService.updateUser(requestDTO, userId));
    }

    @Test
    public void createUser_shouldCallKafkaProducer() {
        CreateUserRequestDTO requestDTO = new CreateUserRequestDTO();
        requestDTO.setName("test user");
        requestDTO.setEmail("test@foo.com");
        requestDTO.setPassword("plainPassword");

        User userFromMapper = new User();
        userFromMapper.setName(requestDTO.getName());
        userFromMapper.setEmail(requestDTO.getEmail());
        userFromMapper.setPassword("plainPassword");

        UserDTO finalUserDto = new UserDTO();
        finalUserDto.setId(1L);
        finalUserDto.setName(requestDTO.getName());
        finalUserDto.setEmail(requestDTO.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(createUserMapper.toModel(requestDTO)).thenReturn(userFromMapper);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        when(userMapper.toDTO(any(User.class))).thenReturn(finalUserDto);

        userService.createUser(requestDTO);

        verify(kafkaProducer).publish(anyString(), anyString(), any());
    }
}

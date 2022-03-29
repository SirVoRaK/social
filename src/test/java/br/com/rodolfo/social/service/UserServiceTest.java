package br.com.rodolfo.social.service;

import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    public static final String USERNAME = "TestUser";
    public static final String EMAIL = "test@email.com";
    public static final String PASSWORD = "123456";
    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        this.userService = new UserService(userRepository);
    }

    @Test
    public void canGetAllUsers() {
        userService.getUsers();
        verify(userRepository).findAll();
    }

    @Test
    public void itShouldCreateUser() throws NoSuchAlgorithmException {
        User user = new User()
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setPassword(PASSWORD);

        userService.create(user);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    public void itShouldThrowWhenEmailIsInvalid() throws NoSuchAlgorithmException {
        User user = new User()
                .setUsername(USERNAME)
                .setEmail("dfhgfghfgh")
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email");
    }
}

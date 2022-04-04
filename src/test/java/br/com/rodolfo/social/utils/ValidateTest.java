package br.com.rodolfo.social.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ValidateTest {
    @Test
    public void itShouldValidatePassword() {
        String password = "Password@1234";
        boolean result = Validate.password(password);
        assertThat(result).isTrue();
    }

    @Test
    public void itShouldNotValidatePasswordLessThanMinimum() {
        String password = "Pa@1";
        boolean result = Validate.password(password);
        assertThat(result).isFalse();
    }

    @Test
    public void itShouldNotValidatePasswordGreaterThanMaximum() {
        String password = "Password@1234567890000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        boolean result = Validate.password(password);
        assertThat(result).isFalse();
    }

    @Test
    public void itShouldNotValidatePasswordLowerCase() {
        String password = "password@1234";
        boolean result = Validate.password(password);
        assertThat(result).isFalse();
    }

    @Test
    public void itShouldNotValidatePasswordUpperCase() {
        String password = "PASSWORD@1234";
        boolean result = Validate.password(password);
        assertThat(result).isFalse();
    }

    @Test
    public void itShouldNotValidateWithoutSpecialCharacter() {
        String password = "Password1234";
        boolean result = Validate.password(password);
        assertThat(result).isFalse();
    }

    @Test
    public void itShouldNotValidateWithoutNumber() {
        String password = "Password@";
        boolean result = Validate.password(password);
        assertThat(result).isFalse();
    }

    @Test
    public void itShouldValidateEmail() {
        String email = "email.email_email@email.com";
        boolean result = Validate.email(email);
        assertThat(result).isTrue();
    }

    @Test
    public void itShouldNotValidateEmailWithoutAtSign() {
        String email = "email.email_email.com";
        boolean result = Validate.email(email);
        assertThat(result).isFalse();
    }

    @Test
    public void itShouldNotValidateEmailWithoutDot() {
        String email = "email@emailcom";
        boolean result = Validate.email(email);
        assertThat(result).isFalse();
    }

    @Test
    public void itShouldNotValidateEmailWithoutDomain() {
        String email = "email@.com";
        boolean result = Validate.email(email);
        assertThat(result).isFalse();
    }

    @Test
    public void itShouldNotValidateEmailWithoutUsername() {
        String email = "@email.com";
        boolean result = Validate.email(email);
        assertThat(result).isFalse();
    }

    @Test
    public void itShouldNotValidateEmailWithoutTLD() {
        String email = "email.email_email@email.";
        boolean result = Validate.email(email);
        assertThat(result).isFalse();
    }
}

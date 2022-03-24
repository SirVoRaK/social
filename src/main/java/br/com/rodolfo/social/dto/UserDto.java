package br.com.rodolfo.social.dto;

import br.com.rodolfo.social.model.User;

public class UserDto {
    private String username;
    private String email;

    public UserDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

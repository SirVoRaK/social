package br.com.rodolfo.social.dto;

public class UserTokenDto {
    private final String token;
    private final String type = "Bearer";

    public UserTokenDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public String getType() {
        return this.type;
    }
}

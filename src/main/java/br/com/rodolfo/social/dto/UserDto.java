package br.com.rodolfo.social.dto;

import br.com.rodolfo.social.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDto {
    private String username;
    private String avatarUrl;
    private List<String> following;
    private List<String> followers;

    public UserDto() {
    }

    public UserDto(User user) {
        this.username = user.getUsername();
        this.avatarUrl = user.getAvatarUrl();
        this.following = user.getFollowing();
        this.followers = user.getFollowers();
    }

    public static List<UserDto> convert(List<User> likes) {
        List<UserDto> listDto = new ArrayList<UserDto>();
        for (User user : likes)
            listDto.add(new UserDto(user));
        return listDto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}

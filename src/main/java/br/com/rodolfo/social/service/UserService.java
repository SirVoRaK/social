package br.com.rodolfo.social.service;

import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.InvalidCredentialsException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.json.FileUploadJson;
import br.com.rodolfo.social.jwt.UserJWT;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.UserRepository;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private UserJWT userJWT;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User create(User user) throws IllegalArgumentException {
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new IllegalArgumentException("Email is required");
        if (user.getUsername() == null || user.getUsername().isEmpty())
            throw new IllegalArgumentException("Username is required");
        if (user.getPassword() == null || user.getPassword().isEmpty())
            throw new IllegalArgumentException("Password is required");
        if (this.findByUsername(user.getUsername()).isPresent())
            throw new IllegalArgumentException("Username already taken");
        if (this.findByEmail(user.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already taken");
        if (!this.isEmailValid(user.getEmail()))
            throw new IllegalArgumentException("Invalid email");

        return userRepository.save(user);
    }

    private Boolean isEmailValid(String email) {
        return Pattern.matches("[a-zA-Z_.0-9]+@[a-zA-Z_.0-9]+\\.[a-zA-Z_.0-9]+", email);
    }

    private Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String signin(User user) throws InvalidCredentialsException {
        Optional<User> userOptional = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (userOptional.isEmpty())
            throw new InvalidCredentialsException("Invalid email or password");

        if (userJWT == null)
            this.userJWT = new UserJWT(this.userRepository);

        return this.userJWT.create(userOptional.get().getUsername());
    }

    public Optional<User> validateToken(String token) {
        if (userJWT == null)
            this.userJWT = new UserJWT(this.userRepository);

        return this.userJWT.verify(token);
    }

    public User updateAvatar(String token, MultipartFile file) throws IllegalArgumentException, ForbiddenException {
        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Authorization header needs to start with Bearer");

        User user;
        try {
            user = this.validateToken(token).orElseThrow(() -> new ForbiddenException("Invalid token"));
        } catch (Exception e) {
            throw new ForbiddenException("Invalid token");
        }
        try {
            user.setAvatarUrl(this.uploadAvatar(file));
        } catch (Exception ignored) {
        }
        return this.userRepository.save(user);
    }

    private String uploadAvatar(MultipartFile file) throws IOException {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://natabox.herokuapp.com/files/upload");
        post.setHeader("User", "rodolfo.carneiro@sensedia.com");
        post.setHeader("Password", "rodolfo.carneiro");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("folder", "-1");
        builder.addBinaryBody("file[]", file.getInputStream(), ContentType.APPLICATION_OCTET_STREAM, file.getOriginalFilename());
        HttpEntity multipart = builder.build();
        post.setHeader("Content-Type", "multipart/form-data; boundary=" + multipart.getContentType().getValue());
        post.setEntity(multipart);
        HttpResponse response = client.execute(post);
        HttpEntity responseEntity = response.getEntity();

        String responseString = EntityUtils.toString(responseEntity);
        Gson gson = new Gson();
        FileUploadJson[] fileUploadJson = gson.fromJson(responseString, FileUploadJson[].class);

        return fileUploadJson[0].getPath();
    }

    public User getByName(String authorName) throws NotFoundException {
        return this.userRepository.findByUsername(authorName).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void follow(String token, String userName) throws NotFoundException, ForbiddenException {
        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Token must start with Bearer");

        User user = this.validateToken(token).orElseThrow(() -> new ForbiddenException("Invalid token"));
        User userToFollow = this.getByName(userName);

        // if already following, unfollow
        if (user.getFollowing().contains(userToFollow.getUsername())) {
            user.getFollowing().remove(userToFollow.getUsername());
            userToFollow.getFollowers().remove(user.getUsername());
        } else {
            user.getFollowing().add(userToFollow.getUsername());
            userToFollow.getFollowers().add(user.getUsername());
        }

        this.userRepository.save(user);
        this.userRepository.save(userToFollow);
    }
}

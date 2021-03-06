package br.com.rodolfo.social.service;

import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.InvalidCredentialsException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.exception.UnauthorizedException;
import br.com.rodolfo.social.forms.UserForgotPasswordForm;
import br.com.rodolfo.social.forms.UserResetPasswordForm;
import br.com.rodolfo.social.json.FileUploadJson;
import br.com.rodolfo.social.jwt.UserJWT;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.UserRepository;
import br.com.rodolfo.social.utils.Random;
import br.com.rodolfo.social.utils.SendEmail;
import br.com.rodolfo.social.utils.Validate;
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

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    public static final int passwordMinLength = 8;
    public static final int passwordMaxLength = 32;
    private final SendEmail email = new SendEmail();
    private final List<String> imageExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "svg", "webp");
    private String joinedImageExtensions;
    @Autowired
    private UserRepository userRepository;
    private UserJWT userJWT;

    @Autowired
    private VerificationCodeService verificationCodeService;

    private final Random random = new Random();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.generateJoinedImageExtensions();
    }

    public UserService() {
        this.generateJoinedImageExtensions();
    }

    private void generateJoinedImageExtensions() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < this.imageExtensions.size(); i++) {
            str.append(this.imageExtensions.get(i));
            if (i == this.imageExtensions.size() - 2)
                str.append(" or ");
            else if (i != this.imageExtensions.size() - 1)
                str.append(", ");
        }
        this.joinedImageExtensions = str.toString();
    }

    public User create(User user, Boolean sendEmail) {
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
        if (!Validate.email(user.getEmail()))
            throw new IllegalArgumentException("Invalid email, it should be like: " + Validate.emailExample);
        if (!Validate.password(user.getOriginalPassword()))
            throw new IllegalArgumentException("Invalid password, it should be between " + passwordMinLength + " and " + passwordMaxLength + " characters, and contain at least one number, one uppercase letter, one lowercase letter and one special character. Should be like: " + Validate.passwordExample);

        if (sendEmail == null) sendEmail = true;

        User userSaved = this.userRepository.save(user);
        if (sendEmail) {
            new Thread(() -> {
                try {
                    this.email.send(userSaved.getEmail(), "Welcome, " + user.getUsername(), "Account created successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        return userSaved;
    }

    private Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String signin(User user) throws InvalidCredentialsException {
        User savedUser = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (userJWT == null)
            this.userJWT = new UserJWT(this.userRepository);

        return this.userJWT.create(savedUser.getUsername());
    }

    public User validateToken(String token) throws ForbiddenException {
        if (userJWT == null)
            this.userJWT = new UserJWT(this.userRepository);

        if (token.isEmpty())
            throw new IllegalArgumentException("Missing token in Authorization request header");

        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Token must start with 'Bearer '");

        return this.userJWT.verify(token);
    }

    public User updateAvatar(String token, MultipartFile file) throws IllegalArgumentException, ForbiddenException {
        if (file == null) throw new IllegalArgumentException("The file must be sent in the 'avatar' form-data field");
        User user = this.validateToken(token);

        String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (!this.isImageFile(extension))
            throw new IllegalArgumentException("The file must be an image, it must be a " + this.joinedImageExtensions + " file");

        try {
            user.setAvatarUrl(this.uploadAvatar(file));
        } catch (Exception ignored) {
        }
        return this.userRepository.save(user);
    }

    private boolean isImageFile(String extension) {
        return this.imageExtensions.contains(extension);
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

    public User follow(String token, String userName) throws NotFoundException, ForbiddenException, UnauthorizedException {
        User user = this.validateToken(token);
        User userToFollow = this.getByName(userName);

        if (user.getUsername().equals(userToFollow.getUsername()))
            throw new UnauthorizedException("You cannot follow yourself");

        // if already following, unfollow
        if (user.getFollowing().contains(userToFollow.getUsername())) {
            // unfollow
            user.getFollowing().remove(userToFollow.getUsername());
            userToFollow.getFollowers().remove(user.getUsername());
        } else {
            // follow
            user.getFollowing().add(userToFollow.getUsername());
            userToFollow.getFollowers().add(user.getUsername());
        }

        this.userRepository.save(user);
        this.userRepository.save(userToFollow);

        return userToFollow;
    }

    public void forgotPassword(UserForgotPasswordForm user) throws NotFoundException, MessagingException {
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new IllegalArgumentException("Email must be informed");
        User userFound = this.userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new NotFoundException("This email is not registered"));
        this.verificationCodeService.create(userFound.getEmail());
    }

    public void resetPassword(UserResetPasswordForm user) throws NotFoundException {
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new IllegalArgumentException("Email must be informed");
        if (user.getPassword() == null || user.getPassword().isEmpty())
            throw new IllegalArgumentException("Password must be informed");
        if (user.getCode() == null || user.getCode().isEmpty())
            throw new IllegalArgumentException("Code must be informed");

        User userFound = this.userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new NotFoundException("This email is not registered"));
        if (!this.verificationCodeService.isValid(user.getCode(), userFound.getEmail()))
            throw new NotFoundException("The verification code is not valid or it was not created");
        if (!Validate.password(user.getPassword()))
            throw new IllegalArgumentException("Invalid password, it should be between " + passwordMinLength + " and " + passwordMaxLength + " characters, and contain at least one number, one uppercase letter, one lowercase letter and one special character. Should be like: " + Validate.passwordExample);
        userFound.setPassword(user.getPassword());
        this.verificationCodeService.delete(user.getCode());
        this.userRepository.save(userFound);
    }
}

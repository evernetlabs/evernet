package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.evernet.auth.AuthenticatedUser;
import xyz.evernet.auth.Jwt;
import xyz.evernet.exception.AuthenticationException;
import xyz.evernet.exception.ClientException;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.User;
import xyz.evernet.repository.UserRepository;
import xyz.evernet.request.UserPasswordChangeRequest;
import xyz.evernet.request.UserSignUpRequest;
import xyz.evernet.request.UserTokenRequest;
import xyz.evernet.request.UserUpdateRequest;
import xyz.evernet.response.UserTokenResponse;
import xyz.evernet.util.Password;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final Jwt jwt;

    public User signUp(UserSignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ClientException("Username %s is already taken".formatted(request.getUsername()));
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(Password.hash(request.getPassword()))
                .displayName(request.getDisplayName())
                .build();

        return userRepository.save(user);
    }

    public UserTokenResponse getToken(UserTokenRequest request) {
        User user = userRepository.findByUsername(request.getUsername());

        if (user == null || !Password.verify(user.getPassword(), request.getPassword())) {
            throw new AuthenticationException();
        }

        String token = jwt.getUserToken(AuthenticatedUser
                .builder()
                .username(request.getUsername())
                .build());

        return UserTokenResponse.builder().token(token).build();
    }

    public User get(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new NotFoundException("User %s not found".formatted(username));
        }

        return user;
    }

    public User update(String username, UserUpdateRequest request) {
        User user = get(username);

        if (StringUtils.hasText(request.getDisplayName())) {
            user.setDisplayName(request.getDisplayName());
        }

        return userRepository.save(user);
    }

    public User changePassword(String username, UserPasswordChangeRequest request) {
        User user = get(username);
        user.setPassword(Password.hash(request.getPassword()));
        return userRepository.save(user);
    }

    public User delete(String username) {
        User user = get(username);
        userRepository.delete(user);
        return user;
    }
}

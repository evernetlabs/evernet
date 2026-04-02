package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.evernet.auth.AuthenticatedUser;
import xyz.evernet.auth.Jwt;
import xyz.evernet.bean.NodeAddress;
import xyz.evernet.bean.UserAddress;
import xyz.evernet.exception.AuthenticationException;
import xyz.evernet.exception.ClientException;
import xyz.evernet.exception.NotAllowedException;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.Node;
import xyz.evernet.model.User;
import xyz.evernet.repository.UserRepository;
import xyz.evernet.request.*;
import xyz.evernet.response.UserPasswordResponse;
import xyz.evernet.response.UserTokenResponse;
import xyz.evernet.util.Password;
import xyz.evernet.util.Random;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final NodeService nodeService;

    private final Jwt jwt;

    private final VertexConfigService vertexConfigService;

    public User signUp(String nodeIdentifier, UserSignUpRequest request) {
        Node node = nodeService.get(nodeIdentifier);
        if (!node.getOpen()) {
            throw new NotAllowedException();
        }

        if (userRepository.existsByNodeIdentifierAndUsername(node.getIdentifier(), request.getUsername())) {
            throw new ClientException(String.format("Username %s is already taken on node %s", request.getUsername(), node.getIdentifier()));
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(Password.hash(request.getPassword()))
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .nodeIdentifier(node.getIdentifier())
                .build();

        return userRepository.save(user);
    }

    public UserTokenResponse getToken(String nodeIdentifier, UserTokenRequest request) throws Exception {
        User user = userRepository.findByNodeIdentifierAndUsername(nodeIdentifier, request.getUsername());

        if (user == null || !Password.verify(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException();
        }

        NodeAddress targetNode = null;
        String currentVertexEndpoint = vertexConfigService.getVertexEndpoint();

        if (StringUtils.hasText(request.getTargetNodeAddress())) {
            targetNode = NodeAddress.fromString(request.getTargetNodeAddress());
        } else {
            targetNode = NodeAddress.builder()
                    .identifier(nodeIdentifier)
                    .vertexEndpoint(currentVertexEndpoint)
                    .build();
        }

        String token = jwt.getUserToken(AuthenticatedUser.builder()
                .address(UserAddress.builder()
                        .username(user.getUsername())
                        .nodeAddress(NodeAddress.builder()
                                .identifier(nodeIdentifier)
                                .vertexEndpoint(currentVertexEndpoint)
                                .build())
                        .build())
                .targetNodeAddress(targetNode)
                .build(), nodeService.get(nodeIdentifier).getSigningKey().getPrivateKeyObject());

        return UserTokenResponse.builder().token(token).build();
    }

    public User get(String nodeIdentifier, String username) {
        User user = userRepository.findByNodeIdentifierAndUsername(nodeIdentifier, username);

        if (user == null) {
            throw new NotFoundException(String.format("User %s not found on node %s", username, nodeIdentifier));
        }

        return user;
    }

    public User update(String nodeIdentifier, String username, UserUpdateRequest request) {
        User user = get(nodeIdentifier, username);
        if (StringUtils.hasText(request.getDisplayName())) {
            user.setDisplayName(request.getDisplayName());
        }

        user.setDescription(request.getDescription());
        return userRepository.save(user);
    }

    public User delete(String nodeIdentifier, String username) {
        User user = get(nodeIdentifier, username);
        userRepository.delete(user);
        return user;
    }

    public User changePassword(String nodeIdentifier, String username, UserPasswordChangeRequest request) {
        User user = get(nodeIdentifier, username);
        user.setPassword(Password.hash(request.getPassword()));
        return userRepository.save(user);
    }

    public UserPasswordResponse add(String nodeIdentifier, UserAdditionRequest request, String creator) {
        if (userRepository.existsByNodeIdentifierAndUsername(nodeIdentifier, request.getUsername())) {
            throw new ClientException(String.format("Username %s is already taken on node %s", request.getUsername(), nodeIdentifier));
        }

        String password = Random.generateRandomString(16);
        Node node = nodeService.get(nodeIdentifier);
        User user = User.builder()
                .username(request.getUsername())
                .password(Password.hash(password))
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .nodeIdentifier(node.getIdentifier())
                .creator(creator)
                .build();

        user = userRepository.save(user);

        return UserPasswordResponse.builder().user(user).password(password).build();
    }

    public List<User> list(String nodeIdentifier, Pageable pageable) {
        return userRepository.findByNodeIdentifier(nodeIdentifier, pageable);
    }

    public UserPasswordResponse resetPassword(String nodeIdentifier, String username) {
        User user = get(nodeIdentifier, username);
        user.setPassword(Password.hash(user.getPassword()));
        userRepository.save(user);
        return UserPasswordResponse.builder().user(user).password(user.getPassword()).build();
    }
}

package io.summarizeit.backend.service;

import io.summarizeit.backend.dto.request.ListQuery;
import io.summarizeit.backend.dto.request.auth.RegisterRequest;
import io.summarizeit.backend.dto.request.auth.ResetPasswordRequest;
import io.summarizeit.backend.dto.request.user.UpdateUserRequest;
import io.summarizeit.backend.dto.response.user.UserPaginationResponse;
import io.summarizeit.backend.dto.response.user.UserResponse;
import io.summarizeit.backend.entity.Folder;
import io.summarizeit.backend.entity.Group;
import io.summarizeit.backend.entity.Role;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.entity.specification.criteria.GenericCriteria;
import io.summarizeit.backend.entity.specification.criteria.PaginationCriteria;
import io.summarizeit.backend.event.UserPasswordResetSendEvent;
import io.summarizeit.backend.exception.NotFoundException;
import io.summarizeit.backend.repository.CustomUserRepository;
import io.summarizeit.backend.repository.FolderRepository;
import io.summarizeit.backend.repository.GroupRepository;
import io.summarizeit.backend.repository.RoleRepository;
import io.summarizeit.backend.repository.UserRepository;
import io.summarizeit.backend.security.JwtUserDetails;
import io.summarizeit.backend.util.Constants;
import io.summarizeit.backend.util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final CustomUserRepository customUserRepository;

    private final RoleRepository roleRepository;

    private final GroupRepository groupRepository;

    private final PasswordEncoder passwordEncoder;

    private final PasswordResetTokenService passwordResetTokenService;

    private final ApplicationEventPublisher eventPublisher;

    private final MessageSourceService messageSourceService;

    private final FolderRepository folderRepository;

    // private final GroupService groupService;

    /**
     * Get authentication.
     *
     * @return Authentication
     */
    @Transactional(readOnly = true)
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Return the authenticated user.
     *
     * @return user User
     */
    @Transactional(readOnly = true)
    public User getUser() {
        Authentication authentication = getAuthentication();
        if (authentication.isAuthenticated()) {
            try {
                return findById(getPrincipal(authentication).getId());
            } catch (ClassCastException | NotFoundException e) {
                log.warn("[JWT] User details not found!");
                throw new BadCredentialsException(messageSourceService.get("bad_credentials"));
            }
        } else {
            log.warn("[JWT] User not authenticated!");
            throw new BadCredentialsException(messageSourceService.get("bad_credentials"));
        }
    }

    /**
     * Find a user by id.
     *
     * @param id UUID
     * @return User
     */
    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("user") })));
    }

    /**
     * Find a user by id.
     *
     * @param id String
     * @return User
     */
    @Transactional(readOnly = true)
    public User findById(String id) {
        return findById(UUID.fromString(id));
    }

    /**
     * Find a user by email.
     *
     * @param email String.
     * @return User
     */
    @Transactional(readOnly = true)
    public User findByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("user") })));
    }

    /**
     * Load user details by username.
     *
     * @param email String
     * @return UserDetails
     * @throws UsernameNotFoundException email not found exception.
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserByEmail(final String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("user") })));

        return JwtUserDetails.create(user);
    }

    /**
     * Loads user details by UUID string.
     *
     * @param id String
     * @return UserDetails
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(final String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("user") })));

        return JwtUserDetails.create(user);
    }

    /**
     * Get UserDetails from security context.
     *
     * @param authentication Wrapper for security context
     * @return the Principal being authenticated or the authenticated principal
     *         after authentication.
     */
    @Transactional(readOnly = true)
    public JwtUserDetails getPrincipal(final Authentication authentication) {
        return (JwtUserDetails) authentication.getPrincipal();
    }

    /**
     * Register user.
     *
     * @param request RegisterRequest
     * @return User
     */
    @Transactional
    public User register(final RegisterRequest request) throws BindException {
        log.info("Registering user with email: {}", request.getEmail());

        Folder folder = new Folder();
        folderRepository.save(folder);

        User user = createUser(request);
        user.setRootFolder(folder);
        userRepository.save(user);

        log.info("User registered with email: {}, {}", user.getEmail(), user.getId());

        return user;
    }

    /**
     * Reset password.
     *
     * @param token   String
     * @param request ResetPasswordRequest
     */
    @Transactional
    public void resetPassword(String token, ResetPasswordRequest request) {
        User user = passwordResetTokenService.getUserByToken(token);
        log.info("Resetting password for user with email: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        passwordResetTokenService.deleteByUserId(user.getId());
        log.info("Password reset for user with email: {}", user.getEmail());
    }

    /**
     * Send password reset mail.
     *
     * @param email String
     */
    @Transactional
    public void sendEmailPasswordResetMail(String email) {
        log.info("Sending password reset mail to email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("user") })));

        user.setPasswordResetToken(passwordResetTokenService.create(user));
        eventPublisher.publishEvent(new UserPasswordResetSendEvent(this, user));
        log.info("Password reset mail sent to email: {}", email);
    }

    /**
     * Delete user.
     *
     * @param id UUID
     */
    @Transactional
    public void delete(String id) {
        userRepository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    public UserPaginationResponse list(ListQuery listQuery, UUID organizationId) {

        GenericCriteria criteria = GenericCriteria.builder().ids(listQuery.getIds())
                .search(listQuery.getSearch()).build();
        PaginationCriteria paginationCriteria = PaginationCriteria.builder().page(listQuery.getPage())
                .size(Constants.PAGE_SIZE).build();

        Page<User> users = customUserRepository.findAll(criteria, organizationId,
                PageRequestBuilder.build(paginationCriteria));

        return new UserPaginationResponse(users, users.stream().map(UserResponse::convert).toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getOrganizationUser(UUID id, UUID organizationId) {
        User user = customUserRepository.findOne(id, organizationId)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("user") })));
        ;

        List<UUID> roleIds = user.getRoles().stream().map(Role::getId).toList();
        List<UUID> groupIds = user.getGroups().stream().map(Group::getId).toList();
        return UserResponse.builder().firstName(user.getFirstName()).lastName(user.getLastName())
                .id(user.getId()).email(user.getEmail()).groupIds(groupIds)
                .roleIds(roleIds).build();
    }

    @Transactional
    public void updateOrganizationUser(UUID id, UUID organizationId, UpdateUserRequest updateRequest) {
        User user = customUserRepository.findOneNotOrg(id, organizationId)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("user") })));

        List<Role> roles = roleRepository.findByOrganizationIdAndIdIn(organizationId, updateRequest.getRoleIds());

        List<Group> groups = groupRepository.findByOrganizationIdAndIdIn(organizationId, updateRequest.getGroupIds());

        if (groups.size() != updateRequest.getGroupIds().size() && roles.size() != updateRequest.getRoleIds().size())
            throw new NotFoundException(messageSourceService.get("not_found_with_param",
                    new String[] { messageSourceService.get("group_or_role") }));

        Role defaultRole = roleRepository.findByOrganizationIdAndIsDefault(organizationId, true).get();
        roles.add(defaultRole);

        roles.addAll(user.getRoles());
        groups.addAll(user.getGroups());

        user.setGroups(new HashSet<>(groups));
        user.setRoles(new HashSet<>(roles));

        userRepository.save(user);
    }

    /**
     * Create user.
     *
     * @param request AbstractBaseCreateUserRequest
     * @return User
     */
    @Transactional
    private User createUser(RegisterRequest request) throws BindException {
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    log.error("User with email: {} already exists", request.getEmail());
                    bindingResult.addError(new FieldError(bindingResult.getObjectName(), "email",
                            messageSourceService.get("unique_email")));
                });

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getName())
                .lastName(request.getLastName())
                .build();
    }
}

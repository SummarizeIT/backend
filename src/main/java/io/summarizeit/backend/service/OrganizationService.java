package io.summarizeit.backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.summarizeit.backend.dto.AdminPermissions;
import io.summarizeit.backend.dto.request.organization.CreateOrganizationRequest;
import io.summarizeit.backend.dto.request.organization.UpdateOrganizationRequest;
import io.summarizeit.backend.dto.request.user.InviteUserRequest;
import io.summarizeit.backend.entity.Folder;
import io.summarizeit.backend.entity.Invite;
import io.summarizeit.backend.entity.Organization;
import io.summarizeit.backend.entity.Role;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.event.InviteUserOrganizationEvent;
import io.summarizeit.backend.exception.BadRequestException;
import io.summarizeit.backend.exception.NotFoundException;
import io.summarizeit.backend.repository.CustomUserRepository;
import io.summarizeit.backend.repository.FolderRepository;
import io.summarizeit.backend.repository.InviteRepository;
import io.summarizeit.backend.repository.OrganizationRepository;
import io.summarizeit.backend.repository.RoleRepository;
import io.summarizeit.backend.repository.UserRepository;
import io.summarizeit.backend.repository.content.OrganizationContentStore;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    private final FolderRepository folderRepository;

    private final CustomUserRepository customUserRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final InviteRepository inviteRepository;

    private final MessageSourceService messageSourceService;

    private final UserService userService;

    private final OrganizationContentStore organizationContentStore;

    @Transactional
    public void createOrganization(CreateOrganizationRequest createOrganizationRequest) {
        Folder rootFolder = Folder.builder().name("").isPublic(false).build();
        rootFolder = folderRepository.save(rootFolder);

        Organization organization = Organization.builder().name(createOrganizationRequest.getName())
                .rootFolder(rootFolder).build();

        organization.setRootFolder(rootFolder);
        organizationRepository.save(organization);

        User user = userService.getUser();
        Role defaultRole = Role.builder().name("Default").isDefault(true).organization(organization).build();
        Role adminRole = Role.builder().name("Admin").organization(organization)
                .build();

        roleRepository.saveAll(List.of(defaultRole, adminRole));
        user.addRole(adminRole);
        user.addRole(defaultRole);
        userRepository.save(user);
    }

    @Transactional
    public void updateOrganization(UUID organizationId, UpdateOrganizationRequest updateOrganizationRequest) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("organization") })));
        organization.setName(updateOrganizationRequest.getName());
        organizationRepository.save(organization);
    }

    @Transactional
    public void deleteOrganization(UUID organizationid) {
        organizationRepository.deleteById(organizationid);
    }

    @Transactional
    public void addUserToOrganization(User user, UUID organizationId) {
        Role role = roleRepository.findByOrganizationIdAndIsDefault(organizationId, true)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("role") })));
        user.addRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void kickUserFromOrganization(UUID userId, UUID organizationId) {
        User user = customUserRepository.findOneNotOrg(userId, organizationId)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("user") })));
        userRepository.save(user);
    }

    @Transactional
    public void inviteUserToOrganization(InviteUserRequest inviteUserRequest, UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("organization") })));

        Optional<User> user = userRepository.findByEmailAndRoles_Organization_Id(inviteUserRequest.getEmail(),
                organizationId);
        if (user.isPresent())
            throw new BadRequestException(messageSourceService.get("user_exists_in_org"));

        List<Invite> invites = inviteRepository.findByUserEmail(inviteUserRequest.getEmail());

        if (invites.size() != 0)
            throw new BadRequestException(messageSourceService.get("invite_exists"));

        Invite invite = Invite.builder().userEmail(inviteUserRequest.getEmail()).organization(organization).build();
        inviteRepository.save(invite);

        eventPublisher.publishEvent(new InviteUserOrganizationEvent(this, inviteUserRequest.getEmail(), organization));
        log.info("Invite organization mail sent to email: {}", inviteUserRequest.getEmail());
    }

    @Transactional
    public void updateOrganizationAvatar(UUID organizationId, MultipartFile file) throws IOException {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("organization") })));

        organizationContentStore.setContent(organization, file.getInputStream());
    }

    @Transactional
    public void deleteOrganizationAvatar(UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("organization") })));
        organizationContentStore.unsetContent(organization);
    }
}

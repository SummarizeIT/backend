package io.summarizeit.backend.service;

import static io.summarizeit.backend.util.Constants.getStaticFileUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.summarizeit.backend.dto.AdminPermissions;
import io.summarizeit.backend.dto.request.me.UpdateMeRequest;
import io.summarizeit.backend.dto.response.me.InviteResponse;
import io.summarizeit.backend.dto.response.me.MeResponse;
import io.summarizeit.backend.dto.response.me.UserOrganization;
import io.summarizeit.backend.entity.Invite;
import io.summarizeit.backend.entity.Organization;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.exception.NotFoundException;
import io.summarizeit.backend.repository.InviteRepository;
import io.summarizeit.backend.repository.OrganizationRepository;
import io.summarizeit.backend.repository.UserRepository;
import io.summarizeit.backend.repository.content.UserContentStore;
import io.summarizeit.backend.util.Constants;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;

    private final UserService userService;

    private final InviteRepository inviteRepository;

    private final OrganizationRepository organizationRepository;

    private final OrganizationService organizationService;

    private final UserContentStore userContentStore;

    private final MessageSourceService messageSourceService;

    @Transactional(readOnly = true)
    public MeResponse getMe() {
        User user = userRepository
                .findCustomAllRoles(UUID.fromString(userService.getPrincipal(userService.getAuthentication()).getId()))
                .get(0);

        List<Organization> organizations = organizationRepository.findByRoles_Users_Id(user.getId());

        List<Invite> invites = inviteRepository.findByUserEmail(user.getEmail());

        List<UserOrganization> userOrganizations = new ArrayList<>();
        organizations.forEach(org -> userOrganizations.add(UserOrganization.builder().name(org.getName())
                .avatar(org.getId() != null ? getStaticFileUrl(org.getId()) : "")
                .rootFolder(org.getRootFolder().getId()).id(org.getId()).build()));

        user.getRoles().forEach(role -> {
            userOrganizations.stream().filter(org -> org.getId().equals(role.getOrganization().getId())).findFirst()
                    .ifPresent(org -> {
                        if(role.getName() == Constants.ADMIN_ROLE_NAME) {
                            org.addAllAdminPermissions(List.of(AdminPermissions.values()));
                        }
                        else {
                            org.addAllAdminPermissions(role.getAdminPermissions());
                        }
        });
        });

        List<InviteResponse> userInvites = new ArrayList<>();
        invites.forEach(invite -> userInvites.add(InviteResponse.builder().name(invite.getOrganization().getName())
                .avatar(invite.getOrganization().getId() != null ? getStaticFileUrl(invite.getOrganization().getId())
                        : "")
                .id(invite.getOrganization().getId()).build()));

        String url = user.getAvatarId() != null ? getStaticFileUrl(user.getAvatarId()) : "";

        return MeResponse.builder().firstName(user.getFirstName()).lastName(user.getLastName())
                .id(user.getId()).organizations(userOrganizations).invites(userInvites).rootFolder(user.getRootFolder().getId())
                .avatar(url)
                .build();
    }

    @Transactional
    public void updateMe(UpdateMeRequest updateMeRequest) {
        User user = userService.getUser();
        user.setFirstName(updateMeRequest.getFirstName());
        user.setLastName(updateMeRequest.getLastName());
        userRepository.save(user);
    }

    @Transactional
    public void acceptOrganizationInvite(UUID organizationId) {
        User user = userService.getUser();
        Invite invite = inviteRepository.findByUserEmailAndOrganization_Id(user.getEmail(), organizationId)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("invite") })));
        inviteRepository.delete(invite);
        organizationService.addUserToOrganization(user, organizationId);
    }

    @Transactional
    public void declineOrganizationInvite(UUID organizationId) {
        User user = userService.getUser();
        Invite invite = inviteRepository.findByUserEmailAndOrganization_Id(user.getEmail(), organizationId)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[] { messageSourceService.get("invite") })));
        inviteRepository.delete(invite);
    }

    @Transactional
    public void leaveOrganization(UUID organizationId) {
        User user = userService.getUser();
        organizationService.kickUserFromOrganization(user.getId(), organizationId);
    }

    @Transactional
    public void updateMyAvatar(MultipartFile file) throws IOException {
        User user = userService.getUser();
        userContentStore.setContent(user, file.getInputStream());
    }

    @Transactional
    public void clearMyAvatar() {
        User user = userService.getUser();
        userContentStore.unsetContent(user);
    }
}

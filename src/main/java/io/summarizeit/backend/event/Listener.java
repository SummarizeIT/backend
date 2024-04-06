package io.summarizeit.backend.event;

import io.summarizeit.backend.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Listener {
    private final MailSenderService mailSenderService;

    @EventListener(UserPasswordResetSendEvent.class)
    public void onUserPasswordResetSendEvent(UserPasswordResetSendEvent event) {
        mailSenderService.sendUserPasswordReset(event.getUser());
    }

    @EventListener(InviteUserOrganizationEvent.class)
    public void onInviteUserOrganizationEvent(InviteUserOrganizationEvent event) {
        mailSenderService.sendOrganizationInvite(event.getEmail(), event.getOrganization());
    }
}

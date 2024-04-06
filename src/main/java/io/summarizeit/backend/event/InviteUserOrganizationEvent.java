package io.summarizeit.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import io.summarizeit.backend.entity.Organization;

@Getter
public class InviteUserOrganizationEvent extends ApplicationEvent {
    private final String email;

    private final Organization organization;

    public InviteUserOrganizationEvent(Object source, String email, Organization organization) {
        super(source);
        this.email = email;
        this.organization = organization;
    }
}

package io.summarizeit.backend.event;

import org.springframework.stereotype.Component;

import io.summarizeit.backend.entity.Organization;
import io.summarizeit.backend.repository.content.OrganizationContentStore;
import jakarta.persistence.PostRemove;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class OrganizationListener {
    private final OrganizationContentStore organizationContentStore;

    @PostRemove
    public void removeAvatar(Organization organization) {
        organizationContentStore.unsetContent(organization);
    }
}

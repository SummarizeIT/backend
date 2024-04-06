package io.summarizeit.backend.event;

import org.springframework.stereotype.Component;

import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.repository.content.UserContentStore;
import jakarta.persistence.PostRemove;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserListener {
        private final UserContentStore userContentStore;

    @PostRemove
    public void removeAvatar(User user) {
        userContentStore.unsetContent(user);
    }
}

package io.summarizeit.backend.repository.content;

import java.util.UUID;

import org.springframework.content.commons.store.ContentStore;

import io.summarizeit.backend.entity.User;

public interface UserContentStore extends ContentStore<User, UUID>{}

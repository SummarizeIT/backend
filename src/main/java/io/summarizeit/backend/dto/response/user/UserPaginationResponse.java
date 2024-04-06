package io.summarizeit.backend.dto.response.user;

import org.springframework.data.domain.Page;

import io.summarizeit.backend.dto.response.PaginationResponse;

import java.util.List;

public class UserPaginationResponse extends PaginationResponse<UserResponse> {
    public UserPaginationResponse(final Page<?> pageModel, final List<UserResponse> items) {
        super(pageModel, items);
    }
}
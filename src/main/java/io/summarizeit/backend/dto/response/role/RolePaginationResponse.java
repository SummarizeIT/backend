package io.summarizeit.backend.dto.response.role;

import java.util.List;

import org.springframework.data.domain.Page;

import io.summarizeit.backend.dto.response.PaginationResponse;

public class RolePaginationResponse extends PaginationResponse<RoleResponse> {
    public RolePaginationResponse(final Page<?> pageModel, final List<RoleResponse> items) {
        super(pageModel, items);
    }
}

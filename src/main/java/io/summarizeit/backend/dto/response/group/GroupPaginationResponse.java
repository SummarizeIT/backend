package io.summarizeit.backend.dto.response.group;

import java.util.List;

import org.springframework.data.domain.Page;

import io.summarizeit.backend.dto.response.PaginationResponse;

public class GroupPaginationResponse extends PaginationResponse<GroupResponse> {
    public GroupPaginationResponse(final Page<?> pageModel, final List<GroupResponse> items) {
        super(pageModel, items);
    }
}

package io.summarizeit.backend.util;

import io.summarizeit.backend.entity.specification.criteria.PaginationCriteria;
import io.summarizeit.backend.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Slf4j
public class PageRequestBuilder {
    public static PageRequest build(final PaginationCriteria paginationCriteria) {
        if (paginationCriteria.getPage() == null || paginationCriteria.getPage() < 1) {
            log.warn("Page number is not valid");
            throw new BadRequestException("Page must be greater than 0!");
        }

        paginationCriteria.setPage(paginationCriteria.getPage() - 1);

        if (paginationCriteria.getSize() == null || paginationCriteria.getSize() < 1) {
            log.warn("Page size is not valid");
            throw new BadRequestException("Size must be greater than 0!");
        }

        PageRequest pageRequest = PageRequest.of(paginationCriteria.getPage(), paginationCriteria.getSize());

        if (paginationCriteria.getSort() != null) {
            // Assuming sortString is valid
            String[] sortString = paginationCriteria.getSort().split(":");
            Sort sort = Sort.by(Direction.fromString(sortString[1]), sortString[0]);
            pageRequest.withSort(sort);
        }

        return pageRequest;
    }
}

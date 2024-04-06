package io.summarizeit.backend.entity.specification.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationCriteria {
    private Integer page;

    private Integer size;
    
    private String sort;
}

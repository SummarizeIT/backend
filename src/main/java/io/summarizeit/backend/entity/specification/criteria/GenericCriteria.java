package io.summarizeit.backend.entity.specification.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericCriteria {
    private UUID[] ids;

    private String search;

    public Boolean isEmpty(){
        return (this.ids == null || this.ids.length == 0) && (this.search == null || this.search == "");
    }
}

package io.summarizeit.backend.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ListQueryRequest", description = "Query parameters for list endpoints")
public class ListQuery {
    @Size(max = 50, message = "{max_length}")
    @Schema(name = "search", description = "Search query", type = "String", example = "John Doe")
    private String search;

    @Schema(name = "page", description = "Page number", type = "Integer", example = "1")
    private Integer page;

    @Schema(name = "ids", description = "Only show the following ids", type = "UUID[]", example = "[\"120b2663-412a-4a98-8c7b-19115fd8a0b0\"]")
    private UUID[] ids;
}

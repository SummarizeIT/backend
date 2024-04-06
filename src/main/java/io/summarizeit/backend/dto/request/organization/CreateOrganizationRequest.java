package io.summarizeit.backend.dto.request.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class CreateOrganizationRequest extends UpdateOrganizationRequest{}

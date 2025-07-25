package org.example.wallet.api.dtos.error;

import lombok.Builder;

import java.util.List;

@Builder
public record ErrorDtoResponse (
        List<SingleErrorDto> errors
) {}

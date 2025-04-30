package es.users.records;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
        @Nullable @Schema(name = "nif", requiredMode = Schema.RequiredMode.NOT_REQUIRED) @JsonProperty("nif") String nif,
        @Nullable @Schema(name = "nombre", requiredMode = Schema.RequiredMode.NOT_REQUIRED) @JsonProperty("nombre") String nombre,
        @Nullable @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED) @JsonProperty("email") String email) {
}

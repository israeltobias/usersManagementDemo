package es.users.records;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(@Nullable @JsonProperty("nif") String nif, @Nullable @JsonProperty("nombre") String name,
        @Nullable @JsonProperty("email") String email) {
}

package es.users.util;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;

public class SupportedMediaTypes {

    private SupportedMediaTypes() {
        super();
    }

    private static final Set<String> SUPPORTED_MEDIA_TYPES = Set.of(MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE);

    public static boolean isInvalidAcceptHeader(HttpServletRequest request) {
        String accept = Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT)).orElse("").trim().toLowerCase();
        return !SUPPORTED_MEDIA_TYPES.contains(accept);
    }
}

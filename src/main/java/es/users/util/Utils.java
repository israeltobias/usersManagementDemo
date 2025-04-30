package es.users.util;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.MediaType;

import es.users.Constants;
import jakarta.servlet.http.HttpServletRequest;

public class Utils {

    private Utils() {
        super();
    }

    private static final Set<String> SUPPORTED_MEDIA_TYPES = Set.of(MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE);

    public static boolean isInvalidAcceptHeader(HttpServletRequest request) {
        String accept = Optional.ofNullable(request.getHeader(Constants.ACCEPT_STRING)).orElse("").trim().toLowerCase();
        return !SUPPORTED_MEDIA_TYPES.contains(accept);
    }
}

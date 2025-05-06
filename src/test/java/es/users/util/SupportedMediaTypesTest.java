package es.users.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupportedMediaTypes class test ")
class SupportedMediaTypesTest {

    @Mock
    HttpServletRequest request;

    @ParameterizedTest
    @CsvSource({
            // Valid headers (case insensitive)
            "'application/json',          false", "'APPLICATION/JSON',          false",
            "'application/xml',           false", "'APPLICATION/XML',           false",
            // Invalid headers
            "'text/html',                 true", "'application/pdf',           true",
            "'',                          true", // Empty header
            "' ',                         true", // Whitespace
            "null,                        true", // Null header
            "'application/json;v=2',      true", // With parameters
            "'application/json, text/xml', true" // Multiple media types
    })
    @DisplayName("Is invalid accept header should return correct validation")
    void isInvalidAcceptHeader_ShouldReturnCorrectValidation(String headerValue, boolean expected) {
        when(request.getHeader(anyString())).thenReturn("null".equals(headerValue) ? null : headerValue);
        boolean result = SupportedMediaTypes.isInvalidAcceptHeader(request);
        assertEquals(expected, result);
    }


    @Test
    @DisplayName("Is invalid accept header should handle null request gracefully")
    void isInvalidAcceptHeader_ShouldHandleNullRequestGracefully() {
        assertThrows(NullPointerException.class, () -> SupportedMediaTypes.isInvalidAcceptHeader(null));
    }


    @Test
    @DisplayName("Is invalid accept header should trim and lower case headers")
    void isInvalidAcceptHeader_ShouldTrimAndLowercaseHeaders() {
        when(request.getHeader(anyString())).thenReturn("  APPLICATION/JSON  ");
        assertFalse(SupportedMediaTypes.isInvalidAcceptHeader(request));
    }


    @Test
    @DisplayName("Should have private constructor")
    void shouldHavePrivateConstructor() throws NoSuchMethodException {
        Constructor<SupportedMediaTypes> constructor = SupportedMediaTypes.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor must be private");
        constructor.setAccessible(true);
        assertDoesNotThrow(() -> {
            constructor.newInstance();
        }, "Private constructor instance should not fail");
    }
}

package es.users.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Este controlador es imprescindible para poder mostrar la información recogida en un fichero yaml, de manera
 * independiente, al código generado por: openapi-generator-maven-plugin. Así es posible incluir saltos de línea,
 * ejemplos, negritas, etc.
 * 
 * En el fichero properties es necesario añadir la siguiente línea: springdoc.swagger-ui.url=/sialentidadesOpenapi.yaml
 * 
 */
@RestController
public class AppController {

    @GetMapping(value = "users.yaml", produces = "application/yaml;charset=UTF-8")
    public ResponseEntity<String> openapiYaml() {
        return ResponseEntity.ok(getOpenApiYaml());
    }


    private String getOpenApiYaml() {
        ClassPathResource resource = new ClassPathResource("openApiFiles/users.yaml");
        try (InputStream inputStream = resource.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return "Error loading OpenAPI YAML: " + e.getMessage();
        }
    }
}

package ru.yandex.incoming34.config;

import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.ApiInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    ApiInfo apiInfo() {
        return new ApiInfo(
                "Swagger REST API для загрузки файла и нахождения нужного значения",
                "Тестовое задание",
                componentVersion(),
                null,
                new Contact("Sergei Aidinov", null, "incoming34@yandex.ru"),
                null, null, Collections.emptyList());
    }

    @SuppressWarnings("deprecation")
    private String componentVersion() {
        final String propertiesFileName = "pom.xml";
        String componentVersion = "Версия не указана";
        List<Path> pathList = null;
        try (Stream<Path> files = Files.walk(Paths.get(System.getenv().get("PWD")))) {
            pathList = files
                    .filter(f -> f.getFileName().toString().equals(propertiesFileName))
                    .toList();
            System.out.println();

        } catch (IOException ignored) {
        }
        if (Objects.nonNull(pathList) && pathList.isEmpty()) return componentVersion;
        File file = new File(String.valueOf(pathList.get(0)));
        XmlMapper xmlMapper = new XmlMapper();
        try {
            JsonSchema jsonSchema = xmlMapper.generateJsonSchema(String.class);
            JsonSchema json = xmlMapper.readValue(file, jsonSchema.getClass());
            componentVersion = Objects.nonNull(json.getSchemaNode().get("version"))
                    ? String.valueOf(json.getSchemaNode().get("version")).replaceAll("\"", "")
                    : componentVersion;
        } catch (Exception e) {
            return componentVersion;
        }
        return componentVersion;
    }

}

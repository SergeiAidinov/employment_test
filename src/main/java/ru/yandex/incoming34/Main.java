package ru.yandex.incoming34;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Slf4j
public class Main {

    public static void main(String[] args) throws LifecycleException {
        Properties props = new Properties();
        final String propertiesFileName = "application.properties";
        List<Path> pathList = null;
        try (Stream<Path> files = Files.walk(Paths.get(System.getenv().get("PWD")))) {
            pathList = files
                    .filter(f -> f.getFileName().toString().equals(propertiesFileName))
                    .toList();
            System.out.println();

        } catch (IOException ignored) {
        }
        File file = new File(String.valueOf(pathList.get(0)));
        int port = Integer.parseInt(props.getProperty("server.port", "8080"));
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector().setEnableLookups(true);
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        var context = tomcat.addContext("", baseDir.getAbsolutePath());
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(ru.yandex.incoming34.config.AppConfig.class);
        appContext.register(ru.yandex.incoming34.config.SwaggerConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(appContext);
        Tomcat.addServlet(context, "dispatcher", dispatcherServlet);
        context.addServletMappingDecoded("/", "dispatcher");
        tomcat.start();
        System.out.println("Server started at http://localhost:" + port);
        tomcat.getServer().await();
    }
}
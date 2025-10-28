package ru.yandex.incoming34;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws LifecycleException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
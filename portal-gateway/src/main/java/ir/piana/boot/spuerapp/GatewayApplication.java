package ir.piana.boot.spuerapp;

import ir.piana.boot.spuerapp.servlet.StripPrefixGatewayServlet;
import jakarta.servlet.http.HttpServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class GatewayApplication
{
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @Bean
    public ServletRegistrationBean<HttpServlet> AuthServlet(RestClient restClient) {
        ServletRegistrationBean<HttpServlet> servRegBean = new ServletRegistrationBean<>();
        servRegBean.setServlet(new StripPrefixGatewayServlet(restClient, "http://localhost:8081", 1));
        servRegBean.addUrlMappings("/auth/*");
        servRegBean.setLoadOnStartup(1);
        return servRegBean;
    }
}

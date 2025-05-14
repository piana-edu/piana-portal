package ir.piana.boot.spuerapp.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author m.rahmati on 12/10/2024
 */
@Component
@ConfigurationProperties(prefix = "super-app.gateway")
public class SuperAppProperties {
    private AppPropertyModel auth;

    public AppPropertyModel getAuth() {
        return auth;
    }

    public void setAuth(AppPropertyModel auth) {
        this.auth = auth;
    }
}

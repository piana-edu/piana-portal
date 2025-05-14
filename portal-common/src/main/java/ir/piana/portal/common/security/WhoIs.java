package ir.piana.portal.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface WhoIs {
    @JsonIgnore
    String getAuthentication();
}

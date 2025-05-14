package ir.piana.portal.common.security;

import java.util.ArrayList;
import java.util.List;

public record AuthenticatedInfo(
        String username,
        String uuid,
        String mobile,
        String mail,
        List<String> authorities
)  implements WhoIs {
    public AuthenticatedInfo(String uuid) {
        this(uuid, "anonymous", null, null, new ArrayList<>() {{
            add("anonymous");
        }});
    }

    public AuthenticatedInfo(String username, String mobile, String mail, List<String> authorities) {
        this(username, null, mobile, mail, authorities);
    }

    @Override
    public String getAuthentication() {
        return mobile != null ? mobile : (mail != null ? mail : uuid);
    }
}

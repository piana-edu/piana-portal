import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.portal.common.security.AuthenticatedInfo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

public class AuthenticatedInfoSerializeTest {

    @Test
    public void test() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        AuthenticatedInfo authenticatedInfo = new AuthenticatedInfo(
                "a", UUID.randomUUID().toString(), "0912", "1@gmail", Arrays.asList("roles"));

        String s = objectMapper.writeValueAsString(authenticatedInfo);
        System.out.println(s);
        objectMapper.readValue(s, AuthenticatedInfo.class);

        AuthenticatedInfo authenticatedInfo2 = new AuthenticatedInfo(
                "a", null, "0912", "1@gmail", null);
        String s2 = objectMapper.writeValueAsString(authenticatedInfo2);
        System.out.println(s2);
        objectMapper.readValue(s2, AuthenticatedInfo.class);
    }
}

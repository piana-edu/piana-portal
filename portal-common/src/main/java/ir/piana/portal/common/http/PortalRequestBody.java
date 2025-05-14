package ir.piana.portal.common.http;

import ir.piana.portal.common.security.AuthenticatedInfo;

public record PortalRequestBody<T> (
        AuthenticatedInfo authenticatedInfo,
        T requestBody
) {
}

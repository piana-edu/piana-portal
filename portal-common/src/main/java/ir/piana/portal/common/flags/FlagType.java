package ir.piana.portal.common.flags;

import ir.piana.portal.common.errors.OneHttpHeader;
import jakarta.servlet.http.HttpServletResponse;

public enum FlagType {
    NEED_TO_AUTHENTICATION("need-to-authentication"),
    NEED_TO_CHANGE_AUTHENTICATED_INFO("need-to-change-authenticated-info"),
    ;

    static final String httpHeaderFlagName = "portal-flag";

    private String flag;

    FlagType(String flag) {
        this.flag = flag;
    }

    public static FlagType byFlag(String flag) {
        for (FlagType value : FlagType.values()) {
            if (value.getFlag().equals(flag)) {
                return value;
            }
        }
        return null;
    }

    static FlagType fromResponseHeader(HttpServletResponse response) {
        if (response.getHeader(httpHeaderFlagName) != null && !response.getHeader(httpHeaderFlagName).trim().isEmpty()) {
            return byFlag(response.getHeader(httpHeaderFlagName));
        }
        return null;
    }

    public String getFlag() {
        return flag;
    }

    public OneHttpHeader getHeader() {
        return new OneHttpHeader(httpHeaderFlagName, flag);
    }
}

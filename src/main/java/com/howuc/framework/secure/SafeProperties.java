package com.howuc.framework.secure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import static com.howuc.framework.secure.SafeProperties.PREFIX;

@Data
@RefreshScope
@ConfigurationProperties(prefix = PREFIX)
public class SafeProperties {
    public static final String PREFIX = "safe";

    /**
     * permission key store in redis
     */
    private String permissionKey = "safe:pms";

    /**
     * permission cache expire seconds
     */
    private long permissionTTL = 300;

    /**
     * key for encrypt/decrypt client token
     */
    private String aesKey = "";

    /**
     * whether hide not exposed handler
     */
    private boolean hideNotExposedHandler = true;

    /**
     * token name
     */
    private String tokenSymbol = "X-Token";

    /**
     * token brings way
     */
    private WorkMode workMode = WorkMode.HEADER;

    /**
     * cookie domain value when WorkMode is COOKIE
     */
    private String cookieDomain = "";

    /**
     * cookie path value when WorkMode is COOKIE
     */
    private String cookiePath = "/";

    /**
     * cookie age value when WorkMode is COOKIE
     */
    private int cookieAge = -1;

    /**
     * token expired seconds after last access time
     */
    private long expireAfter = 172800;

    public enum WorkMode {
        HEADER, COOKIE
    }
}


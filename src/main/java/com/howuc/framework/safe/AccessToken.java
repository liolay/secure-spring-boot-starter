package com.howuc.framework.safe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.servlet.http.Cookie;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {
    @JsonIgnore
    public static ObjectMapper objectMapper;
    @JsonIgnore
    public static SafeProperties safeProperties;

    private String id;
    private Long subjectId;
    private String subjectName;
    private Map<String, String> subjectData;
    private boolean identified;
    private long createTime;
    private long lastTime;

    public static AccessToken create(long subjectId) {
        AccessToken accessToken = create();
        accessToken.setSubjectId(subjectId);
        return accessToken;
    }

    public static AccessToken create(long subjectId, String subjectName) {
        AccessToken accessToken = create(subjectId);
        accessToken.setSubjectName(subjectName);
        return accessToken;
    }

    public static AccessToken create() {
        AccessToken accessToken = new AccessToken();
        accessToken.setId(UUID.randomUUID().toString());
        accessToken.setCreateTime(Instant.now().getEpochSecond());
        accessToken.setLastTime(Instant.now().getEpochSecond());
        return accessToken;
    }

    public AccessToken touch() {
        identified = isAuthenticated();
        this.lastTime = Instant.now().getEpochSecond();
        return this;
    }

    @JsonIgnore
    public boolean isAuthenticated() {
        return identified && !isExpired();
    }

    @JsonIgnore
    private boolean isExpired() {
        return this.lastTime + safeProperties.getExpireAfter() < Instant.now().getEpochSecond();
    }

    public AccessToken withData(Map<String, String> data) {
        this.subjectData = data;
        return this;
    }

    public AccessToken revokeAuthenticate() {
        this.identified = false;
        return this;
    }

    public AccessToken authenticate() {
        if (this.subjectId == null) throw new RuntimeException("required subjectId is not provide");

        this.identified = true;
        return this;
    }

    public AccessToken store() {
        WebContext.get().setAccessToken(this);
        return this;
    }

    public void write() {
        try {
            String token = AES.encrypt(objectMapper.writeValueAsString(this));

            if (safeProperties.getWorkMode() == SafeProperties.WorkMode.COOKIE) {
                if (WebContext.get().getRequest().getCookies() != null){
                    for (Cookie cookie : WebContext.get().getRequest().getCookies()) {
                        if (cookie.getName().equals(safeProperties.getTokenSymbol())) {
                            cookie.setMaxAge(0);
                            cookie.setValue(null);
                            WebContext.get().getResponse().addCookie(cookie);
                        }
                    }
                }


                Cookie cookie = new Cookie(safeProperties.getTokenSymbol(), token);
                cookie.setDomain(safeProperties.getCookieDomain());
                cookie.setPath(safeProperties.getCookiePath());
                cookie.setMaxAge(safeProperties.getCookieAge());
                cookie.setHttpOnly(true);
                WebContext.get().getResponse().addCookie(cookie);
            } else {
                WebContext.get().getResponse().setHeader(safeProperties.getTokenSymbol(), token);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.howuc.framework.secure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Data
public class SessionManager {
    public static SafeProperties safeProperties;
    public static StringRedisTemplate redisTemplate;
    public static ObjectProvider<AuthorizingService> authorizingServiceProvider;
    public static ObjectMapper objectMapper;

    private static String getPmsKey(long accountId) {
        return "permissions:account_id:" + accountId;
    }

    private static String getRoleKey(long accountId) {
        return "roles:account_id:" + accountId;
    }

    public static void clearAllRoleAndPermission() {
        redisTemplate.delete(safeProperties.getPermissionKey());
    }

    public static void clearRoleAndPermission(long accountId) {
        redisTemplate.execute(
                new DefaultRedisScript<Void>("redis.call('HDEL',KEYS[1],KEYS[2],KEYS[3])"),
                Lists.newArrayList(safeProperties.getPermissionKey(), getRoleKey(accountId), getPmsKey(accountId))
        );
    }

    public static void clearRole(long accountId) {
        redisTemplate.execute(
                new DefaultRedisScript<Void>("redis.call('HDEL',KEYS[1],KEYS[2])"),
                Lists.newArrayList(safeProperties.getPermissionKey(), getRoleKey(accountId))
        );
    }

    public static void clearPermission(long accountId) {
        redisTemplate.execute(
                new DefaultRedisScript<Void>("redis.call('HDEL',KEYS[1],KEYS[2])"),
                Lists.newArrayList(safeProperties.getPermissionKey(), getPmsKey(accountId))
        );
    }

    public static Set<String> getPermission() {
        return getPms(subjectId -> authorizingServiceProvider.getObject().permissions(subjectId));
    }

    public static Set<String> getRole() {
        return getPms(subjectId -> authorizingServiceProvider.getObject().roles(subjectId));
    }

    public static boolean hasRole(String... role) {
        return getRole().containsAll(Arrays.asList(role));
    }

    public static boolean hasAnyRole(String... role) {
        return Stream.of(role).anyMatch(getRole()::contains);
    }

    public static boolean hasPermission(String... pms) {
        return getPermission().containsAll(Arrays.asList(pms));
    }

    public static boolean hasAnyPermission(String... pms) {
        return Stream.of(pms).anyMatch(getPermission()::contains);
    }

    public static boolean isLogin() {
        return WebContext.get().getAccessToken().isAuthenticated();
    }

    public static Long getSubjectId() {
        return WebContext.get().getAccessToken().getSubjectId();
    }

    public static String getSubjectName() {
        return WebContext.get().getAccessToken().getSubjectName();
    }

    public static Map<String, String> getSubjectData() {
        return WebContext.get().getAccessToken().getSubjectData();
    }

    public static Set<String> getPms(Function<Long, Set<String>> pmsSupplier) {
        Long subjectId = WebContext.get().getAccessToken().getSubjectId();
        if (subjectId == null) return Sets.newHashSetWithExpectedSize(0);

        BoundHashOperations<String, String, String> pmsStore = redisTemplate.boundHashOps(safeProperties.getPermissionKey());

        String pmsKey = getRoleKey(subjectId);
        if (BooleanUtils.isNotTrue(pmsStore.hasKey(pmsKey))) {
            Set<String> pms = pmsSupplier.apply(subjectId);
            if (pms == null) {
                pms = Sets.newHashSetWithExpectedSize(0);
            }

            try {
                pmsStore.put(pmsKey, objectMapper.writeValueAsString(new PmsCache(Instant.now().plusSeconds(safeProperties.getPermissionTTL()).getEpochSecond(), pms)));
            } catch (JsonProcessingException e) {
                log.error("save permission error", e);
            }
            return pms;
        }

        try {
            PmsCache cachedPms = objectMapper.readValue(Objects.requireNonNull(pmsStore.get(pmsKey)), PmsCache.class);
            if (cachedPms.isExpired()) {
                log.info("permission cache expired, read new");

                redisTemplate.boundHashOps(safeProperties.getPermissionKey()).delete(pmsKey);
                return getRole();
            }
            return cachedPms.getPms();
        } catch (JsonProcessingException e) {
            log.error("read permission error", e);
        }
        return Sets.newHashSet();
    }

    public static void logout() {
        WebContext.get().getAccessToken().revokeAuthenticate().store().write();
    }
}

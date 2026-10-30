package io.rd.qltb.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

/**
 * Lấy username cho cả 2 luồng:
 * 1. Web QLTB login cookie/OIDC → {@link OidcUser}
 * 2. Gateway / mobile Bearer JWT → {@link Jwt}
 */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static String name(OidcUser oidcUser, Jwt jwt) {
        if (oidcUser != null && StringUtils.hasText(oidcUser.getName())) {
            return oidcUser.getName();
        }
        if (jwt != null) {
            return fromJwt(jwt);
        }
        return name();
    }

    public static String name() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Chưa đăng nhập");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getName();
        }
        if (principal instanceof Jwt jwt) {
            return fromJwt(jwt);
        }
        String name = authentication.getName();
        if (StringUtils.hasText(name) && !"anonymousUser".equals(name)) {
            return name;
        }
        throw new IllegalStateException("Không lấy được user từ cookie login hoặc JWT");
    }

    private static String fromJwt(Jwt jwt) {
        String preferred = jwt.getClaimAsString("preferred_username");
        if (StringUtils.hasText(preferred)) {
            return preferred;
        }
        String username = jwt.getClaimAsString("username");
        if (StringUtils.hasText(username)) {
            return username;
        }
        if (StringUtils.hasText(jwt.getSubject())) {
            return jwt.getSubject();
        }
        throw new IllegalStateException("JWT không có preferred_username");
    }
}

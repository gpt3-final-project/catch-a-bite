package com.deliveryapp.catchabite.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityUtil: read current authenticated principal from session context.
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static CurrentUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Unauthorized");
        }

        String principal = authentication.getName();
        String[] parts = parsePrincipal(principal);
        return new CurrentUser(parts[0], parts[1]);
    }

    private static String[] parsePrincipal(String principal) {
        if (principal == null || !principal.contains(":")) {
            throw new IllegalStateException("Unauthorized");
        }
        String[] parts = principal.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalStateException("Unauthorized");
        }
        String accountType = parts[0].trim().toUpperCase();
        String loginKey = parts[1].trim();
        if (accountType.isBlank() || loginKey.isBlank()) {
            throw new IllegalStateException("Unauthorized");
        }
        return new String[] { accountType, loginKey };
    }

    public record CurrentUser(String accountType, String loginKey) {
    }
}

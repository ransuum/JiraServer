package org.parent.jira.security.cookiemanagement;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.parent.jira.exception.TokenNotFoundException;

import java.util.Arrays;

public record CookieUtils(Cookie[] cookies) {
    public CookieUtils(Cookie[] cookies) {
        this.cookies = cookies == null ? null : cookies.clone();
    }

    public String getRefreshTokenCookie() {
        return cookies == null ? null : Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token is not found in cookies"));
    }

    public void clearCookies(HttpServletResponse response) {
        final var clearCookie = "refreshToken=deleted" +
                "; Max-Age=0" +
                "; Path=/" +
                "; HttpOnly" +
                "; Secure" +
                "; SameSite=None";

        response.setHeader("Set-Cookie", clearCookie);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final CookieUtils that = (CookieUtils) o;
        return Arrays.equals(cookies(), that.cookies());
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(cookies);
    }

    @Override
    public @NonNull String toString() {
        return "CookieUtils{" +
                "cookies=" + Arrays.toString(cookies) +
                '}';
    }
}

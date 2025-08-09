package org.parent.jira.utils;

import java.security.SecureRandom;
import java.util.stream.IntStream;

public final class PasswordUtils {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtils() {}

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));

        return sb.toString();
    }

    public static String generateSecure(int length) {
        if (length < 4) length = 12;

        final var upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final var lower = "abcdefghijklmnopqrstuvwxyz";
        final var digits = "0123456789";
        final var symbols = "!@#$%^&*";

        StringBuilder password = new StringBuilder();
        password.append(upper.charAt(RANDOM.nextInt(upper.length())));
        password.append(lower.charAt(RANDOM.nextInt(lower.length())));
        password.append(digits.charAt(RANDOM.nextInt(digits.length())));
        password.append(symbols.charAt(RANDOM.nextInt(symbols.length())));

        IntStream.range(4, length).forEach(_ ->
                password.append(CHARS.charAt(RANDOM.nextInt(CHARS.length()))));

        char[] chars = password.toString().toCharArray();
        final int lengthOfChar = chars.length;

        IntStream.range(0, lengthOfChar).forEach(i -> {
            final int randomIndex = RANDOM.nextInt(lengthOfChar);
            final char temp = chars[i];
            chars[i] = chars[randomIndex];
            chars[randomIndex] = temp;
        });

        return new String(chars);
    }
}

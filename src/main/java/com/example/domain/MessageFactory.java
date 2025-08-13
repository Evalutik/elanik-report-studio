package com.example.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class MessageFactory {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "lang.messages",
            Locale.forLanguageTag("ru"),
            new ResourceBundle.Control() {
                @Override
                public ResourceBundle newBundle(
                        String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
                    // The below is nearly identical to the default, except for UTF-8
                    String bundleName = toBundleName(baseName, locale);
                    String resourceName = toResourceName(bundleName, "properties");
                    try (InputStream is = loader.getResourceAsStream(resourceName)) {
                        if (is == null) return null;
                        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                            return new PropertyResourceBundle(reader);
                        }
                    }
                }
            }
    );

    private MessageFactory() {}

    public static String get(String key, Object... args) {
        String pattern = BUNDLE.getString(key);
        return MessageFormat.format(pattern, args);
    }
}

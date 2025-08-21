package com.example.models;

import java.util.Objects;

public final class ReportOptions {
    public enum Format { PDF, HTML }
    public enum Lang { RU, EN }

    private final Format format;
    private final Lang lang;

    public ReportOptions(Format format, Lang lang) {
        this.format = Objects.requireNonNull(format);
        this.lang   = Objects.requireNonNull(lang);
    }

    public Format getFormat() { return format; }
    public Lang getLang()     { return lang; }
}
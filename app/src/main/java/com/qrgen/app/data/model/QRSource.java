package com.qrgen.app.data.model;

public enum QRSource {
    GENERATED("generated"),
    SCANNED("scanned");

    private final String value;

    QRSource(String value) {
        this.value = value;
    }

    public String getValue() { return value; }

    public static QRSource fromValue(String value) {
        for (QRSource source : values()) {
            if (source.value.equals(value)) return source;
        }
        return GENERATED;
    }
}
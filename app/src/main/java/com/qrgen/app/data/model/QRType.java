package com.qrgen.app.data.model;

public enum QRType {
    TEXT("Текст", "text"),
    URL("URL", "url"),
    WIFI("Wi-Fi", "wifi"),
    CONTACT("Контакт", "contact"),
    EMAIL("Email", "email"),
    PHONE("Телефон", "phone"),
    SMS("SMS", "sms"),
    GEO("Геолокация", "geo");

    private final String displayName;
    private final String value;

    QRType(String displayName, String value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() { return displayName; }
    public String getValue() { return value; }

    public static QRType fromValue(String value) {
        for (QRType type : values()) {
            if (type.value.equals(value)) return type;
        }
        return TEXT;
    }
}
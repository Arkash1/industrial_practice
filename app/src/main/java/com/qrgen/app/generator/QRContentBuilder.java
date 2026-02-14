package com.qrgen.app.generator;

public class QRContentBuilder {

    public static String buildTextContent(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Текст не может быть пустым");
        }
        return text.trim();
    }

    public static String buildUrlContent(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL не может быть пустым");
        }
        String trimmed = url.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "https://" + trimmed;
        }
        return trimmed;
    }

    public static String buildWifiContent(String ssid, String password,
                                          String encryptionType, boolean hidden) {
        if (ssid == null || ssid.trim().isEmpty()) {
            throw new IllegalArgumentException("SSID не может быть пустым");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("WIFI:");
        sb.append("T:").append(encryptionType != null ? encryptionType : "nopass").append(";");
        sb.append("S:").append(escapeWifiField(ssid)).append(";");

        if (password != null && !password.isEmpty() && !"nopass".equals(encryptionType)) {
            sb.append("P:").append(escapeWifiField(password)).append(";");
        }

        if (hidden) {
            sb.append("H:true;");
        }

        sb.append(";");
        return sb.toString();
    }

    public static String buildVCardContent(String name, String phone,
                                           String email, String organization,
                                           String address) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCARD\n");
        sb.append("VERSION:3.0\n");
        sb.append("FN:").append(name.trim()).append("\n");

        if (phone != null && !phone.trim().isEmpty()) {
            sb.append("TEL:").append(phone.trim()).append("\n");
        }
        if (email != null && !email.trim().isEmpty()) {
            sb.append("EMAIL:").append(email.trim()).append("\n");
        }
        if (organization != null && !organization.trim().isEmpty()) {
            sb.append("ORG:").append(organization.trim()).append("\n");
        }
        if (address != null && !address.trim().isEmpty()) {
            sb.append("ADR:").append(address.trim()).append("\n");
        }

        sb.append("END:VCARD");
        return sb.toString();
    }

    public static String buildEmailContent(String address, String subject, String body) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("mailto:").append(address.trim());

        boolean hasParams = false;
        if (subject != null && !subject.trim().isEmpty()) {
            sb.append("?subject=").append(subject.trim());
            hasParams = true;
        }
        if (body != null && !body.trim().isEmpty()) {
            sb.append(hasParams ? "&" : "?");
            sb.append("body=").append(body.trim());
        }

        return sb.toString();
    }

    public static String buildPhoneContent(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Номер телефона не может быть пустым");
        }
        return "tel:" + phone.trim();
    }

    public static String buildSmsContent(String phone, String message) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Номер телефона не может быть пустым");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("smsto:").append(phone.trim());
        if (message != null && !message.trim().isEmpty()) {
            sb.append(":").append(message.trim());
        }
        return sb.toString();
    }

    public static String buildGeoContent(double latitude, double longitude, String label) {
        StringBuilder sb = new StringBuilder();
        sb.append("geo:").append(latitude).append(",").append(longitude);
        if (label != null && !label.trim().isEmpty()) {
            sb.append("?q=").append(latitude).append(",").append(longitude);
            sb.append("(").append(label.trim()).append(")");
        }
        return sb.toString();
    }

    private static String escapeWifiField(String field) {
        return field.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace(":", "\\:");
    }

    public static int getMaxContentLength() {
        return 4296;
    }

    public static boolean isContentTooLong(String content) {
        return content != null && content.length() > getMaxContentLength();
    }
}
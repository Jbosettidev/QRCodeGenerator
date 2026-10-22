package com.bosetti.qrcode.generator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class QRCodeGeneratorService {

    @Value("${qrcode.output.directory:${user.home}/Downloads}")
    private String outputDirectory;

    public byte[] generate(QRCodeRequest request) throws Exception {
        String content = buildContent(request);

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2);

        BitMatrix matrix = new MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                400,
                400,
                hints
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", output);

        byte[] image = output.toByteArray();
        saveImage(image);

        return image;
    }

    private void saveImage(byte[] image) throws IOException {
        Path directory = Paths.get(outputDirectory)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(directory);

        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")
        );

        Path file = directory.resolve("QRCode-" + timestamp + ".png");

        Files.write(file, image);

        System.out.println("QR Code salvo em: " + file);
    }

    private String buildContent(QRCodeRequest request) {
        if (request == null || request.type() == null) {
            throw new IllegalArgumentException(
                    "Selecione o tipo de QR Code."
            );
        }

        return switch (request.type().toLowerCase(Locale.ROOT)) {
            case "url" -> buildUrl(request.url());
            case "wifi" -> buildWifi(request);
            default -> throw new IllegalArgumentException(
                    "Tipo inválido. Use url ou wifi."
            );
        };
    }

    private String buildUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Informe um link.");
        }

        url = url.trim();

        if (!url.matches("(?i)^https?://.*")) {
            url = "https://" + url;
        }

        try {
            java.net.URI uri = java.net.URI.create(url);

            if (uri.getHost() == null ||
                    !("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme()))) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Informe uma URL válida.");
        }

        return url;
    }

    private String buildWifi(QRCodeRequest request) {
        String ssid = request.ssid();
        String password = request.password() == null
                ? "" : request.password();
        String security = request.security();

        if (ssid == null || ssid.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o nome da rede."
            );
        }

        if (security == null) {
            throw new IllegalArgumentException(
                    "Selecione a segurança."
            );
        }

        security = security.toUpperCase(Locale.ROOT);

        if (!security.equals("WPA")
                && !security.equals("WEP")
                && !security.equals("NOPASS")) {
            throw new IllegalArgumentException(
                    "Segurança inválida."
            );
        }

        if (security.equals("NOPASS")) {
            security = "nopass";
            password = "";
        } else if (password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Informe a senha do Wi-Fi."
            );
        }

        return "WIFI:T:" + security
                + ";S:" + escapeWifi(ssid)
                + ";P:" + escapeWifi(password)
                + ";;";
    }

    private String escapeWifi(String value) {
        return value.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace(":", "\\:")
                .replace("\"", "\\\"");
    }
}

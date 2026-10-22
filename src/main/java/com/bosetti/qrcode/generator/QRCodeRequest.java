package com.bosetti.qrcode.generator;

public record QRCodeRequest(
        String type,
        String url,
        String ssid,
        String password,
        String security
) {
}

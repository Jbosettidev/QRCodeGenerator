package com.bosetti.qrcode.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static com.bosetti.qrcode.generator.QRCodeGeneratorApplication.log;

@RestController

public class QRCodeController {

    @Autowired
    QRCodeGeneratorService qrCodeGeneratorService;

    @PostMapping("/qrcode")
    public String addCustomer(@RequestBody String message){
        log.info("Input Message is {} ",message);
        qrCodeGeneratorService.generateQRCode(message);
        return "Created Qr Code";
    }
}

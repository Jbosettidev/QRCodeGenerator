package com.bosetti.qrcode.generator;

import ch.qos.logback.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Scanner;

@SpringBootApplication
public class QRCodeGeneratorApplication implements CommandLineRunner {

    static final Logger log = LoggerFactory.getLogger(QRCodeGeneratorApplication.class);

    @Autowired
    QRCodeGeneratorService qrCodeGeneratorService;


    public static void main(String[] args) {
		SpringApplication.run(QRCodeGeneratorApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        while (true){
            Scanner sc = new Scanner(System.in);

            log.info("Enter your message: ");

            String inputMessage = sc.nextLine();

            log.info("Input Message from console - {}",inputMessage);
            qrCodeGeneratorService.generateQRCode(inputMessage);
            if (StringUtil.isNullOrEmpty(inputMessage)){
                qrCodeGeneratorService.generateQRCode(inputMessage);
            }
        }
    }
}

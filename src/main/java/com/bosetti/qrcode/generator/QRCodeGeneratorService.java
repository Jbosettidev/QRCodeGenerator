package com.bosetti.qrcode.generator;

import ch.qos.logback.core.util.StringUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class QRCodeGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(QRCodeGeneratorService.class);
    @Value("${qrcode.output.directory}")
    private String outputLocation;

    private static final String charset = "UTF-8";
    private static final String strDateFormat = "yyyyMMddhhmmss";

    public void generateQRCode(String message){
        log.info("### Generating QRCode ###");

        log.info("Output directory - {}",outputLocation);

        try{
            String finalMessage = (StringUtil.notNullNorEmpty(message))?message:"";
            log.info("Final Input Message - {}",finalMessage);
            processQRCode(finalMessage,prepareOutputFileName(),charset, 400, 400);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    private String prepareOutputFileName(){
        Date date = new Date();

        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);

        StringBuilder sb = new StringBuilder();
        sb.append(outputLocation).append("/").append("QRCode-").append(formattedDate).append(".png");
        log.info("Final output file - "+sb.toString());
        File dir = new File(outputLocation);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            log.info("Output directory created: {}", created);
        }

        return sb.toString();

    }
    private void processQRCode(String data,String path, String charset, int height,int width) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset),charset),BarcodeFormat.QR_CODE, width,height);
        MatrixToImageWriter.writeToFile(matrix,path.substring(path.lastIndexOf('.')+1),new File(path));
    }
}

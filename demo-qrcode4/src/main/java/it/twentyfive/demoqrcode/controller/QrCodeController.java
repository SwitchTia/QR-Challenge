package it.twentyfive.demoqrcode.controller;

import it.twentyfive.demoqrcode.model.CustomQrRequest;
import it.twentyfive.demoqrcode.model.ResponseImage;
import it.twentyfive.demoqrcode.utils.MethodUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
@RestController
public class QrCodeController {

    // public static final int DEFAULT_QR_WIDTH = 350;
    // public static final int DEFAULT_QR_HEIGHT = 350;

    @PostMapping("/generate")
    public ResponseEntity<ResponseImage> downloadQrCodeBase64(@RequestBody CustomQrRequest request) {
        try {
            byte[] bytes = MethodUtils.generateQrCodeImage(request);
            
            if (bytes == null) {
                // Handle the case where generateQrCodeImage method returns null bytes
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            
            // Add logo to the center
            BufferedImage qrImage = ImageIO.read(new ByteArrayInputStream(bytes));
            BufferedImage imageWithLogo = MethodUtils.addLogoToCenter(request, qrImage);
            
            if (imageWithLogo == null) {
                // Handle the case where logo couldn't be added to the center
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            
            // Convert the modified image to bytes before encoding to base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(imageWithLogo, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            // Encode the modified image to base64
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            base64 = "data:image/png;base64," + base64;

            // Create and return the response
            ResponseImage response = new ResponseImage();
            response.setImageBase64(base64);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            // Log the IO exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            // Log other exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

package it.twentyfive.demoqrcode.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import it.twentyfive.demoqrcode.model.CustomColor;
import it.twentyfive.demoqrcode.model.CustomQrRequest;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
public class MethodUtils {


    public static byte[] generateQrCodeImage(CustomQrRequest qrCode) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCode.getRequestUrl(), BarcodeFormat.QR_CODE, qrCode.getWidth(), qrCode.getHeight());
        Color onColor = Color.decode(qrCode.getCustomColor().getOnColor());
        Color offColor = Color.decode(qrCode.getCustomColor().getOffColor());
        
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(onColor.getRGB(),offColor.getRGB());
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);

        // Calcola le dimensioni del riquadro bianco al centro
        int whiteBoxSize = (int) (Math.min(qrCode.getWidth(), qrCode.getHeight()) * 0.135); // Riduci la dimensione del riquadro bianco
        int whiteBoxX = (qrCode.getWidth() - whiteBoxSize) / 2;
        int whiteBoxY = (qrCode.getHeight() - whiteBoxSize) / 2;

        BufferedImage overlayImage = new BufferedImage(qrCode.getWidth(), qrCode.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D graphics = overlayImage.createGraphics();
        graphics.setColor(new Color(255, 255, 255, 0)); // Trasparente
        graphics.fillRect(0, 0, qrCode.getWidth(), qrCode.getHeight());
        graphics.setColor(Color.WHITE);
        graphics.fillRect(whiteBoxX, whiteBoxY, whiteBoxSize, whiteBoxSize);
        graphics.dispose();

        // Sovrappone l'immagine al codice QR
        Graphics2D qrGraphics = qrImage.createGraphics();
        qrGraphics.drawImage(overlayImage, 0, 0, null);
        qrGraphics.dispose();

        BufferedImage centerLogo = loadImageFromUrl(qrCode.getLogoCenterUrl());
        centerLogo = resizeLogoForCenter (centerLogo, whiteBoxSize, whiteBoxSize);

        BufferedImage imageWithLogo = addLogoToCenter(qrCode, qrImage);
        qrImage = imageWithLogo;

        // if (qrCode.getCustomBorder().getBorderColor() != null) {
        //     qrImage = addBorder(qrImage, 25, borderColor);
        // }
        if (qrCode.getCustomBorder() != null) {
            int top= qrCode.getCustomBorder().getBordSizeTop();
            int bottom= qrCode.getCustomBorder().getBordSizeBottom();
            int left= qrCode.getCustomBorder().getBordSizeLeft();
            int right =qrCode.getCustomBorder().getBordSizeRight();
            qrImage= addBorder(qrImage, left, right, top, bottom, qrCode.getCustomBorder().getBorderColor());
        }
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    public static BufferedImage addBorder(BufferedImage img, int bordSizeLeft, int bordSizeRight, int bordSizeTop, int bordSizeBottom, Color borderColor) {
        int newWidth=img.getWidth() + bordSizeLeft+bordSizeRight;
        int newHeight=img.getHeight() + bordSizeTop+bordSizeBottom;
        BufferedImage imageWithBorder = new BufferedImage(newWidth,newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = imageWithBorder.createGraphics();
        g.setColor(borderColor);
        g.fillRect(0, 0, imageWithBorder.getWidth(), imageWithBorder.getHeight());
        int qrX = bordSizeLeft;
        int qrY = bordSizeBottom;
        g.drawImage(img, qrX, qrY, null);
        g.dispose();

        return imageWithBorder;
    }

    // public static BufferedImage addBorder(BufferedImage img, int borderSize, Color borderColor) {
    //     BufferedImage imageWithBorder = new BufferedImage(
    //             img.getWidth() + 2 * borderSize,
    //             img.getHeight() + 2 * borderSize,
    //             BufferedImage.TYPE_INT_ARGB);

    //             Graphics2D g = imageWithBorder.createGraphics();
    //             g.setColor(borderColor);
    //             g.fillRect(0, 0, imageWithBorder.getWidth(), imageWithBorder.getHeight());
    //             g.drawImage(img, borderSize, borderSize, null);
    //             g.dispose();

    //             return imageWithBorder;
    // }

    public static BufferedImage loadImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        BufferedImage image = ImageIO.read(url);
        return image;
    }

    public static BufferedImage resizeLogo(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resizedImage;
    }

    public static BufferedImage resizeLogoForCenter (BufferedImage logo, int targetWidth, int targetHeight) {
        Image scaledLogo = logo.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedLogo = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        resizedLogo.createGraphics().drawImage(scaledLogo, 0, 0, null);
        return resizedLogo;
    }

    public static BufferedImage addLogoToCenter(CustomQrRequest qrCode, BufferedImage qrImage) {
        BufferedImage logoImage = null;
        if (qrCode.getLogoCenterUrl() != null && !qrCode.getLogoCenterUrl().isEmpty()) {
            try {
                URL url = new URL(qrCode.getLogoCenterUrl());
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                logoImage = ImageIO.read(connection.getInputStream());
                logoImage = resizeLogo(logoImage, qrImage.getWidth() / 8, qrImage.getHeight() / 8);
            } catch (IOException e) {
                // Log the error or handle it as per your requirement
                e.printStackTrace();
                // Return null to indicate that the logo image couldn't be loaded
                return null;
            }
        }
        if (logoImage != null) {
            // Calculate the position to place the logo at the center of the QR code
            int logoX = (qrImage.getWidth() - logoImage.getWidth()) / 2;
            int logoY = (qrImage.getHeight() - logoImage.getHeight()) / 2;
    
            // Draw the logo onto the QR code image
            Graphics2D g = qrImage.createGraphics();
            g.drawImage(logoImage, logoX, logoY, null);
            g.dispose();
        }
        return qrImage;
    }
}

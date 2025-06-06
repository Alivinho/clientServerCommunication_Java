package controller;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageDisplayHelper {
    private static final int MAX_IMAGE_WIDTH = 300;
    private static final int MAX_IMAGE_HEIGHT = 300;

    public static boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
    }

    public static String createImageHTML(File imageFile, String senderName, boolean isOwnMessage) {
        try {
            BufferedImage originalImage = ImageIO.read(imageFile);
            
            // Calcular dimensões mantendo proporção
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            float ratio = (float) width / (float) height;
            
            if (width > MAX_IMAGE_WIDTH) {
                width = MAX_IMAGE_WIDTH;
                height = (int) (width / ratio);
            }
            if (height > MAX_IMAGE_HEIGHT) {
                height = MAX_IMAGE_HEIGHT;
                width = (int) (height * ratio);
            }
            
            // Estilo baseado no remetente (própria mensagem ou de outros)
            String alignment = isOwnMessage ? "right" : "left";
            String bgColor = isOwnMessage ? "#DCF8C6" : "#FFFFFF";
            String borderRadius = isOwnMessage ? "15px 15px 0 15px" : "15px 15px 15px 0";
            String borderStyle = isOwnMessage ? "" : "border:1px solid #EEE;";
            
            return String.format(
                "<html><div style='text-align:%s; margin:5px 10px 5px %s;'>" +
                "<div style='background:%s; display:inline-block; padding:8px 12px; " +
                "border-radius:%s; max-width:70%%; word-wrap:break-word; %s'>" +
                "<div style='font-family:Segoe UI, sans-serif; font-size:12px; color:#555; margin-bottom:5px;'>%s</div>" +
                "<img src='%s' width='%d' height='%d' style='max-width:100%%; display:block;'/>" +
                "<div style='font-size:11px; color:#666; margin-top:5px;'>%s</div>" +
                "</div></div></html>",
                alignment,
                isOwnMessage ? "50px" : "10px",
                bgColor,
                borderRadius,
                borderStyle,
                senderName,
                imageFile.toURI().toURL(),
                width,
                height,
                imageFile.getName()
            );
        } catch (IOException e) {
            return String.format("<html><div style='color:red;'>Erro ao carregar imagem: %s</div></html>", 
                               imageFile.getName());
        }
    }
}
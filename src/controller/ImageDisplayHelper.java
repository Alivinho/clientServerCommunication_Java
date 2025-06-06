package controller;

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
	
	public static String createImageHTML(File imageFile, String senderName) {
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
            
            // Criar HTML para exibir a imagem
            return String.format(
                "<html><div style='margin:5px; text-align:%s;'>" +
                "<div style='background:#%s; display:inline-block; padding:8px 12px; " +
                "border-radius:15px; max-width:%dpx;'>" +
                "<img src='%s' width='%d' height='%d' style='max-width:100%%;'/>" +
                "<div style='font-size:11px; color:#666; margin-top:5px;'>%s</div>" +
                "</div></div></html>",
                senderName.equals("Você") ? "right" : "left",
                senderName.equals("Você") ? "DCF8C6" : "FFFFFF",
                width + 24,
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

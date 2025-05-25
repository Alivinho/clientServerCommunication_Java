package controller;

import java.io.*;
import java.net.Socket;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import visual.FileProgressDialog;

public class FileTransfer {
    private Socket socket;
    private String downloadFolder;
    private JTextPane chatArea;
    private JFrame parentFrame;

    public FileTransfer(Socket socket, String downloadFolder, JTextPane jTextPane, JFrame parentFrame) {
        this.socket = socket;
        this.downloadFolder = downloadFolder;
        this.chatArea = jTextPane;
        this.parentFrame = parentFrame;

        new File(downloadFolder).mkdirs();
    }

    public void sendText(String message) {
        new Thread(() -> {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("TEXT");
                dos.writeUTF(message);
                dos.flush();
            } catch (IOException ex) {
                appendToChat("[Erro] Falha ao enviar mensagem: " + ex.getMessage());
            }
        }).start();
    }

    public void sendFile(File file) {
        FileProgressDialog progressDialog = new FileProgressDialog(parentFrame, "Enviando: " + file.getName());
        progressDialog.setVisible(true);

        new Thread(() -> {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                FileInputStream fis = new FileInputStream(file);

                dos.writeUTF("FILE");
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());
                dos.flush();

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytesRead = 0;
                long fileSize = file.length();

                while ((bytesRead = fis.read(buffer)) > 0) {
                    dos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    int progress = (int) ((totalBytesRead * 100) / fileSize);
                    SwingUtilities.invokeLater(() -> progressDialog.updateProgress(progress));
                }

                fis.close();
                appendToChat("[Sistema] Arquivo enviado: " + file.getName());

            } catch (IOException ex) {
                appendToChat("[Erro] Falha ao enviar arquivo: " + ex.getMessage());
            } finally {
                SwingUtilities.invokeLater(progressDialog::dispose);
            }
        }).start();
    }

    public void receive() {
        new Thread(() -> {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                while (true) {
                    String command = dis.readUTF();
                    if ("TEXT".equals(command)) {
                        String message = dis.readUTF();
                        // Formata como mensagem recebida (esquerda)
                        String formattedMsg = "<div style='text-align:left; margin:5px;'>" +
                            "<div style='background:#FFFFFF; display:inline-block; padding:8px; " +
                            "border-radius:8px; border:1px solid #EEE; max-width:70%; word-wrap:break-word;'>" +
                            message + "</div></div>";
                        appendToChat(formattedMsg);
                    } else if ("FILE".equals(command)) {
                        receiveFile(dis);
                    }
                }
            } catch (IOException ex) {
                appendToChat("[Erro] Conexão perdida: " + ex.getMessage());
            }
        }).start();
    }

    private void receiveFile(DataInputStream dis) throws IOException {
        String fileName = dis.readUTF();
        long fileSize = dis.readLong();

        File outputFile = new File(downloadFolder, fileName);
        FileOutputStream fos = new FileOutputStream(outputFile);

        byte[] buffer = new byte[8192];
        long totalBytesRead = 0;
        int bytesRead;

        while (totalBytesRead < fileSize &&
                (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) > 0) {
            fos.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
        }

        fos.close();
        appendToChat("[Sistema] Arquivo recebido: " + fileName + "\nSalvo em: " + outputFile.getAbsolutePath());
    }

    private void appendToChat(String message) {
        // Verifica se é mensagem do sistema (não formatar)
        if (message.startsWith("[") && message.endsWith("]")) {
            String systemMsg = String.format(
                "<html><div style='text-align:center; color:#666; margin:5px; font-style:italic;'>%s</div></html>",
                message
            );
            appendFormattedMessage(systemMsg);
            return;
        }

        // Formata como mensagem recebida (esquerda) por padrão
        String formattedMsg = String.format(
            "<html><div style='text-align:left; margin:5px 50px 5px 10px;'>" +
            "<div style='background:#FFFFFF; display:inline-block; padding:8px 12px; " +
            "border-radius:15px 15px 15px 0; max-width:70%%; word-wrap:break-word; " +
            "font-family:Segoe UI, sans-serif; font-size:14px; color:#000; border:1px solid #EEE;'>" +
            "%s</div></div></html>",
            message
        );
        
        appendFormattedMessage(formattedMsg);
    }

    private void appendFormattedMessage(String html) {
        SwingUtilities.invokeLater(() -> {
            try {
                chatArea.setEditable(true);
                HTMLEditorKit editorKit = (HTMLEditorKit) chatArea.getEditorKit();
                HTMLDocument doc = (HTMLDocument) chatArea.getDocument();
                
                editorKit.insertHTML(doc, doc.getLength(), html, 0, 0, null);
                chatArea.setCaretPosition(doc.getLength());
                chatArea.setEditable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

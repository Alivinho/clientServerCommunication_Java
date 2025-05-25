package controller;

import java.io.*;
import java.net.Socket;

import javax.swing.*;

import visual.FileProgressDialog;

public class FileTransfer {
    private Socket socket;
    private String downloadFolder;
    private JTextArea chatArea;
    private JFrame parentFrame;

    public FileTransfer(Socket socket, String downloadFolder, JTextArea chatArea, JFrame parentFrame) {
        this.socket = socket;
        this.downloadFolder = downloadFolder;
        this.chatArea = chatArea;
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
                        appendToChat(message);
                    } else if ("FILE".equals(command)) {
                        receiveFile(dis);
                    } else {
                        appendToChat("[Erro] Comando desconhecido: " + command);
                    }
                }
            } catch (IOException ex) {
                appendToChat("[Erro] Falha ao receber: " + ex.getMessage());
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
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }
}

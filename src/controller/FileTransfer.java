package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

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
        
        // Garante que a pasta de downloads existe
        new File(downloadFolder).mkdirs();
    }

    // Método para enviar arquivos
    public void sendFile(File file) {
        FileProgressDialog progressDialog = new FileProgressDialog(parentFrame, "Enviando: " + file.getName());
        progressDialog.setVisible(true);

        new Thread(() -> {
            try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 FileInputStream fis = new FileInputStream(file)) {
                
                // Envia cabeçalho
                dos.writeUTF("FILE_TRANSFER");
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());
                
                // Envia dados do arquivo
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;
                long fileSize = file.length();
                
                while ((bytesRead = fis.read(buffer)) > 0) {
                    dos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    
                    // Atualiza progresso
                    int progress = (int) ((totalBytesRead * 100) / fileSize);
                    SwingUtilities.invokeLater(() -> progressDialog.updateProgress(progress));
                }
                
                appendToChat("[Sistema] Arquivo enviado: " + file.getName());
                
            } catch (IOException ex) {
                appendToChat("[Erro] Falha ao enviar arquivo: " + ex.getMessage());
            } finally {
                SwingUtilities.invokeLater(progressDialog::dispose);
            }
        }).start();
    }

    // Método para receber arquivos
    public void receiveFile() {
        new Thread(() -> {
            try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                // Lê comando inicial
                String command = dis.readUTF();
                if (!"FILE_TRANSFER".equals(command)) {
                    appendToChat("[Erro] Comando inesperado: " + command);
                    return;
                }

                // Recebe metadados
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();
                
                // Prepara para receber o arquivo
                File outputFile = new File(downloadFolder, fileName);
                FileOutputStream fos = new FileOutputStream(outputFile);
                
                // Recebe dados do arquivo
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;
                
                while (totalBytesRead < fileSize && 
                      (bytesRead = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalBytesRead))) > 0) {
                    fos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }
                
                fos.close();
                appendToChat("[Sistema] Arquivo recebido: " + fileName + "\nSalvo em: " + outputFile.getAbsolutePath());
                
            } catch (IOException ex) {
                appendToChat("[Erro] Falha ao receber arquivo: " + ex.getMessage());
            }
        }).start();
    }

    private void appendToChat(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
        });
    }
}

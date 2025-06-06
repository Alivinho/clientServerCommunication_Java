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
    private String nomeUsuario;

    private DataOutputStream outputStream;
    private final Object streamLock = new Object(); 

    public FileTransfer(Socket socket, String downloadFolder, JTextPane chatArea, JFrame parentFrame, String nomeUsuario) {
        this.socket = socket;
        this.downloadFolder = downloadFolder;
        this.chatArea = chatArea;
        this.parentFrame = parentFrame;
        this.nomeUsuario = nomeUsuario;

        new File(downloadFolder).mkdirs();

        try {
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Falha ao criar streams", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendText(String message) {
        new Thread(() -> {
            synchronized (streamLock) {
                try {
                    outputStream.writeUTF("TEXT");
                    outputStream.writeUTF(message);
                    outputStream.flush();
                } catch (IOException ex) {
                    appendToChat("[Erro] Falha ao enviar mensagem: " + ex.getMessage());
                }
            }
        }).start();
    }

    public void sendFile(File file) {
        FileProgressDialog progressDialog = new FileProgressDialog(parentFrame, "Enviando: " + file.getName());
        
        // Se for imagem, exibe no chat local primeiro
        if (ImageDisplayHelper.isImageFile(file)) {
            String html = ImageDisplayHelper.createImageHTML(file, nomeUsuario, true);
            appendToChat(html);
        }
        
        
        progressDialog.setVisible(true);
        
        new Thread(() -> {
            try {
                // DELAY INICIAL: Permite que o usuário veja o dialog e possa cancelar
				Thread.sleep(300);
				if (progressDialog.isTransferCancelled()) {
					appendToChat("[Sistema] Envio cancelado: " + file.getName());
					SwingUtilities.invokeLater(progressDialog::dispose);
					return;
				}
                
                synchronized (streamLock) { // Usa o mesmo lock para tudo
                    try (FileInputStream fis = new FileInputStream(file)) {
                        // Envia cabeçalho do arquivo
                        outputStream.writeUTF("FILE");
                        outputStream.writeUTF(file.getName());
                        outputStream.writeLong(file.length());
                        
                        outputStream.writeUTF(nomeUsuario);
                        
                        // BUFFER ADAPTATIVO: Menor para arquivos pequenos, maior para grandes
                        long fileSize = file.length();
                        int bufferSize;
                        int sleepDelay;
                        
                        if (fileSize < 50 * 1024) { 
                            bufferSize = 1024; 
                            sleepDelay = 50;   
                        } else if (fileSize < 500 * 1024) { 
                            bufferSize = 2048;
                            sleepDelay = 20;
                        } else { 
                            bufferSize = 8192;
                            sleepDelay = 0;
                        }
                        
                        byte[] buffer = new byte[bufferSize];
                        int bytesRead;
                        long totalBytesRead = 0;
                        long startTime = System.currentTimeMillis();

                        while ((bytesRead = fis.read(buffer)) > 0) {
                            if (progressDialog.isTransferCancelled()) {
                                appendToChat("[Sistema] Envio cancelado: " + file.getName());
                                return;
                            }
                            
                            outputStream.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;

                            // Calcula progresso e estatísticas
                            final int progress = (int) ((totalBytesRead * 100) / fileSize);
                            final long currentTime = System.currentTimeMillis();
                            final long elapsedTime = currentTime - startTime;
                            
                            final long finalTotalBytesRead = totalBytesRead;
                            final long finalFileSize = fileSize;
                            final String finalFileName = file.getName();
                            
                            SwingUtilities.invokeLater(() -> {
                                if (!progressDialog.isTransferCancelled()) {
                                    progressDialog.updateProgress(progress);
                                    
                                    if (elapsedTime > 500) { 
                                        double speedKBps = (finalTotalBytesRead / 1024.0) / (elapsedTime / 1000.0);
                                        String speedText = String.format("%.1f KB/s", speedKBps);
                                        
                                        // Estima tempo restante
                                        if (speedKBps > 0) {
                                            long remainingBytes = finalFileSize - finalTotalBytesRead;
                                            long etaSeconds = (long) (remainingBytes / (speedKBps * 1024));
                                            String etaText = etaSeconds > 60 ? 
                                                String.format("%dm %ds", etaSeconds / 60, etaSeconds % 60) :
                                                String.format("%ds", etaSeconds);
                                            
                                            progressDialog.updateStatus(String.format(
                                                "Enviando: %s (%s) - ETA: %s", 
                                                finalFileName, speedText, etaText
                                            ));
                                        } else {
                                            progressDialog.updateStatus(String.format(
                                                "Enviando: %s (%s)", 
                                                finalFileName, speedText
                                            ));
                                        }
                                    } else {
                                        progressDialog.updateStatus(String.format(
                                            "Enviando: %s (%d%%)", 
                                            finalFileName, progress
                                        ));
                                    }
                                }
                            });
                            
                            if (sleepDelay > 0) {
                                try {
                                    Thread.sleep(sleepDelay);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                            }
                            
                            if (totalBytesRead % (bufferSize * 10) == 0) {
                                outputStream.flush();
                            }
                        }
                        
                        outputStream.flush();
                        
                        // DELAY FINAL: Mantém a barra visível por um tempo mínimo
                        SwingUtilities.invokeLater(() -> {
                            progressDialog.updateProgress(100);
                            progressDialog.updateStatus("Concluído: " + file.getName());
                        });
                        
                        if (fileSize < 100 * 1024) {
                            Thread.sleep(800); 
                        } else {
                            Thread.sleep(300); 
                        }
                        
                        appendToChat("[Sistema] Arquivo enviado: " + file.getName());
                        
                    } catch (IOException ex) {
                        appendToChat("[Erro] Falha ao enviar arquivo: " + ex.getMessage());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                appendToChat("[Sistema] Envio interrompido: " + file.getName());
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
                        boolean isOwnMessage = message.startsWith(nomeUsuario + ":");
                        String formattedMsg;
                        if (isOwnMessage) {
                            formattedMsg = formatarMensagemDireita(message);
                        } else {
                            formattedMsg = formatarMensagemEsquerda(message);
                        }
                        appendToChat(formattedMsg);
                    } else if ("FILE".equals(command)) {
                        receiveFile(dis); 
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Conexão Perdida", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void receiveFile(DataInputStream dis) throws IOException {
        String fileName = dis.readUTF();
        long fileSize = dis.readLong();
        String senderName = dis.readUTF();

        File outputFile = new File(downloadFolder, fileName);
        
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[8192]; 
            long totalBytesRead = 0;
            int bytesRead;

            while (totalBytesRead < fileSize
                    && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) > 0) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                System.out.println("Progresso recebimento: " + totalBytesRead + "/" + fileSize + " bytes");
            }
        }
        
     // Verifica se é imagem e exibe no chat
        if (ImageDisplayHelper.isImageFile(outputFile)) {
            boolean isOwnMessage = senderName.equals(nomeUsuario);
            String html = ImageDisplayHelper.createImageHTML(outputFile, senderName, isOwnMessage);
            appendToChat(html);
        } else {
            appendToChat("[Sistema] Arquivo recebido: " + fileName + "\nSalvo em: " + outputFile.getAbsolutePath());
        }
    }

    
    // Método para gerenciar diferentes tipos de mensagens 
    private void appendToChat(String message) {
        if (message.startsWith("<html>")) {
            appendFormattedMessage(message);
            return;
        }
        
        // Verifica se é mensagem do sistema
        if (message.startsWith("[Sistema]") || message.startsWith("[Erro]")) {
            String systemMsg = String.format(
                    "<html><div style='text-align:center; color:#666; margin:5px; font-style:italic;'>%s</div></html>",
                    message);
            appendFormattedMessage(systemMsg);
            return;
        }
        if (message.startsWith("<html>")) {
            appendFormattedMessage(message);
            return;
        }

        // Verifica se é mensagem própria
        boolean isOwnMessage = message.startsWith(nomeUsuario + ":");
        String formattedMsg;

        if (isOwnMessage) {
            formattedMsg = formatarMensagemDireita(message);
        } else {
            formattedMsg = formatarMensagemEsquerda(message);
        }

        appendFormattedMessage(formattedMsg);
    }
    
    
    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
    }

 // Método par Inserir as mensagens formatadas na área do Chat
    private void appendFormattedMessage(String html) {
        SwingUtilities.invokeLater(() -> {
            try {
                chatArea.setEditable(true);
                HTMLEditorKit editorKit = (HTMLEditorKit) chatArea.getEditorKit();
                HTMLDocument doc = (HTMLDocument) chatArea.getDocument();
                
                String wrappedHtml = "<div style='margin: 0; padding: 0;'>" + html + "</div>";

                editorKit.insertHTML(doc, doc.getLength(), wrappedHtml, 0, 0, null);
                chatArea.setCaretPosition(doc.getLength());
                chatArea.setEditable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String formatarMensagemDireita(String mensagem) {
        return String.format(
                "<html><div style='text-align:right; margin:5px 10px 5px 50px;'>"
                        + "<div style='background:#DCF8C6; display:inline-block; padding:8px 12px; "
                        + "border-radius:15px 15px 0 15px; max-width:70%%; word-wrap:break-word; "
                        + "font-family:Segoe UI, sans-serif; font-size:14px; color:#000;'>" 
                        + "%s</div></div></html>",
                mensagem);
    }

    private String formatarMensagemEsquerda(String mensagem) {
        return String.format(
                "<html><div style='text-align:left; margin:5px 50px 5px 10px;'>"
                        + "<div style='background:#FFFFFF; display:inline-block; padding:8px 12px; "
                        + "border-radius:15px 15px 15px 0; max-width:70%%; word-wrap:break-word; "
                        + "font-family:Segoe UI, sans-serif; font-size:14px; color:#000; border:1px solid #EEE;'>"
                        + "%s</div></div></html>", 
                mensagem);
    }
}
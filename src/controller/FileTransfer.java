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

    // Streams dedicados para texto e arquivos
    private DataOutputStream textOutputStream;
    private DataOutputStream fileOutputStream;

    public FileTransfer(Socket socket, String downloadFolder, JTextPane chatArea, JFrame parentFrame, String nomeUsuario) {
        this.socket = socket;
        this.downloadFolder = downloadFolder;
        this.chatArea = chatArea;
        this.parentFrame = parentFrame;
        this.nomeUsuario = nomeUsuario;

        new File(downloadFolder).mkdirs();

        try {
            // Cria streams separados para evitar conflitos
            this.textOutputStream = new DataOutputStream(socket.getOutputStream());
            this.fileOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            appendToChat("[Erro] Falha ao criar streams: " + e.getMessage());
        }
    }

    public void sendText(String message) {
        new Thread(() -> {
            synchronized (textOutputStream) {
                try {
                    textOutputStream.writeUTF("TEXT");
                    textOutputStream.writeUTF(message);
                    textOutputStream.flush();
                } catch (IOException ex) {
                    appendToChat("[Erro] Falha ao enviar mensagem: " + ex.getMessage());
                }
            }
        }).start();
    }

    public void sendFile(File file) {
        FileProgressDialog progressDialog = new FileProgressDialog(parentFrame, "Enviando: " + file.getName());
        progressDialog.setVisible(true);

        new Thread(() -> {
            synchronized (fileOutputStream) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    fileOutputStream.writeUTF("FILE");
                    fileOutputStream.writeUTF(file.getName());
                    fileOutputStream.writeLong(file.length());
                    fileOutputStream.flush();

                    byte[] buffer = new byte[16000];
                    int bytesRead;
                    long totalBytesRead = 0;
                    long fileSize = file.length();

                    while ((bytesRead = fis.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                        fileOutputStream.flush(); // Libera os dados imediatamente
                        totalBytesRead += bytesRead;

                        int progress = (int) ((totalBytesRead * 100) / fileSize);
                        SwingUtilities.invokeLater(() -> progressDialog.updateProgress(progress));
                    }

                    appendToChat("[Sistema] Arquivo enviado: " + file.getName());
                } catch (IOException ex) {
                    appendToChat("[Erro] Falha ao enviar arquivo: " + ex.getMessage());
                } finally {
                    SwingUtilities.invokeLater(progressDialog::dispose);
                }
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
						// Verifica se a mensagem é do próprio usuário (contém o nome do usuário)
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
				appendToChat("[Erro] Conexão perdida: " + ex.getMessage());
			}
		}).start();
	}

	private void receiveFile(DataInputStream dis) throws IOException {
		String fileName = dis.readUTF();
		long fileSize = dis.readLong();

		File outputFile = new File(downloadFolder, fileName);
		FileOutputStream fos = new FileOutputStream(outputFile);

		byte[] buffer = new byte[16000];
		long totalBytesRead = 0;
		int bytesRead;

		while (totalBytesRead < fileSize
				&& (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) > 0) {
			fos.write(buffer, 0, bytesRead);
			totalBytesRead += bytesRead;
		    System.out.println("Progresso: " + totalBytesRead + "/" + fileSize + " bytes");
		}

		fos.close();
		appendToChat("[Sistema] Arquivo recebido: " + fileName + "\nSalvo em: " + outputFile.getAbsolutePath());
	}

	private void appendToChat(String message) {
		// Se já estiver formatado como HTML, apenas exibe
		if (message.startsWith("<html>")) {
			appendFormattedMessage(message);
			return;
		}
		// Verifica se é mensagem do sistema
		if (message.startsWith("[") && message.endsWith("]")) {
			String systemMsg = String.format(
					"<html><div style='text-align:center; color:#666; margin:5px; font-style:italic;'>%s</div></html>",
					message);
			appendFormattedMessage(systemMsg);
			return;
		}

		// Verifica se é mensagem própria
		boolean isOwnMessage = message.startsWith(nomeUsuario + ":");
		String formattedMsg;

		if (isOwnMessage) {
			formattedMsg = String.format("<html><div style='text-align:right; margin:5px 10px 5px 50px;'>"
					+ "<div style='background:#DCF8C6; display:inline-block; padding:8px 12px; "
					+ "border-radius:15px 15px 0 15px; max-width:70%%; word-wrap:break-word; "
					+ "font-family:Segoe UI, sans-serif; font-size:14px; color:#000;'>" + "%s</div></div></html>",
					message);
		} else {
			formattedMsg = String.format("<html><div style='text-align:left; margin:5px 50px 5px 10px;'>"
					+ "<div style='background:#FFFFFF; display:inline-block; padding:8px 12px; "
					+ "border-radius:15px 15px 15px 0; max-width:70%%; word-wrap:break-word; "
					+ "font-family:Segoe UI, sans-serif; font-size:14px; color:#000; border:1px solid #EEE;'>"
					+ "%s</div></div></html>", message);
		}

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

	private String formatarMensagemDireita(String mensagem) {
		return String.format(
				"<html>" + "<div style='text-align:right; margin:5px 10px 5px 50px;'>"
						+ "<div style='background:#DCF8C6; display:inline-block; padding:8px 12px; "
						+ "border-radius:15px 15px 0 15px; max-width:70%%; word-wrap:break-word; "
						+ "font-family:Segoe UI, sans-serif; font-size:14px; color:#000;'>" + "%s</div></div></html>",
				mensagem);
	}

	private String formatarMensagemEsquerda(String mensagem) {
		return String.format("<html>" + "<div style='text-align:left; margin:5px 50px 5px 10px;'>"
				+ "<div style='background:#FFFFFF; display:inline-block; padding:8px 12px; "
				+ "border-radius:15px 15px 15px 0; max-width:70%%; word-wrap:break-word; "
				+ "font-family:Segoe UI, sans-serif; font-size:14px; color:#000; border:1px solid #EEE;'>"
				+ "%s</div></div></html>", mensagem);
	}
}

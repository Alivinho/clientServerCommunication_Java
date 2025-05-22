package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

import visual.PanelServer;

public class ControllerServer {

	private ServerSocket serverSocket;
	private Socket clientSocket;

	private JFrame frame;
	private PanelServer panelServer;

	private boolean running;

	public ControllerServer() {
		initialize();
		startServer();
	}

	private void initialize() {
		frame = new JFrame("Chat Servidor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 500);
		frame.setLocationRelativeTo(null);

		panelServer = new PanelServer();
		frame.setContentPane(panelServer);
		frame.setVisible(true);

		// Configurar ação do botão enviar
		panelServer.getBtnEnviar().addActionListener(e -> sendMessage());

		// Permitir enviar mensagem pressionando Enter
		panelServer.getInputField().addActionListener(e -> sendMessage());
	}

	private void startServer() {
		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(12345);
				running = true;
				appendMessage("Servidor iniciado. Aguardando conexão...");

				clientSocket = serverSocket.accept();
				appendMessage("Cliente conectado: " + clientSocket.getInetAddress());

				// Iniciar thread para receber mensagens
				new Thread(this::receiveMessages).start();

			} catch (IOException e) {
				appendMessage("Erro no servidor: " + e.getMessage());
			}
		}).start();
	}
	
	  private void receiveMessages() {
	        try {
	            var in = clientSocket.getInputStream();
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            
	            while (running && (bytesRead = in.read(buffer)) != -1) {
	                String message = new String(buffer, 0, bytesRead);
	                appendMessage("Cliente: " + message);
	            }
	        } catch (IOException e) {
	            appendMessage("Erro ao receber mensagem: " + e.getMessage());
	        } finally {
	            try {
	                if (clientSocket != null) clientSocket.close();
	                if (serverSocket != null) serverSocket.close();
	            } catch (IOException e) {
	                appendMessage("Erro ao fechar conexão: " + e.getMessage());
	            }
	        }
	    }

	    private void sendMessage() {
	        String message = panelServer.getInputField().getText().trim();
	        if (!message.isEmpty() && clientSocket != null && clientSocket.isConnected()) {
	            try {
	                var out = clientSocket.getOutputStream();
	                out.write(message.getBytes());
	                out.flush();
	                appendMessage("Servidor: " + message);
	                panelServer.getInputField().setText("");
	            } catch (IOException e) {
	                appendMessage("Erro ao enviar mensagem: " + e.getMessage());
	            }
	        }
	    }

	    private void appendMessage(String message) {
	        SwingUtilities.invokeLater(() -> {
	            panelServer.getTextAreaChatServer().append(message + "\n");
	            panelServer.getTextAreaChatServer().setCaretPosition(
	                panelServer.getTextAreaChatServer().getDocument().getLength());
	        });
	    }

	    public static void main(String[] args) {
	        javax.swing.SwingUtilities.invokeLater(() -> {
	            new ControllerServer();
	        });
	    }

}

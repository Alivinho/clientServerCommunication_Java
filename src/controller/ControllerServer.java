package controller;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import visual.PanelServer;

public class ControllerServer {
    private JFrame frame;
    private PanelServer panel;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    
    private static final int SERVER_PORT = 50001;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControllerServer().startServer());
    }

    public void startServer() {
        initializeGUI();
        setupServer();
    }

    private void initializeGUI() {
        frame = new JFrame("Chat Servidor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        panel = new PanelServer();
        frame.getContentPane().add(panel);
        frame.setVisible(true);

        // Configurar listeners
        panel.getBtnEnviar().addActionListener(e -> sendMessage());
        panel.getInputField().addActionListener(e -> sendMessage());
    }

    private void setupServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
                appendMessage("Servidor iniciado. Aguardando conexão na porta " + SERVER_PORT);
                
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                appendMessage("Cliente conectado: " + clientSocket.getInetAddress());
                
                // Thread para receber mensagens
                new Thread(this::receiveMessages).start();
                
            } catch (IOException e) {
                appendMessage("Erro no servidor: " + e.getMessage());
            }
        }).start();
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                appendMessage("Cliente: " + message);
            }
        } catch (IOException e) {
            appendMessage("Erro ao receber mensagem: " + e.getMessage());
        } finally {
            closeConnections();
        }
    }

    private void sendMessage() {
        String text = panel.getInputField().getText().trim();
        if (!text.isEmpty() && out != null) {
            out.println(text);
            appendMessage("Servidor: " + text);
            panel.getInputField().setText("");
        }
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            panel.getTextAreaChatServer().append(message + "\n");
            panel.getTextAreaChatServer().setCaretPosition(
                panel.getTextAreaChatServer().getDocument().getLength());
        });
    }

    private void closeConnections() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            appendMessage("Erro ao fechar conexões: " + e.getMessage());
        }
    }
}
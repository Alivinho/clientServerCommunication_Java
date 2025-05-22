package controller;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import visual.PanelClient;

public class ControllerClient {
    private JFrame frame;
    private PanelClient panel;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    
    private static final String SERVER_IP = "localhost"; // altere para o IP do servidor
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControllerClient().startClient());
    }

    public void startClient() {
        initializeGUI();
        connectToServer();
    }

    private void initializeGUI() {
        frame = new JFrame("Chat Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        panel = new PanelClient();
        frame.getContentPane().add(panel);
        frame.setVisible(true);

        // Configurar listeners
        panel.getBtnEnviar().addActionListener(e -> sendMessage());
        panel.getInputField().addActionListener(e -> sendMessage());
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                appendMessage("Conectado ao servidor " + SERVER_IP + ":" + SERVER_PORT);
                
                // Thread para receber mensagens
                new Thread(this::receiveMessages).start();
                
            } catch (IOException e) {
                appendMessage("Erro ao conectar ao servidor: " + e.getMessage());
            }
        }).start();
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                appendMessage("Servidor: " + message);
            }
        } catch (IOException e) {
            appendMessage("Conexão perdida com o servidor: " + e.getMessage());
        } finally {
            closeConnections();
        }
    }

    private void sendMessage() {
        String text = panel.getInputField().getText().trim();
        if (!text.isEmpty() && out != null) {
            out.println(text);
            appendMessage("Cliente: " + text);
            panel.getInputField().setText("");
        }
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            panel.getTextAreaChatClient().append(message + "\n");
            panel.getTextAreaChatClient().setCaretPosition(
                panel.getTextAreaChatClient().getDocument().getLength());
        });
    }

    private void closeConnections() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            appendMessage("Erro ao fechar conexões: " + e.getMessage());
        }
    }
}
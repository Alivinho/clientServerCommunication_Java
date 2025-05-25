package controller;

import visual.*;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class ControllerServer {
    private JFrame frame;
    private PanelServer panel;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private String nomeUsuario;

    private FileTransfer fileTransfer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControllerServer().iniciar());
    }

    public void iniciar() {
        PanelMain panelMain = new PanelMain(true);

        frame = new JFrame("Configuração do Servidor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(panelMain);
        frame.setVisible(true);

        panelMain.getBtnConectar().addActionListener(e -> {
            nomeUsuario = panelMain.getNome();
            int porta = panelMain.getPorta();

            if (nomeUsuario.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, informe seu nome", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (porta <= 0) {
                JOptionPane.showMessageDialog(frame, "Porta inválida", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            frame.dispose();
            iniciarServidor(porta);
        });
    }

    private void iniciarServidor(int porta) {
        frame = new JFrame("Chat Servidor - " + nomeUsuario);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        panel = new PanelServer();
        frame.getContentPane().add(panel);
        frame.setVisible(true);

        panel.getBtnEnviar().addActionListener(e -> enviarMensagem());
        panel.getInputField().addActionListener(e -> enviarMensagem());

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(porta);
                System.out.println("Servidor iniciado na porta " + porta);
                System.out.println("Aguardando conexão...");

                clientSocket = serverSocket.accept();

                fileTransfer = new FileTransfer(
                        clientSocket,
                        System.getProperty("user.home") + File.separator + "Downloads",
                        panel.getTextAreaChatServer(),
                        frame
                );

                System.out.println("Cliente conectado!");

                //fileTransfer.sendText("NOME_SERVIDOR:" + nomeUsuario);

                fileTransfer.receive();

            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame,
                        "Erro no servidor: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void enviarMensagem() {
        String text = panel.getInputField().getText().trim();
        if (!text.isEmpty()) {
            fileTransfer.sendText(nomeUsuario + ": " + text);
            panel.getTextAreaChatServer().append(nomeUsuario + ": " + text + "\n");
            panel.getInputField().setText("");
        }
    }
}

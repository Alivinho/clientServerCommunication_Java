package controller;

import visual.*;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class ControllerClient {
    private JFrame frame;
    private PanelClient panel;

    private Socket socket;
    private String nomeUsuario;
    private String nomeServidor;

    private FileTransfer fileTransfer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControllerClient().iniciar());
    }

    public void iniciar() {
        PanelMain panelMain = new PanelMain(false);

        frame = new JFrame("Configuração do Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(panelMain);
        frame.setVisible(true);

        panelMain.getBtnConectar().addActionListener(e -> {
            nomeUsuario = panelMain.getNome();
            String ip = panelMain.getIP();
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
            conectarAoServidor(ip, porta);
        });
    }

    private void conectarAoServidor(String ip, int porta) {
        frame = new JFrame("Chat Cliente - " + nomeUsuario);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        panel = new PanelClient();
        frame.getContentPane().add(panel);
        frame.setVisible(true);

        panel.getBtnEnviar().addActionListener(e -> enviarMensagem());
        panel.getInputField().addActionListener(e -> enviarMensagem());
        panel.getBtnUpload().addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                fileTransfer.sendFile(fileChooser.getSelectedFile());
            }
        });

        new Thread(() -> {
            try {
                socket = new Socket(ip, porta);

                fileTransfer = new FileTransfer(
                        socket,
                        System.getProperty("user.home") + File.separator + "Downloads",
                        panel.getTextAreaChatClient(),
                        frame
                );

                //fileTransfer.sendText("NOME_CLIENTE:" + nomeUsuario);

                System.out.println("Conectado ao servidor " + ip + ":" + porta);

                fileTransfer.receive();

            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame,
                        "Erro ao conectar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE));
                frame.dispose();
            }
        }).start();
    }

    private void enviarMensagem() {
        String text = panel.getInputField().getText().trim();
        if (!text.isEmpty()) {
            fileTransfer.sendText(nomeUsuario + ": " + text);
            panel.getTextAreaChatClient().append(nomeUsuario + ": " + text + "\n");
            panel.getInputField().setText("");
        }
    }
}

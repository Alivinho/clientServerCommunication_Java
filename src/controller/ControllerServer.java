package controller;

import visual.*;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

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

        // Desabilita o botão de upload até que o cliente se conecte
        panel.getBtnUpload().setEnabled(false);

        // Configura os listeners (mas o upload só funcionará após a conexão)
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
                    frame,
                    nomeUsuario
                );
                System.out.println("Cliente conectado!");

                // Habilita o botão de upload APÓS a conexão ser estabelecida
                SwingUtilities.invokeLater(() -> panel.getBtnUpload().setEnabled(true));

                fileTransfer.receive();

            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    frame,
                    "Erro no servidor: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                ));
            }
        }).start();

        // Configura o listener do botão de upload (só será chamado após a conexão estar pronta)
        panel.getBtnUpload().addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                fileTransfer.sendFile(fileChooser.getSelectedFile());
            }
        });
    }

    private void enviarMensagem() {
        String text = panel.getInputField().getText().trim();
        if (!text.isEmpty()) {
            // Envia apenas o texto puro (sem formatação HTML)
            String rawMessage = nomeUsuario + ": " + text;
            fileTransfer.sendText(rawMessage);
            
            appendToChat(formatarMensagemDireita(rawMessage));
            panel.getInputField().setText("");
        }
    }
    
    private void appendToChat(String html) {
        JTextPane textPane = (JTextPane) panel.getTextAreaChatServer(); 
        try {
            textPane.setEditable(true);
            HTMLEditorKit editorKit = (HTMLEditorKit) textPane.getEditorKit();
            HTMLDocument doc = (HTMLDocument) textPane.getDocument();
                        
            // Insere a mensagem formatada
            editorKit.insertHTML(doc, doc.getLength(), html, 0, 0, null);
            
            textPane.setCaretPosition(doc.getLength());
            textPane.setEditable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

   
}

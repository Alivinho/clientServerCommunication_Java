package controller;

import visual.*;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import java.io.*;
import java.net.*;

public class ControllerClient {
	private JFrame frame;
	private PanelClient panel;

	private Socket socket;
	private String nomeUsuario;

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
				JOptionPane.showMessageDialog(frame, "Por favor, informe seu nome", "Aviso",
						JOptionPane.WARNING_MESSAGE);
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

	    // Desabilita o botão de upload até que a conexão seja estabelecida
	    panel.getBtnUpload().setEnabled(false);

	    panel.getBtnEnviar().addActionListener(e -> enviarMensagem());
	    panel.getInputField().addActionListener(e -> enviarMensagem());

	    new Thread(() -> {
	        try {
	            socket = new Socket(ip, porta);
	           
	            fileTransfer = new FileTransfer(
	                socket,
	                System.getProperty("user.home") + File.separator + "Downloads",
	                panel.getTextAreaChatClient(),
	                frame,
	                nomeUsuario
	            );

	            System.out.println("Conectado ao servidor " + ip + ":" + porta);

	            // Habilita o botão de upload APÓS a conexão ser estabelecida
	            SwingUtilities.invokeLater(() -> panel.getBtnUpload().setEnabled(true));

	            fileTransfer.receive();

	        } catch (IOException e) {
	            SwingUtilities.invokeLater(() -> {
	                JOptionPane.showMessageDialog(
	                    frame,
	                    "Erro ao conectar: " + e.getMessage(),
	                    "Erro",
	                    JOptionPane.ERROR_MESSAGE
	                );
	                frame.dispose();
	            });
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
		JTextPane textPane = (JTextPane) panel.getTextAreaChatClient();
		try {
			textPane.setEditable(true);
			HTMLEditorKit editorKit = (HTMLEditorKit) textPane.getEditorKit();
			HTMLDocument doc = (HTMLDocument) textPane.getDocument();

			// Insere a mensagem formatada
			editorKit.insertHTML(doc, doc.getLength(), html, 0, 0, null);

			// Move o scroll para o final
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

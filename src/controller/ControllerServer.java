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
	private PrintWriter out;
	private BufferedReader in;
	private String nomeUsuario;

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
				JOptionPane.showMessageDialog(frame, "Por favor, informe seu nome", "Aviso",
						JOptionPane.WARNING_MESSAGE);
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
				panel.getTextAreaChatServer().append("Servidor iniciado na porta " + porta + "\n");
				panel.getTextAreaChatServer().append("Aguardando conexão...\n");

				clientSocket = serverSocket.accept();
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				panel.getTextAreaChatServer().append("Cliente conectado!\n");

				// Enviar nome do servidor para o cliente
				out.println("NOME_SERVIDOR:" + nomeUsuario);

				new Thread(this::receberMensagens).start();

			} catch (IOException ex) {
				JOptionPane.showMessageDialog(frame, "Erro no servidor: " + ex.getMessage(), "Erro",
						JOptionPane.ERROR_MESSAGE);
			}
		}).start();
	}

	private void receberMensagens() {
		try {
			String message;
			while ((message = in.readLine()) != null) {
				if (message.startsWith("NOME_CLIENTE:")) {
					final String nomeCliente = message.substring(13);
					SwingUtilities.invokeLater(() -> {
						panel.getTextAreaChatServer().append("Cliente " + nomeCliente + " entrou no chat\n");
					});
				} else {
					final String msgFinal = message;
					SwingUtilities.invokeLater(() -> {
						panel.getTextAreaChatServer().append("Cliente: " + msgFinal + "\n");
					});
				}
			}
		} catch (IOException e) {
			SwingUtilities.invokeLater(() -> {
				panel.getTextAreaChatServer().append("Erro ao receber mensagem: " + e.getMessage() + "\n");
			});
		} finally {
			fecharConexoes();
		}
	}

	private void enviarMensagem() {
		String text = panel.getInputField().getText().trim();
		if (!text.isEmpty() && out != null) {
			out.println(text);
			panel.getTextAreaChatServer().append(nomeUsuario + ": " + text + "\n");
			panel.getInputField().setText("");
		}
	}

	private void fecharConexoes() {
		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (clientSocket != null)
				clientSocket.close();
			if (serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {
			SwingUtilities.invokeLater(() -> {
				panel.getTextAreaChatServer().append("Erro ao fechar conexões: " + e.getMessage() + "\n");
			});
		}
	}
}
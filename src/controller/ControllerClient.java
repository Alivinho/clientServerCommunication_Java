package controller;

import visual.*;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class ControllerClient {
	private JFrame frame;
	private PanelClient panel;
	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;
	private String nomeUsuario;
	private String nomeServidor;

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

		panel.getBtnEnviar().addActionListener(e -> enviarMensagem());
		panel.getInputField().addActionListener(e -> enviarMensagem());

		new Thread(() -> {
			try {
				socket = new Socket(ip, porta);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// Enviar nome do usuário primeiro
				out.println("NOME_CLIENTE:" + nomeUsuario);

				panel.getTextAreaChatClient().append("Conectado ao servidor " + ip + ":" + porta + "\n");

				// Receber nome do servidor
				String primeiraMensagem = in.readLine();
				if (primeiraMensagem.startsWith("NOME_SERVIDOR:")) {
					nomeServidor = primeiraMensagem.substring(14);
					panel.getTextAreaChatClient().append("Você está conectado ao servidor de " + nomeServidor + "\n");
				}

				new Thread(this::receberMensagens).start();

			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "Erro ao conectar: " + e.getMessage(), "Erro",
						JOptionPane.ERROR_MESSAGE);
				frame.dispose();
			}
		}).start();
	}

	private void receberMensagens() {
		try {
			String message;
			while ((message = in.readLine()) != null) {
				final String msgFinal = message;
				SwingUtilities.invokeLater(() -> {
					panel.getTextAreaChatClient().append(nomeServidor + ": " + msgFinal + "\n");
				});
			}
		} catch (IOException e) {
			SwingUtilities.invokeLater(() -> {
				panel.getTextAreaChatClient().append("Conexão perdida com o servidor: " + e.getMessage() + "\n");
			});
		} finally {
			fecharConexoes();
		}
	}

	private void enviarMensagem() {
		String text = panel.getInputField().getText().trim();
		if (!text.isEmpty() && out != null) {
			out.println(text);
			panel.getTextAreaChatClient().append(nomeUsuario + ": " + text + "\n");
			panel.getInputField().setText("");
		}
	}

	private void fecharConexoes() {
		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			SwingUtilities.invokeLater(() -> {
				panel.getTextAreaChatClient().append("Erro ao fechar conexões: " + e.getMessage() + "\n");
			});
		}
	}
}
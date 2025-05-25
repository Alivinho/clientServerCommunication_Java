package visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PanelServer extends JPanel {

	private JTextArea chatServer;
	private JTextField inputField;
	private JButton btnEnviar;
	private JScrollPane scrollPane;
	// Image backgroundImage;

	public PanelServer() {
		setLayout(new BorderLayout());
		//setOpaque(false);
		
		//getTextAreaChatServer().setOpaque(false);
		//getScrollPaneChatServer().setOpaque(false);
		//getScrollPaneChatServer().getViewport().setOpaque(false);

		setBackground(Color.WHITE);

		add(getScrollPaneChatServer(), BorderLayout.CENTER); // Área de mensagens
		add(getBottomPanel(), BorderLayout.SOUTH); // Campo de entrada + botão
		
		  /*try {
	            backgroundImage = ImageIO.read(getClass().getResource("/img/fundo_Chat.jpg"));
	        } catch (IOException e) {
	            e.printStackTrace();
	            backgroundImage = null;
	        }*/
	}
	
	 //@Override
	    /*protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        // Desenha a imagem de fundo
	        if (backgroundImage != null) {
	            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
	        }
	    }*/

	// Área onde as mensagens são exibidas
	public JTextArea getTextAreaChatServer() {
		if (chatServer == null) {
			chatServer = new JTextArea();
			chatServer.setEditable(false);
			chatServer.setFont(new Font("SansSerif", Font.PLAIN, 14));
			chatServer.setLineWrap(true);
			chatServer.setWrapStyleWord(true);
		}
		return chatServer;
	}

	public JScrollPane getScrollPaneChatServer() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getTextAreaChatServer());
			scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		}
		return scrollPane;
	}

	public JTextField getInputField() {
		if (inputField == null) {
			inputField = new JTextField();
			inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
			inputField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
		}
		return inputField;
	}

	public JButton getBtnEnviar() {
		if (btnEnviar == null) {
			btnEnviar = new JButton("Enviar");
			btnEnviar.setBackground(new Color(0, 153, 76));
			btnEnviar.setForeground(Color.WHITE);
			btnEnviar.setFocusPainted(false);
			btnEnviar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
		}
		return btnEnviar;
	}

	// Painel inferior com campo e botão
	private JPanel getBottomPanel() {
		JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.add(getInputField(), BorderLayout.CENTER);
		bottomPanel.add(getBtnEnviar(), BorderLayout.EAST);
		return bottomPanel;
	}

}

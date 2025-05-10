package visual;

import java.awt.*;
import javax.swing.*;

public class PanelClient extends JPanel {
	
	private JTextArea chatClient;
	private JTextField inputField;
	private JButton bntEnviar;
	private JScrollPane scrollPane;

	public PanelClient() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		add(getScrollPaneChatClient(), BorderLayout.CENTER);  // Área de mensagens com rolagem
		add(getBottomPanel(), BorderLayout.SOUTH);             // Campo de entrada + botão
	}
	
	// Área de texto onde o chat aparece
	public JTextArea getTextAreaChatClient() {
        if (chatClient == null) {
        	chatClient = new JTextArea();
        	chatClient.setEditable(false);
        	chatClient.setFont(new Font("SansSerif", Font.PLAIN, 14));
        	chatClient.setLineWrap(true);
        	chatClient.setWrapStyleWord(true);
        }
        return chatClient;
    }

    public JScrollPane getScrollPaneChatClient() {
    	if (scrollPane == null) {
    		scrollPane = new JScrollPane(getTextAreaChatClient());
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
        if (bntEnviar == null) {
        	bntEnviar = new JButton("Enviar");
        	bntEnviar.setBackground(new Color(30, 144, 255));
        	bntEnviar.setForeground(Color.WHITE);
            bntEnviar.setFocusPainted(false);
            bntEnviar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        }
        return bntEnviar;
    }

    // Painel inferior com campo de texto + botão
    private JPanel getBottomPanel() {
    	JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
    	bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	bottomPanel.setBackground(Color.WHITE);
    	bottomPanel.add(getInputField(), BorderLayout.CENTER);
    	bottomPanel.add(getBtnEnviar(), BorderLayout.EAST);
    	return bottomPanel;
    }
}

package visual;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

public class PanelClient extends JPanel {
	
	//private JTextArea chatClient;
	private JTextPane  chatClient; 
	private JTextField inputField;
	
	private JButton bntEnviar;
	private JButton btnUpload; 
	
	private JScrollPane scrollPane;

	public PanelClient() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		add(getScrollPaneChatClient(), BorderLayout.CENTER);  // Área de mensagens com rolagem
		add(getBottomPanel(), BorderLayout.SOUTH);             // Campo de entrada + botão
	}
	
	// Área de texto onde o chat aparece
	/*public JTextArea getTextAreaChatClient() {
        if (chatClient == null) {
        	chatClient = new JTextArea();
        	chatClient.setEditable(false);
        	chatClient.setFont(new Font("SansSerif", Font.PLAIN, 14));
        	chatClient.setLineWrap(true);
        	chatClient.setWrapStyleWord(true);
        }
        return chatClient;
    }*/
	
	public JTextPane getTextAreaChatClient() {
	    if (chatClient == null) {
	        chatClient = new JTextPane();
	        chatClient.setEditable(false);
	        chatClient.setContentType("text/html");
	        chatClient.setEditorKit(new HTMLEditorKit());
	        chatClient.setBackground(new Color(240, 242, 245)); // Cor de fundo similar ao WhatsApp
	        chatClient.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        
	        // Configura margens internas
	        chatClient.setMargin(new Insets(5, 5, 5, 5));
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
	        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
	        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        //bottomPanel.setBackground(Color.WHITE);
	        
	        // Painel para os componentes da esquerda (upload + input)
	        JPanel leftPanel = new JPanel(new BorderLayout(5, 0));
	        
	        // Adiciona o botão de upload
	        leftPanel.add(getBtnUpload(), BorderLayout.WEST);
	        leftPanel.add(getInputField(), BorderLayout.CENTER);
	        
	        bottomPanel.add(leftPanel, BorderLayout.CENTER);
	        bottomPanel.add(getBtnEnviar(), BorderLayout.EAST);
	        
	        return bottomPanel;
	    }

	    public JButton getBtnUpload() {
	        if (btnUpload == null) {
	            // Carrega o ícone
	            ImageIcon uploadIcon = new ImageIcon(getClass().getResource("/icons/icon_Upload_02.png"));
	            
	            // Redimensiona o ícone se necessário
	            Image img = uploadIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
	            uploadIcon = new ImageIcon(img);
	            
	            btnUpload = new JButton(uploadIcon);
	            btnUpload.setToolTipText("Enviar arquivo");
	            //btnUpload.setBackground(new Color(230, 230, 230));
	            btnUpload.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	        }
	        return btnUpload;
	    }

    
   

}

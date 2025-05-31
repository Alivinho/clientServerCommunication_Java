package visual;

import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

@SuppressWarnings("serial")
public class PanelClient extends JPanel {
	
	private JTextPane  chatClient; 
	private JTextField inputField;
	
	private JButton bntEnviar;
	private JButton btnUpload; 
	
	Image backgroundImage;

	
	private JScrollPane scrollPane;

	public PanelClient() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setOpaque(false);
		
		
		getTextAreaChatClient().setOpaque(false);
		getScrollPaneChatClient().setOpaque(false);
		getScrollPaneChatClient().getViewport().setOpaque(false);

		add(getScrollPaneChatClient(), BorderLayout.CENTER);  // Área de mensagens com rolagem
		add(getBottomPanel(), BorderLayout.SOUTH);             // Campo de entrada + botão
		
		try {
			backgroundImage = ImageIO.read(getClass().getResource("/img/fundo_Chat1.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
			backgroundImage = null;
		}
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

	
	public JTextPane getTextAreaChatClient() {
	    if (chatClient == null) {
	        chatClient = new JTextPane();
	        chatClient.setEditable(false);
	        chatClient.setContentType("text/html");
	        chatClient.setEditorKit(new HTMLEditorKit());
	        chatClient.setBackground(new Color(240, 242, 245)); 
	        chatClient.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        
	        chatClient.setText("<html><body style='margin: 0; padding: 0;'></body></html>");
	        
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
	        
	        JPanel leftPanel = new JPanel(new BorderLayout(5, 0));
	        	        leftPanel.add(getBtnUpload(), BorderLayout.WEST);
	        leftPanel.add(getInputField(), BorderLayout.CENTER);
	        
	        bottomPanel.add(leftPanel, BorderLayout.CENTER);
	        bottomPanel.add(getBtnEnviar(), BorderLayout.EAST);
	        
	        return bottomPanel;
	    }

	    public JButton getBtnUpload() {
	        if (btnUpload == null) {
	            ImageIcon uploadIcon = new ImageIcon(getClass().getResource("/icons/icon_Upload_02.png"));	            
	            Image img = uploadIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
	            uploadIcon = new ImageIcon(img);
	            btnUpload = new JButton(uploadIcon);
	            btnUpload.setToolTipText("Enviar arquivo");
	            btnUpload.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	        }
	        return btnUpload;
	    }

    
   

}

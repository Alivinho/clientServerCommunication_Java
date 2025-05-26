package visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;

public class PanelServer extends JPanel {

	private JTextPane  chatServer; 
	private JScrollPane scrollPane;


	private JTextField inputField;

	private JButton btnEnviar;
	private JButton btnUpload; 

	// Image backgroundImage;

	public PanelServer() {
		setLayout(new BorderLayout());
		

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

	
	public JTextPane getTextAreaChatServer() {
	    if (chatServer == null) {
	    	chatServer = new JTextPane();
	    	chatServer.setEditable(false);
	        chatServer.setContentType("text/html");
	        chatServer.setEditorKit(new HTMLEditorKit());
	        chatServer.setBackground(new Color(240, 242, 245)); 
	        chatServer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        
	        // Configura margens internas
	        chatServer.setMargin(new Insets(5, 5, 5, 5));
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

	  private JPanel getBottomPanel() {
	        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
	        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        //bottomPanel.setBackground(Color.WHITE);
	        
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

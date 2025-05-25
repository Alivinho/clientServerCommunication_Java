package visual;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

public class PanelMain extends JPanel {
    private JTextField txtNome;
    private JTextField txtIP;
    private JTextField txtPorta;
    private JButton btnConectar;
    
    public PanelMain(boolean isServer) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(240, 240, 240));

        // Título
        GridBagConstraints gbcTitulo = new GridBagConstraints();
        gbcTitulo.gridx = 0;
        gbcTitulo.gridy = 0;
        gbcTitulo.gridwidth = 2;
        gbcTitulo.insets = new Insets(10, 10, 20, 10);
        gbcTitulo.anchor = GridBagConstraints.WEST;
        
        JLabel lblTitulo = new JLabel(isServer ? "Configurar Servidor" : "Conectar ao Servidor");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(70, 70, 70));
        contentPanel.add(lblTitulo, gbcTitulo);

        // Campo Nome
        GridBagConstraints gbcLabelNome = new GridBagConstraints();
        gbcLabelNome.gridx = 0;
        gbcLabelNome.gridy = 1;
        gbcLabelNome.insets = new Insets(10, 10, 10, 10);
        gbcLabelNome.anchor = GridBagConstraints.WEST;
        contentPanel.add(new JLabel("Nome:"), gbcLabelNome);
        
        GridBagConstraints gbcTxtNome = new GridBagConstraints();
        gbcTxtNome.gridx = 1;
        gbcTxtNome.gridy = 1;
        gbcTxtNome.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtNome.insets = new Insets(10, 10, 10, 10);
        txtNome = new JTextField(15);
        txtNome.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        contentPanel.add(txtNome, gbcTxtNome);

        // Campo IP (apenas para cliente)
        if (!isServer) {
            GridBagConstraints gbcLabelIP = new GridBagConstraints();
            gbcLabelIP.gridx = 0;
            gbcLabelIP.gridy = 2;
            gbcLabelIP.insets = new Insets(10, 10, 10, 10);
            gbcLabelIP.anchor = GridBagConstraints.WEST;
            contentPanel.add(new JLabel("IP do Servidor:"), gbcLabelIP);
            
            GridBagConstraints gbcTxtIP = new GridBagConstraints();
            gbcTxtIP.gridx = 1;
            gbcTxtIP.gridy = 2;
            gbcTxtIP.fill = GridBagConstraints.HORIZONTAL;
            gbcTxtIP.insets = new Insets(10, 10, 10, 10);
            txtIP = new JTextField("localhost", 15);
            txtIP.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            contentPanel.add(txtIP, gbcTxtIP);
        }

        // Campo Porta
        GridBagConstraints gbcLabelPorta = new GridBagConstraints();
        gbcLabelPorta.gridx = 0;
        gbcLabelPorta.gridy = isServer ? 2 : 3;
        gbcLabelPorta.insets = new Insets(10, 10, 10, 10);
        gbcLabelPorta.anchor = GridBagConstraints.WEST;
        contentPanel.add(new JLabel("Porta:"), gbcLabelPorta);
        
        GridBagConstraints gbcTxtPorta = new GridBagConstraints();
        gbcTxtPorta.gridx = 1;
        gbcTxtPorta.gridy = isServer ? 2 : 3;
        gbcTxtPorta.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtPorta.insets = new Insets(10, 10, 10, 10);
        txtPorta = new JTextField("50001", 15);
        txtPorta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        contentPanel.add(txtPorta, gbcTxtPorta);

        // Botão Conectar
        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.gridx = 0;
        gbcButton.gridy = isServer ? 3 : 4;
        gbcButton.gridwidth = 2;
        gbcButton.fill = GridBagConstraints.NONE;
        gbcButton.anchor = GridBagConstraints.CENTER;
        gbcButton.insets = new Insets(20, 10, 10, 10);
        
        btnConectar = new JButton(isServer ? "Iniciar Servidor" : "Conectar");
        btnConectar.setBackground(new Color(65, 131, 215));
        btnConectar.setForeground(Color.WHITE);
        btnConectar.setFocusPainted(false);
        btnConectar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnConectar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        contentPanel.add(btnConectar, gbcButton);

        add(contentPanel, BorderLayout.CENTER);
    }
    
    public JButton getBtnConectar() {
        return btnConectar;
    }
    
    public String getNome() {
        return txtNome.getText().trim();
    }
    
    public String getIP() {
        return txtIP != null ? txtIP.getText().trim() : "";
    }
    
    public int getPorta() {
        try {
            return Integer.parseInt(txtPorta.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
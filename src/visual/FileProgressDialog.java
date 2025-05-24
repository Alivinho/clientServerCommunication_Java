package visual;

import javax.swing.*;
import java.awt.*;

public class FileProgressDialog extends JDialog {
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JButton btnCancel;
    private boolean transferCancelled;

    public FileProgressDialog(JFrame parent, String fileName) {
        super(parent, "Transferência de Arquivo", true);
        setSize(400, 150);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        transferCancelled = false;

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Label com nome do arquivo
        lblStatus = new JLabel("Arquivo: " + fileName);
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Barra de progresso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SansSerif", Font.PLAIN, 11));

        // Botão de cancelamento
        btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> {
            transferCancelled = true;
            dispose();
        });

        // Painel inferior para o botão
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnCancel);

        // Layout
        panel.add(lblStatus, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
    }

    public void updateProgress(int progress) {
        progressBar.setValue(progress);
    }

    public void updateStatus(String status) {
        lblStatus.setText(status);
    }

    public boolean isTransferCancelled() {
        return transferCancelled;
    }

    public void setTransferCancelled(boolean cancelled) {
        this.transferCancelled = cancelled;
    }
}
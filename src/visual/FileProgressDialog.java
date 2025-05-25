package visual;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class FileProgressDialog extends JDialog {
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JButton btnCancel;
    private boolean transferCancelled;
    private DataOutputStream dos;

    public FileProgressDialog(JFrame parent, String fileName) {
        super(parent, "Transferência de Arquivo", true);
        setSize(400, 150);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        transferCancelled = false;

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblStatus = new JLabel("Arquivo: " + fileName);
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SansSerif", Font.PLAIN, 11));

        btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> {
            transferCancelled = true;
            if (dos != null) {
                try {
                    dos.writeUTF("TRANSFER_CANCELLED");
                    dos.flush();
                } catch (IOException ex) {
                }
            }
            dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnCancel);

        panel.add(lblStatus, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
    }

    public void setOutputStream(DataOutputStream dos) {
        this.dos = dos;
    }

    public void updateProgress(int progresso) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(progresso);
            progressBar.setString(progresso + "%");
        });
    }

    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText(status);
        });
    }

    public boolean isTransferCancelled() {
        return transferCancelled;
    }

    public void setTransferCancelled(boolean cancelled) {
        this.transferCancelled = cancelled;
    }
}
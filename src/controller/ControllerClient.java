package controller;

import javax.swing.JFrame;
import visual.PanelClient;

public class ControllerClient {

    private JFrame frame;
    private PanelClient panelClient;

    public ControllerClient() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Chat Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);

        panelClient = new PanelClient();
        frame.setContentPane(panelClient);
        frame.setVisible(true);
    }

    public PanelClient getPanelClient() {
        return panelClient;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ControllerClient();
        });
    }
}

package pl.koder95.intencje.gui;

import javax.swing.JFrame;
import java.awt.HeadlessException;

import static pl.koder95.intencje.Main.APP_HEADER;

public class MainFrame extends JFrame {

    public MainFrame() throws HeadlessException {
        super(APP_HEADER);
        initComponents();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void setTitle(String title) {
        // do nothing
    }

    private void initComponents() {
    }
}

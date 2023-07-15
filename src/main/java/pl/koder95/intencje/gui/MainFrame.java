package pl.koder95.intencje.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
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
        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        setContentPane(contentPane);

        contentPane.add(createHeader(), BorderLayout.NORTH);
        contentPane.add(createLeftSide(), BorderLayout.WEST);
        contentPane.add(createMainPane(), BorderLayout.CENTER);
        contentPane.add(createRightSide(), BorderLayout.EAST);
        contentPane.add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createRightSide() {
        return null;
    }

    private JPanel createLeftSide() {
        return null;
    }

    private JPanel createHeader() {
        return null;
    }

    private JPanel createMainPane() {
        return null;
    }

    private JPanel createFooter() {
        return null;
    }
}

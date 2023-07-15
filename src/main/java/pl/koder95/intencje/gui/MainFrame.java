package pl.koder95.intencje.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;

import java.util.function.Consumer;
import java.util.function.Function;

import static pl.koder95.intencje.Main.APP_HEADER;
import static pl.koder95.intencje.Main.APP_NAME;
import static pl.koder95.intencje.Main.PARISH_NAME;

public class MainFrame extends JFrame {

    private static final String FOOTER_CONTENT = "©: Kamil Mularski — 2023";

    private static final Function<Integer, EmptyBorder> EMPTY_BORDER_FACTORY = m -> new EmptyBorder(m, m, m, m);
    public static final Consumer<JPanel> MARGIN_FACTORY = p -> p.setBorder(EMPTY_BORDER_FACTORY.apply(10));

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
        JPanel panel = new JPanel();
        JToolBar tools = new JToolBar();
        tools.setName("Zarządzanie intencjami");
        tools.setOrientation(SwingConstants.VERTICAL);
        tools.setFloatable(false);

        panel.add(tools);

        MARGIN_FACTORY.accept(panel);
        return panel;
    }

    private JPanel createLeftSide() {
        JPanel panel = new JPanel();

        MARGIN_FACTORY.accept(panel);
        return panel;
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JLabel header = new JLabel(PARISH_NAME);
        JLabel subHeader = new JLabel(APP_NAME);

        header.setFont(header.getFont().deriveFont(Font.BOLD,24f));
        header.setHorizontalAlignment(JLabel.CENTER);
        subHeader.setFont(subHeader.getFont().deriveFont(16f));
        subHeader.setHorizontalAlignment(JLabel.CENTER);

        panel.add(header);
        panel.add(subHeader);

        MARGIN_FACTORY.accept(panel);
        return panel;
    }

    private JPanel createMainPane() {
        return new SingleDayPanel();
    }

    private JPanel createFooter() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        JLabel text = new JLabel(FOOTER_CONTENT);
        text.setHorizontalAlignment(JLabel.CENTER);
        text.setFont(text.getFont().deriveFont(10f));

        panel.add(text);

        MARGIN_FACTORY.accept(panel);
        return panel;
    }
}

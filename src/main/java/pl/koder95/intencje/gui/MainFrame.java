package pl.koder95.intencje.gui;

import com.github.lgooddatepicker.components.CalendarPanel;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.ClassLoader.getSystemResource;
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
        Action addN = new AbstractAction("add-n", new ImageIcon(getSystemResource("add-n-icon-23.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Dodaj");
            }
        };
        Action editN = new AbstractAction("edit-n", new ImageIcon(getSystemResource("edit-n-icon-23.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Edytuj");
            }
        };
        Action removeN = new AbstractAction("remove-n", new ImageIcon(getSystemResource("remove-n-icon-23.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Usuń");
            }
        };
        Action addI = new AbstractAction("add-i", new ImageIcon(getSystemResource("add-i-icon-23.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Dodaj");
            }
        };
        Action editI = new AbstractAction("edit-i", new ImageIcon(getSystemResource("edit-i-icon-23.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Edytuj");
            }
        };
        Action removeI = new AbstractAction("remove-i", new ImageIcon(getSystemResource("remove-i-icon-23.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Usuń");
            }
        };

        editN.setEnabled(false);
        removeN.setEnabled(false);
        editI.setEnabled(false);
        removeI.setEnabled(false);

        tools.add(addN).setToolTipText("Dodaj nazwę dnia");
        tools.add(editN).setToolTipText("Edytuj nazwę dnia");
        tools.add(removeN).setToolTipText("Usuń nazwę dnia");
        tools.addSeparator();
        tools.add(addI).setToolTipText("Dodaj nową intencję");
        tools.add(editI).setToolTipText("Edytuj zaznaczoną intencję");
        tools.add(removeI).setToolTipText("Usuń zaznaczoną intencję");

        tools.setFloatable(false);

        panel.add(tools);

        MARGIN_FACTORY.accept(panel);
        return panel;
    }

    private JPanel createLeftSide() {
        JPanel panel = new JPanel();
        CalendarPanel calendar = new CalendarPanel();

        panel.setMinimumSize(new Dimension(300, 200));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        calendar.setMinimumSize(new Dimension(200, 200));
        DatePickerSettings datePickerSettings = new DatePickerSettings();
        datePickerSettings.setFirstDayOfWeek(DayOfWeek.SUNDAY);
        calendar.setSettings(datePickerSettings);
        calendar.addCalendarSelectionListener(event -> {
            if (!event.isDuplicate()) fireSelect(event.getNewDate());
        });

        panel.add(calendar);
        calendar.setLocation(0, 0);
        calendar.setSize(new Dimension(300, 200));

        MARGIN_FACTORY.accept(panel);
        return panel;
    }

    private LocalDate selected = null;

    private void fireSelect(LocalDate selected) {
        this.selected = selected;
        LayoutManager l = getContentPane().getLayout();
        if (l instanceof BorderLayout) {
            BorderLayout bl = (BorderLayout) l;
            Component main = bl.getLayoutComponent(BorderLayout.CENTER);
            if (main instanceof SingleDayPanel) {
                SingleDayPanel sdp = (SingleDayPanel) main;
                sdp.setDate(selected);
            }
        }
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

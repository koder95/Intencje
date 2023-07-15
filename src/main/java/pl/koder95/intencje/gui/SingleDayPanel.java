package pl.koder95.intencje.gui;

import com.github.lgooddatepicker.tableeditors.TimeTableEditor;
import pl.koder95.intencje.core.RealIntention;
import pl.koder95.intencje.LoadException;
import pl.koder95.intencje.core.RealDayName;
import pl.koder95.intencje.core.VirtualDayName;
import pl.koder95.intencje.core.DayName;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static java.lang.ClassLoader.getSystemResource;

class SingleDayPanel extends AbstractMainPanel {

    private final IntentionTableModel intentionTableModel = new IntentionTableModel();
    private final JLabel dayName = new JLabel();
    private final DayNameModel dayNameModel = new DayNameModel(dayName);

    public IntentionTableModel getIntentionTableModel() {
        return intentionTableModel;
    }

    public SingleDayPanel() {
        this(LocalDate.now());
    }

    public SingleDayPanel(LocalDate date) {
        super(new BorderLayout());
        initComponents();
        setDate0(date);
    }

    public LocalDate getDate() {
        try {
            return dayNameModel.getDate();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setDate(LocalDate date) {
        setDate0(date);
    }

    private void setDate0(LocalDate date) {
        getIntentionTableModel().clear();
        try {
            dayNameModel.setValue(null);
            Collection<Object> list = new ArrayList<>(Arrays.asList(RealIntention.load(date)
                    .stream().map(i -> (pl.koder95.intencje.core.Intention) i).toArray()));
            RealDayName real = RealDayName.load(date);
            list.add(real == null? new VirtualDayName(date) : real);
            list.removeIf(Objects::isNull);
            this.load(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        JPanel name = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dayNameLabel = new JLabel("Nazwa dnia:");
        JTable table = new JTable(intentionTableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(100);
        table.getColumnModel().getColumn(1).setMinWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(100);
        table.getColumnModel().getColumn(0).sizeWidthToFit();
        table.getColumnModel().getColumn(1).sizeWidthToFit();
        table.setDefaultEditor(LocalTime.class, new TimeTableEditor());
        table.setCellSelectionEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);

        dayNameLabel.setLabelFor(dayName);
        table.getTableHeader().setReorderingAllowed(false);
        name.add(dayNameLabel);
        name.add(dayName);
        JButton changeNameButton = new JButton();
        changeNameButton.setHideActionText(true);
        changeNameButton.setBorderPainted(false);
        changeNameButton.setAction(new AbstractAction("change-dayname", new ImageIcon(getSystemResource("edit-n-icon-23.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = JOptionPane.showInputDialog(SingleDayPanel.this, "Podaj nazwę dnia:", "Zmiana nazwy dnia", JOptionPane.QUESTION_MESSAGE);
                try {
                    if (newName != null) {
                        dayNameModel.setName(newName);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        name.add(changeNameButton);
        this.add(name, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);

        MainFrame.MARGIN_FACTORY.accept(name);
        MainFrame.MARGIN_FACTORY.accept(this);
    }

    @Override
    public void load(Collection<Object> data) {
        data.forEach(o -> {
            if (o instanceof pl.koder95.intencje.core.DayName) {
                pl.koder95.intencje.core.DayName dayName = (pl.koder95.intencje.core.DayName) o;
                try {
                    dayNameModel.setValue(dayName);
                } catch (PropertyVetoException e) {
                    throw new LoadException("Cannot change " + dayNameModel.getValue() + " to: " + dayName, e);
                }
            } else if (o instanceof pl.koder95.intencje.core.Intention) {
                pl.koder95.intencje.core.Intention row = (pl.koder95.intencje.core.Intention) o;
                try {
                    getIntentionTableModel().add(row);
                } catch (Exception e) {
                    throw new LoadException("Cannot load this intention: " + row, e);
                }
            } else {
                throw new LoadException("Unsupported object type: " + o);
            }
        });
    }

    @Override
    public void save() throws Exception {
        DayName value = dayNameModel.getValue();
        if (value == null)
            throw new IllegalStateException("Nie można zapisać nazwy dnia, ponieważ nie została wcześniej załadowana", new NullPointerException());
        else {
            if (value instanceof RealDayName) {
                RealDayName dayName = (RealDayName) value;
                dayName.sync();
            } else if (value instanceof VirtualDayName) {
                dayNameModel.setValue(((VirtualDayName) value).toReal());
            } else {
                pl.koder95.intencje.core.db.DayName db = pl.koder95.intencje.core.db.DayName.get(dayNameModel.getDate());
                if (!Objects.isNull(db))
                    db.sync(dayNameModel);
                else
                    pl.koder95.intencje.core.db.DayName.create(dayNameModel.getDate(), dayNameModel.getName());
            }
        }
        if (intentionTableModel.getRowCount() == 0)
            throw new IllegalStateException("Nie można zapisać intencji, ponieważ wcześniej nie zostały żadne załadowane", new IndexOutOfBoundsException());
        else {
            for (int i = 0; i < intentionTableModel.getRowCount(); i++) {
                pl.koder95.intencje.core.Intention toSave = intentionTableModel.getRow(i);
                if (toSave instanceof RealIntention) {
                    ((RealIntention) toSave).sync();
                } else {
                    pl.koder95.intencje.core.db.Intention saver = pl.koder95.intencje.core.db.Intention.get(toSave.getMassTime());
                    saver.sync(toSave);
                }
            }
        }
    }
}

/*
 * Copyright (c) 2022.
 */

package pl.koder95.intencje.gui;

import pl.koder95.intencje.core.Intention;

import javax.swing.table.AbstractTableModel;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IntentionTableModel extends AbstractTableModel {

    private final LinkedList<Intention> list = new LinkedList<>();
    private final List<String> columnNames = Collections.unmodifiableList(Arrays.asList("Godzina Mszy", "Kaplica", "Intencje"));

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    private static Object getValue(Intention i, int columnIndex) throws Exception{
        return columnIndex == 0? i.getMassTime().toLocalTime() :
                columnIndex == 1? i.getChapel() :
                        columnIndex == 2? i.getContent() : null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Intention intention = getRow(rowIndex);
        try {
            return getValue(intention, columnIndex);
        } catch (Exception e) {
            return "błąd (" + e.getClass().getTypeName() + "): " + e.getLocalizedMessage();
        }
    }
    
    public Intention getRow(int rowIndex) {
        return list.get(rowIndex);
    }

    public List<Object> getColumn(int columnIndex) {
        if (columnIndex > 2) return null;

        return list.stream().map(i -> {
            try {
                return getValue(i, columnIndex);
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList());
    }

    public boolean add(Intention i) {
        boolean add = list.add(i);
        if (add) {
            int row = list.indexOf(i);
            fireTableRowsInserted(row, row);
        }
        return add;
    }

    public boolean addAll(Collection<Intention> intentions) {
        boolean addAll = list.addAll(intentions);
        if (addAll) {
            Optional<Intention> firstRow = intentions.stream().findFirst();
            Optional<Intention> lastRow = intentions.stream().skip(intentions.size()-1).findFirst();
            if (firstRow.isPresent() && lastRow.isPresent()) {
                int firstRowIndex = list.indexOf(firstRow.get());
                int lastRowIndex = list.indexOf(lastRow.get());
                fireTableRowsInserted(firstRowIndex, lastRowIndex);
            }
        }
        return addAll;
    }

    public boolean remove(Intention i) {
        int row = list.indexOf(i);
        boolean remove = list.remove(i);
        if (remove) {
            fireTableRowsDeleted(row, row);
        }
        return remove;
    }

    public void clear() {
        int lastRow = getRowCount() - 1;
        if (lastRow < 0) return;
        list.clear();
        fireTableRowsDeleted(0, lastRow);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Intention row = getRow(rowIndex);
        try {
            switch (columnIndex) {
                case 0: row.setMassTime(row.getMassTime().toLocalDate().atTime((LocalTime) aValue)); break;
                case 1: row.setChapel((String) aValue); break;
                case 2: row.setContent((String) aValue); break;
                default: throw new IndexOutOfBoundsException();
            }
            if (getValueAt(rowIndex,  columnIndex).equals(aValue))
                fireTableCellUpdated(rowIndex, columnIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return LocalTime.class;
            case 1:
            case 2:
                return String.class;
            default: return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}

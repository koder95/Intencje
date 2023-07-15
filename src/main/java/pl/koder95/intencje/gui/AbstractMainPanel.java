/*
 * Copyright (c) 2022.
 */

package pl.koder95.intencje.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public abstract class AbstractMainPanel extends JPanel {

    /**
     * Creates a new AbstractMainPanel with the specified layout manager and buffering
     * strategy.
     *
     * @param layout           the LayoutManager to use
     * @param isDoubleBuffered a boolean, true for double-buffering, which
     *                         uses additional memory space to achieve fast, flicker-free
     */
    public AbstractMainPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * Create a new buffered AbstractMainPanel with the specified layout manager
     *
     * @param layout the LayoutManager to use
     */
    public AbstractMainPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * Creates a new <code>AbstractMainPanel</code> with <code>FlowLayout</code>
     * and the specified buffering strategy.
     * If <code>isDoubleBuffered</code> is true, the <code>AbstractMainPanel</code>
     * will use a double buffer.
     *
     * @param isDoubleBuffered a boolean, true for double-buffering, which
     *                         uses additional memory space to achieve fast, flicker-free
     *                         updates
     */
    public AbstractMainPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * Creates a new <code>AbstractMainPanel</code> with a double buffer
     * and a flow layout.
     */
    public AbstractMainPanel() {
    }

    /**
     * Przetwarza kolekcję obiektów i ładuje je do wyświetlenia w panelu.
     *
     * @throws pl.koder95.intencje.LoadException obiekt z kolekcji nie jest wspierany lub spowodował problemy z jego załadowaniem
     * @param data kolekcja obiektów do załadowania
     */
    public abstract void load(Collection<Object> data);

    /**
     * Zapisuje w bazie danych wszystkie wcześniej załadowane obiekty poprzez metodę {@link #load(Collection)}.
     *
     * @throws java.sql.SQLException nie można zapisać w bazie danych załadowanych obiektów
     * @throws IllegalStateException nie załadowano wcześniej żadnych obiektów
     * @throws UnsupportedOperationException nie można zapisać obiektu, ponieważ jest nieobsługiwany
     * @throws Exception wystąpiły inne problemy z zapisaniem obiektów
     */
    public abstract void save() throws Exception;
}

package pl.koder95.intencje.gui.baskets;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface BasketCommand<T> extends ActionListener {

    /**
     * Wykonuje akcję dla konkretnego elementu w koszyku. Domyślnie jest wykonywana przez metodę
     * {@link #actionPerformed(ActionEvent, Basket)}, która wywołuje tę metodę dla każdego elementu.
     *
     * @param event zdarzenie, które wywołało tę akcję
     * @param basket koszyk, który zawiera {@code element}
     * @param element jeden z elementów znajdujących się w koszyku {@code basket}
     */
    void actionPerformed(ActionEvent event, Basket<?> basket, T element);

    /**
     * Wywołuje akcję dla konkretnego koszyka i w domyślnej implementacji dla każdego elementu tego koszyka
     * wywołuje metodę {@link #actionPerformed(ActionEvent, Basket, T)}. Następnie wywołuje trzy metody,
     * aby zaktualizować widok: {@link Basket#invalidate()}, {@link Basket#validate()} i {@link Basket#repaint()}.
     *
     * @param event zdarzenie, które wywołało tę akcję
     * @param basket koszyk, którego dotyczy ta akcja
     */
    @SuppressWarnings("unchecked")
    default void actionPerformed(ActionEvent event, Basket<?> basket) {
        basket.getElements().forEach(e -> actionPerformed(event, basket, (T) e));
        basket.revalidate();
        basket.repaint();
    }

    /**
     * Jeżeli źródło zdarzenia to koszyk to dla niego wywoływana jest metoda {@link #actionPerformed(ActionEvent,
     * Basket)}, ale jeżeli źródło zdarzenia jest instancją {@link Container kontenera AWT} to stara się
     * znaleźć wśród kontenerów wyżej w hierarchii taki, który jest koszykiem i dla niego
     * wywołuje metodę {@link #actionPerformed(ActionEvent, Basket)}.
     * W przeciwnym razie metoda nie robi nic.
     *
     * @param event zdarzenie, które wywołało akcję
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    @Override
    default void actionPerformed(ActionEvent event) {
        Object src = event.getSource();
        Basket<?> basket = null;
        if (src instanceof Basket) {
            basket = (Basket<?>) src;
        } else if (src instanceof Container) {
            Container parent = (Container) src;
            while (parent != null) {
                if (parent instanceof Basket) {
                    basket = (Basket<?>) parent;
                    break;
                }
                parent = parent.getParent();
            }
        }
        if (basket != null) {
            actionPerformed(event, basket);
        }
    }
}

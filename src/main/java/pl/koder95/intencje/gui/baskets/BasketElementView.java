package pl.koder95.intencje.gui.baskets;

/**
 * Interfejs, który powinien być implementowany przez klasę rozszerzającą {@link java.awt.Component komponent AWT}),
 * ponieważ wtedy spełnia swoje zadanie. Dzięki takiemu połączeniu obiekt może być dodany do koszyka i wykorzystany.
 *
 * @param <T> typ elementu koszyka
 */
public interface BasketElementView<T> {

    /**
     * @return element, który reprezentowany jest przez ten widok
     */
    T getElement();
}

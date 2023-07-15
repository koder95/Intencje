package pl.koder95.intencje.gui.baskets;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Koszyk (ang. Basket) jest panelem, który wyświetla elementy przy pomocy określonego widoku, który implementuje
 * interfejs {@link BasketElementView}. Na elementach wewnątrz koszyka można później wykonać polecenie zdefiniowane
 * przez interfejs {@link BasketCommand}.
 *
 * <p>Aby dodać element do panelu, należy do koszyka dodać widok tego elementu przy pomocy metody {@link #add(Component)},
 * co oznacza, że obiekt implementujący interfejs {@link BasketElementView} musi być klasy rozszerzającej klasę
 * {@link Component}.</p>
 *
 * Przykład:
 * <pre>
 *     class MyView extends JPanel implements BasketElementView&lt;Object&gt; {
 *
 *         private final Object element;
 *
 *         public MyView() {
 *             this(new Object());
 *         }
 *
 *         public MyView(Object o) {
 *             super();
 *             this.element = o;
 *         }
 *
 *         public Object getElement() {
 *             return element;
 *         }
 *     }
 *
 *     Basket&lt;Object&gt; myBasket = new Basket&lt;&gt;();
 *     myBasket.add(new MyView());
 * </pre>
 *
 * @param <E> typ elementów wewnątrz koszyka
 */
public class Basket<E> extends JPanel {

    @SuppressWarnings("unchecked")
    List<BasketElementView<E>> getViews() {
        //noinspection ConstantConditions
        return Collections.unmodifiableList(
                Arrays.stream(getComponents())
                        .filter(e -> e instanceof BasketElementView)
                        .map(e -> {
                            try {
                                return (BasketElementView<E>) e;
                            } catch (ClassCastException ex) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    List<E> getElements() {
        return Collections.unmodifiableList(
                getViews().stream()
                        .map(BasketElementView::getElement)
                        .collect(Collectors.toList()));
    }
}

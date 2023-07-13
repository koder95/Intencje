package pl.koder95.intencje.event;

import java.util.EventObject;

public class ConnectionTestingEvent extends EventObject {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ConnectionTestingEvent(Object source) {
        super(source);
    }

}

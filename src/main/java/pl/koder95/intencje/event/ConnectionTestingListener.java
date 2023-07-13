package pl.koder95.intencje.event;

import pl.koder95.intencje.core.db.ConnectionTester;

import java.util.EventListener;

public interface ConnectionTestingListener extends EventListener {

    void before(ConnectionTestingEvent evt, ConnectionTester.Step step);
    void after(ConnectionTestingEvent evt, ConnectionTester.Step step);
}

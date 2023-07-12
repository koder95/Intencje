package pl.koder95.intencje.event;

import pl.koder95.intencje.core.db.ConnectionTester;

public abstract class ConnectionTestingAdapter implements ConnectionTestingListener {

    @Override
    public void before(ConnectionTestingEvent evt, ConnectionTester.Step step) {
        // do nothing
    }

    @Override
    public void after(ConnectionTestingEvent evt, ConnectionTester.Step step) {
        // do nothing
    }
}

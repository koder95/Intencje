package pl.koder95.intencje.core;

import java.time.LocalDateTime;

public interface Intention {

    LocalDateTime getMassTime() throws Exception;

    void setMassTime(LocalDateTime massTime) throws Exception;

    String getChapel() throws Exception;

    void setChapel(String chapel) throws Exception;

    String getContent() throws Exception;

    void setContent(String content) throws Exception;
}

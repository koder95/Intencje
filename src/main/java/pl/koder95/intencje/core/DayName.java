package pl.koder95.intencje.core;

import java.time.LocalDate;

public interface DayName {

    LocalDate getDate() throws Exception;

    void setDate(LocalDate date) throws Exception;

    String getName() throws Exception;

    void setName(String name) throws Exception;
}

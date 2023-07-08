package pl.koder95.intencje.core;

import pl.koder95.intencje.core.Intention;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Klasa rozszerzająca {@link VirtualIntention wirtualną intencję},
 * której dane mają swoją reprezentację w bazie danych. Wirtualna intencja
 * to taka, której dane znajdują się jedynie w pamięci ulotnej. Realna to taka,
 * która ma swoje dane zapisane w sposób trwały. Usunięcie obiektu nie usuwa danych,
 * które on przechowywał.
 */
public class RealIntention extends VirtualIntention {

    private final pl.koder95.intencje.core.db.Intention dbInstance;

    /**
     * Tworzenie nowego obiektu, który powiązany będzie z konkretnym obiektem obsługującym
     * intencję w bazie danych
     * @param db obiekt sterujący bazą danych pobierając i zapisując w niej intencje
     */
    public RealIntention(pl.koder95.intencje.core.db.Intention db) {
        dbInstance = Objects.requireNonNull(db);
        try {
            update();
        } catch (Exception e) {
            throw new RuntimeException("Nie pobrano danych z bazy", e);
        }
    }

    /**
     * Zapisywanie danych obiektu w bazie danych i aktualizowanie obiektu zgodnie z danymi w bazie.
     *
     * @throws Exception problemy podczas zapisywania (np. brak połączenia z bazą)
     * lub ładowaniem ich do obiektów zapisujących (np. dane nie spełniają wewnętrznych wymagań bazy)
     * @see pl.koder95.intencje.core.db.Intention#sync(Intention)
     * @see #update()
     */
    public void sync() throws Exception {
        dbInstance.sync(this);
        update();
    }

    /**
     * Aktualizuje obiekt z bazą danych.
     *
     * @throws Exception problemy z pobieraniem danych (np. brak połączenia z bazą)
     * lub ładowaniem ich do obiektu (np. dane nie spełniają wewnętrznych wymagań)
     * @see pl.koder95.intencje.core.db.Intention#getMassTime()
     * @see pl.koder95.intencje.core.db.Intention#getChapel()
     * @see pl.koder95.intencje.core.db.Intention#getContent()
     */
    public void update() throws Exception {
        setMassTime(dbInstance.getMassTime());
        setChapel(dbInstance.getChapel());
        setContent(dbInstance.getContent());
    }

    @Override
    public RealIntention toReal() {
        try {
            sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Usuwa dane zapisane w sposób trwały i zwraca obiekt z danymi, które przechowuje ten obiekt.
     * Jeżeli zwrócony obiekt ma zawierać dane, które zostały wcześniej zapisane, należy najpierw
     * wywołać metodę {@link #update()}.
     *
     * @return obiekt klasy {@link VirtualIntention} z danymi pochodzącymi obiektu referencyjnego
     * @throws Exception podczas próby usuwania zapisów może dojść do problemów, które uniemożliwią
     * usunięcie (np. wcześniej zostały już usunięte dane obiektu)
     */
    public VirtualIntention toVirtual() throws Exception {
        dbInstance.kill();
        return new VirtualIntention(getMassTime(), getChapel(), getContent());
    }

    /**
     * Wczytuje wszystkie intencje ze źródła, którym jest baza danych.
     * @return lista obiektów, które zawierają dane zaciągnięte z bazy danych
     * @throws Exception problem z połączeniem z bazą lub ze wczytaniem danych
     */
    public static List<RealIntention> loadAll() throws Exception {
        List<pl.koder95.intencje.core.Intention> loaded = pl.koder95.intencje.core.db.Intention.loadAll();
        for (int i = 0; i < loaded.size(); i++) {
            pl.koder95.intencje.core.Intention e = loaded.get(i);
            if (e instanceof pl.koder95.intencje.core.db.Intention) {
                loaded.set(i, new RealIntention((pl.koder95.intencje.core.db.Intention) e));
            }
        }
        List<RealIntention> result = loaded.stream().filter(intention -> intention instanceof RealIntention)
                .map(intention -> (RealIntention) intention).collect(Collectors.toList());
        loaded.clear();
        return result;
    }

    /**
     * Wczytuje intencje, które przypisane są do podanej daty.
     *
     * @param date data warunkująca pojawienie się intencji w zwracanej liście
     * @return lista intencji, obiekty typu {@link RealIntention}
     * @throws Exception problem z połączeniem z bazą danych lub ze wczytaniem danych z niej
     */
    public static List<RealIntention> load(LocalDate date) throws Exception {
        return pl.koder95.intencje.core.db.Intention.load(date).stream()
                .filter(l -> l instanceof pl.koder95.intencje.core.db.Intention)
                .map(db -> new RealIntention((pl.koder95.intencje.core.db.Intention) db))
                .collect(Collectors.toList());
    }
}

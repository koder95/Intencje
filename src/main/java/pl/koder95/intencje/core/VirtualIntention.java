package pl.koder95.intencje.core;

import pl.koder95.intencje.core.db.Intention;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VirtualIntention implements pl.koder95.intencje.core.Intention {

    private LocalDateTime massTime;
    private String chapel;
    private String content;

    public VirtualIntention(LocalDateTime massTime, String chapel, String content) {
        this.massTime = massTime;
        this.chapel = chapel;
        this.content = content;
    }

    public VirtualIntention(LocalDateTime massTime, String content) {
        this(massTime, "", content);
    }

    public VirtualIntention(LocalDateTime massTime) {
        this(massTime, "");
    }

    public VirtualIntention() {
        this(LocalDate.now().plusDays(1).atTime(12, 0));
    }

    @Override
    public LocalDateTime getMassTime() {
        return massTime;
    }

    public void setMassTime(LocalDateTime massTime) {
        this.massTime = massTime;
    }

    @Override
    public String getChapel() {
        return chapel;
    }

    public void setChapel(String chapel) {
        this.chapel = chapel;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public RealIntention toReal() throws Exception {
        return new RealIntention(Intention.create(getMassTime(), getChapel(), getContent()));
    }
}

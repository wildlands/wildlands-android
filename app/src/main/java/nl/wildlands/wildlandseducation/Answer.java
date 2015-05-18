package nl.wildlands.wildlandseducation;


public class Answer
{
    private String answer;
    private boolean good;
    private long id;
    private long vraagId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVraagId() {
        return vraagId;
    }

    public void setVraagId(long vraagId) {
        this.vraagId = vraagId;
    }

    /**
     * Maak een nieuw antwoord aan met een string waarde en wel of niet goed
     * @param answer
     * @param good
     */
    public Answer(long id, long vraagId, String answer, boolean good)
    {
        this.id = id;
        this.vraagId = vraagId;
        this.answer = answer;
        this.good = good;
    }

    /**
     *
     * @return answer
     */
    public String getAnswer()
    {
        return answer;
    }

    /**
     *
     * @return good
     */
    public boolean isGood()
    {
        return good;
    }
}

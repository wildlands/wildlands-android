package nl.wildlands.wildlandseducation.quiz;


/**
 * Answer is een object voor een antwoord van een vraag in de quiz
 */
public class Answer
{
    private String answer;                  // Het antwoord
    private boolean good;                   // Goed of fout
    private long id;                        // Antwoordid voor opslag in sqlite
    private long vraagId;                   // Id van de vraag, waar het antwoord bij hoort



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

    /**
     *
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return vraagid
     */
    public long getVraagId() {
        return vraagId;
    }

    /**
     *
     * @param vraagId
     */
    public void setVraagId(long vraagId) {
        this.vraagId = vraagId;
    }
}

package nl.wildlands.wildlandseducation;

/**
 * Created by stefan on 6/23/2015.
 */
public class Score {
    private int energiescore;
    private int energietotaal;
    private int waterscore;
    private int watertotaal;
    private int materiaalscore;
    private int materiaaltotaal;
    private int bioscore;
    private int biototaal;
    private int dierenscore;
    private int dierentotaal;

    public Score()
    {
            energiescore = 0;
            energietotaal = 0;
        waterscore =0;
        watertotaal = 0;
        materiaaltotaal = 0;
        materiaalscore =0;
        dierenscore = 0;
        dierentotaal = 0;
        bioscore = 0;
        biototaal = 0;
    }

    public int getEnergiescore() {
        return energiescore;
    }

    public void setEnergiescore() {
        energiescore+=1;
    }

    public int getEnergietotaal() {
        return energietotaal;
    }

    public void setEnergietotaal() {
        energietotaal+=1;
    }

    public int getWaterscore() {
        return waterscore;
    }

    public void setWaterscore() {
        waterscore+=1;
    }

    public int getWatertotaal() {
        return watertotaal;
    }

    public void setWatertotaal() {
        watertotaal+=1;
    }

    public int getMateriaalscore() {
        return materiaalscore;
    }

    public void setMateriaalscore() {
        materiaalscore+=1;
    }

    public int getMateriaaltotaal() {
        return materiaaltotaal;
    }

    public void setMateriaaltotaal() {
        materiaaltotaal+=1;
    }

    public int getBioscore() {
        return bioscore;
    }

    public void setBioscore() {
        bioscore+=1;
    }

    public int getBiototaal() {
        return biototaal;
    }

    public void setBiototaal() {
        biototaal+=1;
    }

    public int getDierenscore() {
        return dierenscore;
    }

    public void setDierenscore() {
        dierenscore+=1;
    }

    public int getDierentotaal() {
        return dierentotaal;
    }

    public void setDierentotaal() {
        dierentotaal+=1;
    }
}

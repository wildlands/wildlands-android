package nl.wildlands.wildlandseducation.GlobalSettings;

/**
 * Values heeft alle statische waardes.
 */
public class Values {
    // Urls voor ophalen json en images
    public static final String BASE_URL = "http://smrt-kvm37.spothost.nl/";
    public static final String GET_QUESTIONS = "api/api.php?c=GetAllQuestions";
    public static final String GET_PINPOINTS = "api/api.php?c=GetAllPinpoints";
    public static final String GET_LEVELS = "api/api.php?c=GetAllLevels";
    public static final String GET_CHECKSUM = "api/api.php?c=GetDatabaseChecksum";
    public static final String GET_LAYERS = "api/api.php?c=GetAllLayers";
    public static final String IMAGE_BASE = "app/images/";

    // Socket url
    public static final String SOCKET_URL = "http://smrt-kvm37.spothost.nl:2345";

    // De hoofdthema's
    public static final String THEMA_1 = "Energie";
    public static final String THEMA_2 = "Water";
    public static final String THEMA_3 = "Bio Mimicry";
    public static final String THEMA_4 = "Materiaal";
    public static final String THEMA_5 = "Dierenwelzijn";

}

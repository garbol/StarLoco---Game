package org.starloco.locos.util.lang;

import org.starloco.locos.client.Player;
import org.starloco.locos.util.lang.type.English;
import org.starloco.locos.util.lang.type.French;
import org.starloco.locos.util.lang.type.Spanish;


/**
 * Created by Locos on 06/12/2015.
 */
public interface Lang {

    byte FRENCH = 1, ENGLISH = 2, SPANISH = 3;

    String get(int id);
    void initialize();

    static String get(Player player, int index) {
        return get(player.getGameClient().getLanguage(), index);
    }

    static String get(byte language, int index) {
        switch(language) {
            case FRENCH:
                return French.INSTANCE.getget(index);
            case ENGLISH:
                return English.INSTANCE.getget(index);
            case SPANISH:
                return Spanish.INSTANCE.getget(index);
            default:
                return "Unknown lang data, please contact administrator.";
        }
    }
}

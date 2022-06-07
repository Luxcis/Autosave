package top.luckyluxcis.autosave;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import org.apache.log4j.Logger;

public class AutosaveModPlugin extends BaseModPlugin {
    private static final Logger Log = Global.getLogger(AutosaveModPlugin.class);

    private static Autosaver saver;

    public void onApplicationLoad() throws Exception {
        Log.debug("Loading settings");
        Autosaver.reloadSettings();
        saver = new Autosaver();
        Log.debug("Settings loaded");
    }

    public void onGameLoad(boolean newGame) {
        saver.resetTimeSinceLastSave();
        Global.getSector().addTransientScript(saver);
        Global.getSector().addTransientListener(saver);
        Log.debug("Added autosaver to sector");
    }

    public void afterGameSave() {
        saver.resetTimeSinceLastSave();
    }
}

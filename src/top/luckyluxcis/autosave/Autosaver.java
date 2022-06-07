package top.luckyluxcis.autosave;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import org.json.JSONException;
import org.lazywizard.lazylib.JSONUtils;

import java.io.IOException;

public class Autosaver extends BaseCampaignEventListener implements EveryFrameScript {
    private static int autosaveIntervalMinutes;

    private static int autosaveAfterMinutes;

    private static boolean enableAutosave;

    private static boolean forceSaveAfterPlayerJumped;

    private static boolean forceSaveAfterMinutes;

    private static long lastAutosaveTime;

    private boolean shouldAutosave = false;

    public Autosaver() {
        super(false);
        resetTimeSinceLastSave();
    }

    protected void resetTimeSinceLastSave() {
        lastAutosaveTime = 0L;
    }

    public static void reloadSettings() throws IOException, JSONException {
        JSONUtils.CommonDataJSONObject settings = JSONUtils.loadCommonJSON("autosave_settings.json", "autosave_settings.json.default");
        enableAutosave = settings.getBoolean("enableAutosave");
        forceSaveAfterPlayerJumped = settings.getBoolean("forceSaveAfterPlayerJumped");
        forceSaveAfterMinutes = settings.getBoolean("forceSaveAfterMinutes");
        autosaveIntervalMinutes = settings.getInt("autosaveIntervalMinutes") * 60 * 1000;
        autosaveAfterMinutes = settings.getInt("forceAutosaveMinutes") * 60 * 1000;
    }

    @Override
    public void reportFleetJumped(CampaignFleetAPI fleet, SectorEntityToken from, JumpPointAPI.JumpDestination to) {
        if (forceSaveAfterPlayerJumped && fleet.isPlayerFleet()) {
            this.shouldAutosave = true;
        }
    }

    private void check() {
        long current = System.currentTimeMillis();
        long interval = current - lastAutosaveTime;
        if (interval <= autosaveIntervalMinutes) {
            this.shouldAutosave = false;
        }
        if (forceSaveAfterMinutes && interval >= autosaveAfterMinutes) {
            this.shouldAutosave = true;
        }
    }

    @Override
    public void advance(float v) {
        CampaignUIAPI ui = Global.getSector().getCampaignUI();
        if (Global.getSector().isInNewGameAdvance() || ui.isShowingDialog() || ui.isShowingMenu()) {
            return;
        }
        check();
        if (enableAutosave && this.shouldAutosave) {
            this.shouldAutosave = false;
            lastAutosaveTime = System.currentTimeMillis();
            Global.getSector().getCampaignUI().cmdSave();
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }
}

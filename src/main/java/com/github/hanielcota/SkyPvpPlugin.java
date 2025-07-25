package com.github.hanielcota;

import com.github.hanielcota.logging.MinecraftLogger;
import com.github.hanielcota.points.PlayerPoints;
import com.github.hanielcota.registry.CommandRegistry;
import com.github.hanielcota.registry.ListenerRegistry;
import com.github.hanielcota.scoreboard.Scoreboard;
import com.github.hanielcota.utils.ConfigUtils;
import com.github.hanielcota.utils.LocationUtils;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import static com.github.hanielcota.task.ItemSpawnTask.startTask;


@Getter
public class SkyPvpPlugin extends JavaPlugin {

    private ConfigUtils locationConfig;
    private ConfigUtils dropsConfig;
    private ConfigUtils messageConfig;
    private MinecraftLogger log;
    private LocationUtils locationUtils;
    private CommandRegistry commandRegistry;
    private PlayerPoints playerPoints;

    @Override
    public void onEnable() {
        initializeLogger();
        loadConfiguration();
        initializeUtils();
        initializePlayerPoints();
        initializeListenerRegistry();
        initializeCommandRegistry();
        new Scoreboard(this).startScoreboardUpdateTask();
        startTask(this, locationUtils);
        log.info("SkyPvp - Starting...");
    }

    private void initializeLogger() {
        log = new MinecraftLogger("Floruit SkyPvP");
    }

    private void loadConfiguration() {
        locationConfig = new ConfigUtils(this, "locations.yml");
        messageConfig = new ConfigUtils(this, "messages.yml");
        dropsConfig = new ConfigUtils(this, "drops.yml");
    }

    private void initializeUtils() {
        locationUtils = new LocationUtils(this);
    }

    private void initializePlayerPoints() {
        playerPoints = new PlayerPoints();
    }

    private void initializeListenerRegistry() {
        new ListenerRegistry(this).registerAllEventListeners();
    }

    private void initializeCommandRegistry() {
        commandRegistry = new CommandRegistry(this);
        commandRegistry.registerAllCommands();
    }
}

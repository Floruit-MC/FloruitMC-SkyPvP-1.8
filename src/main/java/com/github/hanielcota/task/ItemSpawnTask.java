package com.github.hanielcota.task;

import com.github.hanielcota.SkyPvpPlugin;
import com.github.hanielcota.utils.LocationUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ItemSpawnTask implements Runnable {

    private final LocationUtils locationUtils;
    private final SkyPvpPlugin plugin;
    private final Random random = new Random();
    private List<Material> materials;
    @Getter
    private long interval;

    public ItemSpawnTask(SkyPvpPlugin plugin, LocationUtils locationUtils) {
        if (plugin == null || locationUtils == null) {
            throw new IllegalArgumentException("Plugin e LocationUtils não podem ser nulos.");
        }
        this.plugin = plugin;
        this.locationUtils = locationUtils;
        loadConfiguration();
    }

    private void loadConfiguration() {
        FileConfiguration config = plugin.getDropsConfig().getConfiguration();

        if (config == null) {
            Bukkit.getLogger().severe("Configuração não foi carregada corretamente.");
            return;
        }

        materials = new ArrayList<>();
        List<String> materialNames = config.getStringList("itemspawn.materials");
        if (materialNames.isEmpty()) {
            Bukkit.getLogger().warning("Nenhum material definido no config.");
            return;
        }

        for (String materialName : materialNames) {
            Material material = Material.getMaterial(materialName);
            if (material != null) {
                materials.add(material);
            } else {
                Bukkit.getLogger().warning("Material inválido no config: " + materialName);
            }
        }

        if (materials.isEmpty()) {
            Bukkit.getLogger().severe("Lista de materiais está vazia após tentativa de carregamento.");
            return;
        }

        interval = config.getLong("itemspawn.interval", 600L);
        if (interval <= 0) {
            Bukkit.getLogger().warning("Intervalo inválido no config, usando padrão 600L.");
            interval = 600L;
        }
    }

    @Override
    public void run() {
        if (materials == null || materials.isEmpty()) {
            Bukkit.getLogger().severe("Não há materiais configurados para spawn.");
            return;
        }

        Optional<Location> spawnLocationOpt = locationUtils.getLocation("itemSpawn");
        if (spawnLocationOpt.isEmpty()) {
            Bukkit.getLogger().info("Nenhuma localização de spawn de itens específica foi definida.");
            return;
        }

        Location spawnLocation = spawnLocationOpt.get();
        if (spawnLocation.getWorld() == null) {
            Bukkit.getLogger().severe("A localização de spawn ou o mundo são nulos.");
            return;
        }

        Material randomMaterial = materials.get(random.nextInt(materials.size()));

        if (randomMaterial == null) {
            Bukkit.getLogger().severe("Material aleatório gerado é nulo.");
            return;
        }

        ItemStack itemStack = new ItemStack(randomMaterial);
        Item item = spawnLocation.getWorld().dropItemNaturally(spawnLocation, itemStack);
        if (item == null) {
            Bukkit.getLogger().severe("O item gerado é nulo.");
            return;
        }

        Bukkit.getLogger().info("Item " + randomMaterial.name() + " spawnado na localização de itens.");
    }

    public static void startTask(SkyPvpPlugin plugin, LocationUtils locationUtils) {
        if (plugin == null || locationUtils == null) {
            throw new IllegalArgumentException("Plugin e LocationUtils não podem ser nulos.");
        }

        ItemSpawnTask task = new ItemSpawnTask(plugin, locationUtils);
        Bukkit.getScheduler().runTaskTimer(plugin, task, 0L, task.getInterval());
    }

}

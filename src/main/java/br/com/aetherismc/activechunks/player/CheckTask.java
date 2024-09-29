package br.com.aetherismc.activechunks.player;

import br.com.aetherismc.activechunks.Core;
import org.bukkit.scheduler.BukkitRunnable;

public class CheckTask extends BukkitRunnable {

    @Override
    public void run() {
        Core.core.getDatabase().checkTask();
    }
}

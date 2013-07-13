package risjarv.rpbl.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import risjarv.rpbl.main;
/**
 * @author risjarv
 */
public class ChatListener implements Listener {
    main plugin;

    public ChatListener(main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onChat(final AsyncPlayerChatEvent e) {
        if (!plugin.Queue.containsKey(e.getPlayer().getName())) {
            return;
        } 
        plugin.rpbl_handleChat(e.getPlayer(), e.getMessage());
        e.setCancelled(true);
    }
}
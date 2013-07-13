/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package risjarv.rpbl.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import risjarv.rpbl.main;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter.Result;
/*
 * @author risjarv
 */
public class HeroChatListener implements Listener {
    private main plugin;
    
    public HeroChatListener(main plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onHeroChat(ChannelChatEvent e) {
        if (!plugin.Queue.containsKey(e.getSender().getPlayer().getName())) {
            return;
        } 
        plugin.rpbl_handleChat(e.getSender().getPlayer(), e.getMessage());
        e.setResult(Result.FAIL);
    }
}

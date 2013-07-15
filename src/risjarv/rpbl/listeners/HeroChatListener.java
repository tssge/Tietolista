/*****************************************************************
 * 
 * HeroChatListener
 * 
 * @version 1.0
 * 
 * @author Risjarv
 * 
 * Copyright (c) 2013 - $(year), Risjarv, All rights reserved.
 * 
 ****************************************************************/

package risjarv.rpbl.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import risjarv.rpbl.main;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter.Result;

/**
 * The listener interface for receiving heroChat events.
 * The class that is interested in processing a heroChat
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addHeroChatListener<code> method. When
 * the heroChat event occurs, that object's appropriate
 * method is invoked.
 *
 * @see HeroChatEvent
 */
public class HeroChatListener implements Listener {
    
    /** The plugin. */
    private main plugin;
    
    /**
     * Instantiates a new hero chat listener.
     *
     * @param plugin the plugin
     */
    public HeroChatListener(main plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handles the HeroChat event
     *
     * @param The HeroChat chat event, e
     */
    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onHeroChat(ChannelChatEvent e) {
        if (!plugin.Queue.containsKey(e.getSender().getPlayer().getName())) {
            return;
        } 
        plugin.rpbl_handleChat(e.getSender().getPlayer(), e.getMessage());
        e.setResult(Result.FAIL);
    }
}

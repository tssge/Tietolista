/*****************************************************************
 *  
 * ChatListener
 * 
 * @version 1.0
 * 
 * @author Risjarv
 *  
 * Copyright (c) 2013 - 2013, Risjarv, All rights reserved.
 * 
 ****************************************************************/

package risjarv.rpbl.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import risjarv.rpbl.main;

/**
 * The listener interface for receiving chat events.
 * The class that is interested in processing a chat
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addChatListener<code> method. When
 * the chat event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ChatEvent
 */
public class ChatListener implements Listener {
    
    /** The instance of this plugins main class. */
    main plugin;

    /**
     * Instantiates a new chat listener.
     *
     * @param plugin the plugin
     */
    public ChatListener(main plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles the AsyncPlayerChat event.
     *
     * @param event, the fired AsyncPlayerChatEvent
     */
    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onChat(final AsyncPlayerChatEvent e) {
        if (!plugin.Queue.containsKey(e.getPlayer().getName())) {
            return;
        } 
        plugin.rpbl_handleChat(e.getPlayer(), e.getMessage());
        e.setCancelled(true);
    }
}
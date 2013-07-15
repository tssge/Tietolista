package risjarv.rpbl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import risjarv.rpbl.listeners.ChatListener;
import risjarv.rpbl.listeners.Command;
import risjarv.rpbl.listeners.HeroChatListener;
import risjarv.rpbl.util.network;

/**
 *
 * @author risjarv
 */
public class main extends JavaPlugin implements Listener {

    public List<String> banCommand;
    public boolean automaticInfo;
    public boolean automaticInfo_toConsole;
    public String authKey = "";

    public String cServer = "";

    private HashMap<String,Integer> State = new HashMap<String, Integer>();
    public HashMap<String, String> Queue = new HashMap<String, String>();
    private ChatListener chatListener;
    private HeroChatListener heroChatListener;
    private Command exec;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();
        this.banCommand = this.getConfig().getStringList("ban_commands");
        this.automaticInfo = this.getConfig().getBoolean("automatic_info");
        this.automaticInfo_toConsole = this.getConfig().getBoolean("log_to_console");

        getServer().getPluginManager().registerEvents(this,this);

        /*
         * Just to listen herochat custom events, in a case it is enabled.
         */
        if( Bukkit.getPluginManager().getPlugin("Herochat") != null ) {
            this.heroChatListener = new HeroChatListener(this);
	    Bukkit.getServer().getPluginManager().registerEvents(this.heroChatListener, this);
            getLogger().info("Listening herochat events!");
        //otherwise just use normal listener
	} else{
            this.chatListener = new ChatListener(this);
            Bukkit.getServer().getPluginManager().registerEvents(chatListener, this);
	}

        exec = new Command(this);
        getCommand("rbpl").setExecutor(exec);

        //I know, next 3 lines might override other plugin commands, if theyre already registered.
        //or even other plugins may override them.
        getCommand("raportti").setExecutor(exec);
        getCommand("tunnistaudu").setExecutor(exec);
        getCommand("poista").setExecutor(exec);

    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onPreCommand(PlayerCommandPreprocessEvent event) {

        final Player p = event.getPlayer();
        final String cmd[] = event.getMessage().split(" ");

        if( cmd.length > 0 ) {

            if( banCommand.contains(cmd[0].toLowerCase()) && p.hasPermission("rpbl.ban") ) {

                if (Queue.containsKey(p.getName())) {
                    return;
                }

                // Just to be sure first argument is player
                Player target = null;
                OfflinePlayer off = null;
                String name = "";
                target = Bukkit.getPlayer(cmd[1]);
                if (target != null) {
                    name = target.getName();
                } else {
                    off = Bukkit.getServer().getOfflinePlayer(cmd[1]);
                    if (off != null) {
                        name = off.getName();
                    }
                }

                if (name.equals("")) {
                    p.sendMessage("Kirjoitit bannittavan pelaajan nimen väärin.");
                } else {
                    Queue.put(p.getName(), cServer + " " + p.getName() + " " + name);

                    getLogger().info(event.getMessage());
                    p.sendMessage("Kuvaile lyhyesti parilla sanalla mikä oli bannin syy ja kesto.");

                }
            }
        }
    }

    /*
     * Probably should ask nickname, instead of 'konsoli'.
     * Not fully done.
     */
    @EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onServerCommand(ServerCommandEvent event) {
        String console = event.getSender().getName();
        String cmd[] = event.getCommand().split(" ");

        if( cmd.length > 0 ) {

            if( banCommand.contains("/"+cmd[0].toLowerCase()) ) {

                if( Queue.containsKey(console) ) {
                    return;
                }

                Player target = null;
                OfflinePlayer off = null;
                String name = "";

                target = Bukkit.getPlayer(cmd[1]);
                if (target != null) {
                    name = target.getName();
                } else {
                    off = Bukkit.getServer().getOfflinePlayer(cmd[1]);
                    if (off != null) {
                        name = off.getName();
                    }
                }

                if( name.equals("") ) {
                    event.getSender().sendMessage("Kirjoitit bannittavan pelaajan nimen väärin.");
                } else {
                    Queue.put(console, this.cServer+" konsoli "+name);

                    getLogger().info(event.getCommand());
                    event.getSender().sendMessage("Kuvaile lyhyesti parilla sanalla mikä olin bannin syy ja kesto.");
                }
            }
        }
    }

    /*
     * Not firing in craftbukkit, when offline mode is enabled.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {

        //new player check.
        long b = Bukkit.getServer().getOfflinePlayer(event.getName()).getLastPlayed();

        if( b == 0L && automaticInfo == true ) {

            Map<String,String> map = new ConcurrentHashMap<String, String>();
            map.put("player", event.getName());
            String text = network.sendData("http://localhost/search.php",map);

            for (Player current : Bukkit.getServer().getOnlinePlayers()) {
                if (current != null && current.hasPermission("rbpl.autoinfo")) {
                    current.sendMessage(text);
                }
            }

            if (automaticInfo_toConsole == true) {
                getLogger().info(text);
            }

        }
    }

    public void rpbl_handleChat(Player player, String message) {
        String name = player.getName();
        String msg = Queue.get(name);

         getLogger().info(message);
        if( message.equalsIgnoreCase("ei") ) {
            Queue.remove(name);
            State.remove(name);
        } else if( message.isEmpty() ) {
            player.sendMessage("Viesti oli tyhjä. Jos haluat perua lähetyksen, kirjoita: ei");
        }

        if( !State.isEmpty() && State.get(name) == 1 ) {
            msg = msg+ " " + message;
            getLogger().info(msg);
            Queue.remove(name);
            State.remove(name);
            player.sendMessage("Tiedot on lähetetty.");

        } else {
            Queue.put(name,msg+ " " + message);
            getLogger().info(Queue.get(name));
            State.put(name,1);
            player.sendMessage("Kuvaile pitempi versio bannin syystä ja omat mietteet bannitusta.");
            player.sendMessage("Kun olet painanu enter, tiedot lähetetään.");
        }

    }
}

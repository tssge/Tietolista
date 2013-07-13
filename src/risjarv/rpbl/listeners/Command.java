package risjarv.rpbl.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import risjarv.rpbl.main;
import risjarv.rpbl.util.network;

/*
 * @author risjarv
 */
public class Command implements CommandExecutor {

    private main plugin;
    public Map<String,String> msg = new ConcurrentHashMap<>();

    public Command(main plugin) {
	this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender cs, org.bukkit.command.Command cmds, String label, final String[] args) {
        String cmd = cmds.getName();
        final String name = cs.getName();
        Player p = Bukkit.getPlayer(name);

        if ( cmd.toLowerCase().equals("/raportti") && p.hasPermission("rbpl.report") ) {
            if( args.length >= 0 ) {

                final Map<String, String> map = new ConcurrentHashMap<>();

                map.put("player", args[0]);
                if (args.length > 1 && args[1].equalsIgnoreCase("bans")) {
                    map.put("more", "1");
                    if (args.length > 1) {
                        map.put("server", args[2]);
                    }
                }

                final BukkitTask task;
                task = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new BukkitRunnable() {
                    public boolean once = false;
                    @Override
                    public void run() {
                        final String text;
                        if (once == false) {
                            once = true;
                            text = network.sendData("http://localhost/search.php", map);

                            plugin.getServer().getScheduler().runTask(plugin, new BukkitRunnable() {
                                @Override
                                public void run() {
                                    cs.sendMessage(text);
                                }
                            });
                        }
                    }
                });

                return true;
            } else {
                return false;
            }
        } else if ( cmd.toLowerCase().equals("/tunnistaudu") && p.hasPermission("rbpl.register") ) {
            if( args.length > 0 ) {
                plugin.cServer = args[0];
                for(int i = 0; i < args.length; i++)
                    plugin.getLogger().info( args[i] );

                this.plugin.getConfig().set("auth_key", "empty");
                this.plugin.saveConfig();
                //args[2]
                return true;
            } else {
                return false;
            }
        } else if ( cmd.toLowerCase().equals("/poista") && p.hasPermission("rbpl.remove") ) {
            if( args.length > 0 ) {

                return true;
            } else {
                return false;
            }
        } else if( cmd.toLowerCase().equals("/rbpl") && p.hasPermission("rbpl.reload") ) {
            if( args.length > 0 && args[0].equalsIgnoreCase("reload") ) {
                this.plugin.reloadConfig();
                this.plugin.banCommand = this.plugin.getConfig().getStringList("ban_commands");
                this.plugin.automaticInfo = this.plugin.getConfig().getBoolean("automatic_info");
                this.plugin.automaticInfo_toConsole = this.plugin.getConfig().getBoolean("log_to_console");
                p.sendMessage("Pluginin asetukset on uudelleenladattu.");
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

}

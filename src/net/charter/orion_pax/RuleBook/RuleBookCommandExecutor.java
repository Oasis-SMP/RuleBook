package net.charter.orion_pax.RuleBook;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RuleBookCommandExecutor implements CommandExecutor 
{
    public static RuleBook plugin;
    
    public RuleBookCommandExecutor(RuleBook instance)
    {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("rules")) 
        {
            if (args.length == 0) 
            {
                if (sender instanceof Player) 
                {
                    Player player = (Player) sender;
                    if(player.hasPermission("rulebook.players") || player.hasPermission("rulebook.staff"))
                    {
                        plugin.givebook(player,1);
                        return true;
                    }
                } 
                else 
                {
                    sender.sendMessage("That command is not available on the console!");
                    return false;
                }
            }
            else if (args.length == 1) 
            {
                if (sender.hasPermission("rulebook.staff")) 
                {
                	if (args[0].equalsIgnoreCase("replace"))
                	{
                		Player player = (Player) sender;
                		plugin.GetNewRuleBook(player);
                		return true;
                	}
                	if (args[0].equalsIgnoreCase("restore"))
                	{
                		File configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("RuleBook").getDataFolder(), "config.yml");
                        if (configFile.exists()) {
                        	configFile.delete();
                        	plugin.saveDefaultConfig();
                        	plugin.reloadConfig();
                        	plugin.book=null;
                        	plugin.author=null;
                        	plugin.title=null;
                        	plugin.pages=null;
                        	plugin.LoadRuleBook();
                        	plugin.CraftRuleBook(1);
                        	plugin.givebookmessage = ChatColor.GOLD + "You were given The Official "+ChatColor.BLUE + plugin.title +ChatColor.GOLD + " written by "+ ChatColor.GREEN + plugin.author;
                        	return true;
                        }
                	}
                    if (args[0].equalsIgnoreCase("reload"))
                    {
                        plugin.reloadConfig();
                        plugin.pages = null;
                        plugin.LoadRuleBook();
                        plugin.CraftRuleBook(1);
                        plugin.saveConfig();
                        plugin.getLogger().info("RuleBook config reloaded!");
                        sender.sendMessage(ChatColor.GOLD+"Rulebook config reloaded!");
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("give"))
                    {
                        sender.sendMessage(ChatColor.RED + "Too few arguments.");
                    }
                    else if (args[0].equalsIgnoreCase("togglekick"))
                    {
                        plugin.enablekick ^= true;
                        if (!(plugin.enablekick))
                        {
                            if (sender instanceof Player)
                            {
                                sender.sendMessage(ChatColor.GOLD + "Rulebook give on kick " + ChatColor.RED + "DISABLED.");
                            }
                            plugin.getLogger().info("Rulebook give on kick DISABLED.");
                        } 
                        else 
                        {
                            if (sender instanceof Player)
                            {
                                sender.sendMessage(ChatColor.GOLD + "Rulebook give on kick " + ChatColor.GREEN + "ENABLED.");
                            }
                            plugin.getLogger().info("Rulebook give on kick ENABLED.");
                        }
                        return true;
                    }
                }
            }
            else if (args.length > 1) 
            {
                if (sender.hasPermission("rulebook.staff")) 
                {
                    if (args[0].equalsIgnoreCase("give"))
                    {
                        if (args[1].equalsIgnoreCase("all"))
                        {
                            Player[] onlinePlayers = Bukkit.getServer().getOnlinePlayers();
                            for (Player player : onlinePlayers)
                            {
                                if (player != null)
                                {
                                    plugin.givebook(player,1);
                                    return true;
                                }
                            }
                        }
                        else
                        {
                            Player target = (Bukkit.getServer().getPlayer(args[1]));
                            if (target == null || !(target.isOnline() == true)) 
                            {
                                sender.sendMessage(args[1] + " is not online!");
                                return false;
                            }
                            else
                            {
                                if (args[2].equalsIgnoreCase(null))
                                {
                                    plugin.totalbooks=1;
                                }
                                else if (!(args[2].equalsIgnoreCase(null)))
                                {
                                    try
                                    {
										plugin.totalbooks = Integer.parseInt(args[2]);
									}
                                    catch (NumberFormatException nFE)
                                    {
										sender.sendMessage(ChatColor.RED + "Must be a WHOLE NUMBER!");
										return false;
									}
                                }
                                plugin.givebook(target,plugin.totalbooks);
                                return true;
                            }
                        }
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "Invalid command.");
                    }
                }
            }
        }
        return false;
    }
}
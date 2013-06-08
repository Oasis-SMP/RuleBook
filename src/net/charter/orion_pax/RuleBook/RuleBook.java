package net.charter.orion_pax.RuleBook;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class RuleBook extends JavaPlugin implements Listener
{
    public static RuleBook plugin;
    String author; //These were giving you the NPE, you were trying to give null tags in a book.
    String title; //I edited the CraftRuleBook() method to prevent the NPE.
    List<String> pages;
    //String[] pages; //You're welcome. Now, can you teach me how to do Github? I want to get into it. I'm so confused when doing it, though.
    ItemStack book;
    String[] playerdatabase; //Watch it with setting things to null.
    String naughty = ChatColor.RED + "You have been a bad boy/girl! So here is another rulebook!"; //Moved so that
    String givebookmessage;
    String yourfull = ChatColor.GOLD + "Your inventory is full, rulebook was dropped on ground!"; //It didn't need to be in the onEnable method.
    ArrayList<String> kickedplayer = new ArrayList<String>(); //Always give an identifier for an ArrayList. It helps prevent errors.
    boolean enablekick = true;
    int totalbooks;
    
    @Override
    public void onEnable()
    {
        this.getCommand("rules").setExecutor(new RuleBookCommandExecutor(this)); //This was never the problem.
        Bukkit.getPluginManager().registerEvents(this, this);
        
         this.saveDefaultConfig();
        
         LoadRuleBook();
        
         CraftRuleBook(1); //Edited to prevent the NPE it was indirectly causing.
        
         givebookmessage = ChatColor.GOLD + "You were given The Official "+ChatColor.BLUE + title +ChatColor.GOLD + " written by "+ ChatColor.GREEN + author; //This class can grab it...
         
         getLogger().info("RuleBook has been enabled!"); //Always add this at the end so that the enabling occurs first. Chronologically, anyways. What if an error is thrown after the enable? That'd confuse me as a user.
    }
    
    @Override
    public void onDisable()
    {
        getLogger().info("RuleBook has been disabled!");
    }
   
    public void givebook(Player player, int total) {
        if (total > 1) {
            book = null; // Watch it with the settings of null!
            CraftRuleBook(total);
        }
        if (!isfull(player)) {
            player.getInventory().addItem(book);
            if (!(kickedplayer.contains(player.getName()))) {
                player.sendMessage(givebookmessage);
            } else {
                player.sendMessage(naughty);
            }
        } else {
            player.getWorld().dropItem(player.getLocation(), book);
            if (kickedplayer.contains(player.getName())) {
                player.sendMessage(naughty);
            }
            player.sendMessage(yourfull);
        }
        if (total > 1) {
            book = null;
            CraftRuleBook(1);
        }
    }

    public boolean isfull(Player player) {
        if (player.getInventory().firstEmpty() != -1) {
            return false;
        } else {
            return true;
        }
    }

    public void LoadRuleBook() {
        pages = this.getConfig().getStringList("pages");
        author = this.getConfig().getString("author");
        title = this.getConfig().getString("title");
    }

    @EventHandler
    public void OnPlayerKick(PlayerKickEvent event) {
        if (event.getPlayer().isBanned() == false) {
            if (enablekick) {
                kickedplayer.add(event.getPlayer().getName());
            }
        }
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer(); // Who set off the PlayerJoinEvent

        if (!player.hasPlayedBefore()) {
            givebook(player, 1);
        }

        if (kickedplayer.contains(player.getName())) {
        	player.sendMessage(ChatColor.DARK_RED + "Apparently you have been a bad bad Minecrafter...");
        	player.sendMessage(ChatColor.DARK_RED + "So here is another rule book for you to read!");
            givebook(player, 1);
            kickedplayer.remove(player.getName());
        }
    }

    public void CraftRuleBook(int total) //Edited to prevent NPE
    {
    	book = new ItemStack(Material.WRITTEN_BOOK,total);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setAuthor(author);
        meta.setTitle(title);
        meta.setPages(pages);
        book.setItemMeta(meta);
    }
    
    public void GetNewRuleBook(Player player)
    {
    	if (player.getItemInHand().getType()==Material.WRITTEN_BOOK) {
			BookMeta meta = (BookMeta) player.getItemInHand().getItemMeta();
			author = meta.getAuthor();
			title = meta.getTitle();
			pages = meta.getPages();
			givebookmessage = ChatColor.GOLD + "You were given The Official "
					+ ChatColor.BLUE + title + ChatColor.GOLD + " written by "
					+ ChatColor.GREEN + author; //This class can grab it...
			this.getConfig().set("author", author);
			this.getConfig().set("title", title);
			this.getConfig().set("pages", pages);
			this.saveConfig();
			book = null;
			CraftRuleBook(1);
		}else{
			player.sendMessage(ChatColor.RED + "Must have a " + ChatColor.BLUE + ChatColor.BOLD + "WRITTEN BOOK" + ChatColor.RED + " in your hand!");
		}
    }
    
    public void RestoreDefaults()
    {
    	this.getConfig().options().copyDefaults(true);
    }
}
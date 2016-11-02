package water.supersoul.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;

public class Main extends JavaPlugin implements Listener {
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("SSProject v1.0 Enabling...");
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		Questions.put(1, "Please tell us your name.");
		Questions.put(2, "Please tell us your age.");
		Questions.put(3, "How long have you benn playing Minecraft?" + ChatColor.RED + "(In forms of years.)");
		Questions.put(4, "How long have you been playing SuperSoul?" + ChatColor.RED + "(In forms of months.)");
		Questions.put(5, "What do you want to build in your world?");
		Questions.put(6, "Will anyone else build with you?");
		Questions.put(7, "What is the name of your world?");
		Questions.put(8, "What is the type of your world?");
		Questions.put(9, "What is the generation type of your world? (Default , Superflat , Large Biomes...)");
		Questions.put(10, "Is PvP enabled in your world? (Yes / No)");
		Questions.put(11, "Can others enter your world? (Yes / No)");
		Questions.put(12, "Is animal spawning enabled? (Yes / No)");
		Questions.put(13, "Is monster spawning enabled? (Yes / No)");
		Questions.put(14, "Is building spawning enabled? (Yes/ No)");
		File answersfile = new File(getDataFolder().getPath(), "Answers.yml");
		FileConfiguration answerscfg = YamlConfiguration.loadConfiguration(answersfile);
		List<String> s = answerscfg.getStringList("Answers");
		for (String str : s) {
			String[] words = str.split(" : ");
			Answers.put(words[0], words[1]);
		}

		File pendingappliesfile = new File(getDataFolder().getPath(), "PendingApply.yml");
		FileConfiguration pendingappliescfg = YamlConfiguration.loadConfiguration(pendingappliesfile);
		List<String> s2 = pendingappliescfg.getStringList("Applies");
		for (String str : s2) {
			String[] words = str.split(" : ");
			PendingApply.put(words[0], words[1]);
		}

	}

	public void onDisable() {
		getLogger().info("SSProject v1.0 Disabling...");

		File answersfile = new File(getDataFolder().getPath(), "Answers.yml");
		FileConfiguration answerscfg = YamlConfiguration.loadConfiguration(answersfile);
		File pendingappliesfile = new File(getDataFolder().getPath(), "PendingApply.yml");
		FileConfiguration pendingappliescfg = YamlConfiguration.loadConfiguration(pendingappliesfile);

		List<String> s = new ArrayList<String>();

		for (Entry<String, String> str : Answers.entrySet()) {
			s.add(str.getKey() + " : " + str.getValue());
		}

		answerscfg.set("Answers", s);
		try {
			answerscfg.save(answersfile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> s2 = new ArrayList<String>();

		for (Entry<String, String> str : PendingApply.entrySet()) {
			s2.add(str.getKey() + " : " + str.getValue());
		}

		pendingappliescfg.set("Applies", s2);
		try {
			pendingappliescfg.save(pendingappliesfile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	

	Map<Player, Integer> Application = new HashMap<Player, Integer>();
	Map<Integer, String> Questions = new HashMap<Integer, String>();
	Map<String, String> Answers = new HashMap<String, String>();
	Map<String, String> PendingApply = new HashMap<String, String>();
	Map<Player, Integer> EditingApply = new HashMap<Player, Integer>();
	String prefix = new String(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "SS " + ChatColor.AQUA + "Project"
			+ ChatColor.DARK_GRAY + "]" + ChatColor.AQUA + " ");
	private Inventory ApplyReview = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Your Application");
	private Inventory ApplyCheck = Bukkit.createInventory(null, 27, "");

	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
		if (sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase("Apply")) {
				if (sender.hasPermission("Supersoul.Apply")) {
					if (args.length == 0) {
						Player p = (Player) sender;
						File f = new File(getDataFolder().getPath(), "" + p.getName() + ".yml");
						if (!(f.exists() && !f.isDirectory())) {
							p.sendMessage(prefix + "Please enter your answer in chat.");
							p.sendMessage(prefix + "Please tell us your name.");
							Application.put(p, 1);
							EditingApply.put(p, 0);
						} else {
							p.sendMessage(prefix + "You have already submitted an application!");
						}
					} else {
						Player p = (Player) sender;
						p.sendMessage(prefix + ChatColor.RED + "Too few / many arguments,0 exptected.");
					}
				} else {
					Player p = (Player) sender;
					p.sendMessage(prefix + "No permisson!");
				}
			}
			if (cmd.getName().equalsIgnoreCase("Exitapply")) {
				if (sender.hasPermission("Supersoul.Apply")) {
					if (args.length == 0) {
						Player p = (Player) sender;
						Application.put(p, null);
						p.sendMessage(prefix + "You have exitted your current application.");
					} else {
						Player p = (Player) sender;
						p.sendMessage(prefix + ChatColor.RED + "Too few / many arguments,0 exptected.");
					}
				} else {
					Player p = (Player) sender;
					p.sendMessage(prefix + "No permisson!");
				}
			}
			if (cmd.getName().equalsIgnoreCase("Deleteapply")) {
				if (sender.hasPermission("Supersoul.Admin")) {
					if (args.length == 1) {
						Player p = (Player) sender;
						Application.put(p, null);
						getLogger().info("" + getDataFolder().getPath());
						File file = new File(getDataFolder().getPath() + "/" + p.getName() + ".yml");
						file.delete();
						p.sendMessage(prefix + "You have deleted an application made by " + args[0] + ".");
					} else {
						Player p = (Player) sender;
						p.sendMessage(prefix + ChatColor.RED + "Too few / many arguments,1 exptected.");
					}
				} else {
					Player p = (Player) sender;
					p.sendMessage(prefix + "No permisson!");
				}
			}
			if (cmd.getName().equalsIgnoreCase("Checkapply")) {
				if (sender.hasPermission("Supersoul.Admin")) {
					if (args.length == 1) {
						int i;
						Player p = (Player) sender;
						File file = new File(getDataFolder().getPath() + "/" + p.getName() + ".yml");
						if (file.exists()) {
							for (i = 0; i < 27; i++) {
								ApplyCheck.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 0));
							}
							ItemStack item2 = new ItemStack(Material.SIGN);
							ItemMeta itemmeta2 = item2.getItemMeta();
							itemmeta2.setDisplayName(ChatColor.RED + "Application of " + args[0]);
							item2.setItemMeta(itemmeta2);
							ApplyCheck.setItem(22, item2);
							ItemStack item = new ItemStack(Material.WOOL, 1, (byte) 10);
							ItemMeta im = item.getItemMeta();
							ArrayList<String> Lore = new ArrayList<String>();
							for (i = 1; i < 10; i = i + 1) {
								Lore.add(ChatColor.AQUA + "Question : " + ChatColor.RED + Questions.get(i));
								Lore.add(ChatColor.AQUA + "Your answer : " + ChatColor.RED
										+ Answers.get(args[0] + "_" + i));
								im.setDisplayName(ChatColor.GOLD + "" + i + ".");
								im.setLore(Lore);
								item.setItemMeta(im);
								ApplyCheck.setItem(i - 1, item);
								Lore.clear();
							}
							for (i = 10; i < 15; i = i + 1) {
								Lore.add(ChatColor.AQUA + "Question : " + ChatColor.RED + Questions.get(i));
								Lore.add(ChatColor.AQUA + "Your answer : " + ChatColor.RED
										+ Answers.get(args[0] + "_" + i));
								im.setDisplayName(ChatColor.GOLD + "" + i + ".");
								im.setLore(Lore);
								item.setItemMeta(im);
								ApplyCheck.setItem(i + 1, item);
								Lore.clear();
							}
							//
							ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK, 1);
							ItemMeta confirmim = confirm.getItemMeta();
							ArrayList<String> Lore3 = new ArrayList<String>();
							confirmim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Accept");
							Lore3.add(ChatColor.GREEN + "Click to accept application by " + args[0]);
							confirmim.setLore(Lore3);
							confirm.setItemMeta(confirmim);
							ApplyCheck.setItem(18, confirm);
							//
							ItemStack cancel = new ItemStack(Material.REDSTONE_BLOCK, 1);
							ItemMeta cancelim = cancel.getItemMeta();
							ArrayList<String> Lore4 = new ArrayList<String>();
							cancelim.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Deny");
							Lore4.add(ChatColor.RED + "Click to deny application by " + args[0]);
							cancelim.setLore(Lore4);
							cancel.setItemMeta(cancelim);
							//
							p.openInventory(ApplyCheck);
							ApplyCheck.setItem(26, cancel);
						}
					} else {
						Player p = (Player) sender;
						p.sendMessage(prefix + ChatColor.RED + "Too few / many arguments,1 exptected.");
					}
				} else {
					Player p = (Player) sender;
					p.sendMessage(prefix + "No permisson!");
				}
			}
		}
		return false;
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public void callReviewInventory(Player p) {
		for (int i = 1; i < 15; i = i + 1) {
			p.sendMessage(prefix + i + ". " + Answers.get(p.getPlayer().getName() + "_" + i));
		}
		int i;
		ItemStack item = new ItemStack(Material.WOOL, 1, (byte) 10);
		ItemMeta im = item.getItemMeta();
		ArrayList<String> Lore = new ArrayList<String>();
		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 0);
		for (i = 0; i < 27; i = i + 1) {
			ApplyReview.setItem(i, glass);
		}
		for (i = 1; i < 10; i = i + 1) {
			Lore.add(ChatColor.AQUA + "Question : " + ChatColor.RED + Questions.get(i));
			Lore.add(ChatColor.AQUA + "Your answer : " + ChatColor.RED + Answers.get(p.getName() + "_" + i));
			im.setDisplayName(ChatColor.GOLD + "" + i + ".");
			im.setLore(Lore);
			item.setItemMeta(im);
			ApplyReview.setItem(i - 1, item);
			Lore.clear();
		}
		for (i = 10; i < 15; i = i + 1) {
			Lore.add(ChatColor.AQUA + "Question : " + ChatColor.RED + Questions.get(i));
			Lore.add(ChatColor.AQUA + "Your answer : " + ChatColor.RED + Answers.get(p.getName() + "_" + i));
			im.setDisplayName(ChatColor.GOLD + "" + i + ".");
			im.setLore(Lore);
			item.setItemMeta(im);
			ApplyReview.setItem(i + 1, item);
			Lore.clear();
		}
		ItemStack message = new ItemStack(Material.SIGN, 1);
		ItemMeta messageim = message.getItemMeta();
		messageim.setDisplayName(ChatColor.RED + "Thanks for applying!");
		messageim.setLore(Arrays.asList(ChatColor.RED + "Click on the wool to edit your answer!",
				ChatColor.RED + "Abusing of this command could be punishable!"));
		message.setItemMeta(messageim);
		//
		ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK, 1);
		ItemMeta confirmim = confirm.getItemMeta();
		ArrayList<String> Lore3 = new ArrayList<String>();
		confirmim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm");
		Lore3.add(ChatColor.GREEN + "Click to submit application.");
		confirmim.setLore(Lore3);
		confirm.setItemMeta(confirmim);
		//
		ItemStack cancel = new ItemStack(Material.REDSTONE_BLOCK, 1);
		ItemMeta cancelim = cancel.getItemMeta();
		ArrayList<String> Lore4 = new ArrayList<String>();
		cancelim.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cancel");
		Lore4.add(ChatColor.RED + "Click to cancel application.");
		cancelim.setLore(Lore4);
		cancel.setItemMeta(cancelim);

		ApplyReview.setItem(22, message);
		ApplyReview.setItem(18, confirm);
		ApplyReview.setItem(26, cancel);
		p.openInventory(ApplyReview);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void playerChat(AsyncPlayerChatEvent event) {
		if (Application.containsKey(event.getPlayer())) {
			File f = new File(getDataFolder().getPath(), "" + event.getPlayer().getName() + ".yml");
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
			String name = new String("Application of " + event.getPlayer().getName());
			List<String> m = cfg.getStringList(name);
			File f2 = new File(getDataFolder().getPath(), "WorldData_" + event.getPlayer().getName() + ".yml");
			FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(f2);
			String name2 = new String("World data of " + event.getPlayer().getName());
			List<String> m2 = cfg2.getStringList(name2);
			if (Application.get(event.getPlayer()) == null) {
				event.setCancelled(true);
			}
			if (Application.get(event.getPlayer()).equals(1)) {
				m.add("Name : " + event.getMessage());
				event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
				cfg.set(name, m);
				try {
					cfg.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Application.put(event.getPlayer(), 2);
				event.getPlayer().sendMessage(prefix + "Please tell us your age." + ChatColor.RED + " (Be honest.)");
				event.setCancelled(true);
				Answers.put(event.getPlayer().getName() + "_" + "1", event.getMessage());
				if (EditingApply.get(event.getPlayer()).equals(1)) {
					callReviewInventory(event.getPlayer());
				}
			} else if (Application.get(event.getPlayer()).equals(2)) {
				if (isInt(event.getMessage())) {
					int age = Integer.parseInt(event.getMessage());
					if (age < 101) {
						m.add("Age : " + event.getMessage());
						event.getPlayer()
								.sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
						cfg.set(name, m);
						try {
							cfg.save(f);
						} catch (IOException e) {
							e.printStackTrace();
						}
						Application.put(event.getPlayer(), 3);
						event.getPlayer().sendMessage(prefix + "How long have you been playing Minecraft?"
								+ ChatColor.RED + " (In forms of years.)");
						event.setCancelled(true);
						Answers.put(event.getPlayer().getName() + "_" + "2", event.getMessage());
						if (EditingApply.get(event.getPlayer()).equals(2)) {
							callReviewInventory(event.getPlayer());
						}
					} else {
						event.getPlayer().sendMessage(prefix + "Invalid age!");
						event.setCancelled(true);
					}
				} else {
					event.getPlayer().sendMessage(prefix + "Answer is not an integer!");
					event.setCancelled(true);
				}
			} else if (Application.get(event.getPlayer()).equals(3)) {
				if (isInt(event.getMessage())) {
					m.add("Years of playing Minecraft : " + event.getMessage());
					event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
					cfg.set(name, m);
					try {
						cfg.save(f);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Application.put(event.getPlayer(), 4);
					event.getPlayer().sendMessage(prefix + "How long have you been playing in SuperSoul?"
							+ ChatColor.RED + " (In forms of months.)");
					event.setCancelled(true);
					Answers.put(event.getPlayer().getName() + "_" + "3", event.getMessage());
					if (EditingApply.get(event.getPlayer()).equals(3)) {
						callReviewInventory(event.getPlayer());
					}
				} else {
					event.getPlayer().sendMessage(prefix + "Answer is not an integer!");
					event.setCancelled(true);
				}
			} else if (Application.get(event.getPlayer()).equals(4)) {
				if (isInt(event.getMessage())) {
					m.add("Months of playing in SuperSoul : " + event.getMessage());
					event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
					try {
						cfg.save(f);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Application.put(event.getPlayer(), 5);
					event.getPlayer().sendMessage(prefix + "What do you want to build in your world?");
					event.setCancelled(true);
					Answers.put(event.getPlayer().getName() + "_" + "4", event.getMessage());
					if (EditingApply.get(event.getPlayer()).equals(4)) {
						callReviewInventory(event.getPlayer());
					}
				} else {
					event.getPlayer().sendMessage(prefix + "Answer is not an integer!");
					event.setCancelled(true);
				}
			} else if (Application.get(event.getPlayer()).equals(5)) {
				m.add("Reason of applying : " + event.getMessage());
				event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
				cfg.set(name, m);
				try {
					cfg.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Application.put(event.getPlayer(), 6);
				event.getPlayer().sendMessage(prefix + "Will anyone else build with you? (Optional)");
				event.setCancelled(true);
				Answers.put(event.getPlayer().getName() + "_" + "5", event.getMessage());
				if (EditingApply.get(event.getPlayer()).equals(5)) {
					callReviewInventory(event.getPlayer());
				}
			} else if (Application.get(event.getPlayer()).equals(6)) {
				m.add("Anyone else who will contribute building : " + event.getMessage());
				event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
				cfg.set(name, m);
				try {
					cfg.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Application.put(event.getPlayer(), 7);
				event.getPlayer().sendMessage(prefix + "What is the name of your world?");
				event.setCancelled(true);
				Answers.put(event.getPlayer().getName() + "_" + "6", event.getMessage());
				if (EditingApply.get(event.getPlayer()).equals(6)) {
					callReviewInventory(event.getPlayer());
				}
			} else if (Application.get(event.getPlayer()).equals(7)) {
				m2.add("Name of world : " + event.getMessage());
				event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
				cfg2.set(name2, m2);
				try {
					cfg2.save(f2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Application.put(event.getPlayer(), 8);
				event.getPlayer().sendMessage(prefix + "What is the type of your world?");
				event.setCancelled(true);
				Answers.put(event.getPlayer().getName() + "_" + "7", event.getMessage());
				if (EditingApply.get(event.getPlayer()).equals(7)) {
					callReviewInventory(event.getPlayer());
				}
			} else if (Application.get(event.getPlayer()).equals(8)) {
				m2.add("Type of world : " + event.getMessage());
				event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
				cfg2.set(name2, m2);
				try {
					cfg2.save(f2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Application.put(event.getPlayer(), 9);
				event.getPlayer().sendMessage(
						prefix + "What is the generation type of your world? (Default , Superflat , Large Biomes....)");
				event.setCancelled(true);
				Answers.put(event.getPlayer().getName() + "_" + "8", event.getMessage());
				if (EditingApply.get(event.getPlayer()).equals(8)) {
					callReviewInventory(event.getPlayer());
				}
			} else if (Application.get(event.getPlayer()).equals(9)) {
				m2.add("Generation type of world : " + event.getMessage());
				event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
				cfg2.set(name2, m2);
				try {
					cfg2.save(f2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Application.put(event.getPlayer(), 10);
				event.getPlayer().sendMessage(prefix + "Is PvP enabled in your world? (Yes / No)");
				event.setCancelled(true);
				Answers.put(event.getPlayer().getName() + "_" + "9", event.getMessage());
				if (EditingApply.get(event.getPlayer()).equals(9)) {
					callReviewInventory(event.getPlayer());
				}
			} else if (Application.get(event.getPlayer()).equals(10)) {
				if (event.getMessage().equalsIgnoreCase("Yes") || event.getMessage().equalsIgnoreCase("No")) {
					m2.add("PvP : " + event.getMessage());
					event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
					cfg2.set(name2, m2);
					try {
						cfg2.save(f2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Application.put(event.getPlayer(), 11);
					event.getPlayer().sendMessage(prefix + "Can others enter your world? (Yes / No)");
					event.setCancelled(true);
					Answers.put(event.getPlayer().getName() + "_" + "10", event.getMessage());
					if (EditingApply.get(event.getPlayer()).equals(10)) {
						callReviewInventory(event.getPlayer());
					}
				} else {
					event.getPlayer().sendMessage(prefix + "Invalid option!");
					event.setCancelled(true);
				}
			} else if (Application.get(event.getPlayer()).equals(11)) {
				if (event.getMessage().equalsIgnoreCase("Yes") || event.getMessage().equalsIgnoreCase("No")) {
					m2.add("Open to public : " + event.getMessage());
					event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
					cfg2.set(name2, m2);
					try {
						cfg2.save(f2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Application.put(event.getPlayer(), 12);
					event.getPlayer().sendMessage(prefix + "Is animal spawning enabled? (Yes / No)");
					event.setCancelled(true);
					Answers.put(event.getPlayer().getName() + "_" + "11", event.getMessage());
					if (EditingApply.get(event.getPlayer()).equals(11)) {
						callReviewInventory(event.getPlayer());
					}
				} else {
					event.getPlayer().sendMessage(prefix + "Invalid option!");
					event.setCancelled(true);
				}
			} else if (Application.get(event.getPlayer()).equals(12)) {
				if (event.getMessage().equalsIgnoreCase("Yes") || event.getMessage().equalsIgnoreCase("No")) {
					m2.add("Animal Spawning : " + event.getMessage());
					event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
					cfg2.set(name2, m2);
					try {
						cfg2.save(f2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Application.put(event.getPlayer(), 13);
					event.getPlayer().sendMessage(prefix + "Is monster spawning enabled? (Yes / No)");
					event.setCancelled(true);
					Answers.put(event.getPlayer().getName() + "_" + "12", event.getMessage());
					if (EditingApply.get(event.getPlayer()).equals(12)) {
						callReviewInventory(event.getPlayer());
					}
				} else {
					event.getPlayer().sendMessage(prefix + "Invalid option!");
					event.setCancelled(true);
				}
			} else if (Application.get(event.getPlayer()).equals(13)) {
				if (event.getMessage().equalsIgnoreCase("Yes") || event.getMessage().equalsIgnoreCase("No")) {
					m2.add("Monster Spawning : " + event.getMessage());
					event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
					cfg2.set(name2, m2);
					try {
						cfg2.save(f2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Application.put(event.getPlayer(), 14);
					event.getPlayer().sendMessage(prefix + "Is building spawning enabled? (Yes / No)");
					event.setCancelled(true);
					Answers.put(event.getPlayer().getName() + "_" + "13", event.getMessage());
					if (EditingApply.get(event.getPlayer()).equals(13)) {
						callReviewInventory(event.getPlayer());
					}
				} else {
					event.getPlayer().sendMessage(prefix + "Invalid option!");
					event.setCancelled(true);
				}
			} else if (Application.get(event.getPlayer()).equals(14)) {
				if (event.getMessage().equalsIgnoreCase("Yes") || event.getMessage().equalsIgnoreCase("No")) {
					m2.add("Building Spawning : " + event.getMessage());
					event.getPlayer().sendMessage(prefix + "Your answer : " + ChatColor.RED + "" + event.getMessage());
					cfg2.set(name2, m2);
					try {
						cfg2.save(f2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					event.setCancelled(true);
					Application.put(event.getPlayer(), null);
					Answers.put(event.getPlayer().getName() + "_" + "14", event.getMessage());
					for (int i = 1; i < 15; i = i + 1) {
						event.getPlayer()
								.sendMessage(prefix + i + ". " + Answers.get(event.getPlayer().getName() + "_" + i));
					}
					int i;
					ItemStack item = new ItemStack(Material.WOOL, 1, (byte) 10);
					ItemMeta im = item.getItemMeta();
					ArrayList<String> Lore = new ArrayList<String>();
					ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 0);
					for (i = 0; i < 27; i = i + 1) {
						ApplyReview.setItem(i, glass);
					}
					for (i = 1; i < 10; i = i + 1) {
						Lore.add(ChatColor.AQUA + "Question : " + ChatColor.RED + Questions.get(i));
						Lore.add(ChatColor.AQUA + "Your answer : " + ChatColor.RED
								+ Answers.get(event.getPlayer().getName() + "_" + i));
						im.setDisplayName(ChatColor.GOLD + "" + i + ".");
						im.setLore(Lore);
						item.setItemMeta(im);
						ApplyReview.setItem(i - 1, item);
						Lore.clear();
					}
					for (i = 10; i < 15; i = i + 1) {
						Lore.add(ChatColor.AQUA + "Question : " + ChatColor.RED + Questions.get(i));
						Lore.add(ChatColor.AQUA + "Your answer : " + ChatColor.RED
								+ Answers.get(event.getPlayer().getName() + "_" + i));
						im.setDisplayName(ChatColor.GOLD + "" + i + ".");
						im.setLore(Lore);
						item.setItemMeta(im);
						ApplyReview.setItem(i + 1, item);
						Lore.clear();
					}
					ItemStack message = new ItemStack(Material.SIGN, 1);
					ItemMeta messageim = message.getItemMeta();
					messageim.setDisplayName(ChatColor.RED + "Thanks for applying!");
					messageim.setLore(Arrays.asList(ChatColor.RED + "Click on the wool to edit your answer!",
							ChatColor.RED + "Abusing of this command could be punishable!"));
					message.setItemMeta(messageim);
					//
					ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK, 1);
					ItemMeta confirmim = confirm.getItemMeta();
					ArrayList<String> Lore3 = new ArrayList<String>();
					confirmim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm");
					Lore3.add(ChatColor.GREEN + "Click to submit application.");
					confirmim.setLore(Lore3);
					confirm.setItemMeta(confirmim);
					//
					ItemStack cancel = new ItemStack(Material.REDSTONE_BLOCK, 1);
					ItemMeta cancelim = cancel.getItemMeta();
					ArrayList<String> Lore4 = new ArrayList<String>();
					cancelim.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cancel");
					Lore4.add(ChatColor.RED + "Click to cancel application.");
					cancelim.setLore(Lore4);
					cancel.setItemMeta(cancelim);
					//
					ApplyReview.setItem(22, message);
					ApplyReview.setItem(18, confirm);
					ApplyReview.setItem(26, cancel);
					event.getPlayer().openInventory(ApplyReview);
				} else {
					event.getPlayer().sendMessage(prefix + "Invalid option!");
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().equals(ApplyCheck) || event.getInventory().equals(ApplyReview)) {
			event.setCancelled(true);
			if (event.getCurrentItem().hasItemMeta()) {
				if (event.getCurrentItem().getItemMeta().getDisplayName()
						.equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm")) {
					event.getWhoClicked().sendMessage(prefix + "You have submitted an application!");
					event.getWhoClicked()
							.sendMessage(prefix + "Your application will be handled within few days.Thank you.");
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
					PendingApply.put(event.getWhoClicked().getName(), "True");
				}
				if (event.getCurrentItem().getItemMeta().getDisplayName()
						.equals(ChatColor.RED + "" + ChatColor.BOLD + "Cancel")) {
					event.getWhoClicked().sendMessage(prefix + "You have cancelled your application!");
					Application.put((Player) event.getWhoClicked(), null);
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
					File f = new File(getDataFolder().getPath(), "" + event.getWhoClicked().getName() + ".yml");
					f.delete();
					File f2 = new File(getDataFolder().getPath(), "WorldData_" + event.getWhoClicked().getName() + ".yml");
					f2.delete();
				}
				if (event.getCurrentItem().getItemMeta().getDisplayName()
						.equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Accept")) {
					event.getWhoClicked().sendMessage(prefix + "You have accepted a application!");
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
					List<String> name = event.getCurrentItem().getItemMeta().getLore();
					String name2 = name.get(0);
					String[] name3 = name2.split(" ");
					PendingApply.remove(name3[5]);
				}
				if (event.getCurrentItem().getItemMeta().getDisplayName()
						.equals(ChatColor.RED + "" + ChatColor.BOLD + "Deny")) {
					event.getWhoClicked().sendMessage(prefix + "You have denied a application!");
					Application.put((Player) event.getWhoClicked(), null);
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
					List<String> name = event.getCurrentItem().getItemMeta().getLore();
					String name2 = name.get(0);
					String[] name3 = name2.split(" ");
					PendingApply.remove(name3[5]);
					File f = new File(getDataFolder().getAbsolutePath(), "" + name3[5] + ".yml");
					File newf = new File(getDataFolder().getAbsolutePath() + "/Denied_Application/",
							"" + name3[5] + ".yml");
					try {
						newf.createNewFile();
						Files.copy(f, newf);
						f.delete();
					} catch (IOException e) {
						e.printStackTrace();
					}
					File f2 = new File(getDataFolder().getAbsolutePath(), "WorldData_" + name3[5] + ".yml");
					File newf2 = new File(getDataFolder().getAbsolutePath() + "/Denied_Application/",
							"" + name3[5] + ".yml");
					try {
						newf2.createNewFile();
						Files.copy(f2, newf2);
						f2.delete();
					} catch (IOException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < 15; i++) {
						Answers.remove(name3[5] + "_" + i);
					}
				}
				for (int i = 1; i < 15; i++) {
					if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "" + i + ".")) {
						Application.put((Player) event.getWhoClicked(), i);
						EditingApply.put((Player) event.getWhoClicked(), i);
						event.getWhoClicked().sendMessage(prefix + "You may now edit question " + i + ".");
						event.getWhoClicked().sendMessage(prefix + "" + Questions.get(i));
						event.getWhoClicked().closeInventory();
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("supersoul.admin")) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					int i = 0;
					event.getPlayer().sendMessage(prefix + "There are applies from :");
					for (Entry<String, String> str : PendingApply.entrySet()) {
						if (str.getValue().equals("True")) {
							i = i + 1;
							event.getPlayer().sendMessage(prefix + str.getKey());
						}
					}
					event.getPlayer().sendMessage(prefix + "There are " + i + " pending applies!");
				}
			}, 60L);
		}
	}
}

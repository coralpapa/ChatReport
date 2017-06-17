package me.fatpigsarefat.chatreport;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.fatpigsarefat.chatreport.commands.ChatreportCommand;
import me.fatpigsarefat.chatreport.events.EventInventoryClick;
import me.fatpigsarefat.chatreport.events.EventInventoryClose;
import me.fatpigsarefat.chatreport.events.EventPlayerChat;
import me.fatpigsarefat.chatreport.events.EventPlayerJoin;
import me.fatpigsarefat.chatreport.nms.Chat;
import me.fatpigsarefat.chatreport.nms.Chat_v1_10_R1;
import me.fatpigsarefat.chatreport.nms.Chat_v1_11_R1;
import me.fatpigsarefat.chatreport.nms.Chat_v1_12_R1;
import me.fatpigsarefat.chatreport.nms.Chat_v1_8_R2;
import me.fatpigsarefat.chatreport.nms.Chat_v1_8_R3;
import me.fatpigsarefat.chatreport.nms.Chat_v1_9_R1;
import me.fatpigsarefat.chatreport.nms.Chat_v1_9_R2;
import me.fatpigsarefat.chatreport.utils.ReportManager;

public class ChatReport extends JavaPlugin {

	private static ChatReport instance;
	private static ReportManager reportManager;
	private static Chat chatButton;
	private static boolean chatSupported;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		File d = new File(getDataFolder() + File.separator + "reports.yml");
		if (d.exists()) {
			try {
				d.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Bukkit.getPluginManager().registerEvents(new EventPlayerChat(), this);
		Bukkit.getPluginManager().registerEvents(new EventInventoryClick(), this);
		Bukkit.getPluginManager().registerEvents(new EventPlayerJoin(), this);
		Bukkit.getPluginManager().registerEvents(new EventInventoryClose(), this);
		getServer().getPluginCommand("chatreport").setExecutor(new ChatreportCommand());
		reportManager = new ReportManager();
		reportManager.pull();
		if (setupChat()) {
			chatSupported = true;
		} else {
			chatSupported = false;
		}
		if (getConfig().getBoolean("chathistory.autosave")) {
			new BukkitRunnable() {

				@Override
				public void run() {
					reportManager.push();
				}

			}.runTaskTimer(this, 12000L, 12000L);
		}
	}

	@Override
	public void onDisable() {
		reportManager.push();
	}

	public static boolean isChatSupported() {
		return chatSupported;
	}

	public static Chat getChat() {
		return chatButton;
	}

	public static ReportManager getReportManager() {
		return reportManager;
	}

	public static ChatReport getInstance() {
		return instance;
	}

	private boolean setupChat() {
		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
			return false;
		}

		getLogger().info("Your server is running version " + version);

		boolean success = false;
		boolean underOnePointEight = false;
		if (version.equals("v1_8_R3")) {
			chatButton = new Chat_v1_8_R3();
			success = true;
		} else if (version.equals("v1_8_R2")) {
			chatButton = new Chat_v1_8_R2();
			success = true;
		} else if (version.equals("v1_9_R2")) {
			chatButton = new Chat_v1_9_R2();
			success = true;
		} else if (version.equals("v1_9_R1")) {
			chatButton = new Chat_v1_9_R1();
			success = true;
		} else if (version.equals("v1_10_R1")) {
			chatButton = new Chat_v1_10_R1();
			success = true;
		} else if (version.equals("v1_11_R1")) {
			chatButton = new Chat_v1_11_R1();
			success = true;
		} else if (version.equals("v1_12_R1")) {
			chatButton = new Chat_v1_12_R1();
			success = true;
		} else {
			underOnePointEight = true;
		}

		if (underOnePointEight) {
			return false;
		}
		if (success) {
			return true;
		}
		return false;
	}
}

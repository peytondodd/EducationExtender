package tv.usasvideos.mcplugins.educationextender;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.bukkit.plugin.java.JavaPlugin;

public class EducationExtender extends JavaPlugin implements Listener {

    private List<String> teachers;
    private List<String> students;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        teachers = getConfig().getStringList("teachers");
        students = getConfig().getStringList("students");
        this.getLogger().log(Level.INFO, "is enabled!");
    }

    @Override
    public void onDisable() {
        getConfig().set("teachers", teachers);
        getConfig().set("students", students);
        saveConfig();
        this.getLogger().log(Level.INFO, "is disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setfly") && args.length == 2) {
            if (sender instanceof Player && students.contains(sender.getName())) {
                return true;
            }
            Player flyer = Bukkit.getServer().getPlayer(args[0]);
            if (args[1].equalsIgnoreCase("off")) {
                flyer.setAllowFlight(false);
                this.getLogger().log(Level.INFO, "{0} is''nt it allowed to fly.", flyer.getName());
                flyer.sendMessage(ChatColor.GREEN + "You are not allowed to fly.");
            } else {
                flyer.setAllowFlight(true);
                this.getLogger().log(Level.INFO, "{0} is it allowed to fly.", flyer.getName());
                flyer.sendMessage(ChatColor.GREEN + "Now you are allowed to fly.");
            }
        }
        if (cmd.getName().equalsIgnoreCase("name")) {
            if (sender instanceof Player && students.contains(sender.getName())) {
                return true;
            }
            if (args.length == 1) {
                Player named = Bukkit.getServer().getPlayer(args[0]);
                named.setDisplayName(named.getName());
                named.setPlayerListName(named.getName());
                this.getLogger().log(Level.INFO, "{0}''s name is now {1}.", new Object[]{named.getName(), named.getDisplayName()});
            }
            if (args.length == 2) {
                Player named = Bukkit.getServer().getPlayer(args[0]);
                named.setDisplayName(args[1]);
                named.setPlayerListName(args[1]);
                this.getLogger().log(Level.INFO, "{0} is now his own name.", named.getName());
            }
        }
        if (cmd.getName().equalsIgnoreCase("job") && args.length > 2) {
            if (sender instanceof Player && !(teachers.contains(sender.getName()))) {
                return true;
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Bukkit.broadcastMessage(getMessage(args, 2));
                    this.getLogger().log(Level.INFO, "{0} set a new job for every student: {1}", new Object[]{sender.getName(), getMessage(args, 2)});
                }
            } else {
                Player jobbed = Bukkit.getServer().getPlayer(args[0]);
                Bukkit.broadcastMessage(getMessage(args, 2));
                this.getLogger().log(Level.INFO, "{0} set {1}''s job to {2}", new Object[]{sender.getName(), jobbed.getName(), getMessage(args, 2)});
            }
        }
        if (cmd.getName().equalsIgnoreCase("gamemode") && sender instanceof Player && students.contains(sender.getName())) {
            this.getLogger().log(Level.INFO, "{0} has no permission to change its gamemode.", sender.getName());
            sender.sendMessage(ChatColor.GREEN + "A student is not permitted to change his gamemode!");
            return false;

        }
        if (cmd.getName().equalsIgnoreCase("teachercommands") && sender instanceof Player && teachers.contains(sender.getName())) {
            sender.sendMessage(ChatColor.GREEN + "---------- Teachercommands (1/1) ----------");
            sender.sendMessage(ChatColor.GREEN + "/teachercommands, /setfly <Player> <on|off>, /name <Player> [<Name>]");
            sender.sendMessage(ChatColor.GREEN + "/gamemode <Mode> [<Player>], /job <Player|all> <Msg>, /studentcommands");
            this.getLogger().log(Level.INFO, "The teacher {0} read the teachercommandhelppage.", sender.getName());
        }
        if (cmd.getName().equalsIgnoreCase("studentcommands") && sender instanceof Player) {
            if (students.contains(sender.getName()) || teachers.contains(sender.getName())) {
                sender.sendMessage(ChatColor.GREEN + "---------- Studentcommands (1/1) ----------");
                sender.sendMessage(ChatColor.GREEN + "/studentcommands");
                if (students.contains(sender.getName())) {
                    this.getLogger().log(Level.INFO, "The student {0} read the studentcommandhelppage.", sender.getName());
                }
                if (teachers.contains(sender.getName())) {
                    this.getLogger().log(Level.INFO, "The teacher {0} read the studentcommandhelppage.", sender.getName());
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("addeducation") && args.length == 2) {
            if (sender instanceof Player && !(sender.isOp())) {
                return true;
            }
            Player added = Bukkit.getServer().getPlayer(args[1]);
            boolean tr = teachers.remove(added.getName());
            boolean sr = students.remove(added.getName());
            if (args[0].equalsIgnoreCase("teacher")) {
                teachers.add(added.getName());
                added.sendMessage(ChatColor.GREEN + "Now you are a teacher!");
                this.getLogger().log(Level.INFO, "{0} is now a teacher.", added.getName());
                return true;
            }
            if (args[0].equalsIgnoreCase("student")) {
                students.add(added.getName());
                added.sendMessage(ChatColor.GREEN + "Now you are a student!");
                this.getLogger().log(Level.INFO, "{0} is now a student.", added.getName());
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                added.sendMessage(ChatColor.GREEN + "You are out of the educationprogram!");
                this.getLogger().log(Level.INFO, "{0} is no longer a teacher or a student.", added.getName());
                return true;
            }
            if (tr) {
                teachers.add(added.getName());
                added.sendMessage(ChatColor.GREEN + "You will remain a teacher!");
                this.getLogger().log(Level.INFO, "{0} is still a teacher.", added.getName());
            }
            if (sr) {
                students.add(added.getName());
                added.sendMessage(ChatColor.GREEN + "You will remain a student!");
                this.getLogger().log(Level.INFO, "{0} is still a student.", added.getName());
            }
        }
        if (cmd.getName().equalsIgnoreCase("educheck")) {
            if (sender instanceof Player && students.contains(sender.getName())) {
                return true;
            }
            if (args.length != 1) {
                return true;
            }
            int groupnum = 0;
            String groupnames = null;
            this.getLogger().log(Level.INFO, "{0} tested if {1} is in a group.", new Object[]{sender.getName(), args[0]});
            if (students.contains(args[0])) {
                groupnum += 1;
                groupnames = "a student";
                this.getLogger().log(Level.INFO, "{0} is a student.", args[0]);
            }
            if (teachers.contains(args[0])) {
                if (groupnum > 0) {
                    groupnames = groupnames + " and a teacher";
                groupnum += 1;
                    this.getLogger().log(Level.INFO, "{0} is also a teacher.", args[0]);
                } else {
                    groupnames = "a teacher";
                groupnum += 1;
                    this.getLogger().log(Level.INFO, "{0} is a teacher.", args[0]);
                }
            }
            if (groupnum == 0) {
                sender.sendMessage(ChatColor.GREEN + args[0] + " ist in keiner Gruppe.");
                this.getLogger().log(Level.INFO, "{0} is in no group.", args[0]);
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + args[0] + " ist " + groupnames + ".");
        }
        return true;
    }

    private String getMessage(String[] args, int start) {
        String msg = args[start - 1];
        for (int i = start; i < args.length; i++) {
            msg = msg + " " + args[i];
        }
        return msg;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String welcome = null;
        String info = null;
        if (teachers.contains(e.getPlayer().getName()) || students.contains(e.getPlayer().getName())) {
            if (teachers.contains(e.getPlayer().getName())) {
                e.setJoinMessage(ChatColor.GREEN + "A teacher named " + e.getPlayer().getName() + " is now online!");
                welcome = "You were classified by EducationExtender as a teacher, so you can give execises and run all teachercommands! You find the list of all teachercommand at /teachercommands";
                info = "As a teacher you are in the creativemode and you can fly. Your students are also creative (we hope it so ;D), but they are NOT allowed to fly.";
                e.getPlayer().setGameMode(GameMode.CREATIVE);
                e.getPlayer().setAllowFlight(true);
            }
            if (students.contains(e.getPlayer().getName())) {
                e.setJoinMessage(ChatColor.GREEN + "The student " + e.getPlayer().getName() + " is online!");
                welcome = "You are a student, you can run run commands that exist specifically for your same. See therefore /studentcommands";
                info = "As a student, you are not permitted to fly, but you are in creativemode and can you every singel block.";
                e.getPlayer().setGameMode(GameMode.CREATIVE);
                e.getPlayer().setAllowFlight(false);
            }
            e.getPlayer().sendMessage(ChatColor.GREEN + e.getPlayer().getName() + ", " + welcome + ".");
            e.getPlayer().sendMessage(ChatColor.GREEN + info);
        }
    }

}

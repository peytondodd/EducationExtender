package tv.usasvideos.mcplugins.education;

import java.util.Locale;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import java.util.logging.Level;
import java.util.List;

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
    private Locale locale = new Locale("de", "DE");
    private ResourceBundle messagebundle = ResourceBundle.getBundle("messages", locale);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        teachers = getConfig().getStringList("teachers");
        students = getConfig().getStringList("students");
        this.getLogger().log(Level.INFO, messagebundle.getString("enable.info"));
    }

    @Override
    public void onDisable() {
        getConfig().set("teachers", teachers);
        getConfig().set("students", students);
        saveConfig();
        this.getLogger().log(Level.INFO, messagebundle.getString("disable.info"));
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
                this.getLogger().log(Level.INFO, messagebundle.getString("fly.disallowed.info"), flyer.getName());
                flyer.sendMessage(ChatColor.GREEN + messagebundle.getString("fly.disallowed.message"));
            } else {
                flyer.setAllowFlight(true);
                this.getLogger().log(Level.INFO, messagebundle.getString("fly.allowed.info"), flyer.getName());
                flyer.sendMessage(ChatColor.GREEN + messagebundle.getString("fly.disallowed.message"));
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
                this.getLogger().log(Level.INFO, messagebundle.getString("name.newname.info"), new Object[]{named.getName(), named.getDisplayName()});
            }
            if (args.length == 2) {
                Player named = Bukkit.getServer().getPlayer(args[0]);
                named.setDisplayName(args[1]);
                named.setPlayerListName(args[1]);
                this.getLogger().log(Level.INFO, messagebundle.getString("name.rename.info"), named.getName());
            }
        }
        if (cmd.getName().equalsIgnoreCase("job") && args.length > 2) {
            if (sender instanceof Player && !(teachers.contains(sender.getName()))) {
                return true;
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Bukkit.broadcastMessage(getMessage(args, 2));
                    this.getLogger().log(Level.INFO, messagebundle.getString("job.forall.info"), new Object[]{sender.getName(), getMessage(args, 2)});
                }
            } else {
                Player jobbed = Bukkit.getServer().getPlayer(args[0]);
                Bukkit.broadcastMessage(getMessage(args, 2));
                this.getLogger().log(Level.INFO, messagebundle.getString("job.forone.info"), new Object[]{sender.getName(), jobbed.getName(), getMessage(args, 2)});
            }
        }
        if (cmd.getName().equalsIgnoreCase("gamemode") && sender instanceof Player && students.contains(sender.getName())) {
            this.getLogger().log(Level.INFO, messagebundle.getString("gamemode.nopermission.info"), sender.getName());
            sender.sendMessage(ChatColor.GREEN + messagebundle.getString("gamemode.nopermission.student.message"));
            return false;

        }
        if (cmd.getName().equalsIgnoreCase("teachercommands") && sender instanceof Player && teachers.contains(sender.getName())) {
            sender.sendMessage(ChatColor.GREEN + "---------- Teachercommands (1/1) ----------");
            sender.sendMessage(ChatColor.GREEN + "/teachercommands, /setfly <Player> <on|off>, /name <Player> [<Name>]");
            sender.sendMessage(ChatColor.GREEN + "/gamemode <Mode> [<Player>], /job <Player|all> <Msg>, /studentcommands");
            this.getLogger().log(Level.INFO, messagebundle.getString("commandlist.teacher.teacherrun.info"), sender.getName());
        }
        if (cmd.getName().equalsIgnoreCase("studentcommands") && sender instanceof Player) {
            if (students.contains(sender.getName()) || teachers.contains(sender.getName())) {
                sender.sendMessage(ChatColor.GREEN + "---------- Studentcommands (1/1) ----------");
                sender.sendMessage(ChatColor.GREEN + "/studentcommands");
                if (students.contains(sender.getName())) {
                    this.getLogger().log(Level.INFO, messagebundle.getString("commandlist.student.studentrun.info"), sender.getName());
                }
                if (teachers.contains(sender.getName())) {
                    this.getLogger().log(Level.INFO, messagebundle.getString("commandlist.student.teacherrun.info"), sender.getName());
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
                added.sendMessage(ChatColor.GREEN + messagebundle.getString("educationstate.change.teacher.message"));
                this.getLogger().log(Level.INFO, messagebundle.getString("educationstate.change.teacher.info"), added.getName());
                return true;
            }
            if (args[0].equalsIgnoreCase("student")) {
                students.add(added.getName());
                added.sendMessage(ChatColor.GREEN + messagebundle.getString("educationstate.change.student.message"));
                this.getLogger().log(Level.INFO, messagebundle.getString("educationstate.change.student.info"), added.getName());
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                added.sendMessage(ChatColor.GREEN + messagebundle.getString("educationstate.change.remove.message"));
                this.getLogger().log(Level.INFO, messagebundle.getString("educationstate.change.remove.info"), added.getName());
                return true;
            }
            if (tr) {
                teachers.add(added.getName());
                added.sendMessage(ChatColor.GREEN + messagebundle.getString("educationstate.stay.teacher.message"));
                this.getLogger().log(Level.INFO, messagebundle.getString("educationstate.stay.teacher.info"), added.getName());
            }
            if (sr) {
                students.add(added.getName());
                added.sendMessage(ChatColor.GREEN + messagebundle.getString("educationstate.stay.student.message"));
                this.getLogger().log(Level.INFO, messagebundle.getString("educationstate.stay.student.info"), added.getName());
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
            this.getLogger().log(Level.INFO, messagebundle.getString("check.info"), new Object[]{sender.getName(), args[0]});
            if (students.contains(args[0])) {
                groupnum += 1;
                groupnames = messagebundle.getString("check.student.namestring");
                this.getLogger().log(Level.INFO, messagebundle.getString("check.student.info"), args[0]);
            }
            if (teachers.contains(args[0])) {
                if (groupnum > 0) {
                    groupnames = groupnames + " " + messagebundle.getString("check.teacher.also.namestring");
                groupnum += 1;
                    this.getLogger().log(Level.INFO, messagebundle.getString("check.teacher.also.info"), args[0]);
                } else {
                    groupnames = messagebundle.getString("check.teacher.namestring");
                groupnum += 1;
                    this.getLogger().log(Level.INFO, messagebundle.getString("check.teacher.info"), args[0]);
                }
            }
            if (groupnum == 0) {
                MessageFormat nogroupmessageformat = new MessageFormat(messagebundle.getString("check.nogroup.message"));
                sender.sendMessage(ChatColor.GREEN + nogroupmessageformat.format(args[0]));
                this.getLogger().log(Level.INFO, messagebundle.getString("check.nogroup.info"), args[0]);
                return true;
            }
                MessageFormat ingroupmessageformat = new MessageFormat(messagebundle.getString("check.nogroup.message"));
                sender.sendMessage(ChatColor.GREEN + ingroupmessageformat.format(new Object[]{args[0], groupnames}));
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
        if (teachers.contains(e.getPlayer().getName()) || students.contains(e.getPlayer().getName())) {
            if (teachers.contains(e.getPlayer().getName())) {
                MessageFormat teacheronlinemessageformat = new MessageFormat(messagebundle.getString("login.teacher.login.message"));
                MessageFormat teacherwelcomemessageformat = new MessageFormat(messagebundle.getString("login.teacher.welcome.message"));
                e.setJoinMessage(ChatColor.GREEN + teacheronlinemessageformat.format(e.getPlayer().getName()));
                e.getPlayer().sendMessage(ChatColor.GREEN + teacherwelcomemessageformat.format(e.getPlayer().getName()));
                e.getPlayer().sendMessage(ChatColor.GREEN + messagebundle.getString("login.teacher.info.message"));
                e.getPlayer().setGameMode(GameMode.CREATIVE);
                e.getPlayer().setAllowFlight(true);
            }
            if (students.contains(e.getPlayer().getName())) {
                MessageFormat studentonlinemessageformat = new MessageFormat(messagebundle.getString("login.student.login.message"));
                MessageFormat studentwelcomemessageformat = new MessageFormat(messagebundle.getString("login.student.welcome.message"));
                e.setJoinMessage(ChatColor.GREEN + studentonlinemessageformat.format(e.getPlayer().getName()));
                e.getPlayer().sendMessage(ChatColor.GREEN + studentwelcomemessageformat.format(e.getPlayer().getName()));
                e.getPlayer().sendMessage(ChatColor.GREEN + messagebundle.getString("login.student.info.message"));
                e.getPlayer().setGameMode(GameMode.CREATIVE);
                e.getPlayer().setAllowFlight(false);
            }
        }
    }

}

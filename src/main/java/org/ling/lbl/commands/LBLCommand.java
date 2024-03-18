package org.ling.lbl.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ling.lbl.LBL;
import org.ling.lbl.bh.BlackHole;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LBLCommand extends AbstractCommands {

        private static final String ERROR_REPLY = ChatColor.RED + "Unknown command. Try /help for a list of commands.";
        private static final String RELOAD_PERMISSION = "lbl.reload";
        private static final String CREATE_PERMISSION = "lbl.create";

        public LBLCommand() {
                super("lbl");
        }

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
                if (args.length == 0 || !(sender instanceof Player)) {
                        sender.sendMessage(ERROR_REPLY);
                        return;
                }

                Player player = (Player) sender;

                if (args[0].equalsIgnoreCase("reload")) {
                        if (!player.hasPermission(RELOAD_PERMISSION)) {
                                sender.sendMessage(ERROR_REPLY);
                                return;
                        }

                        LBL.getInstance().reloadConfig();
                        Bukkit.getScheduler().cancelTasks(LBL.getInstance());

                        sender.sendMessage(ChatColor.GREEN + LBL.getInstance().getConfig().getString("messages.reload"));
                } else if (args[0].equalsIgnoreCase("create")) {
                        if (!player.hasPermission(CREATE_PERMISSION)) {
                                sender.sendMessage(ERROR_REPLY);
                                return;
                        }

                        if (args.length < 5) {
                                sender.sendMessage(ChatColor.RED + "Usage: /lbl create <name> <x> <y> <z> <radius>");
                                return;
                        }

                        String name = args[1];
                        double x, y, z, radius;

                        try {
                                x = args[2].equals("~") ? player.getLocation().getBlockX() : Double.parseDouble(args[2]);
                                y = args[3].equals("~") ? player.getLocation().getBlockY() : Double.parseDouble(args[3]);
                                z = args[4].equals("~") ? player.getLocation().getBlockZ() : Double.parseDouble(args[4]);
                                radius = Double.parseDouble(args[5]);
                        } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "Invalid coordinates or radius.");
                                return;
                        }

                        BlackHole blackHole = new BlackHole();
                        try {
                                blackHole.setName(name);
                        } catch (SQLException e) {
                                sender.sendMessage(ChatColor.RED + "A black hole with this name already exists.");
                                return;
                        }

                        blackHole.setX(x);
                        blackHole.setY(y);
                        blackHole.setZ(z);
                        blackHole.setParticle(Particle.BUBBLE_POP);
                        blackHole.setRadius(radius);
                        blackHole.setWorld(player.getWorld());
                        blackHole.build();

                        sender.sendMessage(ChatColor.GREEN + "Black hole created successfully.");
                } else {
                        sender.sendMessage(ERROR_REPLY);
                }
        }

        @Override
        public List<String> complete(CommandSender sender, String[] args) {
                Player player = (Player) sender;
                List<String> completions = new ArrayList<>();

                if (args.length == 1) {
                        completions.addAll(Arrays.asList("reload", "create"));
                } else if (args[0].equals("create")) {
                        Location location = player.getLocation();

                        switch (args.length) {
                                case 2:
                                        completions.add("<name>");
                                        break;
                                case 3:
                                        completions.add(String.valueOf(location.getBlockX()));
                                        break;
                                case 4:
                                        completions.add(String.valueOf(location.getBlockY()));
                                        break;
                                case 5:
                                        completions.add(String.valueOf(location.getBlockZ()));
                                        break;
                                case 6:
                                        completions.add("<radius>");
                                        break;
                        }
                }

                return completions;
        }
}

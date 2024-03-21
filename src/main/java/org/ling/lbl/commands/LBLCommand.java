package org.ling.lbl.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ling.lbl.LBL;
import org.ling.lbl.bh.BlackHoleHandler;
import org.ling.lbl.bh.Opening;
import org.ling.lbl.bh.api.BlackHole;

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
                if (!(sender instanceof Player) || args.length == 0) {
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
                        new BlackHoleHandler().spawnBlackHole();

                        sender.sendMessage(ChatColor.GREEN + LBL.getInstance().getConfig().getString("messages.reload"));
                } else if (args[0].equalsIgnoreCase("create")) {
                        if (!player.hasPermission(CREATE_PERMISSION)) {
                                sender.sendMessage(ERROR_REPLY);
                                return;
                        }

                        if (args.length < 8) {
                                sender.sendMessage(ChatColor.RED + "Usage: /lbl create <name> <x> <y> <z> <radius> <particle> <quality>");
                                return;
                        }

                        try {
                                String name = args[1];
                                double x = args[2].equals("~") ? player.getLocation().getBlockX() : Double.parseDouble(args[2]);
                                double y = args[3].equals("~") ? player.getLocation().getBlockY() : Double.parseDouble(args[3]);
                                double z = args[4].equals("~") ? player.getLocation().getBlockZ() : Double.parseDouble(args[4]);
                                double radius = Double.parseDouble(args[5]);
                                Particle particle = Particle.valueOf(args[6].toUpperCase());
                                int quality = Integer.parseInt(args[7]);

                                if (quality <= 0) {
                                        sender.sendMessage(ChatColor.RED + "Quality must be greater than 0.");
                                        return;
                                }

                                try {
                                        String existingName = LBL.getInstance().getDataBase().getName(name);
                                        if (existingName != null && existingName.equals(name)) {
                                                sender.sendMessage(ChatColor.RED + "A black hole with the name \"" + name + "\" already exists.");
                                                return;
                                        }
                                } catch (SQLException e) {
                                        sender.sendMessage(ChatColor.RED + "An error occurred while checking the database.");
                                        return;
                                }

                                BlackHole blackHole = new BlackHole();
                                blackHole.setName(name);
                                blackHole.setX(x);
                                blackHole.setY(y);
                                blackHole.setZ(z);
                                blackHole.setParticle(particle);
                                blackHole.setRadius(radius);
                                blackHole.setWorld(player.getWorld());
                                blackHole.setQuality(quality);
                                blackHole.build();

                                Opening opening = new Opening();
                                opening.runOpening();

                                sender.sendMessage(ChatColor.GREEN + "Black hole created successfully.");
                        } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "Invalid number format.");
                        } catch (IllegalArgumentException e) {
                                sender.sendMessage(ChatColor.RED + "Invalid particle type.");
                        }
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
                                case 7:
                                        for (Particle particle : Particle.values()) {
                                                completions.add(particle.toString());
                                        }
                                        break;
                                case 8:
                                        completions.add("<quality>");
                                        break;
                        }
                }

                return completions;
        }
}

package ru.luna_koly.mish;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created with love by luna_koly on 02.05.2018.
 */
public class MishCommand extends CommandBase {
    @Override
    public String getName() {
        return "mish";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/mish [--raw] <path>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            boolean isRaw = false;
            String script = null;

            // parsing args
            int it = 0;
            while (it < args.length) {
                if (args[it].equals("--raw")) {
                    isRaw = true;
                    args = ArrayUtils.removeElement(args, it);

                } else if (script == null) {
                    script = args[it];
                    args = ArrayUtils.removeElement(args, it);

                }

                it++;
            }

            // no script specified
            if (script == null) throw new IndexOutOfBoundsException();
            // name to file
            script += "." + MishMod.SCRIPT_EXTENSION;

            File scriptsDir = MishMod.getScriptsDir();
            boolean found = false;

            if (isRaw) {
                for (File file : Objects.requireNonNull(scriptsDir.listFiles())) {
                    if (file.getName().equals(script)) {
                        executeScriptRaw(file);
                        found = true;
                    }
                }
            } else {
                // environment from args
                HashMap<String, String> envir = generateAliases(args);

                for (File file : Objects.requireNonNull(scriptsDir.listFiles())) {
                    if (file.getName().equals(script)) {
                        executeScript(file, envir);
                        found = true;
                    }
                }
            }

            if (!found) throw new CommandException("Script '" + script + "' not found");

        } catch (IndexOutOfBoundsException e) {
            throw new CommandException("Usage: " + getUsage(sender));
        } catch (FileNotFoundException e) {
            throw new CommandException(e.getMessage());
        } catch (IOException e) {
            throw new CommandException("Something strange happened");
        }
    }


    private static void executeScriptRaw(File script) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(script));
        StringBuilder command;
        String line;

        do {
            line = reader.readLine();
            if (line == null || line.length() == 0) continue;

            command = new StringBuilder(line);
            if (command.charAt(0) != '/') command.insert(0, '/');

            System.out.println("PROCESSING RAW: " + command.toString());
            Minecraft.getMinecraft().player.sendChatMessage(command.toString());
        } while (line != null);
    }

    private static HashMap<String, String> generateAliases(String[] args) throws SyntaxErrorException {
        HashMap<String, String> aliases = new HashMap<>();
        parseCommand(String.join(" ", args), aliases);
        return aliases;
    }

    private static String parseStatement(String line, HashMap<String, String> envir) {
        String[] parts = line.split("=");

        if (parts.length >= 2) {
            for (int it = 0; it < parts.length - 1; it++)
                envir.put(parts[it], parts[parts.length - 1]);
            return "";
        }

        return envir.getOrDefault(line, "");
    }

    private static StringBuilder parseCommand(String line, HashMap<String, String> envir) throws SyntaxErrorException {
        ArrayList<StringBuilder> stack = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        int it = 0;
        while (it < line.length()) {
            if (line.charAt(it) == '\\') {
                if (it < line.length() - 1) current.append(line.charAt(it + 1));
                it += 2;
                continue;
            }

            // try read command
            if (line.charAt(it) == '$' && it < line.length() - 1) {
                if (line.charAt(it + 1) == '{') {
                    stack.add(current);
                    current = new StringBuilder();
                    it += 2;
                }
            } else if (line.charAt(it) == '}') {
                if (stack.size() == 0) throw new SyntaxErrorException("Lonely '}' found");

                current = stack.get(stack.size() - 1)
                        .append(parseStatement(current.toString(), envir));
                stack.remove(stack.size() - 1);
            } else {
                current.append(line.charAt(it));
            }

            it++;
        }

        return current;
    }

    private void executeScript(File script, HashMap<String, String> envir) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(script));
        String line = reader.readLine();
        StringBuilder command;

        HashMap<String, String> aliases = envir != null ? envir : new HashMap<>();

        // parse args

        while (line != null) {
            if (line.length() == 0 || line.charAt(0) == '#') {
                line = reader.readLine();
                continue;
            }

            try {
                command = parseCommand(line, aliases);

                if (command.charAt(0) != '/') command.insert(0, '/');
                System.out.println("PROCESSING: " + command.toString());

                Minecraft.getMinecraft().player.sendChatMessage(command.toString());
            } catch (SyntaxErrorException e) {
                Minecraft.getMinecraft().player.sendChatMessage("Syntax Error: " + e.getMessage());
            } finally {
                line = reader.readLine();
            }
        }
    }
}

package ru.luna_koly.mish;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with love by luna_koly on 02.05.2018.
 */
@SuppressWarnings("WeakerAccess")
public class CommandMish extends CommandBase {
    @Override
    @Nonnull
    public String getName() {
        return "mish";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/mish [--raw] <path>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(
            @Nonnull MinecraftServer server,
            @Nonnull ICommandSender sender,
            @Nonnull String[] args) throws CommandException {
        try {
            Environment envir = new Environment();
            String scriptName = parseArgs(args, envir);
            envir.aliases.put("isServer", MishMod.proxy.isPhysicalServer() ? "true" : "false");
            envir.aliases.put("player", sender.getName());

            // no script specified
            if (scriptName == null)
                throw new FileNotFoundException("Usage: " + getUsage(sender));

            scriptName += "." + MishMod.SCRIPT_EXTENSION;

            // try read files
            File scriptsDir = MishMod.getScriptsDir();
            File script;

            // if sender is an operator then try exec 'op_' script first
            if (!envir.noop && sender.canUseCommand(1, "mish")) {
                script = new File(scriptsDir.getAbsolutePath() + File.separator + "op_" + scriptName);

                if (!script.exists())
                    script = new File(scriptsDir.getAbsolutePath() + File.separator + scriptName);
            } else
                script = new File(scriptsDir.getAbsolutePath() + File.separator + scriptName);

            if (script.exists()) {
                if (envir.isRaw)
                    executeScriptRaw(script);
                else
                    executeScript(sender, script, envir);

            } else throw new FileNotFoundException("Script '" + scriptName + "' not found");

        } catch (FileNotFoundException e) {
            throw new CommandException(e.getMessage());
        } catch (IOException e) {
            throw new CommandException("Something strange happened");
        }
    }

    /**
     * Sets up environment according to the given arguments
     * @param args actually rules for envir
     * @param envir the environment
     * @return requested script name
     * @throws SyntaxErrorException if error occurred while reading args
     */
    private static String parseArgs(String[] args, Environment envir) throws SyntaxErrorException {
        String script = null;
        int it = 0;

        while (it < args.length) {
            if (args[it].equals("--raw")) {
                envir.isRaw = true;
                args = ArrayUtils.removeElement(args, it);

            } else if (args[it].equals("--noop")) {
                envir.noop = true;
                args = ArrayUtils.removeElement(args, it);

            } else if (args[it].equals("--max-loop-depth")) {
                if (it + 1 < args.length) {
                    envir.maxLoopDepth = Integer.parseInt(args[it + 1]);
                    args = ArrayUtils.removeElement(args, it + 1);
                }
                args = ArrayUtils.removeElement(args, it);
                it++;

            } else if (script == null) {
                script = args[it];
                args = ArrayUtils.removeElement(args, it);
            }

            it++;
        }

        // setting up default vars
        StringWork.parseDollarBrackets(String.join(" ", args), src -> parseStatement(src, envir));
        return script;
    }

    /**
     * Merely executes each not empty line from file
     * @param script target file
     * @throws IOException if could not read file or mish syntax error occurred
     */
    public static void executeScriptRaw(@Nonnull File script) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(script));
        StringBuilder command;
        String line;

        do {
            line = reader.readLine();
            if (line == null || line.length() == 0) continue;

            command = new StringBuilder(line);
            if (command.charAt(0) != '/') command.insert(0, '/');
//            System.out.println("PROCESSING RAW: " + command.toString());
            MishMod.proxy.executeCommandAsServer(command);

        } while (line != null);
    }

    /**
     * Executes mish elementary syntax part (single command)
     * @param line command
     * @param envir variables and their values
     * @return command result
     */
    @Nonnull
    private static String parseStatement(
            @Nonnull String line,
            @Nonnull Environment envir) {

        String[] parts = line.split("==");
        if (parts.length >= 2)
            return parts[0].equals(parts[1]) ? "true" : "false";

        parts = line.split("!=");
        if (parts.length >= 2)
            return parts[0].equals(parts[1]) ? "false" : "true";

        parts = line.split("<=");
        if (parts.length >= 2)
            return Integer.parseInt(parts[0]) <= Integer.parseInt(parts[1]) ? "true" : "false";

        parts = line.split(">=");
        if (parts.length >= 2)
            return Integer.parseInt(parts[0]) >= Integer.parseInt(parts[1]) ? "true" : "false";

        parts = line.split("<");
        if (parts.length >= 2)
            return Integer.parseInt(parts[0]) < Integer.parseInt(parts[1]) ? "true" : "false";

        parts = line.split(">");
        if (parts.length >= 2)
            return Integer.parseInt(parts[0]) > Integer.parseInt(parts[1]) ? "true" : "false";

        parts = line.split("\\+=");
        if (parts.length >= 2) {
            if (envir.aliases.get(parts[0]) == null)
                envir.aliases.put(parts[0], String.valueOf(Integer.parseInt(parts[1])));
            else {
                int val = Integer.parseInt(envir.aliases.get(parts[0]));
                envir.aliases.put(parts[0], String.valueOf(val + Integer.parseInt(parts[1])));
            }
            return "";
        }

        parts = line.split("=");
        if (parts.length >= 2) {
            for (int it = 0; it < parts.length - 1; it++)
                envir.aliases.put(parts[it], parts[parts.length - 1]);
            return "";
        }

        return envir.aliases.getOrDefault(line, "");
    }

    /**
     * Executes minecraft command with mish syntax
     * parsing enabled
     * @param sender the one who will receive error messages
     * @param script target file
     * @param envir variables and their values
     * @throws IOException if could not read file or mish syntax error occurred
     */
    public void executeScript(
            @Nullable ICommandSender sender,
            @Nonnull File script,
            @Nonnull Environment envir) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(script));
        String line = reader.readLine();

        while (line != null) {
            envir.commands.add(line);
            line = reader.readLine();
        }

        executeBlock(false, 0, 0, sender, envir);
    }

    /**
     * Executes mish code block with 'indent' depth
     * @param skip the code will not be executed if set to true
     * @param lineNumber initial line number (first is 0)
     * @param currentIndent code block indent
     * @param sender the one who will receive error messages
     * @param envir script context
     * @return number of last line executed
     */
    public int executeBlock(
            boolean skip,
            int lineNumber,
            int currentIndent,
            @Nullable ICommandSender sender,
            @Nonnull Environment envir) {

        boolean execCascadeBasedOnCondition = true;
        String prevCommand = null;
        int whileLineNumberCache;
        StringBuilder command;
        int whileDepth = 0;
        String line;

        // parse args

        while (lineNumber < envir.commands.size()) {
            try {
                line = envir.commands.get(lineNumber);
                lineNumber++;

                // just blank line
                if (line.length() == 0) continue;

                int indent = StringWork.getIndent(line);
                // command is everything after the indent
                command = new StringBuilder(line.substring(indent));

                // if comment
                if (command.charAt(0) == '#') continue;

                // check if indent of non-comment command if incorrect
                if (indent % 4 != 0) {
                    MishMod.proxy.sendMessage(sender, "Incorrect indent at line " + lineNumber);
                    continue;
                }

                if (indent < currentIndent) {
                    return lineNumber - 1;
                }

                if (skip) continue;

                if (indent > currentIndent) {
                    MishMod.proxy.sendMessage(sender, "Incorrect indent at line " + lineNumber);
                    continue;
                }


                command = StringWork.parseDollarBrackets(command.toString(), src -> parseStatement(src, envir));
                line = command.toString();

                if (line.isEmpty()) continue;


                if (line.equals("print") || line.startsWith("print ")) {
                    MishMod.proxy.sendMessage(sender, line.substring(6));
                    prevCommand = "print";

                } else if (line.equals("log") || line.startsWith("log ")) {
                    MishMod.proxy.sendMessage(null, line.substring(3));
                    prevCommand = "log";

                } else if (line.startsWith("if ")) {
                    prevCommand = "if";
                    execCascadeBasedOnCondition = statementCondition(line);
                    lineNumber = executeBlock(
                            !execCascadeBasedOnCondition,
                            lineNumber, currentIndent + 4,
                            sender, envir);

                } else if (line.equals("else") || line.startsWith("else ")) {
                    if ("if".equals(prevCommand)) {
                        prevCommand = "else";
                        lineNumber = executeBlock(
                                execCascadeBasedOnCondition,
                                lineNumber, currentIndent + 4,
                                sender, envir);
                    }

                } else if (line.startsWith("while ")) {
                    prevCommand = "while";
                    whileDepth++;

                    if (whileDepth > envir.maxLoopDepth) {
                        MishMod.proxy.sendMessage(sender, "Max loop depth reached at line " + lineNumber);
                        lineNumber = executeBlock(
                                true,
                                lineNumber, currentIndent + 4,
                                sender, envir);
                    } else {
                        whileLineNumberCache = lineNumber - 1;
                        execCascadeBasedOnCondition = statementCondition(line);
                        int endLineNumber = executeBlock(
                                !execCascadeBasedOnCondition,
                                lineNumber, currentIndent + 4,
                                sender, envir);

                        if (!execCascadeBasedOnCondition) {
                            lineNumber = endLineNumber;
                            whileDepth = 0;
                        } else {
                            lineNumber = whileLineNumberCache;
                        }
                    }

                } else {
                    if (command.charAt(0) != '/') command.insert(0, '/');
                    prevCommand = command.toString();
//                System.out.println("PROCESSING: " + command.toString());
                    MishMod.proxy.executeCommandAsServer(command);
                }

            } catch (SyntaxErrorException e) {
                Minecraft.getMinecraft().player.sendChatMessage("Syntax Error: " + e.getMessage());
            }
        }

        return lineNumber;
    }

    /**
     * Returns true if line matches 'anyCommand true'
     * Otherwise false
     * @param line probably the command
     * @return the boolean value based on the second word
     */
    private static boolean statementCondition(@Nonnull String line) {
        String[] parts = line.split(" ");
        if (parts.length < 2) return false;
        return parts[1].equals("true");
    }


    /**
     * Container for mish script parameters
     */
    public class Environment {
        public ArrayList<String> commands = new ArrayList<>();
        public HashMap<String, String> aliases = new HashMap<>();

        public int maxLoopDepth = 100;
        public boolean isRaw = false;
        public boolean noop = false;

//        public Environment() {
//            aliases.put("player", "@p");
//        }
    }
}

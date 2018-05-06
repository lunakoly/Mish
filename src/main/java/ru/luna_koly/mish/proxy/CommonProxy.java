package ru.luna_koly.mish.proxy;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Created with love by luna_koly on 06.05.2018.
 */
public class CommonProxy {
    /**
     * Replaces all entries 'from' to 'to' in a StringBuilder inst.
     * @param builder target
     * @param from old value
     * @param to new value
     */
    @SuppressWarnings("SameParameterValue")
    private static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(from, index);
        }
    }

    /**
     * Executes minecraft command
     * @param command target command
     */
    public void executeCommand(StringBuilder command) {
        replaceAll(command, "  ", " ");
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        server
                .getCommandManager()
                .executeCommand(server, command.toString());
    }
}

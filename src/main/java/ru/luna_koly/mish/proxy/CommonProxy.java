package ru.luna_koly.mish.proxy;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import ru.luna_koly.mish.StringWork;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created with love by luna_koly on 06.05.2018.
 */
public class CommonProxy {
    /**
     * Shows weather it was called within a physical server
     * @return true if is called within a physical server
     */
    public boolean isPhysicalServer() {
        return true;
    }

    /**
     * Executes minecraft command
     * @param sender the ICommandSender. if null MinecraftServer will be used instead
     * @param command target command
     */
    public void executeCommand(@Nullable ICommandSender sender, StringBuilder command) {
        StringWork.replaceAll(command, "  ", " ");
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        server
                .getCommandManager()
                .executeCommand(sender != null ? sender : server, command.toString());
    }

    /**
     * Sends error message to receiver
     * @param receiver target
     * @param message contents
     */
    public void sendMessage(@Nullable ICommandSender receiver, @Nonnull String message) {
        if (receiver == null) receiver = FMLCommonHandler.instance().getMinecraftServerInstance();
        receiver.sendMessage(new TextComponentString(message));
    }
}

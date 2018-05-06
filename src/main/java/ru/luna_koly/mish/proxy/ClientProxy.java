package ru.luna_koly.mish.proxy;

import net.minecraft.client.Minecraft;

/**
 * Created with love by luna_koly on 06.05.2018.
 */
@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    /**
     * Executes minecraft command
     * @param command target command
     */
    @Override
    public void executeCommand(StringBuilder command) {
        Minecraft.getMinecraft().player.sendChatMessage(command.toString());
    }
}

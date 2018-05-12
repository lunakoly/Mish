package ru.luna_koly.mish;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import ru.luna_koly.mish.proxy.CommonProxy;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created with love by luna_koly on 02.05.2018.
 */
@Mod(modid = MishMod.MODID, version = MishMod.VERSION)
public class MishMod {
    static final String MODID = "mish";
    static final String VERSION = "1.0.4";
    static final String SCRIPT_EXTENSION = "mish";

    @SidedProxy(
            serverSide = "ru.luna_koly.mish.proxy.CommonProxy",
            clientSide = "ru.luna_koly.mish.proxy.ClientProxy")
    static CommonProxy proxy;


    /**
     * Generates 'scripts' dir inside '.minecraft' or the server folder
     * and returns path to it
     * @return path to 'scripts' folder
     * @throws FileNotFoundException if could not create folder
     */
    static File getScriptsDir() throws FileNotFoundException {
        // create scrips folder if needed
        File scriptsDir = new File(".", "scripts");

        if (!scriptsDir.isDirectory()) {
            if (scriptsDir.exists()) {
                if (!scriptsDir.delete())
                    throw new FileNotFoundException("Error with 'scripts' folder. " +
                            "Delete the 'scripts' file in the root data folder " +
                            "('.minecraft' or the server installation directory)");
            }

            if (!scriptsDir.mkdir())
                throw new FileNotFoundException("Error with 'scripts' folder. " +
                        "Could not create it");
        }

        return scriptsDir;
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent e) {

    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent e) {

    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent e) {

    }

    @Mod.EventHandler
    public static void serverStarts(FMLServerStartingEvent e) {
        try { getScriptsDir(); } catch (Exception ex) { System.out.println(ex.getMessage()); }
        e.registerServerCommand(new CommandMish());

        ForgeVersion.CheckResult result = ForgeVersion.getResult(Loader.instance().activeModContainer());
        System.out.println("MISH UPDATING RESULTS: " + result.status);
    }
}

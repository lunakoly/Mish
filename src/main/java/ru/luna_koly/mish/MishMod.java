package ru.luna_koly.mish;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created with love by luna_koly on 02.05.2018.
 */
@Mod(modid = MishMod.MODID, version = MishMod.VERSION)
public class MishMod {
    static final String MODID = "mish";
    static final String VERSION = "1.0.1";
    static final String SCRIPT_EXTENSION = "mish";


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
    public static void preInit(FMLInitializationEvent e) {

    }

    @Mod.EventHandler
    public static void preInit(FMLPostInitializationEvent e) {

    }

    @Mod.EventHandler
    public static void serverStarts(FMLServerStartingEvent e) {
        try { getScriptsDir(); } catch (Exception ex) { System.out.println(ex.getMessage()); }
        e.registerServerCommand(new MishCommand());
    }
}

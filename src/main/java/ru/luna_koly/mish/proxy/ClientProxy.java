package ru.luna_koly.mish.proxy;

/**
 * Created with love by luna_koly on 06.05.2018.
 */
@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    /**
     * Shows weather it was called within a physical server
     * @return true if is called within a physical server
     */
    public boolean isPhysicalServer() {
        return false;
    }
}

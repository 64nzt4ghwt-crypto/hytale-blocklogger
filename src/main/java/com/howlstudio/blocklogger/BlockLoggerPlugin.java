package com.howlstudio.blocklogger;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/** BlockLogger — Staff audit tool. Log block place/break events and search by player or time window. */
public final class BlockLoggerPlugin extends JavaPlugin {
    private BlockLog log;
    public BlockLoggerPlugin(JavaPluginInit init){super(init);}
    @Override protected void setup(){
        System.out.println("[BlockLogger] Loading...");
        log=new BlockLog(getDataDirectory());
        CommandManager.get().register(log.getBlockLogCommand());
        System.out.println("[BlockLogger] Ready. "+log.getEntryCount()+" logged actions.");
    }
    @Override protected void shutdown(){if(log!=null)log.save();System.out.println("[BlockLogger] Stopped.");}
}

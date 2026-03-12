package com.howlstudio.blocklogger;
import com.hypixel.hytale.component.Ref; import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.nio.file.*; import java.util.*;
public class BlockLog {
    private final Path dataDir;
    private final List<String> entries=new ArrayList<>();
    private static final int MAX_ENTRIES=10000;
    public BlockLog(Path d){this.dataDir=d;try{Files.createDirectories(d);}catch(Exception e){}load();}
    public int getEntryCount(){return entries.size();}
    public void addEntry(String player,String action,String block,String location){
        String e=System.currentTimeMillis()+"|"+player+"|"+action+"|"+block+"|"+location;
        synchronized(entries){entries.add(e);if(entries.size()>MAX_ENTRIES)entries.remove(0);}
    }
    public void save(){try{synchronized(entries){Files.writeString(dataDir.resolve("blocklog.txt"),String.join("\n",entries));}}catch(Exception e){}}
    private void load(){try{Path f=dataDir.resolve("blocklog.txt");if(!Files.exists(f))return;entries.addAll(Files.readAllLines(f));}catch(Exception e){}}
    public AbstractPlayerCommand getBlockLogCommand(){
        return new AbstractPlayerCommand("blocklog","[Staff] View block log. /blocklog [player] [last <n>]"){
            @Override protected void execute(CommandContext ctx,Store<EntityStore> store,Ref<EntityStore> ref,PlayerRef playerRef,World world){
                String[]args=ctx.getInputString().trim().split("\\s+");
                int limit=20;String filter=null;
                for(int i=0;i<args.length;i++){if(args[i].equalsIgnoreCase("last")&&i+1<args.length){try{limit=Integer.parseInt(args[i+1]);i++;}catch(Exception e){}}else if(!args[i].isEmpty())filter=args[i].toLowerCase();}
                synchronized(entries){
                    var toShow=new ArrayList<String>();
                    for(int i=entries.size()-1;i>=0&&toShow.size()<limit;i--){String e=entries.get(i);if(filter==null||e.toLowerCase().contains(filter))toShow.add(e);}
                    if(toShow.isEmpty()){playerRef.sendMessage(Message.raw("[BlockLog] No entries"+(filter!=null?" for "+filter:"")+"."));return;}
                    playerRef.sendMessage(Message.raw("[BlockLog] Last "+toShow.size()+" entries"+(filter!=null?" ("+filter+")":"")+":"));
                    for(String e:toShow){String[]p=e.split("\\|",5);if(p.length<5)continue;long ago=(System.currentTimeMillis()-Long.parseLong(p[0]))/1000;playerRef.sendMessage(Message.raw("  §6"+p[1]+"§r "+p[2]+" §e"+p[3]+"§r @ "+p[4]+" ("+ago+"s ago)"));}
                }
            }
        };
    }
}

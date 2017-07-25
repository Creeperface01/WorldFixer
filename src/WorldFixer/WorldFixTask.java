package WorldFixer;

import cn.nukkit.Server;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;

public class WorldFixTask extends AsyncTask {

    String world;

    String message = "";

    public WorldFixTask(String levelName){
        world = levelName;
    }

    @Override
    public void onRun(){
        String path = Server.getInstance().getDataPath() + "worlds/" + world + "/";
        Class<? extends LevelProvider > provider = LevelProviderManager.getProvider(path);

        if(provider == null) {
            message = TextFormat.RED + "Invalid level format";
        }


    }
}

package WorldFixer;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkConverter;
import cn.nukkit.level.format.leveldb.LevelDB;
import cn.nukkit.level.format.mcregion.McRegion;
import cn.nukkit.level.format.mcregion.RegionLoader;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CreeperFace on 20.2.2017.
 */
/*public class LevelConverter extends AsyncTask {

    private String name = "";

    public LevelConverter(String name) {
        this.name = name;
    }

    @Override
    public void onRun() {

    }

    @Override
    public void onCompletion(Server server) {

    }


    private boolean convert() throws IOException {
        String path = Server.getInstance().getDataPath()+"worlds/"+this.name+"/";

        CompoundTag levelData = NBTIO.readCompressed(new FileInputStream(new File(path + "level.dat")), ByteOrder.BIG_ENDIAN);
        LevelProvider result = LevelProviderManager.getProvider(path).getConstructor().newInstance();

        try {
            result = toClass.getConstructor(Level.class, String.class).newInstance(level, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (toClass == Anvil.class) {
            if (provider instanceof McRegion) {
                new File(path, "region").mkdir();
                for (File file : new File(provider.getPath() + "region/").listFiles()) {
                    Matcher m = Pattern.compile("-?\\d+").matcher(file.getName());
                    int regionX, regionZ;
                    try {
                        if (m.find()) {
                            regionX = Integer.parseInt(m.group());
                        } else continue;
                        if (m.find()) {
                            regionZ = Integer.parseInt(m.group());
                        } else continue;
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    RegionLoader region = new RegionLoader(provider, regionX, regionZ);
                    for (Integer index : region.getLocationIndexes()) {
                        int chunkX = index & 0x1f;
                        int chunkZ = index >> 5;
                        BaseFullChunk old = region.readChunk(chunkX, chunkZ);
                        if (old == null) continue;
                        int x = (regionX << 5) | chunkX;
                        int z = (regionZ << 5) | chunkZ;
                        FullChunk chunk = new ChunkConverter(result)
                                .from(old)
                                .to(Chunk.class)
                                .perform();
                        result.saveChunk(x, z, chunk);
                    }
                    region.close();
                }
            }
            if (provider instanceof LevelDB) {
                new File(path, "db").mkdir();
                for (byte[] key : ((LevelDB) provider).getTerrainKeys()) {
                    int x = getChunkX(key);
                    int z = getChunkZ(key);
                    BaseFullChunk old = ((LevelDB) provider).readChunk(x, z);
                    FullChunk chunk = new ChunkConverter(result)
                            .from(old)
                            .to(Chunk.class)
                            .perform();
                    result.saveChunk(x, z, chunk);
                }
            }
            result.doGarbageCollection();
        }
        return result;
    }

    private static int getChunkX(byte[] key) {
        return (key[3] << 24) |
                (key[2] << 16) |
                (key[1] << 8) |
                key[0];
    }

    private static int getChunkZ(byte[] key) {
        return (key[7] << 24) |
                (key[6] << 16) |
                (key[5] << 8) |
                key[4];
    }
}*/

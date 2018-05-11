package WorldFixer;

import WorldFixer.util.BlockEntitySpawner;
import WorldFixer.util.TextMessage;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.format.anvil.RegionLoader;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.MainLogger;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CreeperFace
 */
public class LevelConverter {

    public static void convert(WorldFixer plugin, Level level, boolean fast) {
        LevelProvider provider = level.getProvider();

        Pattern pattern = Pattern.compile("-?\\d+");
        File[] regions = new File(level.getServer().getDataPath() + "worlds/" + level.getFolderName() + "/region").listFiles(
                (f) -> f.isFile() && f.getName().endsWith(".mca")
        );

        if (regions != null && regions.length > 0) {
            double processed = 0;
            int blocks = 0;
            int blockEntities = 0;
            long time = System.currentTimeMillis();
            plugin.getLogger().info("Starting fixing world '" + level.getName() + "'");

            for (File region : regions) {
                Matcher m = pattern.matcher(region.getName());
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

                long start = System.currentTimeMillis();

                try {
                    RegionLoader loader = new RegionLoader(provider, regionX, regionZ);
                    for (Integer index : loader.getLocationIndexes()) {
                        int chunkX = index & 0x1f;
                        int chunkZ = index >> 5;
                        Chunk chunk = loader.readChunk(chunkX, chunkZ);
                        if (chunk == null) continue;
                        chunk.initChunk();

                        boolean chunkChanged = false;

                        for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 256; y++) {
                                for (int z = 0; z < 16; z++) {
                                    int id = chunk.getBlockId(x, y, z);
                                    boolean changed = true;

                                    switch (id) {
                                        case 3:
                                            if (chunk.getBlockData(x, y, z) == 2) {
                                                chunk.setBlockId(x, y, z, Item.PODZOL);
                                            }
                                            break;
                                        case 125:
                                            chunk.setBlockId(x, y, z, Item.DOUBLE_WOODEN_SLAB);
                                            break;
                                        case 126:
                                            chunk.setBlockId(x, y, z, Item.WOOD_SLAB);
                                            break;
                                        case 95:
                                            chunk.setBlockId(x, y, z, 241);
                                            break;
                                        case 160:
                                            chunk.setBlockId(x, y, z, Item.GLASS_PANE);
                                            chunk.setBlockData(x, y, z, 0);
                                            break;
                                        case 166:
                                            chunk.setBlockId(x, y, z, Item.INVISIBLE_BEDROCK);
                                            break;
                                        case 177:
                                            chunk.setBlockId(x, y, z, Item.AIR);
                                            break;
                                        case 188:
                                            chunk.setBlockId(x, y, z, Item.FENCE);
                                            chunk.setBlockData(x, y, z, 1);
                                            break;
                                        case 189:
                                            chunk.setBlockId(x, y, z, Item.FENCE);
                                            chunk.setBlockData(x, y, z, 2);
                                            break;
                                        case 190:
                                            chunk.setBlockId(x, y, z, Item.FENCE);
                                            chunk.setBlockData(x, y, z, 3);
                                            break;
                                        case 191:
                                            chunk.setBlockId(x, y, z, Item.FENCE);
                                            chunk.setBlockData(x, y, z, 4);
                                            break;
                                        case 192:
                                            chunk.setBlockId(x, y, z, Item.FENCE);
                                            chunk.setBlockData(x, y, z, 5);
                                            break;
                                        case 198:
                                            chunk.setBlockId(x, y, z, Item.END_ROD);
                                            break;
                                        case 199:
                                            chunk.setBlockId(x, y, z, Item.CHORUS_PLANT);
                                            break;
                                        case 202:
                                        case 204:
                                            chunk.setBlockId(x, y, z, Item.PURPUR_BLOCK);
                                            break;
                                        case 203:
                                            chunk.setBlockId(x, y, z, 202);
                                            break;
                                        case 208:
                                            chunk.setBlockId(x, y, z, Item.GRASS_PATH);
                                            break;
                                        case 210:
                                            chunk.setBlockId(x, y, z, 188);
                                            break;
                                        case 211:
                                            chunk.setBlockId(x, y, z, 189);
                                            break;
                                        case 158:
                                            chunk.setBlockId(x, y, z, 125);
                                            break;
                                        case 157:
                                            chunk.setBlockId(x, y, z, 126);
                                            break;
                                        case 235:
                                            chunk.setBlockId(x, y, z, 220);
                                            break;
                                        case 236:
                                            chunk.setBlockId(x, y, z, 221);
                                            break;
                                        case 237:
                                            chunk.setBlockId(x, y, z, 222);
                                            break;
                                        case 238:
                                            chunk.setBlockId(x, y, z, 223);
                                            break;
                                        case 239:
                                            chunk.setBlockId(x, y, z, 224);
                                            break;
                                        case 240:
                                            chunk.setBlockId(x, y, z, 225);
                                            break;
                                        case 241:
                                            chunk.setBlockId(x, y, z, 226);
                                            break;
                                        case 242:
                                            chunk.setBlockId(x, y, z, 227);
                                            break;
                                        case 243:
                                            chunk.setBlockId(x, y, z, 228);
                                            break;
                                        case 244:
                                            chunk.setBlockId(x, y, z, 229);
                                            break;
                                        case 245:
                                            chunk.setBlockId(x, y, z, 230);
                                            break;
                                        case 246:
                                            chunk.setBlockId(x, y, z, 231);
                                            break;
                                        case 247:
                                            chunk.setBlockId(x, y, z, 232);
                                            break;
                                        case 248:
                                            chunk.setBlockId(x, y, z, 233);
                                            break;
                                        case 249:
                                            chunk.setBlockId(x, y, z, 234);
                                            break;
                                        case 250:
                                            chunk.setBlockId(x, y, z, 235);
                                            break;
                                        case 251:
                                            chunk.setBlockId(x, y, z, 236);
                                            break;
                                        case 252:
                                            chunk.setBlockId(x, y, z, 237);
                                            break;
                                        case 218:
                                            chunk.setBlockId(x, y, z, 251);
                                            break;
                                        case 207:
                                            chunk.setBlockId(x, y, z, 244);
                                            break;
                                        case Item.STONE_BUTTON:
                                        case Item.WOODEN_BUTTON:
                                            int data = chunk.getBlockData(x, y, z);
                                            int face = data & 0b111;

                                            int meta = 0;
                                            switch (face) {
                                                case 0: //down
                                                    meta = BlockFace.DOWN.getIndex();
                                                    break;
                                                case 1: //east
                                                    meta = BlockFace.EAST.getIndex();
                                                    break;
                                                case 2: //west
                                                    meta = BlockFace.WEST.getIndex();
                                                    break;
                                                case 3: //south
                                                    meta = BlockFace.SOUTH.getIndex();
                                                    break;
                                                case 4: //north
                                                    meta = BlockFace.NORTH.getIndex();
                                                    break;
                                                case 5: //up
                                                    meta = BlockFace.UP.getIndex();
                                                    break;
                                            }

                                            if ((data & 0x08) == 0x08) {
                                                meta |= 0x08;
                                            }

                                            chunk.setBlockData(x, y, z, meta);
                                            break;
                                        default:
                                            changed = false;
                                            break;
                                    }

                                    if (changed) {
                                        chunkChanged = true;
                                        blocks++;
                                    }

                                    if (BlockEntitySpawner.checkBlockEntity(id, chunk, new Vector3(x, y, z))) {
                                        chunkChanged = true;
                                        blockEntities++;
                                    }
                                }
                            }
                        }

                        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                            if (blockEntity instanceof BlockEntitySign) {
                                BlockEntitySign sign = (BlockEntitySign) blockEntity;

                                String[] lines = sign.getText();
                                for (int i = 0; i < lines.length; i++) {
                                    lines[i] = TextMessage.decode(lines[i]).asPlaintext();
                                }
                                sign.setText(lines);

                                chunkChanged = true;
                            }
                        }

                        if (chunkChanged)
                            loader.writeChunk(chunk);
                    }

                    processed++;
                    loader.close();
                } catch (Exception e) {
                    MainLogger.getLogger().logException(e);
                }

                plugin.getLogger().info("Fixing... " + NukkitMath.round((processed / regions.length) * 100, 2) + "% done");

                if (!fast) {
                    long sleep = NukkitMath.floorDouble((System.currentTimeMillis() - start) * 0.25);

                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        plugin.getLogger().info("Main thread was interrupted and world fixing process could not be completed.");
                        return;
                    }
                }
            }

            plugin.getLogger().info("World " + level.getName() + " successfully fixed in " + (System.currentTimeMillis() - time) / 1000 + "s. (Fixed " + blocks + " blocks and " + blockEntities
                    + " blockentities)");
        }
    }
}

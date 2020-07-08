package com.creeperface.nukkitx.worldfixer;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.RegionLoader;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.MainLogger;
import com.creeperface.nukkitx.worldfixer.util.BlockEntitySpawner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

            List<Vector3> chests = new ArrayList<>();

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

                    for (int chunkX = 0; chunkX < 32; chunkX++) {
                        for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
                            BaseFullChunk chunk = loader.readChunk(chunkX, chunkZ);

                            if (chunk == null) continue;
                            chunk.initChunk();

                            boolean chunkChanged = false;

                            for (int x = 0; x < 16; x++) {
                                for (int y = 0; y < 256; y++) {
                                    for (int z = 0; z < 16; z++) {
                                        int id = chunk.getBlockId(x, y, z);
                                        boolean changed = WorldFixer.fixId(chunk, (chunk.getX() << 4) | (x & 0xf), y, (chunk.getZ() << 4) | (z & 0xf), id);

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

                        /*for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                            if (blockEntity instanceof BlockEntitySign) {
                                BlockEntitySign sign = (BlockEntitySign) blockEntity;

                                String[] lines = sign.getText();
                                for (int i = 0; i < lines.length; i++) {
                                    lines[i] = TextMessage.decode(lines[i]).asPlaintext();
                                }
                                sign.setText(lines);

                                chunkChanged = true;
                            }
                        }*/

                            if (chunkChanged) {
                                loader.writeChunk(chunk);
                            }
                        }
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

            /*for(Vector3 pos : chests) {
                if(level.getBlockEntity(pos) != null)
                    continue;

                CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntity.CHEST);

                BlockEntityChest chest = new BlockEntityChest(level.getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4), nbt);
                BlockEntityChest be = null;

                Block b = level.getBlock(pos);

                for (int side = 2; side <= 5; ++side) {
                    if ((b.getDamage() != 4 && b.getDamage() != 5 || side != 4 && side != 5) && (b.getDamage() != 3 && b.getDamage() != 2 || side != 2 && side != 3)) {
                        Block c = b.getSide(BlockFace.fromIndex(side));

                        if (c instanceof BlockChest && c.getDamage() == b.getDamage()) {
                            BlockEntity blockEntity = level.getBlockEntity(c);

                            if (blockEntity instanceof BlockEntityChest && !((BlockEntityChest) blockEntity).isPaired()) {
                                be = (BlockEntityChest) blockEntity;
                                break;
                            }
                        }
                    }
                }

                if (be != null) {
                    be.pairWith(chest);
                    chest.pairWith(be);
                }
            }*/

            plugin.getLogger().info("World " + level.getName() + " successfully fixed in " + (System.currentTimeMillis() - time) / 1000 + "s. (Fixed " + blocks + " blocks and " + blockEntities
                    + " blockentities)");
        }
    }
}

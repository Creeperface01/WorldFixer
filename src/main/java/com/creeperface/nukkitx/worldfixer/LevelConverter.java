package com.creeperface.nukkitx.worldfixer;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.RegionLoader;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.BlockFace;
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
                    Integer[] table = loader.getLocationIndexes();

                    for (Integer index : table) {
                        int chunkX = index & 0x1f;
                        int chunkZ = index >> 5;
                        BaseFullChunk chunk = loader.readChunk(chunkX, chunkZ);

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
                                        case 157:
                                            chunk.setBlockId(x, y, z, 126);
                                            break;
                                        case 158:
                                            chunk.setBlockId(x, y, z, 125);
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
                                        case 202: //pillar
                                        case 204: //double slab
                                        case 205: //slab
                                            chunk.setBlockId(x, y, z, Item.PURPUR_BLOCK);
                                            break;
                                        case 207:
                                            chunk.setBlockId(x, y, z, Item.BEETROOT_BLOCK);
                                            break;
                                        case 208:
                                            chunk.setBlockId(x, y, z, Item.GRASS_PATH);
                                            break;
                                        case 210: //repeating command block
                                            chunk.setBlockId(x, y, z, 188);
                                            break;
                                        case 211: //chain command block
                                            chunk.setBlockId(x, y, z, 189);
                                            break;
                                        case 218:
                                            chunk.setBlockId(x, y, z, Item.OBSERVER);
                                            break;
                                        case 235:
                                            chunk.setBlockId(x, y, z, Item.WHITE_GLAZED_TERRACOTTA);
                                            break;
                                        case 236:
                                            chunk.setBlockId(x, y, z, Item.ORANGE_GLAZED_TERRACOTTA);
                                            break;
                                        case 237:
                                            chunk.setBlockId(x, y, z, Item.MAGENTA_GLAZED_TERRACOTTA);
                                            break;
                                        case 238:
                                            chunk.setBlockId(x, y, z, Item.LIGHT_BLUE_GLAZED_TERRACOTTA);
                                            break;
                                        case 239:
                                            chunk.setBlockId(x, y, z, Item.YELLOW_GLAZED_TERRACOTTA);
                                            break;
                                        case 240:
                                            chunk.setBlockId(x, y, z, Item.LIME_GLAZED_TERRACOTTA);
                                            break;
                                        case 241:
                                            chunk.setBlockId(x, y, z, Item.PINK_GLAZED_TERRACOTTA);
                                            break;
                                        case 242:
                                            chunk.setBlockId(x, y, z, Item.GRAY_GLAZED_TERRACOTTA);
                                            break;
                                        case 243:
                                            chunk.setBlockId(x, y, z, Item.SILVER_GLAZED_TERRACOTTA);
                                            break;
                                        case 244:
                                            chunk.setBlockId(x, y, z, Item.CYAN_GLAZED_TERRACOTTA);
                                            break;
                                        case 245:
                                            chunk.setBlockId(x, y, z, Item.PURPLE_GLAZED_TERRACOTTA);
                                            break;
                                        case 246:
                                            chunk.setBlockId(x, y, z, Item.BLUE_GLAZED_TERRACOTTA);
                                            break;
                                        case 247:
                                            chunk.setBlockId(x, y, z, Item.BROWN_GLAZED_TERRACOTTA);
                                            break;
                                        case 248:
                                            chunk.setBlockId(x, y, z, Item.GREEN_GLAZED_TERRACOTTA);
                                            break;
                                        case 249:
                                            chunk.setBlockId(x, y, z, Item.RED_GLAZED_TERRACOTTA);
                                            break;
                                        case 250:
                                            chunk.setBlockId(x, y, z, Item.BLACK_GLAZED_TERRACOTTA);
                                            break;
                                        case 251:
                                            chunk.setBlockId(x, y, z, Item.CONCRETE);
                                            break;
                                        case 252:
                                            chunk.setBlockId(x, y, z, Item.CONCRETE_POWDER);
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
                                        case Block.CHEST:
                                            chests.add(new Vector3(chunk.getX() << 4 + x, y, chunk.getZ() << 4 + z));
                                            break;
                                        default:
                                            changed = false;
                                            break;
                                    }

                                    if (id >= 219 && id <= 234) { //shulker box
                                        chunk.setBlockId(x, y, z, Item.SHULKER_BOX);
                                        chunk.setBlockData(x, y, z, id - 219);
                                        changed = true;
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

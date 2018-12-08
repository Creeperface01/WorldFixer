package WorldFixer.util;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.MainLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author CreeperFace
 */
public class BlockEntitySpawner {

    private static Map<Integer, String> blocks = new HashMap<>();
    private static Map<Integer, BiConsumer<FullChunk, Vector3>> blockEntities = new HashMap<>();

    static {
        blocks.put(Block.CHEST, BlockEntity.CHEST);
        blocks.put(Block.FURNACE, BlockEntity.FURNACE);
        blocks.put(Block.BREWING_BLOCK, BlockEntity.BREWING_STAND);
        blocks.put(Block.FLOWER_POT_BLOCK, BlockEntity.FLOWER_POT);
        blocks.put(Block.BED_BLOCK, BlockEntity.BED);
        blocks.put(Block.ENDER_CHEST, BlockEntity.ENDER_CHEST);
        blocks.put(Block.SIGN_POST, BlockEntity.SIGN);
        blocks.put(Block.WALL_SIGN, BlockEntity.SIGN);
        blocks.put(Block.ENCHANT_TABLE, BlockEntity.ENCHANT_TABLE);
        blocks.put(Block.ITEM_FRAME_BLOCK, BlockEntity.ITEM_FRAME);
        blocks.put(Block.BEACON, BlockEntity.BEACON);
        blocks.put(Block.HOPPER_BLOCK, BlockEntity.HOPPER);

        blockEntities.put(Block.CHEST, (chunk, pos) -> {
            Vector3 realPos = pos.add(chunk.getX() << 4, 0, chunk.getZ() << 4);
            CompoundTag nbt = BlockEntity.getDefaultCompound(realPos, BlockEntity.CHEST);

            BlockEntityChest chest = new BlockEntityChest(chunk, nbt);
            Block b = getBlock(chunk, realPos);

            if (b instanceof BlockChest) {
                BlockEntityChest be = null;
                BlockFace chestFace = getChestFace(b.getDamage());

                if (chestFace == null) {
                    return;
                }

                for (BlockFace face : new BlockFace[]{chestFace.rotateYCCW(), chestFace.getOpposite().rotateYCCW()}) {
                    Block other = getBlock(chunk, realPos, face);

                    if (other instanceof BlockChest && other.getDamage() == b.getDamage()) {
                        BlockEntity blockEntity = getBlockEntity(chunk, other);

                        if (blockEntity instanceof BlockEntityChest && !((BlockEntityChest) blockEntity).isPaired()) {
                            be = (BlockEntityChest) blockEntity;
                            break;
                        }
                    }
                }

                if (be != null) {
                    be.pairWith(chest);
                    chest.pairWith(be);
                }
            }
        });
    }

    public static boolean checkBlockEntity(int blockId, FullChunk chunk, Vector3 pos) {
        try {
            /*BiConsumer<FullChunk, Vector3> cons = blockEntities.get(blockId);
            if (cons != null) {
                cons.accept(chunk, pos);
                return true;
            }*/

            String bid = blocks.get(blockId);

            if (bid != null && chunk.getTile(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()) == null) {
                Vector3 real = pos.add(chunk.getX() * 16, 0, chunk.getZ() * 16);

                BlockEntity.createBlockEntity(bid, chunk, BlockEntity.getDefaultCompound(real, bid));

                return true;
            }
        } catch (Exception e) {
            MainLogger.getLogger().logException(e);
        }

        return false;
    }

    private static Block getBlock(FullChunk chunk, Vector3 pos, BlockFace offset) {
        return getBlock(chunk, pos.add(offset.getXOffset(), offset.getYOffset(), offset.getZOffset()));
    }

    private static Block getBlock(FullChunk chunk, Vector3 pos) {
        BlockVector3 floor = pos.asBlockVector3();

        int chunkX = floor.x >> 4;
        int chunkZ = floor.z >> 4;

        Block block;
        if (chunkX == chunk.getX() && chunkZ == chunk.getZ()) {
            BlockVector3 chunkPos = pos.asBlockVector3();
            chunkPos.x = chunkPos.x % 16;
            chunkPos.z = chunkPos.z % 16;

            block = Block.get(chunk.getBlockId(chunkPos.x, chunkPos.y, chunkPos.z), chunk.getBlockData(chunkPos.x, chunkPos.y, chunkPos.z));
        } else {
            block = chunk.getProvider().getLevel().getBlock(pos);
        }

        block.position(Position.fromObject(pos));
        return block;
    }

    private static BlockEntity getBlockEntity(FullChunk chunk, Vector3 pos) {
        BlockVector3 floor = pos.asBlockVector3();

        int chunkX = floor.x >> 4;
        int chunkZ = floor.z >> 4;

        if (chunkX == chunk.getX() && chunkZ == chunk.getZ()) {
            BlockVector3 chunkPos = floor.clone();
            chunkPos.x = chunkPos.x % 16;
            chunkPos.z = chunkPos.z % 16;

            return chunk.getTile(chunkPos.x, chunkPos.y, chunkPos.z);
        } else {
            return chunk.getProvider().getLevel().getBlockEntity(pos);
        }
    }

    private static BlockFace getChestFace(int meta) {
        switch (meta) {
            case 2:
                return BlockFace.NORTH;
            case 3:
                return BlockFace.SOUTH;
            case 4:
                return BlockFace.WEST;
            case 5:
                return BlockFace.EAST;
        }

        return null;
    }
}

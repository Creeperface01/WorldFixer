package WorldFixer.util;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author CreeperFace
 */
public class BlockEntitySpawner {

    private static Map<Integer, String> blocks = new HashMap<>();
    private static Map<Integer, BiConsumer<Chunk, Vector3>> blockEntities = new HashMap<>();

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
            CompoundTag nbt = BlockEntity.getDefaultCompound(pos.add(chunk.getX() << 4, 0, chunk.getZ() << 4), BlockEntity.CHEST);

            BlockEntityChest chest = new BlockEntityChest(chunk, nbt);
            Block b = chest.getLevelBlock();

            if (b instanceof BlockChest) {
                BlockEntityChest be = null;

                for (int side = 2; side <= 5; ++side) {
                    if ((b.getDamage() != 4 && b.getDamage() != 5 || side != 4 && side != 5) && (b.getDamage() != 3 && b.getDamage() != 2 || side != 2 && side != 3)) {
                        Block c = b.getSide(BlockFace.fromIndex(side));
                        if (c instanceof BlockChest && c.getDamage() == b.getDamage()) {
                            BlockEntity blockEntity = b.getLevel().getBlockEntity(c);
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
            }
        });
    }

    public static boolean checkBlockEntity(int blockId, Chunk chunk, Vector3 pos) {
        BiConsumer<Chunk, Vector3> cons = blockEntities.get(blockId);
        if (cons != null) {
            cons.accept(chunk, pos);
            return true;
        }

        String bid = blocks.get(blockId);

        if (bid != null && chunk.getTile(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()) == null) {
            BlockEntity.createBlockEntity(bid, chunk, BlockEntity.getDefaultCompound(pos.add(chunk.getX() << 4, 0, chunk.getZ() << 4), bid));
            return true;
        }

        return false;
    }
}

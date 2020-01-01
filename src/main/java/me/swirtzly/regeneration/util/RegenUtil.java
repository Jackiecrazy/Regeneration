package me.swirtzly.regeneration.util;

import me.swirtzly.regeneration.RegenConfig;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.handlers.RegenObjects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

public class RegenUtil {

    private static Random rand = new Random();

    public static boolean isSlimSkin(UUID playerUUID) {
        return (playerUUID.hashCode() & 1) == 1;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = rand.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static float randFloat(float min, float max) {
        return rand.nextFloat() * (max - min) + min;
    }

    public static void genCrater(World world, BlockPos pos, int radius) {
        for (int x = pos.getX() - radius; x < pos.getX() + radius; ++x) {
            for (int y = pos.getY() - radius; y < pos.getY() + radius; ++y) {
                for (int z = pos.getZ() - radius; z < pos.getZ() + radius; ++z) {
                    double squareDistance = Math.pow(x - pos.getX(), 2) + Math.pow(y - pos.getY(), 2) + Math.pow(z - pos.getZ(), 2);
                    if (squareDistance <= Math.pow(radius, 2)) {
                        BlockState block = world.getBlockState(new BlockPos(x, y, z));

                        if (block.getBlock() != Blocks.BEDROCK && block.getBlockHardness(world, new BlockPos(x, y, z)) < 3.0F) {

                            if (!world.isRemote) {

                                if (world.getTileEntity(new BlockPos(x, y, z)) != null) {
                                    TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
                                    if (tileEntity instanceof IInventory) {
                                        InventoryHelper.dropInventoryItems(world, pos, (IInventory) tileEntity);
                                        world.updateComparatorOutputLevel(pos, block.getBlock());
                                    }
                                }

                                InventoryHelper.spawnItemStack(world, x, y, z, new ItemStack(block.getBlock()));
                            }
                            world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    public static void regenerationExplosion(PlayerEntity player) {
        explodeKnockback(player, player.world, player.getPosition(), RegenConfig.onRegen.regenerativeKnockback, RegenConfig.onRegen.regenerativeKnockbackRange);
        explodeKill(player, player.world, player.getPosition(), RegenConfig.onRegen.regenerativeKillRange);
    }

    public static void explodeKnockback(Entity exploder, World world, BlockPos pos, float knockback, int range) {
        world.getEntitiesWithinAABBExcludingEntity(exploder, getReach(pos, range)).forEach(entity -> {
            if (entity instanceof LivingEntity && !exploder.isDead) {
                LivingEntity victim = (LivingEntity) entity;

                if (entity instanceof PlayerEntity && !RegenConfig.onRegen.regenerationKnocksbackPlayers || !victim.isNonBoss())
                    return;

                float densMod = world.getBlockDensity(new Vec3d(pos), entity.getEntityBoundingBox());

                int xr, zr;
                xr = (int) -(victim.posX - exploder.posX);
                zr = (int) -(victim.posZ - exploder.posZ);

                victim.knockBack(exploder, knockback * densMod, xr, zr);
            }
        });
    }

    public static void explodeKill(Entity exploder, World world, BlockPos pos, int range) {
        world.getEntitiesWithinAABBExcludingEntity(exploder, getReach(pos, range)).forEach(entity -> {
            if ((entity instanceof CreatureEntity && entity.isNonBoss()) || (entity instanceof PlayerEntity && RegenConfig.onRegen.regenerationKillsPlayers))
                entity.attackEntityFrom(RegenObjects.REGEN_DMG_ENERGY_EXPLOSION, 4);
        });
    }

    public static AxisAlignedBB getReach(BlockPos pos, int range) {
        return new AxisAlignedBB(pos.up(range).north(range).west(range), pos.down(range).south(range).east(range));
    }

    public static void resetNextSkin(PlayerEntity player) {
        IRegeneration data = RegenCap.get(player);
        data.setNextSkin("NONE");
        data.synchronise();
    }

    public static SoundEvent getRandomSound(SoundEvent[] soundEvents, Random random) {
        return soundEvents[random.nextInt(soundEvents.length)];
    }

    public static boolean isSharp(ItemStack stack) {
        return stack.getItem() instanceof ToolItem || stack.getItem() instanceof SwordItem;
    }
}

package me.swirtzly.regeneration.common.item;

import me.swirtzly.regeneration.RegenConfig;
import me.swirtzly.regeneration.common.advancements.RegenTriggers;
import me.swirtzly.regeneration.common.block.BlockHandInJar;
import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.entity.EntityItemOverride;
import me.swirtzly.regeneration.common.item.arch.IDontStore;
import me.swirtzly.regeneration.common.tiles.TileEntityHandInJar;
import me.swirtzly.regeneration.util.PlayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLindos extends ItemOverrideBase implements IDontStore {

    public ItemLindos() {
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("amount"), new IItemPropertyGetter() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {

                if (stack.getTagCompound() != null) {
                    int amount = getAmount(stack);

                    if (!hasWater(stack)) {
                        return 0.0F;
                    }

                    if (hasWater(stack) && getAmount(stack) <= 0) {
                        return 2F;
                    }

                    if (amount == 100) {
                        return 1.0F;
                    }

                    if (amount >= 90) {
                        return 0.2F;
                    }

                    if (amount >= 50) {
                        return 0.5F;
                    }

                    if (amount >= 10) {
                        return 0.1F;
                    }
                }

                return 2F;
            }
        });
    }

    public static CompoundNBT getStackTag(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new CompoundNBT());
            stack.getTagCompound().setInteger("amount", 0);
        }
        return stack.getTagCompound();
    }

    public static int getAmount(ItemStack stack) {
        return getStackTag(stack).getInt("amount");
    }

    public static void setAmount(ItemStack stack, int amount) {
        getStackTag(stack).setInteger("amount", MathHelper.clamp(amount, 0, 100));
    }

    public static boolean hasWater(ItemStack stack) {
        return getStackTag(stack).getBoolean("water");
    }

    public static void setWater(ItemStack stack, boolean water) {
        getStackTag(stack).putBoolean("water", water);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        if (!playerIn.world.isRemote) {
            RegenTriggers.LINDOS_VIAL.trigger((ServerPlayerEntity) playerIn);
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!(entityLiving instanceof PlayerEntity)) return stack;

        PlayerEntity player = (PlayerEntity) entityLiving;
        IRegeneration cap = CapabilityRegeneration.getForPlayer(player);
        if (!worldIn.isRemote) {

            // If the player is in POST or Regenerating, stop them from drinking it
            if (getAmount(stack) > 100 && hasWater(stack)) {
                if (cap.getState() == PlayerUtil.RegenState.POST || cap.getState() == PlayerUtil.RegenState.REGENERATING || player.isCreative()) {
                    PlayerUtil.sendMessage(player, new TranslationTextComponent("regeneration.messages.cannot_use"), true);
                    return stack;
                }
            }

            if (hasWater(stack)) {
                if (getAmount(stack) == 100) {
                    if (cap.getRegenerationsLeft() <= RegenConfig.regenCapacity) {
                        cap.receiveRegenerations(1);
                        PlayerUtil.sendMessage(player, new TranslationTextComponent("regeneration.messages.jar"), true);
                    } else {
                        PlayerUtil.sendMessage(player, new TranslationTextComponent("regeneration.messages.transfer.max_regens"), true);
                    }

                    setAmount(stack, 0);
                    setWater(stack, false);
                    return stack;
                } else {
                    PlayerUtil.sendMessage(player, new TranslationTextComponent("regeneration.messages.empty_vial"), true);
                    return stack;
                }
            } else {
                PlayerUtil.sendMessage(player, new TranslationTextComponent("regeneration.messages.no_water"), true);
                return stack;
            }
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public void ontick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {

        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new CompoundNBT());
            stack.getTagCompound().putBoolean("live", true);
        } else {
            stack.getTagCompound().putBoolean("live", true);
        }

        if (!worldIn.isRemote) {
            // Entities around
            worldIn.getEntitiesWithinAABB(PlayerEntity.class, entityIn.getEntityBoundingBox().expand(10, 10, 10)).forEach(player -> {
                IRegeneration data = CapabilityRegeneration.getForPlayer((PlayerEntity) entityIn);
                if (data.getState() == PlayerUtil.RegenState.REGENERATING) {
                    if (hasWater(stack) && worldIn.rand.nextInt(100) > 70 && PlayerUtil.isInEitherHand(player, this)) {
                        setAmount(stack, getAmount(stack) + 1);
                    }
                }
            });

            // Player glowing
            if (entityIn instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entityIn;
                if (PlayerUtil.isInEitherHand(player, this)) {
                    if (hasWater(stack) && CapabilityRegeneration.getForPlayer(player).areHandsGlowing() && player.ticksExisted % 100 == 0) {
                        setAmount(stack, getAmount(stack) + 2);
                    }
                }
            }
        }
        super.ontick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack itemStack = player.getHeldItem(hand);
            RayTraceResult raytraceresult = rayTrace(worldIn, player, true);

            if (raytraceresult == null || raytraceresult.getBlockPos() == null) {
                return ActionResultType.FAIL;
            }

            BlockPos blockPos = raytraceresult.getBlockPos();
            BlockState iblockstate = worldIn.getBlockState(blockPos);
            Material material = iblockstate.getMaterial();

            if (iblockstate.getBlock() instanceof BlockHandInJar && player.isSneaking()) {
                if (worldIn.getTileEntity(blockPos) instanceof TileEntityHandInJar) {
                    TileEntityHandInJar jar = (TileEntityHandInJar) worldIn.getTileEntity(blockPos);
                    int has = getAmount(itemStack);
                    int needs = 100 - has;
                    if (jar.getLindosAmont() >= needs) {
                        jar.setLindosAmont(jar.getLindosAmont() - needs);
                        setAmount(itemStack, getAmount(itemStack) + needs);
                    }
                }
                return ActionResultType.SUCCESS;
            }

            if (material == Material.WATER) {
                if (!hasWater(itemStack)) {
                    worldIn.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
                    player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                    setWater(itemStack, true);
                    PlayerUtil.sendMessage(player, new TranslationTextComponent("nbt.item.water_filled"), true);
                } else {
                    PlayerUtil.sendMessage(player, new TranslationTextComponent("nbt.item.water_already_filled"), true);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public UseAction getItemUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (CapabilityRegeneration.getForPlayer(playerIn).getState() == PlayerUtil.RegenState.ALIVE) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
        } else {
            return new ActionResult<ItemStack>(ActionResultType.FAIL, itemstack);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("nbt.item.lindos", getAmount(stack)).getUnformattedText());
        tooltip.add(new TranslationTextComponent("nbt.item.water", hasWater(stack)).getUnformattedText());
    }

    @Override
    public void tick(EntityItemOverride itemOverride) {
        if (itemOverride.world.isRemote) return;
        ItemStack itemStack = itemOverride.getItem();
        if (itemStack.getItem() == this) {
            if (itemOverride.isInWater()) {
                if (itemStack.getTagCompound() != null) {
                    setWater(itemStack, true);
                }
            }
        }
    }
	
}

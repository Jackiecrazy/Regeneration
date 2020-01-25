package me.swirtzly.regeneration.common.item;

import com.sun.istack.internal.Nullable;
import me.swirtzly.regeneration.common.entity.EntityItemOverride;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemOverrideBase extends Item implements EntityItemOverride.IEntityOverride {

    public ItemOverrideBase(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public void update(EntityItemOverride itemOverride) {

    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        EntityItemOverride item = new EntityItemOverride(world, location.getX(), location.getY(), location.getZ(), itemstack);
        item.setEntitySize(item.getHeight(), item.getWidth());
        item.setMotion(location.getMotion());
        return item;
    }

    @Override
    public boolean shouldDie(ItemStack stack) {
        if (stack.getTag() != null) {
            return !stack.getTag().contains("live");
        }
        return true;
    }


}
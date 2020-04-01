package me.swirtzly.regeneration.common.item;

import me.swirtzly.regeneration.client.skinhandling.SkinInfo;
import me.swirtzly.regeneration.common.traits.DnaHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class ItemHand extends Item {

    public ItemHand() {
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("skin_type"), (stack, worldIn, entityIn) -> getSkinType(stack).equals("ALEX") ? 1 : 0);
    }

    public static void setTimeCreated(ItemStack stack, long created) {
        getStackTag(stack).putLong("created", created);
    }

    public static long getTimeCreated(ItemStack stack) {
        return getStackTag(stack).getLong("created");
    }

    public static void setTextureString(ItemStack stack, String textureString) {
        getStackTag(stack).putString("textureString", textureString);
    }

    public static String getTextureString(ItemStack stack) {
        return getStackTag(stack).getString("textureString");
    }

    public static void setSkinType(ItemStack stack, String skinType) {
        getStackTag(stack).putString("skinType", skinType);
    }

    public static String getSkinType(ItemStack stack) {
        return getStackTag(stack).getString("skinType");
    }

    public static void setTrait(ItemStack stack, String trait) {
        getStackTag(stack).putString("trait", trait);
    }

    public static String getTrait(ItemStack stack) {
        return getStackTag(stack).getString("trait");
    }

    public static void setOwner(ItemStack stack, UUID owner) {
        getStackTag(stack).putUniqueId("owner", owner);
    }

    public static UUID getOwner(ItemStack stack) {
        return getStackTag(stack).getUniqueId("owner");
    }

    public static CompoundNBT getStackTag(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new CompoundNBT());
            stack.getTagCompound().putString("textureString", "NONE");
            stack.getTagCompound().putString("skinType", SkinInfo.SkinType.ALEX.name());
            stack.getTagCompound().putUniqueId("owner", UUID.fromString("96511168-1bb3-4ff0-a894-271e42606a39"));
            stack.getTagCompound().putLong("created", 0);
            stack.getTagCompound().putString("trait", DnaHandler.DNA_BORING.resourceLocation.toString());
        }
        return stack.getTagCompound();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return new TranslationTextComponent("item.hand.name", UsernameCache.getLastKnownUsername(getOwner(stack))).getUnformattedComponentText();
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        Date date = new Date(ItemHand.getTimeCreated(stack));
        DateFormat formatter = new SimpleDateFormat("dd/MM/YYYY @ HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateFormatted = formatter.format(date);
        tooltip.add(new TranslationTextComponent("nbt.created", dateFormatted).getUnformattedComponentText());
        tooltip.add("Trait: " + new TranslationTextComponent(DnaHandler.getDnaEntry(new ResourceLocation(getTrait(stack))).getLangKey()).getUnformattedComponentText());
    }
}

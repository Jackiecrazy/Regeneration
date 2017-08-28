package com.afg.regeneration.traits.negative;

import com.afg.regeneration.traits.positive.Strong;
import lucraft.mods.lucraftcore.abilities.Ability;
import lucraft.mods.lucraftcore.abilities.AbilityAttributeModifier;
import lucraft.mods.lucraftcore.attributes.LCAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * Created by AFlyingGrayson on 8/7/17
 */
public class Weak extends AbilityAttributeModifier implements INegativeTrait
{

	public Weak(EntityPlayer player, UUID uuid, float factor, int operation)
	{
		super(player, uuid, factor, operation);
	}

	@Override public IAttribute getAttribute()
	{
		return LCAttributes.PUNCH_DAMAGE;
	}

	@Override public Class<? extends Ability> getPositiveTrait()
	{
		return Strong.class;
	}
}
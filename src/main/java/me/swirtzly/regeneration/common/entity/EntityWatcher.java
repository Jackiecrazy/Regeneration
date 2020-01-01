package me.swirtzly.regeneration.common.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Swirtzly
 * on 22/09/2019 @ 20:57
 */
public class EntityWatcher extends CreatureEntity {

    protected static final AxisAlignedBB COLLIDE = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

    public EntityWatcher(World worldIn) {
        super(worldIn);
        this.tasks.addTask(8, new WaterAvoidingRandomWalkingGoal(this, 0.1D));
        this.targetTasks.addTask(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.tasks.addTask(8, new CreepTowardPlayer(this, 0.25D, false));
    }

    @Override
    public boolean getIsInvulnerable() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return COLLIDE;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
}

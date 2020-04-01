package me.swirtzly.regeneration.common.block;

import me.swirtzly.regeneration.RegenerationMod;
import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.entity.EntityLindos;
import me.swirtzly.regeneration.common.tiles.TileEntityHandInJar;
import me.swirtzly.regeneration.handlers.RegenObjects;
import me.swirtzly.regeneration.util.PlayerUtil;
import net.minecraft.block.*;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockHandInJar extends DirectionalBlock {

	public BlockHandInJar() {
		super(Material.GOURD);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, Direction.SOUTH));
		setHardness(5);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return false;

		if (worldIn.getTileEntity(pos) instanceof TileEntityHandInJar) {
			TileEntityHandInJar jar = (TileEntityHandInJar) worldIn.getTileEntity(pos);
			IRegeneration data = CapabilityRegeneration.getForPlayer(playerIn);

			if (jar.getLindosAmont() >= 100 && data.getState() == PlayerUtil.RegenState.ALIVE && playerIn.isSneaking() && jar.hasHand()) {
				jar.setLindosAmont(jar.getLindosAmont() - 100);
				data.receiveRegenerations(1);
				data.setSyncingFromJar(true);
				worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), RegenObjects.Sounds.HAND_GLOW, SoundCategory.PLAYERS, 1.0F, 0.7F);
				data.synchronise();
				jar.sendUpdates();
				return true;
			}

			if (data.getState() != PlayerUtil.RegenState.REGENERATING && !playerIn.isSneaking()) {
				playerIn.openGui(RegenerationMod.INSTANCE, 77, worldIn, jar.getPos().getX(), jar.getPos().getY(), jar.getPos().getZ());
				return true;
			}

		}
		return false;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return new TileEntityHandInJar();
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).getIndex();
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed blockstate.
	 *
	 * @deprecated call via {@link BlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is fine.
	 */
	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
	 *
	 * @deprecated call via {@link BlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
	 */
	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction enumfacing = Direction.byIndex(meta);

		if (enumfacing.getAxis() == Direction.Axis.Y) {
			enumfacing = Direction.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		if (world.isRemote) return super.removedByPlayer(state, world, pos, player, willHarvest);
		if (world.getTileEntity(pos) instanceof TileEntityHandInJar) {
			TileEntityHandInJar jar = (TileEntityHandInJar) world.getTileEntity(pos);
			if (jar != null && jar.lindosAmont > 0) {
				EntityLindos lindos = new EntityLindos(player.world);
				lindos.setLocationAndAngles(player.posX, player.posY + player.getEyeHeight(), player.posZ, 0, 0);
				lindos.setAmount(jar.lindosAmont);
				player.world.spawnEntity(lindos);
			}
			if (jar != null) {
				InventoryHelper.dropInventoryItems(world, pos, jar);
			}
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public ItemGroup getCreativeTab() {
		return ItemGroup.MISC;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		Direction entityFacing = entity.getHorizontalFacing();

		if (!world.isRemote) {
			if (entityFacing == Direction.NORTH) {
				entityFacing = Direction.SOUTH;
			} else if (entityFacing == Direction.EAST) {
				entityFacing = Direction.WEST;
			} else if (entityFacing == Direction.SOUTH) {
				entityFacing = Direction.NORTH;
			} else if (entityFacing == Direction.WEST) {
				entityFacing = Direction.EAST;
			}

			world.setBlockState(pos, state.withProperty(FACING, entityFacing), 2);
		}
	}
}

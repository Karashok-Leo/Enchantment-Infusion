package karashokleo.enchantment_infusion.api.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jetbrains.annotations.Nullable;

public class NameableSingleStackTile extends BlockEntity implements ContainerSingleItem, Nameable
{
    protected ItemStack item = ItemStack.EMPTY;
    protected Component customName;

    public NameableSingleStackTile(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public ItemStack getTheItem()
    {
        return item;
    }

    @Override
    public ItemStack splitTheItem(int amount)
    {
        return removeItem(0, amount);
    }

    @Override
    public ItemStack removeItem(int slot, int amount)
    {
        ItemStack removed = item.copy();
        amount = Math.min(amount, item.getCount());
        removed.setCount(amount);
        item.shrink(amount);
        update();
        return removed;
    }

    @Override
    public void setTheItem(ItemStack stack)
    {
        item = stack;
        update();
    }

    @Override
    public boolean stillValid(Player player)
    {
        return false;
    }

    @Override
    public int getMaxStackSize()
    {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return this.item.isEmpty();
    }

    public void update()
    {
        this.setChanged();
        if (level == null || level.isClientSide())
        {
            return;
        }
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries)
    {
        super.loadAdditional(nbt, registries);
        this.item = ItemStack.parseOptional(registries, nbt.getCompound("Item"));
        if (nbt.contains("CustomName", Tag.TAG_STRING))
        {
            this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"), registries);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries)
    {
        super.saveAdditional(nbt, registries);
        nbt.put("Item", this.item.saveOptional(registries));
        if (this.hasCustomName())
        {
            nbt.putString("CustomName", Component.Serializer.toJson(this.customName, registries));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components)
    {
        super.applyImplicitComponents(components);
        this.customName = components.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder)
    {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CUSTOM_NAME, this.customName);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag nbt)
    {
        nbt.remove("CustomName");
    }

    @Override
    public Component getName()
    {
        if (this.customName != null)
        {
            return this.customName;
        }
        return this.getBlockState().getBlock().getName();
    }

    public void setCustomName(@Nullable Component customName)
    {
        this.customName = customName;
    }

    @Override
    @Nullable
    public Component getCustomName()
    {
        return this.customName;
    }
}

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NameableSingleStackTile extends BlockEntity implements ContainerSingleItem, Nameable
{
    protected ItemStack item = ItemStack.EMPTY;
    protected Component customName;

    public NameableSingleStackTile(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @NotNull
    @Override
    public ItemStack getTheItem()
    {
        return item;
    }

    @NotNull
    @Override
    public ItemStack splitTheItem(int amount)
    {
        return removeItem(0, amount);
    }

    @NotNull
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
    public void setTheItem(@NotNull ItemStack stack)
    {
        item = stack;
        update();
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return false;
    }

    @Override
    public int getMaxStackSize()
    {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack)
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
    public void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries)
    {
        super.loadAdditional(nbt, registries);
        this.item = ItemStack.parseOptional(registries, nbt.getCompound("Item"));
        if (nbt.contains("CustomName", Tag.TAG_STRING))
        {
            this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"), registries);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries)
    {
        super.saveAdditional(nbt, registries);
        nbt.put("Item", this.item.saveOptional(registries));
        if (this.hasCustomName())
        {
            nbt.putString("CustomName", Component.Serializer.toJson(this.customName, registries));
        }
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries)
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
    protected void applyImplicitComponents(@NotNull DataComponentInput components)
    {
        super.applyImplicitComponents(components);
        this.customName = components.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder builder)
    {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CUSTOM_NAME, this.customName);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag nbt)
    {
        nbt.remove("CustomName");
    }

    @NotNull
    @Override
    public Component getName()
    {
        return Objects.requireNonNullElseGet(this.customName, () -> this.getBlockState().getBlock().getName());
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

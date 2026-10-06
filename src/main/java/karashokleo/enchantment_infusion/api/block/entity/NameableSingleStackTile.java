package karashokleo.enchantment_infusion.api.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class NameableSingleStackTile extends BlockEntity implements SingleStackInventory, Nameable
{
    protected ItemStack item = ItemStack.EMPTY;
    protected Text customName;

    public NameableSingleStackTile(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public ItemStack getStack()
    {
        return item;
    }

    @Override
    public ItemStack decreaseStack(int amount)
    {
        return removeStack(0, amount);
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        ItemStack removed = item.copy();
        amount = Math.min(amount, item.getCount());
        removed.setCount(amount);
        item.decrement(amount);
        update();
        return removed;
    }

    @Override
    public void setStack(ItemStack stack)
    {
        item = stack;
        update();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player)
    {
        return false;
    }

    @Override
    public int getMaxCountPerStack()
    {
        return 1;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack)
    {
        return this.item.isEmpty();
    }

    public void update()
    {
        this.markDirty();
        if (world == null || world.isClient())
        {
            return;
        }
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.readNbt(nbt, registries);
        this.item = ItemStack.fromNbtOrEmpty(registries, nbt.getCompound("Item"));
        if (nbt.contains("CustomName", NbtElement.STRING_TYPE))
        {
            this.customName = Text.Serialization.fromJson(nbt.getString("CustomName"), registries);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.writeNbt(nbt, registries);
        nbt.put("Item", this.item.encodeAllowEmpty(registries));
        if (this.hasCustomName())
        {
            nbt.putString("CustomName", Text.Serialization.toJsonString(this.customName, registries));
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries)
    {
        return createNbt(registries);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void readComponents(ComponentsAccess components)
    {
        super.readComponents(components);
        this.customName = components.get(DataComponentTypes.CUSTOM_NAME);
    }

    @Override
    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt)
    {
        nbt.remove("CustomName");
    }

    @Override
    public Text getName()
    {
        if (this.customName != null)
        {
            return this.customName;
        }
        return this.getCachedState().getBlock().getName();
    }

    public void setCustomName(@Nullable Text customName)
    {
        this.customName = customName;
    }

    @Override
    @Nullable
    public Text getCustomName()
    {
        return this.customName;
    }
}

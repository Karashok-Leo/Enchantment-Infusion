package karashokleo.enchantment_infusion.api.event;

import karashokleo.enchantment_infusion.api.block.entity.InfusionInventory;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface InfusionCompleteCallback
{
    Event EVENT = new Event();

    /** Loader-independent callback facade preserving registration and invocation semantics. */
    final class Event
    {
        private final List<InfusionCompleteCallback> listeners = new CopyOnWriteArrayList<>();

        public void register(InfusionCompleteCallback listener) { listeners.add(listener); }

        public InfusionCompleteCallback invoker()
        {
            return (world, pos, output, inventory, recipe) -> {
                for (InfusionCompleteCallback listener : listeners)
                    listener.onInfusionComplete(world, pos, output, inventory, recipe);
            };
        }
    }

    void onInfusionComplete(ServerWorld world, BlockPos pos, ItemStack output, InfusionInventory inventory, InfusionRecipe recipe);
}

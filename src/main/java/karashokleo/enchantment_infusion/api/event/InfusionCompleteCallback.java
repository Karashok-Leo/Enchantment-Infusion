package karashokleo.enchantment_infusion.api.event;

import karashokleo.enchantment_infusion.api.block.entity.InfusionInventory;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface InfusionCompleteCallback
{
    Event<InfusionCompleteCallback> EVENT = EventFactory.createArrayBacked(
            InfusionCompleteCallback.class,
            (listeners) -> (world, pos, output, inventory, recipe) ->
            {
                for (InfusionCompleteCallback listener : listeners)
                {
                    listener.onInfusionComplete(world, pos, output, inventory, recipe);
                }
            }
    );

    void onInfusionComplete(ServerWorld world, BlockPos pos, ItemStack output, InfusionInventory inventory, InfusionRecipe recipe);
}

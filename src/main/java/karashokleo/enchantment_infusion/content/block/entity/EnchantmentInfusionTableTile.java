package karashokleo.enchantment_infusion.content.block.entity;

import karashokleo.enchantment_infusion.api.block.entity.AbstractInfusionTile;
import karashokleo.enchantment_infusion.api.block.entity.InfusionInventory;
import karashokleo.enchantment_infusion.api.event.InfusionCompleteCallback;
import karashokleo.enchantment_infusion.api.recipe.InfusionRecipe;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EIRecipes;
import karashokleo.enchantment_infusion.init.EITexts;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnchantmentInfusionTableTile extends AbstractInfusionTile
{
    private static final int TOTAL_CRAFT_TICKS = 100;
    private final RecipeManager.CachedCheck<InfusionInventory, InfusionRecipe> matchGetter;
    private int ticks;

    public EnchantmentInfusionTableTile(BlockPos pos, BlockState state)
    {
        super(EIBlocks.INFUSION_TABLE_TILE, pos, state);
        this.matchGetter = RecipeManager.createCheck(EIRecipes.INFUSION_RECIPE_TYPE);
    }

    protected void onInfusingStateChanged(ServerLevel world, BlockPos pos, boolean infusing)
    {
        BlockState state = world.getBlockState(pos);
        if (state.is(EIBlocks.INFUSION_TABLE) &&
            state.getValue(EIBlocks.INFUSING) != infusing)
        {
            world.setBlockAndUpdate(pos, state.setValue(EIBlocks.INFUSING, infusing));
        }
        List<BlockPos> pedestalPoses = getPedestalPoses();
        for (BlockPos pedestalPos : pedestalPoses)
        {
            BlockState pedestalState = world.getBlockState(pedestalPos);
            if (pedestalState.is(EIBlocks.INFUSION_PEDESTAL) &&
                state.getValue(EIBlocks.INFUSING) != infusing)
            {
                world.setBlockAndUpdate(pedestalPos, pedestalState.setValue(EIBlocks.INFUSING, infusing));
            }
        }
    }

    @Override
    public void onUse(ServerLevel world, BlockPos pos, Player player)
    {
        if (ticks != 0)
        {
            return;
        }
        super.onUse(world, pos, player);
        if (isEmpty())
        {
            return;
        }
        NonNullList<AbstractInfusionTile> pedestalInventory = getPedestalTiles(world);
        if (pedestalInventory.size() < 8)
        {
            player.displayClientMessage(EITexts.PNF.get(), true);
            return;
        }
        InfusionInventory inventory = new InfusionInventory(this, pedestalInventory);
        Optional<RecipeHolder<InfusionRecipe>> match = matchGetter.getRecipeFor(inventory, world);
        if (match.isPresent())
        {
            ticks = TOTAL_CRAFT_TICKS;
            onInfusingStateChanged(world, pos, true);
        } else
        {
            player.displayClientMessage(EITexts.RNF.get(), true);
            player.getInventory().placeItemBackInInventory(this.removeTheItem());
        }
    }

    public static void spawnParticles(ServerLevel world, Vec3 pos, int ticks)
    {
        if (ticks == 90)
        {
            spawnEnchantParticles(world, pos, 4.2, 500, 0.02, 0.1, 0.02, 2);
            spawnScrapeParticles(world, pos, 66, 2.5);
            playProcessSound(world, pos, 0.60f);
        } else if (ticks == 62)
        {
            spawnEnchantParticles(world, pos, 3.4, 150, 0.01, 0.05, 0.01, 0.4);
        } else if (ticks == 60)
        {
            spawnScrapeParticles(world, pos, 30, 1.8);
            playProcessSound(world, pos, 0.85f);
        } else if (ticks == 59)
        {
            spawnEnchantParticles(world, pos, 3, 100, 0.01, 0.05, 0.01, 0.25);
        } else if (ticks == 55)
        {
            spawnEnchantParticles(world, pos, 2.72, 50, 0.01, 0.05, 0.01, 0.15);
        } else if (ticks == 30)
        {
            spawnScrapeParticles(world, pos, 15, 0.75);
            playProcessSound(world, pos, 1.1f);
        }
    }

    public static void spawnEnchantParticles(ServerLevel world, Vec3 pos, double yOffset, int count, double deltaX, double deltaY, double deltaZ, double speed)
    {
        world.sendParticles(
            ParticleTypes.ENCHANT,
            pos.x(),
            pos.y() + yOffset,
            pos.z(),
            count, deltaX,
            deltaY,
            deltaZ,
            speed
        );
    }

    public static void spawnScrapeParticles(ServerLevel world, Vec3 pos, int count, double radius)
    {
        for (int i = 0; i < count; i++)
        {
            double angle = 2 * Math.PI * i / count;
            world.sendParticles(
                ParticleTypes.SCRAPE,
                pos.x() + radius * Math.cos(angle),
                pos.y() + 1.5,
                pos.z() + radius * Math.sin(angle),
                1,
                0.02,
                0.01,
                0.02,
                0.01
            );
        }
    }

    public static void spawnEndRodParticles(ServerLevel world, Vec3 pos)
    {
        world.sendParticles(
            ParticleTypes.END_ROD,
            pos.x(),
            pos.y() + 1.3,
            pos.z(),
            16,
            0.01,
            0.01,
            0.01,
            0.06
        );
    }

    public static void playProcessSound(ServerLevel world, Vec3 pos, float pitch)
    {
        world.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.6f, pitch);
    }

    public static void playCompleteSound(ServerLevel world, Vec3 pos)
    {
        world.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public static void spawnLightning(Level world, Vec3 pos)
    {
        LightningBolt lightningEntity = new LightningBolt(EntityType.LIGHTNING_BOLT, world);
        lightningEntity.moveTo(pos);
        lightningEntity.setVisualOnly(true);
        world.addFreshEntity(lightningEntity);
    }

    public static void serverTick(Level world, BlockPos pos, BlockState state, EnchantmentInfusionTableTile entity)
    {
        if (!(world instanceof ServerLevel serverWorld))
        {
            return;
        }
        if (entity.ticks == 0)
        {
            return;
        }
        entity.ticks--;
        Vec3 center = Vec3.atBottomCenterOf(pos);
        spawnParticles(serverWorld, center, entity.ticks);
        if (entity.ticks % 10 == 0)
        {
            NonNullList<AbstractInfusionTile> pedestalInventory = entity.getPedestalTiles(serverWorld);
            if (pedestalInventory.size() < 8)
            {
                entity.interrupt(serverWorld);
                return;
            }
            InfusionInventory inventory = new InfusionInventory(entity, pedestalInventory);
            Optional<RecipeHolder<InfusionRecipe>> match = entity.matchGetter.getRecipeFor(inventory, serverWorld);
            if (match.isEmpty())
            {
                entity.interrupt(serverWorld);
            } else if (entity.ticks == 0)
            {
                entity.craft(serverWorld, match.get().value(), inventory);
                spawnEndRodParticles(serverWorld, center);
                playCompleteSound(serverWorld, center);
                entity.onInfusingStateChanged(serverWorld, pos, false);
            }
        }
    }

    public void craft(ServerLevel world, InfusionRecipe recipe, InfusionInventory inventory)
    {
        ItemStack crafted = recipe.assemble(inventory, world.registryAccess());
        this.setTheItem(crafted);
        inventory.setRemainder(recipe.getRemainingItems(inventory));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new InfusionCompleteCallback(world, worldPosition, crafted, inventory, recipe));
    }

    public void interrupt(ServerLevel world)
    {
        this.ticks = 0;
        Vec3 center = Vec3.atBottomCenterOf(getBlockPos());
        spawnLightning(world, center);
        onInfusingStateChanged(world, getBlockPos(), false);
        Player player = world.getNearestPlayer(center.x(), center.y(), center.z(), 8, false);
        if (player != null)
        {
            player.displayClientMessage(EITexts.EII.get(), true);
        }
    }

    public List<BlockPos> getPedestalPoses()
    {
        List<BlockPos> pedestals = new ArrayList<>();
        BlockPos pos = getBlockPos();
        pedestals.add(pos.north(3));
        pedestals.add(pos.north(2).east(2));
        pedestals.add(pos.east(3));
        pedestals.add(pos.east(2).south(2));
        pedestals.add(pos.south(3));
        pedestals.add(pos.south(2).west(2));
        pedestals.add(pos.west(3));
        pedestals.add(pos.west(2).north(2));
        return pedestals;
    }

    public NonNullList<AbstractInfusionTile> getPedestalTiles(Level world)
    {
        NonNullList<AbstractInfusionTile> tiles = NonNullList.create();
        for (BlockPos pos : getPedestalPoses())
        {
            if (world.getBlockEntity(pos) instanceof AbstractInfusionTile tile)
            {
                tiles.add(tile);
            }
        }
        return tiles;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries)
    {
        super.saveAdditional(nbt, registries);
        nbt.putInt("Ticks", ticks);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries)
    {
        super.loadAdditional(nbt, registries);
        this.ticks = nbt.getInt("Ticks");
    }
}

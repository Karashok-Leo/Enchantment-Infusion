package karashokleo.enchantment_infusion.content.block.entity;

import karashokleo.enchantment_infusion.api.block.entity.AbstractInfusionTile;
import karashokleo.enchantment_infusion.api.block.entity.InfusionInventory;
import karashokleo.enchantment_infusion.init.EIBlocks;
import karashokleo.enchantment_infusion.init.EIRecipes;
import karashokleo.enchantment_infusion.init.EITexts;
import karashokleo.enchantment_infusion.content.recipe.EnchantmentInfusionRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnchantmentInfusionTableTile extends AbstractInfusionTile
{
    private static final int TOTAL_CRAFT_TICKS = 100;
    private final RecipeManager.MatchGetter<InfusionInventory, EnchantmentInfusionRecipe> matchGetter;
    private int ticks;

    public EnchantmentInfusionTableTile(BlockPos pos, BlockState state)
    {
        super(EIBlocks.INFUSION_TABLE_TILE, pos, state);
        this.matchGetter = RecipeManager.createCachedMatchGetter(EIRecipes.EI_TYPE);
    }

    @Override
    public void onUse(ServerWorld world, BlockPos pos, PlayerEntity player)
    {
        if (ticks != 0) return;
        super.onUse(world, pos, player);
        if (isEmpty()) return;
        DefaultedList<AbstractInfusionTile> pedestalInventory = getPedestalTiles(world);
        if (pedestalInventory.size() < 8)
        {
            player.sendMessage(EITexts.PNF.get(), true);
            return;
        }
        InfusionInventory inventory = new InfusionInventory(this, pedestalInventory);
        Optional<EnchantmentInfusionRecipe> match = matchGetter.getFirstMatch(inventory, world);
        if (match.isPresent()) ticks = TOTAL_CRAFT_TICKS;
        else
        {
            player.sendMessage(EITexts.RNF.get(), true);
            player.getInventory().offerOrDrop(this.removeStack());
        }
    }

    public static void spawnParticles(ServerWorld world, Vec3d pos, int ticks)
    {
        if (ticks == 90)
        {
            spawnEnchantParticles(world, pos, 4.2, 500, 0.02, 0.1, 0.02, 2);
            spawnScrapeParticles(world, pos, 66, 2.5);
        } else if (ticks == 62)
        {
            spawnEnchantParticles(world, pos, 3.4, 150, 0.01, 0.05, 0.01, 0.4);
        } else if (ticks == 60)
        {
            spawnScrapeParticles(world, pos, 30, 1.8);
        } else if (ticks == 59)
        {
            spawnEnchantParticles(world, pos, 3, 100, 0.01, 0.05, 0.01, 0.25);
        } else if (ticks == 55)
        {
            spawnEnchantParticles(world, pos, 2.72, 50, 0.01, 0.05, 0.01, 0.15);
        } else if (ticks == 30)
        {
            spawnScrapeParticles(world, pos, 15, 0.75);
        }
    }

    public static void spawnEnchantParticles(ServerWorld world, Vec3d pos, double yOffset, int count, double deltaX, double deltaY, double deltaZ, double speed)
    {
        world.spawnParticles(
                ParticleTypes.ENCHANT,
                pos.getX(),
                pos.getY() + yOffset,
                pos.getZ(),
                count, deltaX,
                deltaY,
                deltaZ,
                speed
        );
    }

    public static void spawnScrapeParticles(ServerWorld world, Vec3d pos, int count, double radius)
    {
        for (int i = 0; i < count; i++)
        {
            double angle = 2 * Math.PI * i / count;
            world.spawnParticles(
                    ParticleTypes.SCRAPE,
                    pos.getX() + radius * Math.cos(angle),
                    pos.getY() + 1.5,
                    pos.getZ() + radius * Math.sin(angle),
                    1,
                    0.02,
                    0.01,
                    0.02,
                    0.01
            );
        }
    }

    public static void spawnEndRodParticles(ServerWorld world, Vec3d pos)
    {
        world.spawnParticles(
                ParticleTypes.END_ROD,
                pos.getX(),
                pos.getY()+1.5,
                pos.getZ(),
                16,
                0.01,
                0.01,
                0.01,
                0.06
        );
    }

    public static void spawnLightning(World world, Vec3d pos)
    {
        LightningEntity lightningEntity;
        if ((lightningEntity = EntityType.LIGHTNING_BOLT.create(world)) != null)
        {
            lightningEntity.refreshPositionAfterTeleport(pos);
            lightningEntity.setCosmetic(true);
            world.spawnEntity(lightningEntity);
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, EnchantmentInfusionTableTile entity)
    {
        if (!(world instanceof ServerWorld serverWorld)) return;
        if (entity.ticks == 0) return;
        entity.ticks--;
        Vec3d center = Vec3d.ofBottomCenter(pos);
        spawnParticles(serverWorld, center, entity.ticks);
        if (entity.ticks % 10 == 0)
        {
            DefaultedList<AbstractInfusionTile> pedestalInventory = entity.getPedestalTiles(world);
            if (pedestalInventory.size() < 8)
            {
                entity.interrupt(world);
                return;
            }
            InfusionInventory inventory = new InfusionInventory(entity, pedestalInventory);
            Optional<EnchantmentInfusionRecipe> match = entity.matchGetter.getFirstMatch(inventory, world);
            if (match.isEmpty()) entity.interrupt(world);
            else if (entity.ticks == 0)
            {
                entity.craft(world, match.get(), inventory);
                spawnEndRodParticles(serverWorld, center);
                spawnLightning(world, center);
            }
        }
    }

    public void craft(World world, EnchantmentInfusionRecipe recipe, InfusionInventory inventory)
    {
        this.setStack(recipe.craft(inventory, world.getRegistryManager()));
        inventory.setRemainder(recipe.getRemainder(inventory));
    }

    public void interrupt(World world)
    {
        this.ticks = 0;
        Vec3d center = Vec3d.ofBottomCenter(getPos());
        spawnLightning(world, center);
        PlayerEntity player = world.getClosestPlayer(center.getX(), center.getY(), center.getZ(), 8, false);
        if (player != null) player.sendMessage(EITexts.EII.get(), true);
    }

    public List<BlockPos> getPedestalPoses()
    {
        List<BlockPos> pedestals = new ArrayList<>();
        BlockPos pos = getPos();
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

    public DefaultedList<AbstractInfusionTile> getPedestalTiles(World world)
    {
        DefaultedList<AbstractInfusionTile> tiles = DefaultedList.of();
        for (BlockPos pos : getPedestalPoses())
            if (world.getBlockEntity(pos) instanceof AbstractInfusionTile tile)
                tiles.add(tile);
        return tiles;
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("Ticks", ticks);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.ticks = nbt.getInt("Ticks");
    }
}

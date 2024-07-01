package karashokleo.enchantment_infusion.api.util;

import com.google.gson.JsonSyntaxException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class EnchantmentSerial
{
    public static String encode(Enchantment enchantment)
    {
        return Optional
                .ofNullable(Registries.ENCHANTMENT.getId(enchantment))
                .orElseThrow(() -> new IllegalArgumentException("Enchantment " + enchantment + " is not registered"))
                .toString();
    }

    public static Enchantment decode(String id)
    {
        return Registries.ENCHANTMENT
                .getOrEmpty(new Identifier(id))
                .orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + id + "'"));
    }
}

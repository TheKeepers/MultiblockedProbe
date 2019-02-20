package cn.yesterday17.probe.serializer;

import cn.yesterday17.probe.ZSRCFile;
import com.google.gson.*;
import mezz.jei.gui.ingredients.IIngredientListElement;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.EntityEntry;

import java.lang.reflect.Type;

public class ZSRCSerializer implements JsonSerializer<ZSRCFile> {
    @Override
    public JsonElement serialize(ZSRCFile src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject zsrc = new JsonObject();

        zsrc.add("mcVersion", context.serialize((src.getMcVersion())));
        zsrc.add("forgeVersion", context.serialize(src.getForgeVersion()));
        zsrc.add("probeVersion", context.serialize((src.getProbeVersion())));

        zsrc.add("mods", context.serialize(src.getMods()));

        // Items
        JsonArray items = new JsonArray();
        // src.getItems().forEach(item -> items.add(context.serialize(item, Item.class)));
        src.getJEIItems().forEach(item -> items.add(context.serialize(item, IIngredientListElement.class)));
        zsrc.add("items", items);

        JsonArray enchantments = new JsonArray();
        src.getEnchantments().forEach(enchantment -> enchantments.add(context.serialize(enchantment, Enchantment.class)));
        zsrc.add("enchantments", enchantments);

        JsonArray entities = new JsonArray();
        src.getEntities().forEach(entity -> entities.add(context.serialize(entity, EntityEntry.class)));
        zsrc.add("entities", entities);

        JsonArray fluids = new JsonArray();
        src.getFluids().forEach(fluid -> fluids.add(context.serialize(fluid, Fluid.class)));
        zsrc.add("fluids", fluids);

        zsrc.add("oredictionary", context.serialize(src.getOreDictionary()));

        return zsrc;
    }
}

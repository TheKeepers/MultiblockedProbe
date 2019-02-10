package cn.yesterday17.probe;

import cn.yesterday17.probe.serializer.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mezz.jei.Internal;
import mezz.jei.gui.ingredients.IIngredientListElement;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Mod(
        modid = Probe.MOD_ID,
        name = Probe.NAME,
        version = Probe.VERSION,
        clientSideOnly = true,
        dependencies = "required-after:crafttweaker" + ";" +
                "required-after:jei" + ";"
)
public class Probe {
    static final String MOD_ID = "probe";
    static final String NAME = "Probe";
    static final String VERSION = "0.1.16";

    private static Logger logger;
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(ArtifactVersion.class, new ArtifactVersionSerializer())
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationSerializer())
            .registerTypeHierarchyAdapter(CreativeTabs.class, new CreativeTabSerializer())
            .registerTypeAdapter(ModMetadata.class, new ModSerializer())
            // .registerTypeHierarchyAdapter(Item.class, new ItemSerializer())
            .registerTypeHierarchyAdapter(IIngredientListElement.class, new JEIItemSerializer())
            .registerTypeHierarchyAdapter(Enchantment.class, new EnchantmentSerializer())
            .registerTypeHierarchyAdapter(EntityEntry.class, new EntitySerializer())
            .registerTypeHierarchyAdapter(Fluid.class, new FluidSerializer())
            .serializeNulls().setPrettyPrinting()
            .create();
    private static ZSRCFile rcFile = new ZSRCFile();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        rcFile.mcVersion = ForgeVersion.mcVersion;
        rcFile.forgeVersion = ForgeVersion.getVersion();
        rcFile.probeVersion = VERSION;

        // Mods
        Loader.instance().getIndexedModList().forEach((modid, container) -> {
            if (container instanceof FMLModContainer) {
                rcFile.Mods.add(container.getMetadata());
            }
        });

        // Items
        Internal.getRuntime().getIngredientFilter().getIngredientList().forEach((element -> {
            if (element.getIngredient() instanceof ItemStack) {
                rcFile.JEIItems.add(element);
            }
        }));
        // remain for compatibility
        // rcFile.Items.addAll(ForgeRegistries.ITEMS.getValuesCollection());

        // Enchantments
        rcFile.Enchantments.addAll(ForgeRegistries.ENCHANTMENTS.getValuesCollection());

        // Entities
        rcFile.Entities.addAll(ForgeRegistries.ENTITIES.getValuesCollection());

        // Fluids
        rcFile.Fluids.addAll(FluidRegistry.getRegisteredFluids().values());

        // Write to .zsrc
        try {
            BufferedWriter rcBufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("./scripts/.zsrc"), StandardCharsets.UTF_8
            ));
            gson.toJson(rcFile, rcBufferedWriter);
            rcBufferedWriter.close();
            logger.info("Probe loaded successfully!");
        } catch (Exception e) {
            logger.error("Probe met an error while loading! Please report to author about the problem!");
            logger.error(e, e);
        }
    }
}

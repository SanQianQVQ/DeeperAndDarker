package com.nitro.deeperdarker.datagen.lang;

import com.nitro.deeperdarker.DeeperAndDarker;
import com.nitro.deeperdarker.registry.blocks.DDBlocks;
import com.nitro.deeperdarker.registry.items.DDItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class ENLanguageProvider extends LanguageProvider {
    public ENLanguageProvider(DataGenerator gen) {
        super(gen, DeeperAndDarker.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.deeperdarker", "Deeper And Darker");

        DDBlocks.BLOCKS.getEntries().forEach(this::addBlock);
        DDItems.ITEMS.getEntries().stream().filter(item -> !(item.get() instanceof BlockItem)).forEach(this::addItem);

        add("advancements.sculk_story.root.title", "Sculk Story");
        add("advancements.sculk_story.root.description", "You feel something pulling you toward the underground...");
        add("advancements.sculk_story.obtain_phantom_membrane.title", "Sleepless Nights");
        add("advancements.sculk_story.obtain_phantom_membrane.description", "Slay a Monster of the Night Sky");
        add("advancements.sculk_story.find_ancient_city.title", "A Metropolis of Restless Souls");
        add("advancements.sculk_story.find_ancient_city.description", "Find an Ancient City");
        add("advancements.sculk_story.obtain_echo_shard.title", "Echoes of the Past");
        add("advancements.sculk_story.obtain_echo_shard.description", "Collect some Echo Shards from the Ancient City");
        add("advancements.sculk_story.summon_warden.title", "Ancient Guardian");
        add("advancements.sculk_story.summon_warden.description", "Summon the Warden, then run!");
    }

    private void addBlock(RegistryObject<Block> block) {
        String key = block.getId().getPath();
        add("block.deeperdarker." + key, convertToName(key));
    }

    private void addItem(RegistryObject<Item> item) {
        String key = item.getId().getPath();
        add("item.deeperdarker." + key, convertToName(key));
    }

    private String convertToName(String key) {
        StringBuilder builder = new StringBuilder(key.substring(0, 1).toUpperCase() + key.substring(1));
        for(int i = 1; i < builder.length(); i++) {
            if(builder.charAt(i) == '_') {
                builder.deleteCharAt(i);
                builder.replace(i, i + 1, " " + Character.toUpperCase(builder.charAt(i)));
            }
        }

        return builder.toString();
    }
}

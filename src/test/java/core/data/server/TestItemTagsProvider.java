package core.data.server;

import com.teamabnormals.blueprint.core.other.tags.BlueprintItemTags;
import core.BlueprintTest;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class TestItemTagsProvider extends ItemTagsProvider {

	public TestItemTagsProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
		super(generator, new BlockTagsProvider(generator, BlueprintTest.MOD_ID, fileHelper), BlueprintTest.MOD_ID, fileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(BlueprintItemTags.CHICKEN_FOOD).add(Items.HUSK_SPAWN_EGG);
		this.tag(BlueprintItemTags.PIG_FOOD).add(Items.CHIPPED_ANVIL);
		this.tag(BlueprintItemTags.STRIDER_FOOD).add(Items.MINECART);
		this.tag(BlueprintItemTags.STRIDER_TEMPT_ITEMS).add(Items.SPYGLASS);
		this.tag(BlueprintItemTags.OCELOT_FOOD).add(Items.DIRT, Items.DIRT_PATH);
		this.tag(BlueprintItemTags.CAT_FOOD).add(Items.APPLE);
	}
}
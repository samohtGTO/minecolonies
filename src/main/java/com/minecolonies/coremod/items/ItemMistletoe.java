package com.minecolonies.coremod.items;

import com.minecolonies.api.creativetab.ModCreativeTabs;

import static com.minecolonies.api.util.constant.Constants.STACKSIZE;

import net.minecraft.world.item.Item.Properties;

/**
 * Class describing the Ancient Tome item.
 */
public class ItemMistletoe extends AbstractItemMinecolonies
{
    /**
     * Sets the name, creative tab, and registers the Ancient Tome item.
     *
     * @param properties the properties.
     */
    public ItemMistletoe(final Properties properties)
    {
        super("mistletoe", properties.stacksTo(STACKSIZE).tab(ModCreativeTabs.MINECOLONIES));
    }
}

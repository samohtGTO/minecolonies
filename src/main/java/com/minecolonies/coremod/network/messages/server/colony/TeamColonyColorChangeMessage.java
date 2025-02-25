package com.minecolonies.coremod.network.messages.server.colony;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.event.ColonyInformationChangedEvent;
import com.minecolonies.coremod.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Message class which manages changing the team color of the colony.
 */
public class TeamColonyColorChangeMessage extends AbstractColonyServerMessage
{
    /**
     * The color to set.
     */
    private int colorOrdinal;

    /**
     * Empty public constructor.
     */
    public TeamColonyColorChangeMessage()
    {
        super();
    }

    /**
     * Creates object for the player to handle the color
     *
     * @param colorOrdinal the color to set.
     * @param building     view of the building to read data from
     */
    public TeamColonyColorChangeMessage(final int colorOrdinal, @NotNull final IBuildingView building)
    {
        super(building.getColony());
        this.colorOrdinal = colorOrdinal;
    }

    /**
     * Transformation from a byteStream to the variables.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void fromBytesOverride(@NotNull final FriendlyByteBuf buf)
    {

        colorOrdinal = buf.readInt();
    }

    /**
     * Transformation to a byteStream.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void toBytesOverride(@NotNull final FriendlyByteBuf buf)
    {
        buf.writeInt(colorOrdinal);
    }

    @Override
    protected void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony)
    {
        colony.setColonyColor(ChatFormatting.values()[colorOrdinal]);

        if (isLogicalServer)
        {
            MinecraftForge.EVENT_BUS.post(new ColonyInformationChangedEvent(colony, ColonyInformationChangedEvent.Type.TEAM_COLOR));
        }
    }
}

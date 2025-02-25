package com.minecolonies.coremod.client.gui;

import com.ldtteam.blockui.Alignment;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonImage;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.views.Box;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.interactionhandling.IInteractionResponseHandler;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.coremod.Network;
import com.minecolonies.coremod.client.gui.citizen.MainWindowCitizen;
import com.minecolonies.coremod.network.messages.server.colony.InteractionClose;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.util.constant.WindowConstants.*;

/**
 * BOWindow for the citizen.
 */
public class WindowInteraction extends AbstractWindowSkeleton
{
    /**
     * Response buttons default id, gets the response index added to the end 1 to x
     */
    public static final String BUTTON_RESPONSE_ID = "response_";

    /**
     * The citizenData.View object.
     */
    private final ICitizenDataView citizen;

    /**
     * The current interactions.
     */
    private final List<IInteractionResponseHandler> interactions;

    /**
     * The current interaction in the list.
     */
    private int currentInteraction = 0;

    /**
     * Constructor to initiate the citizen windows.
     *
     * @param citizen citizen to bind the window to.
     */
    public WindowInteraction(final ICitizenDataView citizen)
    {
        super(Constants.MOD_ID + INTERACTION_RESOURCE_SUFFIX, new MainWindowCitizen(citizen));
        this.citizen = citizen;
        this.interactions = new ArrayList<>(citizen.getOrderedInteractions());
    }

    /**
     * Called when the gui is opened by an player.
     */
    @Override
    public void onOpened()
    {
        super.onOpened();
        interactions.removeIf(interaction -> !interaction.isVisible(Minecraft.getInstance().level));
        setupInteraction();
    }

    /**
     * Setup the current interaction.
     */
    private void setupInteraction()
    {
        if (currentInteraction >= interactions.size())
        {
            close();
            return;
        }

        final IInteractionResponseHandler handler = interactions.get(currentInteraction);
        handler.onOpened(Minecraft.getInstance().player);
        final Box group = findPaneOfTypeByID(RESPONSE_BOX_ID, Box.class);
        group.getChildren().clear();
        int y = 0;
        int x = 0;
        final Text chatText = findPaneOfTypeByID(CHAT_LABEL_ID, Text.class);
        chatText.setTextAlignment(Alignment.TOP_LEFT);
        chatText.setAlignment(Alignment.TOP_LEFT);
        chatText.setText(Component.literal(citizen.getName() + ": " + handler.getInquiry(Minecraft.getInstance().player).getString()));
        int responseIndex = 1;
        for (final Component component : handler.getPossibleResponses())
        {
            final ButtonImage button = new ButtonImage();
            button.setImage(new ResourceLocation(Constants.MOD_ID, MEDIUM_SIZED_BUTTON_RES), false);
            button.setSize(BUTTON_LENGTH, BUTTON_HEIGHT);
            button.setColors(SLIGHTLY_BLUE);
            button.setPosition(x, y);
            button.setID(BUTTON_RESPONSE_ID + responseIndex);
            button.setTextRenderBox(BUTTON_LENGTH, BUTTON_HEIGHT);
            button.setTextAlignment(Alignment.MIDDLE);
            button.setText(component);
            group.addChild(button);
            button.setTextScale(Math.min(1, 20.0 / component.getString().length()));

            y += button.getHeight();
            if (y + button.getHeight() >= group.getHeight())
            {
                y = 0;
                x += BUTTON_HEIGHT + BUTTON_BUFFER + button.getWidth();
            }
            responseIndex++;
        }

        handler.onWindowOpened(this, citizen);
    }

    @Override
    public void onClosed()
    {
        super.onClosed();
        if (currentInteraction < interactions.size())
        {
            interactions.get(currentInteraction).onClosed();
            Network.getNetwork().sendToServer(new InteractionClose(citizen.getColonyId(), citizen.getId(), mc.level.dimension(), interactions.get(currentInteraction).getInquiry()));
        }
    }

    /**
     * Called when a button in the citizen has been clicked.
     *
     * @param button the clicked button.
     */
    @Override
    public void onButtonClicked(@NotNull final Button button)
    {
        if (!interactions.isEmpty())
        {
            final IInteractionResponseHandler handler = interactions.get(currentInteraction);
            try
            {
                if (handler.onClientResponseTriggered(Integer.parseInt(button.getID().replace("response_", "")) - 1, Minecraft.getInstance().player, citizen, this))
                {
                    currentInteraction++;
                    setupInteraction();
                    return;
                }
                setupInteraction();
            }
            catch (final Exception ex)
            {
                Log.getLogger().warn("Wrong button id of interaction.", ex);
            }
        }
    }
}

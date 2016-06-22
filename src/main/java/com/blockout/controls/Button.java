package com.blockout.controls;

import com.blockout.Pane;
import com.blockout.PaneParams;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/**
 * Base button class.
 * Has a handler for when the button is clicked.
 */
public class Button extends Pane
{
    private static final ResourceLocation soundClick = new ResourceLocation("gui.button.press");

    /**
     * Used for windows that have buttons and want to respond to clicks.
     */
    @FunctionalInterface
    public interface Handler
    {
        /**
         * Called when a button is clicked.
         *
         * @param button
         */
        void onButtonClicked(Button button);
    }

    protected Handler handler;
    protected String label;

    /**
     * Default constructor.
     */
    public Button()
    {
        super();
    }

    /**
     * Constructor used when loading from xml.
     *
     * @param params PaneParams from xml file.
     */
    public Button(PaneParams params)
    {
        super(params);
        label = params.getLocalizedStringAttribute("textContent", label);
    }

    /**
     * Button textContent getter.
     *
     * @return button textContent.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Button textContent setter.
     *
     * @param s new textContent.
     */
    public void setLabel(String s)
    {
        label = s;
    }

    /**
     * Set the button handler for this button.
     *
     * @param h The new handler.
     */
    public void setHandler(Handler h)
    {
        handler = h;
    }

    /**
     * Play click sound and find the proper handler.
     *
     * @param mx mouse X coordinate, relative to Pane's top-left
     * @param my mouse Y coordinate, relative to Pane's top-left
     */
    @Override
    public void handleClick(int mx, int my)
    {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(soundClick, 1.0F));

        Handler delegatedHandler = handler;

        if (delegatedHandler == null)
        {
            //  If we do not have a designated handler, find the closest ancestor that is a Handler
            for (Pane p = parent; p != null; p = p.getParent())
            {
                if (p instanceof Handler)
                {
                    delegatedHandler = (Handler)p;
                    break;
                }
            }
        }

        if (delegatedHandler != null)
        {
            delegatedHandler.onButtonClicked(this);
        }
    }
}

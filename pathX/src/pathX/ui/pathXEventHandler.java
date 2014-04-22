package pathX.ui;

import java.awt.event.KeyEvent;
import mini_game.Sprite;
import mini_game.Viewport;
import pathX.data.pathXDataModel;
import static pathX.pathXConstants.*;
/**
 *
 * @author Steven Liao
 */
public class pathXEventHandler 
{
    // THE PATHX GAME, IT PROVIDES ACCESS TO EVERYTHING
    private pathXMiniGame game;

    /**
     * Constructor, it just keeps the game for when the events happen.
     */
    public pathXEventHandler(pathXMiniGame initGame)
    {
        game = initGame;
    }
    
    /**
     * Called when the user clicks the close window button.
     */    
    public void respondToExitRequest()
    {
        // IF THE GAME IS STILL GOING ON, END IT AS A LOSS
        if (game.getDataModel().inProgress())
        {
            game.getDataModel().endGameAsLoss();
        }
        else if (game.isCurrentScreenState(GAME_SCREEN_STATE))
        {
            game.reset();
            game.switchToLevelSelectScreen();
            return;
        }
        // AND CLOSE THE ALL
        System.exit(0);        
    }
    
    /**
     * Called when the user clicks the Play button.
     */
    public void respondToPlayGameRequest()
    {
        if (game.getDataModel().inProgress())
        {
            game.switchToGameScreen();
        }
        else
        {
            game.switchToLevelSelectScreen();
        }
    }
    
    /**
     * Called when the user clicks on a level.
     */
    public void respondToLevelSelectRequest()
    {
        game.switchToGameScreen();
    }
    
    /**
     * Called when the user clicks on the reset button.
     */
    public void respondToResetGameRequest()
    {
        game.reset();
    }
    
    /**
     * Called when the user clicks on the settings button.
     */
    public void respondToSettingsRequest()
    {
        if (!game.isCurrentScreenState(SETTINGS_SCREEN_STATE))
            game.switchToSettingsScreen();
    }
    
    /**
     * Called when the user clicks on the help button.
     */
    public void respondToHelpRequest()
    {
        if (!game.isCurrentScreenState(HELP_SCREEN_STATE))
            game.switchToHelpScreen();
    }
    
    /**
     * Called when the user clicks on the Home button.
     */
    public void respondToHomeRequest()
    {
        game.switchToMenuScreen();
    }
    
    public void respondToMuteRequest(Sprite muteButton)
    {
        if (!muteButton.getState().equals(pathXTileState.SELECTED_STATE.toString()))
        {
            muteButton.setState(pathXTileState.SELECTED_STATE.toString());
        } else
        {
            muteButton.setState(pathXTileState.VISIBLE_STATE.toString());
        }
    }
    
    /**
     * Called when the user clicks on the scroll buttons. This will scroll 
     * the map according to the direction being clicked.
     * 
     * @param direction 
     */
    public void scroll(String direction)
    {
        if (game.isCurrentScreenState(LEVEL_SELECT_SCREEN_STATE))
        {
            Viewport viewport = game.getMapViewport();
            Sprite map = game.getGUIDecor().get(MAP_TYPE);        
    //        viewport.setNorthPanelHeight(80);
    //        viewport.setGameWorldSize((int) map.getAABBwidth(), (int) map.getAABBheight());
    //        viewport.setViewportSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    //        viewport.updateViewportBoundaries();
    //        viewport.initViewportMargins();
            Sprite node = game.getGUIButtons().get(LOCATION_BUTTON_TYPE);
            switch (direction)
            {
                case "UP":
                {
                    //if (map.getY() + WINDOW_HEIGHT - map.getAABBheight() < WINDOW_HEIGHT - map.getAABBheight() + MAP_Y)
                    if (viewport.getMinViewportY() < viewport.getViewportY())
                    {
    //                    map.setY(map.getY() + SCROLL_PIXELS);
                        viewport.scroll(0, -SCROLL_PIXELS);
                        node.setY(node.getY() + SCROLL_PIXELS);
                    }
                } break;

                case "DOWN":
                {
    //                if (map.getY() >= WINDOW_HEIGHT - map.getAABBheight() - 23)
                    if (viewport.getMaxViewportY() > viewport.getViewportY())
                    {
    //                    map.setY(map.getY() - SCROLL_PIXELS);
                        viewport.scroll(0, SCROLL_PIXELS);
                        node.setY(node.getY() - SCROLL_PIXELS);
                    }
                } break;

                case "LEFT":
                {
    //                if (map.getX() + WINDOW_WIDTH - map.getAABBwidth() < WINDOW_WIDTH - map.getAABBwidth())
                    if (viewport.getMinViewportX() < viewport.getViewportX())
                    {
    //                    map.setX(map.getX() + SCROLL_PIXELS);
                        viewport.scroll(-SCROLL_PIXELS, 0);
                        node.setX(node.getX() + SCROLL_PIXELS);
                    }
                } break;

                case "RIGHT":
                {
    //                if (map.getX() >= WINDOW_WIDTH - map.getAABBwidth())
                    if (viewport.getMaxViewportX() > viewport.getViewportX())
                    {
    //                    map.setX(map.getX() - SCROLL_PIXELS);
                        viewport.scroll(SCROLL_PIXELS, 0);
                        node.setX(node.getX() - SCROLL_PIXELS);
                    }
                } break;
            }
        }
    }
    
    /**
     * Called when the user presses a key on the keyboard.
     */    
    public void respondToKeyPress(int keyCode)
    {
        
        if (game.isCurrentScreenState(LEVEL_SELECT_SCREEN_STATE))
        {
            Sprite map = game.getGUIDecor().get(MAP_TYPE);
            
            switch (keyCode)
            {
                // SCROLL UP
                case KeyEvent.VK_UP:
                {
                    scroll("UP");
                } break;
                    
                // SCROLL DOWN
                case KeyEvent.VK_DOWN:
                {
                    scroll("DOWN");
                } break;
                    
                // SCROLL LEFT
                case KeyEvent.VK_LEFT:
                {
                    scroll("LEFT");
                } break;
                    
                // SCROLL RIGHT
                case KeyEvent.VK_RIGHT:
                {
                    scroll("RIGHT");
                } break;
            }
        }
    }
}

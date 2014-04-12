package pathX.ui;

import java.awt.event.KeyEvent;
import mini_game.Sprite;
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
    
    public void respondToResetGameRequest()
    {
        
    }
    
    
    public void respondToSettingsRequest()
    {
        game.switchToSettingsScreen();
    }
    
    
    public void respondToHelpRequest()
    {
        game.switchToHelpScreen();
    }
    
    public void respondToHomeRequest()
    {
        game.switchToMenuScreen();
    }
    
    public void scroll(String direction)
    {
        Sprite map = game.getGUIDecor().get(MAP_TYPE);
        Sprite node = game.getGUIButtons().get(LOCATION_BUTTON_TYPE);
        switch (direction)
        {
            case "UP":
            {
                if (map.getY() + WINDOW_HEIGHT - map.getAABBheight() < WINDOW_HEIGHT - map.getAABBheight() + MAP_Y)
                {
                    map.setY(map.getY() + SCROLL_PIXELS);
                    node.setY(node.getY() + SCROLL_PIXELS);
                }
            } break;
                
            case "DOWN":
            {
                if (map.getY() >= WINDOW_HEIGHT - map.getAABBheight() - 23)
                {
                    map.setY(map.getY() - SCROLL_PIXELS);
                    node.setY(node.getY() - SCROLL_PIXELS);
                }
            } break;
                
            case "LEFT":
            {
                if (map.getX() + WINDOW_WIDTH - map.getAABBwidth() < WINDOW_WIDTH - map.getAABBwidth())
                {
                    map.setX(map.getX() + SCROLL_PIXELS);
                    node.setX(node.getX() + SCROLL_PIXELS);
                }
            } break;
                
            case "RIGHT":
            {
                if (map.getX() >= WINDOW_WIDTH - map.getAABBwidth())
                {
                    map.setX(map.getX() - SCROLL_PIXELS);
                    node.setX(node.getX() - SCROLL_PIXELS);
                }
            } break;
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

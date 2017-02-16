/*
 * This file is a part of NaVisuLeapMotion
 * Copyright (C) 2017 Di Falco Nicola
 *
 * NaVisuLeapMotion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NaVisuLeapMotion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bzh.terrevirtuelle.navisuleapmotion.util;

import bzh.terrevirtuelle.navisuleapmotion.views.PrimaryPresenter;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;

/**
 * SimpleMenu class to represent the Menu item on the PrimaryPresenter
 *
 * @author Di Falco Nicola
 */
public class SimpleMenu {

    /**
     * The image associated to the current Menu
     */
    private Image image;
    
    /**
     * The parent of the current Menu (if it's a root, its parent is null)
     * The same goes for Back Menu (same instance is used multiple time, so it 
     * has technically multiple Parents
     */
    private SimpleMenu parent;
    
    /**
     * Lists of direct sub-menus of the current Menu.
     * If it doesn't have sub-menu, it's null
     */
    private List<SimpleMenu> subMenu;
    
    /**
     * Action that shall be triggered by this Menu.
     * If the Menu only opens a sub-menu, its action is null
     */
    private String action;
    
    /**
     * Base directory for images
     */
    private final String IMGREP = "bzh/terrevirtuelle/navisuleapmotion/views/img/";
    
    /**
     * PrimaryPresenter linked to the Menu (really usefull?)
     */
    private final PrimaryPresenter primaryPresenter;

    /**
     * Default Constructor. Image, Parent and Sub-Menus are set to Null.
     * The action is set to a default state
     * 
     * @param primaryPresenter The PrimaryPresenter linked to the Menu
     */
    public SimpleMenu(PrimaryPresenter primaryPresenter) {
        this.image = null;
        this.parent = null;
        this.subMenu = null;
        this.action = "No actions Yet";
        this.primaryPresenter = primaryPresenter;
    }

    /**
     * Constructor. Parent and Sub-Menus are set to Null.
     * The action is set to a default state
     * 
     * @param imageName Image's name representing the Menu
     * @param primaryPresenter The PrimaryPresenter linked to the Menu
     */
    public SimpleMenu(String imageName, PrimaryPresenter primaryPresenter) {
        this.image = new Image(IMGREP + imageName);
        this.parent = null;
        this.subMenu = null;
        this.action = "No actions Yet";
        this.primaryPresenter = primaryPresenter;
    }

    /**
     * Constructor. Sub-Menus are set to Null.
     * The action is set to a default state
     * 
     * @param imageName Image's name representing the Menu
     * @param parent The menu's parent
     * @param primaryPresenter The PrimaryPresenter linked to the Menu
     */
    public SimpleMenu(String imageName, SimpleMenu parent, PrimaryPresenter primaryPresenter) {
        this.image = new Image(IMGREP + imageName);
        this.parent = parent;
        this.subMenu = null;
        this.action = "No actions Yet";
        this.primaryPresenter = primaryPresenter;
    }

    /**
     * Constructor. Sub-Menus are set to Null
     * 
     * @param imageName Image's name representing the Menu
     * @param parent The menu's parent
     * @param action The menu's action
     * @param primaryPresenter The PrimaryPresenter linked to the Menu
     */
    public SimpleMenu(String imageName, SimpleMenu parent, String action, PrimaryPresenter primaryPresenter) {
        this.image = new Image(IMGREP + imageName);
        this.parent = parent;
        this.subMenu = null;
        this.action = action;
        this.primaryPresenter = primaryPresenter;
    }

    /**
     * Gets the Image's path
     * 
     * @return The Image's path
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the new Image's path
     * 
     * @param imageName The Image's name (the prefix "bzh/terrevirtuelle/navisuleapmotion/views/img/"
     * will be automatically included
     */
    public void setImage(String imageName) {
        this.image = new Image(IMGREP + imageName);
    }

    /**
     * Gets the menu Parent
     * 
     * @return The Parent
     */
    public SimpleMenu getParent() {
        return parent;
    }

    /**
     * Sets the Parent Menu
     * 
     * @param parent The new SimpleMenu parent
     */
    public void setParent(SimpleMenu parent) {
        this.parent = parent;
    }

    /**
     * Gets list of direct sub-menu for the current SimpleMenu
     * 
     * @return The list of sub-menu
     */
    public List<SimpleMenu> getSubMenu() {
        return subMenu;
    }

    /**
     * Sets the new sub-menu List. Sets the action to null (a Menu cannot have 
     * an action and sub-menus)
     * 
     * @param subMenu The new list of sub-menus
     */
    public void setSubMenu(List<SimpleMenu> subMenu) {
        action = null;
        this.subMenu = subMenu;
    }

    /**
     * Adds a sub-menu to the currently existing sub-menu list. Sets the action 
     * to null if not already the case (a Menu cannot have an action and sub-menus)
     * 
     * @param menu The SimpleMenu to add to the list
     */
    public void addSubMenu(SimpleMenu menu) {
        if (action != null) {
            action = null;
        }
        if (this.subMenu == null) {
            this.subMenu = new ArrayList<>();
        }
        this.subMenu.add(menu);
    }

    /**
     * Gets the action associated to the Menu
     * 
     * @return The menu's action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the new menu's action. Sets the sub-menu list to null (a Menu cannot 
     * have an action and sub-menus)
     * 
     * @param action The new action value
     */
    public void setAction(String action) {
        this.subMenu = null;
        this.action = action;
    }
}

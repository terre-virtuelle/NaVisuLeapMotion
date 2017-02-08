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
 *
 * @author Di Falco Nicola
 */
public class SimpleMenu {

        private Image image;
        private SimpleMenu parent;
        private List<SimpleMenu> subMenu;
        private String action;
        private final String IMGREP = "bzh/terrevirtuelle/navisuleapmotion/views/img/";
        private final PrimaryPresenter primaryPresenter;

        public SimpleMenu(PrimaryPresenter primaryPresenter) {
            this.image = null;
            this.parent = null;
            this.subMenu = null;
            this.action = "No actions Yet";
            this.primaryPresenter = primaryPresenter;
        }

        public SimpleMenu(String imageName, PrimaryPresenter primaryPresenter) {
            this.image = new Image(IMGREP + imageName);
            this.parent = null;
            this.subMenu = null;
            this.action = "No actions Yet";
            this.primaryPresenter = primaryPresenter;
        }

        public SimpleMenu(String imageName, SimpleMenu parent, PrimaryPresenter primaryPresenter) {
            this.image = new Image(IMGREP + imageName);
            this.parent = parent;
            this.subMenu = null;
            this.action = "No actions Yet";
            this.primaryPresenter = primaryPresenter;
        }

        public SimpleMenu(String imageName, SimpleMenu parent, String action, PrimaryPresenter primaryPresenter) {
            this.image = new Image(IMGREP + imageName);
            this.parent = parent;
            this.subMenu = null;
            this.action = action;
            this.primaryPresenter = primaryPresenter;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(String imageName) {
            this.image = new Image(IMGREP + imageName);
        }

        public SimpleMenu getParent() {
            return parent;
        }

        public void setParent(SimpleMenu parent) {
            this.parent = parent;
        }

        public List<SimpleMenu> getSubMenu() {
            return subMenu;
        }

        public void setSubMenu(List<SimpleMenu> subMenu) {
            action = null;
            this.subMenu = subMenu;
        }

        public void addSubMenu(SimpleMenu menu) {
            if (action != null) {
                action = null;
            }
            if (this.subMenu == null) {
                this.subMenu = new ArrayList<>();
            }
            this.subMenu.add(menu);
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }
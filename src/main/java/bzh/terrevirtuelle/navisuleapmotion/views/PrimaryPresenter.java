/*
 * This file is a part of NaVisuLeapMotion
 * Copyright (C) 2016 Di Falco Nicola
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
package bzh.terrevirtuelle.navisuleapmotion.views;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import bzh.terrevirtuelle.navisuleapmotion.NaVisuLeapMotion;
import static bzh.terrevirtuelle.navisuleapmotion.NaVisuLeapMotion.WSC;
import bzh.terrevirtuelle.navisuleapmotion.server.Server;
import bzh.terrevirtuelle.navisuleapmotion.util.ARgeoData;
import bzh.terrevirtuelle.navisuleapmotion.util.ArCommand;
import bzh.terrevirtuelle.navisuleapmotion.util.Toast;
import bzh.terrevirtuelle.navisuleapmotion.util.WSClient;
import com.sun.scenario.effect.Effect;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.naming.TimeLimitExceededException;
import org.java_websocket.drafts.Draft_10;

/**
 *
 * @author Di Falco Nicola
 */
public class PrimaryPresenter {
    
    private final String IPADDRESS_PATTERN =
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @FXML
    private View primary;

    @FXML
    private GridPane datagrid;
    
    @FXML
    private Label latval;
    
    @FXML
    private Label lonval;
    
    @FXML
    private Label nameval;
    
    @FXML
    private TextField ip;
    
    @FXML
    private Button button;
    
    @FXML
    private Label serverText;
    
    @FXML
    private Label data;
    
    @FXML
    private Button route0;
    
    @FXML
    private Button cServer;
    
    @FXML
    private Label response;
    
    @FXML
    private ImageView menu1;
    @FXML
    private ImageView menu2;
    @FXML
    private ImageView menu3;
    @FXML
    private ImageView menu4;
    @FXML
    private ImageView menu5;
    @FXML
    private ImageView menu6;
    @FXML
    private ImageView menu7;
    @FXML
    private ImageView menu8;
    @FXML
    private ImageView menu9;
    @FXML
    private ImageView menu10;

    int port = 8899;
    private Server server;
    private boolean isServerRunning = false;
    private final String IMGREP = "bzh/terrevirtuelle/navisuleapmotion/views/img/";
    
    private List<ImageView> menuHolder;
    private List<SimpleMenu> menuList;
    
    private SimpleMenu currMenu;
    private int currIndex;
    private int maxIndex;
    
    public void initialize() {
        primary.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        MobileApplication.getInstance().showLayer(NaVisuLeapMotion.MENU_LAYER)));
                appBar.setTitleText("Primary");
                appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button(e -> 
                        System.out.println("Search")));
            }
        });
        
        menuHolder = new ArrayList<>();
        menuHolder.add(this.menu1);
        menuHolder.add(this.menu2);
        menuHolder.add(this.menu3);
        menuHolder.add(this.menu4);
        menuHolder.add(this.menu5);
        menuHolder.add(this.menu6);
        menuHolder.add(this.menu7);
        menuHolder.add(this.menu8);
        menuHolder.add(this.menu9);
        menuHolder.add(this.menu10);
        
        menuHolder.forEach( (item) -> ((ImageView)item).setVisible(false));
        
        this.menuList = new ArrayList<>();
        initMenu();
    }

    @FXML
    void buttonClick(ActionEvent e) {
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        
        if(WSC != null)
            WSC.close();
        
        if(ip.getText().isEmpty())
            initWSC();
        else {
            Matcher matcher = pattern.matcher(ip.getText());
            if(matcher.matches())
                initWSC(ip.getText());
            else {
                String toastMsg = "Wrong IP format";
                int toastMsgTime = 2000; //3.5 seconds
                int fadeInTime = 500; //0.5 seconds
                int fadeOutTime= 500; //0.5 seconds
                Toast.makeText( (Stage) primary.getScene().getWindow(), toastMsg, toastMsgTime, fadeInTime, fadeOutTime);
                return;
            }
                
        }
        
        while(WSC == null){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(PrimaryPresenter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.data.setText("Test");
        
        route0.setDisable(false);
        datagrid.setVisible(true);
        cServer.setDisable(false);
    }
    
    @FXML
    void buttonRoute0(ActionEvent e) {
        try {
            WSC.ws_request();
        } catch (NotYetConnectedException ex) {
            String toastMsg = "Connection is not initialized (maybe check IP)";
            int toastMsgTime = 2000; //3.5 seconds
            int fadeInTime = 500; //0.5 seconds
            int fadeOutTime= 500; //0.5 seconds
            Toast.makeText( (Stage) primary.getScene().getWindow(), toastMsg, toastMsgTime, fadeInTime, fadeOutTime);
            return;
        } catch (TimeLimitExceededException ex) {
            int toastMsgTime = 2000; //3.5 seconds
            int fadeInTime = 500; //0.5 seconds
            int fadeOutTime= 500; //0.5 seconds
            Toast.makeText( (Stage) primary.getScene().getWindow(), ex.getExplanation(), toastMsgTime, fadeInTime, fadeOutTime);
            return;
        }
        ARgeoData data2 = WSC.getStatic_ARgeoDataArray().get(0);
        Logger.getLogger(WSClient.class.getName()).log(Level.INFO, "Data: "+data2.toString());
        latval.setText(String.valueOf(data2.getLat()));
        lonval.setText(String.valueOf(data2.getLon()));
        nameval.setText(data2.getName());
    }
    
    @FXML
    void serverClick(ActionEvent e) {
        if(isServerRunning){
            try {
                WSC.sendClose();
            } catch (NotYetConnectedException | UnknownHostException ex) {
                Logger.getLogger(PrimaryPresenter.class.getName()).log(Level.SEVERE, null, ex);
            }
            server.StopServer();
            server = null;
            serverText.setText("The  server is closed.");
            cServer.setText("Deploy Server");
            isServerRunning = !isServerRunning;
        }else{
            server = new Server(port, this);
            serverText.setText("The  server is started.");
            
            try {
                WSC.sendIP();
                this.data.setText(WSClient.getIPAddress(true));
            } catch (UnknownHostException | NotYetConnectedException ex) {
                Logger.getLogger(PrimaryPresenter.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            cServer.setText("Close Server");
            isServerRunning = !isServerRunning;
        }
    }
    
    public static void initWSC(){
        Thread thread0 =new Thread((Runnable) () -> {
            System.out.println("Current Time =" + new Date());
            try {
                System.out.println("*********** Connecting *************");
                WSC = new WSClient( new URI( "ws://localhost:8787/navigation" ), new Draft_10() ); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
                WSC.connect();
            } catch (URISyntaxException ex) {
                Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        thread0.start();
    }
    
    
    public static void initWSC(String ip){
        Thread thread0 =new Thread((Runnable) () -> {
            System.out.println("Current Time =" + new Date());
            try {
                System.out.println("*********** Connecting *************");
                WSC = new WSClient( new URI( "ws://" + ip + ":8787/navigation" ), new Draft_10() ); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
                WSC.connect();
            } catch (URISyntaxException ex) {
                Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        thread0.start();
    }
    
    private void initMenu(){
        SimpleMenu io =  new SimpleMenu("system.png");
        SimpleMenu tools = new SimpleMenu("tools.png");
        SimpleMenu charts =  new SimpleMenu("charts.png");
        SimpleMenu tide =  new SimpleMenu("tides.png");
        SimpleMenu meteo =  new SimpleMenu("meteo.png");
        SimpleMenu instr =  new SimpleMenu("instruments.png");
        SimpleMenu nav =  new SimpleMenu("navigation.png");
        SimpleMenu book =  new SimpleMenu("book.png");
        
        SimpleMenu earth = new SimpleMenu("earth.png", tools);
        SimpleMenu devices = new SimpleMenu("devices.png", tools);
        
        SimpleMenu models = new SimpleMenu("models.png", earth);
        SimpleMenu servermenu = new SimpleMenu("server.png", devices);
        SimpleMenu lmotion = new SimpleMenu("leapMotion.png", devices);
        
        
        SimpleMenu bathy = new SimpleMenu("bathy.png", models);
        bathy.setAction("Activate Bathy");
        SimpleMenu noBathy = new SimpleMenu("noBathy.png", models);
        noBathy.setAction("Desactivate Bathy");
        SimpleMenu elevation = new SimpleMenu("elevation.png", models);
        elevation.setAction("Activate Elevation");
        SimpleMenu noElevation = new SimpleMenu("noElevation.png", servermenu);
        noElevation.setAction("Desactivate Elevation");
        SimpleMenu options = new SimpleMenu("options.png", servermenu);
        options.setAction("Open Server Config");
        SimpleMenu lmotionOn = new SimpleMenu("leapMotionOn.png", lmotion);
        lmotionOn.setAction("Enable leapmotion");
        SimpleMenu lmotionOff = new SimpleMenu("leapMotionOff.png", lmotion);
        lmotionOff.setAction("Disable leapmotion");
        
        models.addSubMenu(bathy);
        models.addSubMenu(noBathy);
        models.addSubMenu(elevation);
        models.addSubMenu(noElevation);
        
        servermenu.addSubMenu(options);
        
        lmotion.addSubMenu(lmotionOn);
        lmotion.addSubMenu(lmotionOff);
        
        devices.addSubMenu(servermenu);
        devices.addSubMenu(lmotion);
        
        earth.addSubMenu(models);
        
        tools.addSubMenu(earth);
        tools.addSubMenu(devices);
                
        this.menuList.add(io);
        this.menuList.add(tools);
        this.menuList.add(charts);
        this.menuList.add(tide);
        this.menuList.add(meteo);
        this.menuList.add(instr);
        this.menuList.add(nav);
        this.menuList.add(book);
    }

        
    public void displayMessage(String msg){
        this.response.setText(msg);
    }
    /*
    public void displayImage(String msg){
        System.out.println(msg);
        int num = Integer.decode(msg);
        String url = IMGREP + ((num % 2 == 1)?"1.png":"2.png");
        Image image = new Image(url);
        this.img.setImage(image);
    } */
    
    public void handleCmd(String cmd){
        
        switch(cmd){
            case "openMenu":
                
                menuList.forEach( (menu) -> {ImageView tmp = this.menuHolder.get(menuList.indexOf(menu)); 
                                             tmp.setImage(menu.getImage());
                                             tmp.setVisible(true);
                });

                this.currIndex = 0;
                this.maxIndex = menuList.size()-1;
                this.currMenu = menuList.get(currIndex);
                
                selected();  
                break;
                
            case "closeMenu":
                this.menuHolder.get(this.currIndex).setEffect(null);
                this.currIndex = 0;
                this.maxIndex = 0;
                this.currMenu = null;
                
                menuHolder.forEach( (menuholder) -> ((ImageView)menuholder).setVisible(false) );
                break;
            
            case "selectMenu":
                if(currMenu.getAction() != null)
                    displayMessage(currMenu.getAction());
                else
                    openMenu();
                break;
            
            case "leftMenu":
                this.menuHolder.get(this.currIndex).setEffect(null);
                this.currIndex = (this.currIndex == 0)?(this.maxIndex):(this.currIndex -1);
                if(this.currMenu.getParent() == null)
                    this.currMenu = this.menuList.get(this.currIndex);
                else
                    this.currMenu = this.currMenu.getParent().getSubMenu().get(this.currIndex);
                
                selected();
                break;
                
            case "rightMenu":
                this.menuHolder.get(this.currIndex).setEffect(null);
                this.currIndex  = (this.currIndex  == this.maxIndex)?(0):(this.currIndex +1);
                if(this.currMenu.getParent() == null)
                    this.currMenu = this.menuList.get(this.currIndex);
                else
                    this.currMenu = this.currMenu.getParent().getSubMenu().get(this.currIndex);
                
                selected();
                break;
            
            default:
                break;
        }
    }
    
    private void openMenu(){
        menuHolder.forEach( (menuholder) -> ((ImageView)menuholder).setVisible(false) );
        this.currMenu.getSubMenu().forEach( (menu) -> {ImageView tmp = this.menuHolder.get(this.currMenu.getSubMenu().indexOf(menu)); 
                                             tmp.setImage(menu.getImage());
                                             tmp.setVisible(true);
        });

        this.currIndex = 0;
        this.maxIndex = this.currMenu.getSubMenu().size()-1;
        this.currMenu = this.currMenu.getSubMenu().get(currIndex);
                
        selected();  
    }
    
    private void selected(){
        int depth = 50; //Setting the uniform variable for the glow width and height
        DropShadow borderGlow= new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.GRAY);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);
        this.menuHolder.get(this.currIndex).setEffect(borderGlow);
    }
    
    private class SimpleMenu{
        private Image image;
        private SimpleMenu parent;
        private List<SimpleMenu> subMenu;
        private String action;
        
        public SimpleMenu(){
            this.image = null;
            this.parent = null;
            this.subMenu = null;
            this.action = "No actions Yet";
        }
        
        public SimpleMenu(String imageName){
            this.image = new Image(IMGREP + imageName);
            this.parent = null;
            this.subMenu = null;
            this.action = "No actions Yet";
        }
        
        public SimpleMenu(String imageName, SimpleMenu parent){
            this.image = new Image(IMGREP + imageName);
            this.parent = parent;
            this.subMenu = null;
            this.action = "No actions Yet";
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
            if(action != null)
                action = null;
            this.subMenu = subMenu;
        }
        
        public void addSubMenu(SimpleMenu menu){
            if(action != null)
                action = null;
            if(this.subMenu == null)
                this.subMenu = new ArrayList<SimpleMenu>();
            this.subMenu.add(menu);
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }
    
}

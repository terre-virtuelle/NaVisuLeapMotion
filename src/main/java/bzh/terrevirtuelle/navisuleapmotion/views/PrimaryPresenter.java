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

    int port = 8899;
    private Server server;
    private boolean isServerRunning = false;
    private final String IMGREP = "bzh/terrevirtuelle/navisuleapmotion/views/img/";
    private List<ImageView> lmenu;
    private ImageView currMenu;
    
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
        
        this.menu1.setVisible(false);
        this.menu2.setVisible(false);
        this.menu3.setVisible(false);
        this.menu4.setVisible(false);

        this.menu1.setImage(new Image(IMGREP + "1.png"));
        this.menu2.setImage(new Image(IMGREP + "2.png"));
        this.menu3.setImage(new Image(IMGREP + "3.png"));
        this.menu4.setImage(new Image(IMGREP + "4.png"));
        
        this.lmenu = new ArrayList<>();
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
        
        /**
         * Recherches pour changer le texte en fonction de celui de la réponse.
         * Plus tard, on devra afficher les images
         */
//        String data = Server.getStatic_Data().get(0);
        this.data.setText("Test");
        
        route0.setDisable(false);
        datagrid.setVisible(true);
        cServer.setDisable(false);
        //Possibly try to display a loading icon or something while trying to connect to NaVisu
        //MobileApplication.getInstance().switchView(SECONDARY_VIEW);
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
    
    public void handleCmd(ArCommand arcmd){
        String cmd = arcmd.getCmd();
        
        switch(cmd){
            case "openMenu":
                this.lmenu.clear();
                this.lmenu.add(this.menu1);
                this.lmenu.add(this.menu2);
                this.lmenu.add(this.menu3);
                this.lmenu.add(this.menu4);
                this.currMenu = this.menu1;
                
                selected();
                
                this.menu1.setVisible(true);
                this.menu2.setVisible(true);
                this.menu3.setVisible(true);
                this.menu4.setVisible(true);      
                break;
                
            case "closeMenu":
                this.currMenu.setEffect(null);
                this.currMenu = null;
                this.lmenu.clear();
                
                this.menu1.setVisible(false);
                this.menu2.setVisible(false);
                this.menu3.setVisible(false);
                this.menu4.setVisible(false);
                break;
            
            case "selectMenu":
                this.displayMessage("Selected Menu: "+this.currMenu.toString());
                break;
            
            case "leftMenu":
                int idx = this.lmenu.indexOf(this.currMenu);
                idx = (idx == 0)?(this.lmenu.size()-1):(idx-1);
                
                this.currMenu.setEffect(null);
                this.currMenu = this.lmenu.get(idx);
                selected();
                break;
                
            case "rightMenu":
                int idx2 = this.lmenu.indexOf(this.currMenu);
                idx2 = (idx2 == (this.lmenu.size()-1))?(0):(idx2+1);
                
                this.currMenu.setEffect(null);
                this.currMenu = this.lmenu.get(idx2);
                selected();
                break;
            
            default:
                break;
        }
    }
    
    private void selected(){
        int depth = 50; //Setting the uniform variable for the glow width and height
        DropShadow borderGlow= new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.GRAY);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);
        this.currMenu.setEffect(borderGlow);
    }
    
}

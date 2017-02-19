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
import bzh.terrevirtuelle.navisuleapmotion.util.SimpleMenu;
import bzh.terrevirtuelle.navisuleapmotion.util.WSClient;
import com.gluonhq.charm.glisten.control.Toast;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javax.naming.TimeLimitExceededException;
import org.java_websocket.drafts.Draft_10;

/**
 * PrimaryPresenter class, represents the main page of the app
 * 
 * @author Di Falco Nicola
 */
public class PrimaryPresenter {

    /**
     * Regex expression for an IPv4 address
     */
    private final String IPADDRESS_PATTERN
            = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

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

    /**
     * Port used for the Server
     */
    int port = 8899;
    
    /**
     * Server instance
     */
    private Server server;
    
    /**
     * Is the server running
     */
    private boolean isServerRunning = false;
    
    /**
     * Is the menu open
     */
    private boolean isMenuOpen = false;
    
    /**
     * List of ImageView which will hold images of the current opened menu
     */
    private List<ImageView> menuHolder = null;
    
    /**
     * List of all root menus (with no parents)
     */
    private List<SimpleMenu> menuList;

    /**
     * Used Mainly for back action, as Back menu doesn't have any parent
     */
    private SimpleMenu currParent;
    
    /**
     * Current selected Menu
     */
    private SimpleMenu currMenu;
    
    /**
     * Current index of the Menu. ie this.currParent.getSubMenu().get(currIndex)
     */
    private int currIndex;
    
    /**
     * Max index for the current Menu list. ie 
     *  maxIndex = this.currParent.getSubMenu().size()-1
     */
    private int maxIndex;

    /**
     * Initialization, display is created and the menuList and menuHolder are
     * instanciated
     */
    public void initialize() {
        primary.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e
                        -> MobileApplication.getInstance().showLayer(NaVisuLeapMotion.MENU_LAYER)));
                appBar.setTitleText("Primary");
                appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button(e
                        -> System.out.println("Search")));
            }
        });

        initMenu();
    }

    /**
     * Action performed on click on button "Connect to NaVisu". The IP field is
     * checked (correct format), the WSClient will be instanciated according to
     * the provided IPv4 (empty is localhost).
     * Once done, all buttons are enabled
     * 
     * @param e ActionEvent that have triggered this function
     */
    @FXML
    void buttonClick(ActionEvent e) {
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

        if (WSC != null) {
            WSC.close();
        }

        if (ip.getText().isEmpty()) {
            initWSC();
        } else {
            Matcher matcher = pattern.matcher(ip.getText());
            if (matcher.matches()) {
                initWSC(ip.getText());
            } else {
                Toast toast = new Toast("Wrong IP format", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

        }

        while (WSC == null) {
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

    /**
     * Action performed on click on button "Route 0". A request to the NaVisu
     * server will be performed through the WSClient
     * 
     * @param e ActionEvent that have triggered this function
     */
    @FXML
    void buttonRoute0(ActionEvent e) {
        try {
            WSC.ws_request();
        } catch (NotYetConnectedException ex) {
            String toastMsg = "Connection is not initialized (maybe check IP)";
            Toast toast = new Toast(toastMsg, Toast.LENGTH_SHORT);
            toast.show();
            return;
        } catch (TimeLimitExceededException ex) {
            Toast toast = new Toast(ex.getExplanation(), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        ARgeoData data2 = WSC.getStatic_ARgeoDataArray().get(0);
        Logger.getLogger(WSClient.class.getName()).log(Level.INFO, "Data: " + data2.toString());
        latval.setText(String.valueOf(data2.getLat()));
        lonval.setText(String.valueOf(data2.getLon()));
        nameval.setText(data2.getName());
    }

    /**
     * Action performed on click on button "Deploy (close) Server". The server 
     * will deploy/stop according to its current state. If it will be deployed,
     * once done, the WSClient will send its IP to NaVisu server so that a NaVisu
     * client can connect to the newly deployed RA server
     * 
     * @param e ActionEvent that have triggered this function
     */
    @FXML
    void serverClick(ActionEvent e) {
        if (isServerRunning) {
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
        } else {
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

    /**
     * Initialization of WSClient (it will try to connect to a localhost server)
     */
    public static void initWSC() {
        Thread thread0 = new Thread((Runnable) () -> {
            System.out.println("Current Time =" + new Date());
            try {
                System.out.println("*********** Connecting *************");
                WSC = new WSClient(new URI("ws://localhost:8787/navigation"), new Draft_10()); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
                WSC.connect();
            } catch (URISyntaxException ex) {
                Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        thread0.start();
    }

    /**
     * Initialization of WSClient (it will try to connect to the server whose IP
     * has been provided)
     * 
     * @param ip The server's IP
     */
    public static void initWSC(String ip) {
        Thread thread0 = new Thread((Runnable) () -> {
            System.out.println("Current Time =" + new Date());
            try {
                System.out.println("*********** Connecting *************");
                WSC = new WSClient(new URI("ws://" + ip + ":8787/navigation"), new Draft_10()); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
                WSC.connect();
            } catch (URISyntaxException ex) {
                Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        thread0.start();
    }

    /**
     * Initialization of menuList
     */
    private void initMenu() {
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

        for(ImageView item : this.menuHolder){
            item.setVisible(false);
        }
        
        this.menuList = new ArrayList<>();
        /**
         * Main Menus
         */   
        SimpleMenu io = new SimpleMenu("system.png", this);
        SimpleMenu tools = new SimpleMenu("tools.png", this);
        SimpleMenu charts = new SimpleMenu("charts.png", this);
        SimpleMenu tide = new SimpleMenu("tides.png", this);
        SimpleMenu meteo = new SimpleMenu("meteo.png", this);
        SimpleMenu instr = new SimpleMenu("instruments.png", this);
        SimpleMenu navigation = new SimpleMenu("navigation.png", this);
        SimpleMenu book = new SimpleMenu("book.png", this);

        /**
         * Back Menu
         */
        SimpleMenu back = new SimpleMenu("back.png", this);
        back.setAction("GoBack");
        
        /**
         * I/O Sub-menus
         */
        SimpleMenu IOdata = new SimpleMenu("data.png", io, this);
        SimpleMenu simu = new SimpleMenu("simu.png", io, this);
        
        SimpleMenu files = new SimpleMenu("files.png", IOdata, this);
        SimpleMenu files2 = new SimpleMenu("files.png", simu, this);
        
        SimpleMenu wms = new SimpleMenu("wms.png", files, "Open WMS sites", this);
        SimpleMenu collada = new SimpleMenu("collada.png", files, "Open Collada Files", this);
        SimpleMenu kml = new SimpleMenu("kml.png", files, "Open KML Files", this);
        SimpleMenu shapeFile = new SimpleMenu("shapefile.png", files, "Open ShapeFile Files", this);
        
        SimpleMenu nmeaOn = new SimpleMenu("nmeaOn.png", files2, "Enable NMEA", this);
        SimpleMenu nmeaOff = new SimpleMenu("nmeaOff.png", files2, "Disable NMEA", this);
        
        files2.addSubMenu(back);
        files2.addSubMenu(nmeaOn);
        files2.addSubMenu(nmeaOff);
        
        files.addSubMenu(back);
        files.addSubMenu(wms);
        files.addSubMenu(collada);
        files.addSubMenu(kml);
        files.addSubMenu(shapeFile);
        
        simu.addSubMenu(back);
        simu.addSubMenu(files2);
        
        IOdata.addSubMenu(back);
        IOdata.addSubMenu(files);
        
        io.addSubMenu(back);
        io.addSubMenu(IOdata);
        io.addSubMenu(simu);
        
        /**
         * Tools Sub-menus
         */
        SimpleMenu earth = new SimpleMenu("earth.png", tools, this);
        SimpleMenu devices = new SimpleMenu("devices.png", tools, this);

        SimpleMenu models = new SimpleMenu("models.png", earth, this);
        SimpleMenu servermenu = new SimpleMenu("server.png", devices, this);
        SimpleMenu lmotion = new SimpleMenu("leapMotion.png", devices, this);

        SimpleMenu bathy = new SimpleMenu("bathy.png", models, "Activate Bathy", this);
        SimpleMenu noBathy = new SimpleMenu("noBathy.png", models, "Desactivate Bathy", this);
        SimpleMenu elevation = new SimpleMenu("elevation.png", models, "Activate Elevation", this);
        SimpleMenu noElevation = new SimpleMenu("noElevation.png", servermenu, "Desactivate Elevation", this);
        SimpleMenu options = new SimpleMenu("options.png", servermenu, "Open Server Config", this);
        SimpleMenu lmotionOn = new SimpleMenu("leapMotionOn.png", lmotion, "Enable leapmotion", this);
        SimpleMenu lmotionOff = new SimpleMenu("leapMotionOff.png", lmotion, "Disable leapmotion", this);

        models.addSubMenu(back);
        models.addSubMenu(bathy);
        models.addSubMenu(noBathy);
        models.addSubMenu(elevation);
        models.addSubMenu(noElevation);

        servermenu.addSubMenu(back);
        servermenu.addSubMenu(options);

        lmotion.addSubMenu(back);
        lmotion.addSubMenu(lmotionOn);
        lmotion.addSubMenu(lmotionOff);

        devices.addSubMenu(back);
        devices.addSubMenu(servermenu);
        devices.addSubMenu(lmotion);

        earth.addSubMenu(back);
        earth.addSubMenu(models);

        tools.addSubMenu(back);
        tools.addSubMenu(earth);
        tools.addSubMenu(devices);

        /**
         * Charts Sub-menus
         */
        SimpleMenu nav = new SimpleMenu("nav.png", charts, this);
        SimpleMenu bathyCharts = new SimpleMenu("bathy.png", charts, this);
        SimpleMenu sediment= new SimpleMenu("sediment.png", charts, this);
        SimpleMenu magnet = new SimpleMenu("magnetism.png", charts, this);

        SimpleMenu vector = new SimpleMenu("vector.png", nav, this);
        SimpleMenu raster = new SimpleMenu("raster.png", nav, this);
        
        SimpleMenu images = new SimpleMenu("images.png", bathyCharts, this);
        SimpleMenu dataCBathy = new SimpleMenu("data.png", bathyCharts, this);
        
        SimpleMenu dataCSedi = new SimpleMenu("data.png", sediment, this);
        
        SimpleMenu dataCMagnet = new SimpleMenu("data.png", magnet, this);
        
        SimpleMenu S57 = new SimpleMenu("s57.png", vector, "Open S57 Charts", this);
        
        SimpleMenu bsb = new SimpleMenu("bsbkap.png", raster, "Open BSB Charts", this);
        SimpleMenu geoTiff = new SimpleMenu("geotiff.png", raster, "Open GeoTiff Charts", this);
        
        SimpleMenu emodnet = new SimpleMenu("emodnet.png", images, "Load EMODnet Images", this);
        SimpleMenu gebcoImages = new SimpleMenu("gebco.png", images, "Load GEBCO Images", this);
        
        SimpleMenu shomOn = new SimpleMenu("dbshomon.png", dataCBathy, "Enable SHOM Data", this);
        SimpleMenu shomOff = new SimpleMenu("dbshomoff.png", dataCBathy, "Disable SHOM Data", this);
        SimpleMenu gebcoData = new SimpleMenu("gebco.png", dataCBathy, "Load GEBCO Data", this);
        
        SimpleMenu shom = new SimpleMenu("shom.png", dataCSedi, "Open SHOM Chart", this);
        
        SimpleMenu noaa = new SimpleMenu("noaa.png", dataCMagnet, "Open NOAA Chart", this);
        
        vector.addSubMenu(back);
        vector.addSubMenu(S57);
        
        raster.addSubMenu(back);
        raster.addSubMenu(bsb);
        raster.addSubMenu(geoTiff);
        
        images.addSubMenu(back);
        images.addSubMenu(emodnet);
        images.addSubMenu(gebcoImages);

        dataCBathy.addSubMenu(back);
        dataCBathy.addSubMenu(shomOn);
        dataCBathy.addSubMenu(shomOff);
        dataCBathy.addSubMenu(gebcoData);

        dataCSedi.addSubMenu(back);
        dataCSedi.addSubMenu(shom);
        
        dataCMagnet.addSubMenu(back);
        dataCMagnet.addSubMenu(noaa);
        
        nav.addSubMenu(back);
        nav.addSubMenu(vector);
        nav.addSubMenu(raster);
        
        bathyCharts.addSubMenu(back);
        bathyCharts.addSubMenu(images);
        bathyCharts.addSubMenu(dataCBathy);
        
        sediment.addSubMenu(back);
        sediment.addSubMenu(dataCSedi);
        
        magnet.addSubMenu(back);
        magnet.addSubMenu(dataCMagnet);
        
        charts.addSubMenu(back);
        charts.addSubMenu(nav);
        charts.addSubMenu(bathyCharts);
        charts.addSubMenu(sediment);
        charts.addSubMenu(magnet);
        
        /**
         * Tides Sub-menus
         */
        SimpleMenu currents = new SimpleMenu("currents.png", tide, this);
        SimpleMenu waves = new SimpleMenu("waves.png", tide, this);
        SimpleMenu tideSub = new SimpleMenu("tide.png", tide, this);

        SimpleMenu modelCurrents = new SimpleMenu("model.png", currents, this);
        
        SimpleMenu modelWaves = new SimpleMenu("model.png", waves, this);
        
        SimpleMenu modelTide = new SimpleMenu("model.png", tideSub, this);
        

        SimpleMenu gribCurrent = new SimpleMenu("grib.png", modelCurrents, "Open Currents Grib", this);
       
        SimpleMenu gribWaves = new SimpleMenu("grib.png", modelWaves, "Open Waves Grib", this);
        
        SimpleMenu gribTide = new SimpleMenu("grib.png", modelTide, "Open Tide Grib", this);
 
        modelCurrents.addSubMenu(back);
        modelCurrents.addSubMenu(gribCurrent);
        
        modelWaves.addSubMenu(back);
        modelWaves.addSubMenu(gribWaves);
        
        modelTide.addSubMenu(back);
        modelTide.addSubMenu(gribTide);

        currents.addSubMenu(back);
        currents.addSubMenu(modelCurrents);

        waves.addSubMenu(back);
        waves.addSubMenu(modelWaves);
        
        tideSub.addSubMenu(back);
        tideSub.addSubMenu(modelTide);
        
        tide.addSubMenu(back);
        tide.addSubMenu(currents);
        tide.addSubMenu(waves);
        tide.addSubMenu(tideSub);
        
        /**
         * Meteo Sub-menus
         */
        SimpleMenu filesMeteo = new SimpleMenu("files.png", meteo, this);
        SimpleMenu sites = new SimpleMenu("sites.png", meteo, this);

        SimpleMenu gribMeteo = new SimpleMenu("grib.png", filesMeteo, this);
        SimpleMenu modelMeteo = new SimpleMenu("model.png", filesMeteo, this);
        
        SimpleMenu local = new SimpleMenu("local.png", sites, this);
        
        SimpleMenu wind = new SimpleMenu("wind.png", gribMeteo, "Open Wind Grib", this);
        SimpleMenu pressure = new SimpleMenu("pressure.png", gribMeteo, "Open Pressure Grib", this);
        
        SimpleMenu dump = new SimpleMenu("dump.png", modelMeteo, "Dump Model", this);
 
        SimpleMenu darkSky = new SimpleMenu("darkSky.png", local, "Dark Sky mode", this);
        
        local.addSubMenu(back);
        local.addSubMenu(darkSky);
        
        modelMeteo.addSubMenu(back);
        modelMeteo.addSubMenu(dump);
        
        gribMeteo.addSubMenu(back);
        gribMeteo.addSubMenu(wind);
        gribMeteo.addSubMenu(pressure);

        filesMeteo.addSubMenu(back);
        filesMeteo.addSubMenu(gribMeteo);
        filesMeteo.addSubMenu(modelMeteo);

        sites.addSubMenu(back);
        sites.addSubMenu(local);

        meteo.addSubMenu(back);
        meteo.addSubMenu(filesMeteo);
        meteo.addSubMenu(sites);
        
        /**
         * Instruments Sub-menus
         */
        SimpleMenu ais = new SimpleMenu("ais.png", instr, this);
        SimpleMenu gps = new SimpleMenu("gps.png", instr, this);
        SimpleMenu compass = new SimpleMenu("compass.png", instr, this);
        SimpleMenu bathyInstrument = new SimpleMenu("bathy.png", instr, this);
        SimpleMenu time = new SimpleMenu("time.png", instr, this);

        SimpleMenu aisRadarOn = new SimpleMenu("aisRadarOn.png", ais, "Enable AIS Radar", this);
        SimpleMenu aisRadarOff = new SimpleMenu("aisRadarOff.png", ais, "Disable AIS Radar", this);
        SimpleMenu aisPlotOn = new SimpleMenu("aisPlotOn.png", ais, "Enable AIS Plot", this);
        SimpleMenu aisPlotOff = new SimpleMenu("aisPlotOff.png", ais, "Disable AIS Plot", this);
        SimpleMenu aisLogOn = new SimpleMenu("aisLogOn.png", ais, "Enable AIS Log", this);
        SimpleMenu aisLogOff = new SimpleMenu("aisLogOff.png", ais, "Disable AIS Log", this);
        
        SimpleMenu gpsPlotOn = new SimpleMenu("gpsPlotOn.png", ais, "Enable GPS Plot", this);
        SimpleMenu gpsPlotOff = new SimpleMenu("gpsPlotOff.png", ais, "Disable GPS Plot", this);
        SimpleMenu gpsPlotRouteOn = new SimpleMenu("gpsPlotWithRouteOn.png", ais, "Enable GPS Plot with Route", this);
        SimpleMenu gpsPlotRouteOff = new SimpleMenu("gpsPlotWithRouteOff.png", ais, "Disable GPS Plot with Route", this);
        SimpleMenu gpsTrackOn = new SimpleMenu("gpsTrackOn.png", ais, "Enable GPS Tracking", this);
        SimpleMenu gpsTrackOff = new SimpleMenu("gpsTrackOff.png", ais, "Disable GPS Tracking", this);
        SimpleMenu gpsLogOn = new SimpleMenu("gpsLogOn.png", ais, "Enable GPS Log", this);
        SimpleMenu gpsLogOff = new SimpleMenu("gpsLogOff.png", ais, "Disable GPS Log", this);
        
        SimpleMenu compassSub = new SimpleMenu("compass.png", compass, "Compass", this);
        
        SimpleMenu sonarOn = new SimpleMenu("sonarOn.png", bathyInstrument, "Sonar", this);
        
        SimpleMenu clocks = new SimpleMenu("clocks.png", time, "Clocks", this);
        
        ais.addSubMenu(back);
        ais.addSubMenu(aisRadarOn);
        ais.addSubMenu(aisRadarOff);
        ais.addSubMenu(aisPlotOn);
        ais.addSubMenu(aisPlotOff);
        ais.addSubMenu(aisLogOn);
        ais.addSubMenu(aisLogOff);
        
        gps.addSubMenu(back);
        gps.addSubMenu(gpsPlotOn);
        gps.addSubMenu(gpsPlotOff);
        gps.addSubMenu(gpsPlotRouteOn);
        gps.addSubMenu(gpsPlotRouteOff);
        gps.addSubMenu(gpsTrackOn);
        gps.addSubMenu(gpsTrackOff);
        gps.addSubMenu(gpsLogOn);
        gps.addSubMenu(gpsLogOff);
        
        compass.addSubMenu(back);
        compass.addSubMenu(compassSub);
        
        bathyInstrument.addSubMenu(back);
        bathyInstrument.addSubMenu(sonarOn);
        
        time.addSubMenu(back);
        time.addSubMenu(clocks);

        instr.addSubMenu(back);
        instr.addSubMenu(ais);
        instr.addSubMenu(gps);
        instr.addSubMenu(compass);
        instr.addSubMenu(bathyInstrument);
        instr.addSubMenu(time);
        
        /**
         * Navigation Sub-menus
         */
        SimpleMenu navigationSub = new SimpleMenu("navigationSub.png", navigation, this);

        SimpleMenu tracks = new SimpleMenu("tracks.png", navigationSub, this);
        SimpleMenu routes = new SimpleMenu("routes.png", navigationSub, this);
        
        SimpleMenu gpx = new SimpleMenu("gpx.png", tracks, "Open GPX File", this);
        SimpleMenu kmlTracks = new SimpleMenu("kml.png", tracks, "Open KML File", this);

        SimpleMenu measureTools = new SimpleMenu("measuretools.png", routes, "Open Measure Tools", this);
        SimpleMenu routeEditor = new SimpleMenu("routeeditor.png", routes, "Open Route Editor", this);
        SimpleMenu routeDataEditor = new SimpleMenu("routedataeditor.png", routes, "Open Route Data Editor", this);
        SimpleMenu routePhotoEditor = new SimpleMenu("routephotoeditor.png", routes, "Open Route Photo Editor", this);
        
        tracks.addSubMenu(back);
        tracks.addSubMenu(gpx);
        tracks.addSubMenu(kmlTracks);
        
        routes.addSubMenu(back);
        routes.addSubMenu(measureTools);
        routes.addSubMenu(routeEditor);
        routes.addSubMenu(routeDataEditor);
        routes.addSubMenu(routePhotoEditor);
        
        navigationSub.addSubMenu(back);
        navigationSub.addSubMenu(tracks);
        navigationSub.addSubMenu(routes);
        
        navigation.addSubMenu(back);
        navigation.addSubMenu(navigationSub);
        
        /**
         * Book Sub-menus
         */
        SimpleMenu logBook = new SimpleMenu("logbook.png", book, "Work In Progress", this);
        SimpleMenu listLights = new SimpleMenu("lightsbook.png", book, this);
        SimpleMenu sailingDirections = new SimpleMenu("sailingbook.png", book, this);
        
        SimpleMenu imagesLights = new SimpleMenu("images.png", listLights, this);

        SimpleMenu worldWide = new SimpleMenu("worldwide_sailing_directions.png", sailingDirections, "Work In Progress", this);
        SimpleMenu IrelandSouthernHarbours = new SimpleMenu("IrelandSouth_sailing_directions.png", sailingDirections, "Work In Progress", this);
        SimpleMenu shomBook = new SimpleMenu("shom.png", sailingDirections, this);
        
        SimpleMenu emodLight = new SimpleMenu("emodnet.png", imagesLights, this);

        SimpleMenu viewer = new SimpleMenu("viewer.png", shomBook, "Open Viewer", this);
        SimpleMenu editor = new SimpleMenu("editor.png", shomBook, "Open Editor", this);
        
        shomBook.addSubMenu(back);
        shomBook.addSubMenu(viewer);
        shomBook.addSubMenu(editor);
        
        imagesLights.addSubMenu(back);
        imagesLights.addSubMenu(emodLight);
        
        sailingDirections.addSubMenu(back);
        sailingDirections.addSubMenu(worldWide);
        sailingDirections.addSubMenu(IrelandSouthernHarbours);
        sailingDirections.addSubMenu(shomBook);
        
        listLights.addSubMenu(back);
        listLights.addSubMenu(imagesLights);
        
        book.addSubMenu(back);
        book.addSubMenu(logBook);
        book.addSubMenu(listLights);
        book.addSubMenu(sailingDirections);
        
        /**
         * Building Main Menu list
         */
        this.menuList.add(io);
        this.menuList.add(tools);
        this.menuList.add(charts);
        this.menuList.add(tide);
        this.menuList.add(meteo);
        this.menuList.add(instr);
        this.menuList.add(navigation);
        this.menuList.add(book);
        this.currParent = null;
    }

    /**
     * Displays the provided message on a field in the app
     * 
     * @param msg The message to display
     */
    public void displayMessage(String msg) {
        this.response.setText(msg);
    }

    /**
     * Actions to take according to the command given. For the moment, 5 actions
     * are recognized: openMenu, closeMenu, selectMenu, leftMenu, rightMenu
     * 
     * @param cmd The command to process
     */
    public void handleCmd(String cmd) {
        switch (cmd) {
            /**
             * If the menu is already open, do nothing
             * Else, open the menu (Root Menu will be displayed)
             */
            case "openMenu":
                if(isMenuOpen)
                    break;
                initOpen();
                this.isMenuOpen = true;
                break;

            /**
             * If the menu is alrady closed, do nothing
             * Else, deselect the current menu, close the menu (visible = false)
             * and all other attributes are returned to a default state
             */
            case "closeMenu":
                if(!isMenuOpen)
                    break;
                this.menuHolder.get(this.currIndex).setEffect(null);
                this.currIndex = 0;
                this.maxIndex = 0;
                this.currMenu = null;
                this.isMenuOpen = false;
                for(ImageView item : this.menuHolder)
                    item.setVisible(false);
                
                break;

            /**
             * If the menu is closed, do nothing
             * Else:
             *  -If the menu has an action:
             *      -If it's "GoBack", it will use the currParent attribute to 
             *       display the parent menu
             *      -Else, for the moment, display a message corresponding to
             *       to the action
             *  -Else: it will open the sub-Menu of the selected Menu
             */
            case "selectMenu":
                if(!isMenuOpen)
                    break;
                if (currMenu.getAction() != null) {
                    if (currMenu.getAction().equals("GoBack")) {
                        if (this.currParent.getParent() == null) {
                            initOpen();
                        } else {
                            openMenu();
                        }
                    } else {
                        try {
                            String request = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><arCommand><cmd>ActionMenu</cmd><arg>%s</arg></arCommand>",currMenu.getAction());
                            WSC.ws_request(request);
                        } catch (TimeLimitExceededException ex) {
                            Logger.getLogger(PrimaryPresenter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        displayMessage(currMenu.getAction());
                    }
                } else {
                    openMenu();
                }
                break;

            /**
             * Will shift the selection to the left: index will be shifted, and
             * the same goes for the selection effect
             */
            case "leftMenu":
                if(!isMenuOpen)
                    break;
                this.menuHolder.get(this.currIndex).setEffect(null);
                this.currIndex = (this.currIndex == 0) ? (this.maxIndex) : (this.currIndex - 1);
                if (this.currParent == null) {
                    this.currMenu = this.menuList.get(this.currIndex);
                } else {
                    this.currMenu = this.currParent.getSubMenu().get(this.currIndex);
                }

                selected();
                break;

            /**
             * Same than leftMenu, but to the right
             */
            case "rightMenu":
                if(!isMenuOpen)
                    break;
                this.menuHolder.get(this.currIndex).setEffect(null);
                this.currIndex = (this.currIndex == this.maxIndex) ? (0) : (this.currIndex + 1);
                if (this.currParent == null) {
                    this.currMenu = this.menuList.get(this.currIndex);
                } else {
                    this.currMenu = this.currParent.getSubMenu().get(this.currIndex);
                }

                selected();
                break;

            /**
             * Else, not handled action, so it does nothing
             */
            default:
                break;
        }
    }

    /**
     * Opens Root Menus and selects the 1st one
     */
    private void initOpen() {
        for(SimpleMenu menu : this.menuList){
            ImageView tmp = this.menuHolder.get(menuList.indexOf(menu));
            tmp.setImage(menu.getImage());
            tmp.setEffect(null);
            tmp.setVisible(true);
        }

        this.currParent = null;
        this.currIndex = 0;
        this.maxIndex = menuList.size() - 1;
        this.currMenu = menuList.get(currIndex);

        selected();
    }

    /**
     * Opens the sub-menus of the current selected menu
     */
    private void openMenu() {
        this.currIndex = 0;
        
        if (this.currMenu.getAction() != null && this.currMenu.getAction().equals("GoBack")) {
            this.currParent = this.currParent.getParent();
            
            for(ImageView menuholder : this.menuHolder)
                menuholder.setVisible(false);
            
            for(SimpleMenu menu : this.currParent.getSubMenu()){
                ImageView tmp = this.menuHolder.get(this.currParent.getSubMenu().indexOf(menu));
                tmp.setImage(menu.getImage());
                tmp.setEffect(null);
                tmp.setVisible(true);
            }
            
            this.maxIndex = this.currParent.getSubMenu().size() - 1;
            this.currMenu = this.currParent.getSubMenu().get(currIndex);
        } else {
            for(ImageView menuholder : this.menuHolder)
                menuholder.setVisible(false);
            
            for(SimpleMenu menu : this.currMenu.getSubMenu()){
                ImageView tmp = this.menuHolder.get(this.currMenu.getSubMenu().indexOf(menu));
                tmp.setImage(menu.getImage());
                tmp.setEffect(null);
                tmp.setVisible(true);
            }
            
            this.currParent = this.currMenu;
            this.maxIndex = this.currMenu.getSubMenu().size() - 1;
            this.currMenu = this.currMenu.getSubMenu().get(currIndex);
        }
        selected();
    }

    /**
     * Adds a gray halo to the current selected menu
     */
    private void selected() {
        int depth = 50; //Setting the uniform variable for the glow width and height
        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.GRAY);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);
        this.menuHolder.get(this.currIndex).setEffect(borderGlow);
    }
}

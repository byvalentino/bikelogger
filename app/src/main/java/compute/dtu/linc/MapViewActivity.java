package compute.dtu.linc;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.linc.R;

import compute.dtu.linc.Services.BackgroundService;
import compute.dtu.linc.Util.WebServicesUtil;
import compute.dtu.linc.Variables.Variables;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.ColorUtils;
import com.nullwire.trace.ExceptionHandler;

import org.altbeacon.beacon.Beacon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;


public class MapViewActivity extends AppCompatActivity  implements
        OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {

    private ActionBarDrawerToggle mtoggle;
    private Fragment active;

    private String userID;

    //Mapbox
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LatLng BOUND_CORNER_NE; //= new LatLng(55.79000, 12.53500); //was final
    private LatLng BOUND_CORNER_SW; //= new LatLng(55.77500, 12.50000);
    private LatLngBounds RESTRICTED_BOUNDS_AREA; // = new LatLngBounds.Builder()
     //       .include(BOUND_CORNER_NE)
     //       .include(BOUND_CORNER_SW)
     //       .build();
    private static final String BS_SOURCE = "busstop_source";
    private static final String BS_LAYER = "busstop_layer";
    private static final String BS_IMAGE = "busstop_image";
    private static final String BS_CALLOUT = "busstop_call_id";

    private static final String BSP_SOURCE = "busstopp_source";
    private static final String BSP_LAYER = "busstopp_layer";
    private static final String BSP_IMAGE = "busstopp_image";
    private static final String BSP_CALLOUT = "busstopp_call_id";

    private static final String L_SOURCE = "line_source";
    private static final String L_LAYER = "line_layer";

    private static final String L1_SOURCE = "line_source1";
    private static final String L1_LAYER = "line_layer1";

    private static final String L2_SOURCE = "line_source2";
    private static final String L2_LAYER = "line_layer2";

    private static final String BUS_SOURCE1 = "bus_source1";
    private static final String BUS_SOURCE2 = "bus_source2";
    private static final String BUS_SOURCE3 = "bus_source3";

    private static final String BUS_LAYER1 = "bus_layer1";
    private static final String BUS_LAYER2 = "bus_layer2";
    private static final String BUS_LAYER3 = "bus_layer3";

    private static final String BUS_IMAGE1 = "bus_image1";
    private static final String BUS_IMAGE2 = "bus_image2";
    private static final String BUS_IMAGE3 = "bus_image3";

    private static final String BUS_CALLOUT1 = "bus_call_id1";
    private static final String BUS_CALLOUT2 = "bus_call_id2";
    private static final String BUS_CALLOUT3 = "bus_call_id3";

    private static final String HBUS_SOURCE = "hbus_source";
    private static final String HBUS_LAYER = "hbus_layer";
    private static final String HBUS_IMAGE = "hbus_image";
    private static final String HBUS_CALLOUT = "hbus_call_id";


    private List<Feature> busses;
    private List<Feature> hackbus;
    private GeoJsonSource bus_source1;
    private GeoJsonSource bus_source2;
    private GeoJsonSource bus_source3;

    private GeoJsonSource hbus_source;

    private int on_bus = -1; //A reference to which busses feature should be highlighed -1 if none.
    private boolean highlighted = false;

    public BackgroundService gpsService;
    public boolean mTracking = false;

    private Handler handler=  new Handler();
    private int refreshdelay = 1000; //milliseconds

    //Used for timer animations and toast refreshes
    CountDownTimer toastCountDown;Toast toast;String debugtext;//TODO debug remove later
    CountDownTimer animation_countdown; Boolean[] animatej;

    ArrayList<LineLayer> routes = new ArrayList<>();

    //Button functionality:
    Button toggleButton;
    int toggleState = 0;

    //Help popup
    Dialog dia;

    long beaconstamp = 0;

    ArrayList<ArrayList<Double>> previousLocation;
    ArrayList<ArrayList<Double>> lastLocations;

    //Variables for storing settings
    String route1;
    String route2;
    String route3;
    String center;
    String northeast;
    String southwest;
    String zoomMin;
    String zoomMax;
    String zoom;
    ArrayList<Feature> stops;
    ArrayList<String> stop_ids;
    ArrayList<JSONArray> local_route; //Stores the individual bus routes JSON

    //Stores ints representing which bus is on which route [1,1,2] ~ means 2 unique routes:
    ArrayList<Integer> bus_route_relation = new ArrayList<>();

    int refresh_schedule_data = 0;

    private Runnable refreshTimer = new Runnable(){
        public void run(){

            //Test if we are in a bus
            if(gpsService != null) {
                ArrayList<Beacon> beacons = gpsService.getCurrentBeaconsInRange();
                for (Beacon b : beacons) {
                    if (Integer.parseInt(b.getId2().toString()) == 801 && b.getRssi() > -80) {
                        //System.out.println(b.getId3().toString());
                        if (Integer.parseInt(b.getId3().toString()) == 1) {
                            on_bus = 0;
                            beaconstamp = System.currentTimeMillis();
                            break;
                        } else if (Integer.parseInt(b.getId3().toString()) == 2) {
                            on_bus = 1;
                            beaconstamp = System.currentTimeMillis();
                            break;
                        } else if (Integer.parseInt(b.getId3().toString()) == 3) {
                            on_bus = 2;
                            beaconstamp = System.currentTimeMillis();
                            break;
                        }
                    }
                }

                //If a bus beacon was not seen for 10 seconds assume we are no longer on the bus
                if (System.currentTimeMillis() >= beaconstamp + 10000) {
                    on_bus = -1;
                }

                //System.out.println("In refresh");

                if (busses != null && lastLocations != null) {
                    //System.out.println("On_bus: "+on_bus);
                    if (on_bus == -1) {
                        hackbus.set(0, Feature.fromGeometry(Point.fromLngLat(0, 0)));
                    }
                    for (int i = 0; i < busses.size(); i++) {
                        Feature f = Feature.fromGeometry(Point.fromLngLat(lastLocations.get(i).get(1), lastLocations.get(i).get(0)));
                        if (busses.get(i).getStringProperty("Name") != null) {
                            f.addStringProperty("Name", busses.get(i).getStringProperty("Name"));
                        }
                        //if i = highlighed bus bring the hackbus to its location and remove the bus from view
                        if (i == on_bus) {
                            busses.set(i, Feature.fromGeometry(Point.fromLngLat(0, 0)));
                            hackbus.set(0, f);
                        } else { // Else set location normally
                            busses.set(i, f);

                        }


                    }
                    bus_source1.setGeoJson(FeatureCollection.fromFeatures(busses.subList(0, 1)));
                    bus_source2.setGeoJson(FeatureCollection.fromFeatures(busses.subList(1, 2)));
                    bus_source3.setGeoJson(FeatureCollection.fromFeatures(busses.subList(2, 3)));

                    hbus_source.setGeoJson(FeatureCollection.fromFeatures(hackbus));
                }
                if (previousLocation != null && lastLocations != null) {
                    for (int i = 0; i < previousLocation.size(); i++) {
                        if (!previousLocation.get(i).get(0).equals(lastLocations.get(i).get(0)) || !previousLocation.get(i).get(1).equals(lastLocations.get(i).get(1))) {
                            //System.out.println("Inside of direction animation");

                            Location gps1 = new Location(LocationManager.GPS_PROVIDER);
                            Location gps2 = new Location(LocationManager.GPS_PROVIDER);

                            gps1.setLatitude(previousLocation.get(i).get(0));
                            gps1.setLongitude(previousLocation.get(i).get(1));

                            gps2.setLatitude(lastLocations.get(i).get(0));
                            gps2.setLongitude(lastLocations.get(i).get(1));

                            float bearing = gps1.bearingTo(gps2); // float or radians?
                            //System.out.println("Bearing = " + bearing);


                            //Sample code for merging images, rotating and creating drawables:
                            RotateDrawable iconPlayRotate = new RotateDrawable();
                            iconPlayRotate.setFromDegrees(bearing);
                            iconPlayRotate.setToDegrees(bearing);
                            //iconPlayRotate.setPivotXRelative(true);
                            //iconPlayRotate.setPivotYRelative(true);
                            //iconPlayRotate.setPivotX(0.13f);
                            //iconPlayRotate.setPivotY(-0.4f);
                            iconPlayRotate.setDrawable(getDrawable(R.drawable.arrow));
                            iconPlayRotate.setLevel(1);
                            //style.addImage(BUS_IMAGE,iconPlayRotate);

                            Drawable plusIcon = getDrawable(R.drawable.new_bus);

                            LayerDrawable finalDrawable = new LayerDrawable(new Drawable[]{plusIcon, iconPlayRotate});
                            finalDrawable.setLayerInset(0, 0, 0, 0, 0);
                            finalDrawable.setLayerInset(1, 0, 0, 0, 0);

                            if (i == 0) {
                                mapboxMap.getStyle().addImage(BUS_IMAGE1, finalDrawable);
                            } else if (i == 1) {
                                mapboxMap.getStyle().addImage(BUS_IMAGE2, finalDrawable);
                            } else if (i == 2) {
                                mapboxMap.getStyle().addImage(BUS_IMAGE3, finalDrawable);
                            }
                        }
                    }

                }
                //Run again in x sec, meanwhile execute a new fetch:
                handler.postDelayed(this, refreshdelay);
                //fetchNewBusLocations();

                //TODO make these seperate layers (bus image id)

                //Update schedule data every 10 sec
                /*   if(refresh_schedule_data > 20){
                    refresh_schedule_data = 0;
                    WebServicesUtil.sendScheduleRequest(getBaseContext());
                }else{
                    refresh_schedule_data++;
                }*/
            }
        }
    };

    //Navigation implementation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.map:
                    if(active != null) {
                        getSupportFragmentManager().beginTransaction().remove(active).commit();
                        active = null;
                    }
                    break;
                case R.id.questionnaires:
                    selectedFragment = new QuestionnaireFragment();
                    active = selectedFragment;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    break;
            }
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bind remote stack trace to activity
        ExceptionHandler.register(this,"http://python-service-linc-dtu.eu-de.mybluemix.net/bugReport");

        //Checks that bluetooth is enabled
        bluetoothEnabledCheck();

        fetchNewRouteData();
        loadNewRouteData();


        //Mapbox
        Mapbox.getInstance(this,"pk.eyJ1IjoiYnl2YWxlbnRpbm8iLCJhIjoiY2p3OXB3azZjMDFveTRhdDd2cGhtOHNwbCJ9.UQb7n2YllozVK6PC3FL9dA");
        setContentView(R.layout.activity_map_view);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //Buttom nav
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Side nav
        DrawerLayout dl = findViewById(R.id.drawerlayout);
        mtoggle = new ActionBarDrawerToggle(this,dl,R.string.open,R.string.close);
        dl.addDrawerListener(mtoggle);
        mtoggle.syncState();
        DrawerArrowDrawable arrow = mtoggle.getDrawerArrowDrawable();
        arrow.setColor(ContextCompat.getColor(this,R.color.text));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //add button listener:
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //System.out.println("Inside button, State: " + toggleState);
                toggleState++;

                if(toggleState == 4) toggleState = 0;

                if(toggleState == 0){
                    toggleButton.setBackgroundResource(R.drawable.allrings);
                    routes.get(0).setProperties(
                            lineColor(Color.GREEN)
                    );
                    routes.get(1).setProperties(
                            lineColor(Color.YELLOW)
                    );
                    routes.get(2).setProperties(
                            lineColor(Color.RED)
                    );
                }else if(toggleState == 1){
                    toggleButton.setBackgroundResource(R.drawable.greenring);
                    routes.get(0).setProperties(
                            lineColor(Color.GREEN)
                    );
                    routes.get(1).setProperties(
                            lineColor(Color.TRANSPARENT)
                    );
                    routes.get(2).setProperties(
                            lineColor(Color.TRANSPARENT)
                    );
                }else if(toggleState == 2){
                    toggleButton.setBackgroundResource(R.drawable.yellowring);
                    routes.get(0).setProperties(
                            lineColor(Color.TRANSPARENT)
                    );
                    routes.get(1).setProperties(
                            lineColor(Color.YELLOW)
                    );
                    routes.get(2).setProperties(
                            lineColor(Color.TRANSPARENT)
                    );
                }else if(toggleState == 3){
                    toggleButton.setBackgroundResource(R.drawable.redring);
                    routes.get(0).setProperties(
                            lineColor(Color.TRANSPARENT)
                    );
                    routes.get(1).setProperties(
                            lineColor(Color.TRANSPARENT)
                    );
                    routes.get(2).setProperties(
                            lineColor(Color.RED)
                    );
                }
            }
        });

        NavigationView nv = findViewById(R.id.navigation);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.website:
                        openBrowserWithURL("https://lincproject.dk");
                        break;
                    case R.id.experiment:
                        openBrowserWithURL("https://lincproject.dk/blivtestpassager/dettestervi/");
                        break;
                    case R.id.about:
                        openBrowserWithURL("https://lincproject.dk/blivtestpassager/faq_dtu/");
                        break;
                    case R.id.account:
                        Intent k = new Intent(MapViewActivity.this,AccountActivity.class);
                        startActivity(k);
                        break;
                    case R.id.messages:
                        Intent r = new Intent(MapViewActivity.this,CommunicationsModule.class);
                        startActivity(r);
                        break;
                    case R.id.schedule_button:
                        Intent e = new Intent(MapViewActivity.this,Schedule.class);
                        startActivity(e);
                        break;
                }
                return true;
            }
        });

        //check ID and commit relevant methods.
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app",0);
        userID = sharedPreferences.getString("id", null);
        //System.out.println("UserID = "+userID);
        if(userID == null){
            Intent k = new Intent(MapViewActivity.this, SignUpActivity.class);
            startActivity(k);
            finish();
            return;
        }

        //Start background service for tracking:
        Intent intent = new Intent(this, BackgroundService.class);
        //this.startService(intent);
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


    }

    public void openBrowserWithURL(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


    //***********************************************************************************
    // Background service functionality
    //***********************************************************************************
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("BackgroundService")) {
                gpsService = ((BackgroundService.LocationServiceBinder) service).getService();
                gpsService.startTrackingIf();
                //gpsService.startTracking(); //use only for force debug
            }
        }


        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                gpsService = null;
            }
        }
    };
    //***********************************************************************************
    // Mapbox service functionality
    //***********************************************************************************
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapViewActivity.this.mapboxMap = mapboxMap;
        loadConfig();
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/thetoucan/ck6hopufq0s3d1ipfnrut4vmn"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        try {
                            //TODO remove unnessary code later!!!
                            if (route1 != null && !route1.contains("[]") && bus_route_relation.get(0) == 1) {
                                style.addSource(new GeoJsonSource(L_SOURCE, FeatureCollection.fromJson(route1)));

                            } else{
                                style.addSource(new GeoJsonSource(L_SOURCE, FeatureCollection.fromJson(loadJsonFromAsset("map.geojson"))));
                            }
                            style.addLayer(new LineLayer(L_LAYER, L_SOURCE).withProperties(
                                    PropertyFactory.lineWidth(6f),
                                    PropertyFactory.lineColor(ColorUtils.colorToRgbaString(Color.GREEN))
                            ));
                            routes.add((LineLayer) style.getLayer(L_LAYER));

                            if (route2 != null && !route2.contains("[]") && bus_route_relation.get(0) == 2) {
                                style.addSource(new GeoJsonSource(L1_SOURCE, FeatureCollection.fromJson(route2)));

                            } else {
                                style.addSource(new GeoJsonSource(L1_SOURCE, FeatureCollection.fromJson(loadJsonFromAsset("map1.geojson"))));
                            }
                            style.addLayer(new LineLayer(L1_LAYER, L1_SOURCE).withProperties(
                                    PropertyFactory.lineWidth(3f),
                                    PropertyFactory.lineColor(ColorUtils.colorToRgbaString(Color.YELLOW))
                            ));
                            routes.add((LineLayer) style.getLayer(L1_LAYER));
                            if (route3 != null && !route3.contains("[]") && bus_route_relation.get(0) == 1) {
                                style.addSource(new GeoJsonSource(L2_SOURCE, FeatureCollection.fromJson(route3)));

                            } else {
                                style.addSource(new GeoJsonSource(L2_SOURCE, FeatureCollection.fromJson(loadJsonFromAsset("map2.geojson"))));
                            }
                            style.addLayer(new LineLayer(L2_LAYER, L2_SOURCE).withProperties(
                                    PropertyFactory.lineWidth(1f),
                                    PropertyFactory.lineColor(ColorUtils.colorToRgbaString(Color.RED))
                            ));
                            routes.add((LineLayer) style.getLayer(L2_LAYER));


                        } catch (Error | IOException e) {
                            e.printStackTrace();
                        }


                        // Populate the map with LINC bus stops:
                        List<Feature> busStops = stops;
                        if (busStops != null) {
                            style.addImage(BS_IMAGE, BitmapFactory.decodeResource(MapViewActivity.this.getResources(), R.drawable.stop_linc));
                            style.addSource(new GeoJsonSource(BS_SOURCE, FeatureCollection.fromFeatures(busStops)));
                            style.addLayer(new SymbolLayer(BS_LAYER, BS_SOURCE).withProperties(
                                    PropertyFactory.iconAllowOverlap(true),
                                    PropertyFactory.iconIgnorePlacement(true),
                                    PropertyFactory.iconImage(BS_IMAGE),
                                    PropertyFactory.iconSize(0.4f)
                            ));
                            //Fixes offset of image
                            style.addLayer(new SymbolLayer(BS_CALLOUT, BS_SOURCE).withProperties(
                                    iconImage("{name}"),
                                    iconAnchor(ICON_ANCHOR_BOTTOM),
                                    iconAllowOverlap(true),
                                    iconOffset(new Float[]{-2f, -28f})
                            ));

                            // Populate the map with public transport nodes:
                            List<Feature> busStopspublic = loadBusStops("publictransportdtu");
                            if (busStopspublic != null) {
                                style.addImage(BSP_IMAGE, BitmapFactory.decodeResource(MapViewActivity.this.getResources(), R.drawable.public_busstop));
                                style.addSource(new GeoJsonSource(BSP_SOURCE, FeatureCollection.fromFeatures(busStopspublic)));
                                style.addLayer(new SymbolLayer(BSP_LAYER, BSP_SOURCE).withProperties(
                                        PropertyFactory.iconAllowOverlap(true),
                                        PropertyFactory.iconIgnorePlacement(true),
                                        PropertyFactory.iconImage(BSP_IMAGE),
                                        PropertyFactory.iconSize(0.4f)
                                ));
                                //Fixes offset of image
                                style.addLayer(new SymbolLayer(BSP_CALLOUT, BSP_SOURCE).withProperties(
                                        iconImage("{name}"),
                                        iconAnchor(ICON_ANCHOR_BOTTOM),
                                        iconAllowOverlap(true),
                                        iconOffset(new Float[]{-2f, -28f})
                                ));
                            }



                            //Add busses
                            busses = new ArrayList<>();
                            Feature f = Feature.fromGeometry(Point.fromLngLat(12.5226464,55.7819611122));
                            f.addStringProperty("Name","B1");
                            busses.add(f);
                            Feature f2 = Feature.fromGeometry(Point.fromLngLat(12.5226464,55.7819611122));
                            f2.addStringProperty("Name","B2");
                            busses.add(f2);
                            Feature f3 = Feature.fromGeometry(Point.fromLngLat(12.5226464,55.7819611122));
                            f3.addStringProperty("Name","B3");
                            busses.add(f3);


                            style.addImage(BUS_IMAGE1, BitmapFactory.decodeResource(MapViewActivity.this.getResources(), R.drawable.new_bus));
                            bus_source1 = new GeoJsonSource(BUS_SOURCE1, FeatureCollection.fromFeatures(busses.subList(0,1)));
                            style.addSource(bus_source1);
                            style.addLayer(new SymbolLayer(BUS_LAYER1, BUS_SOURCE1).withProperties(
                                    PropertyFactory.iconAllowOverlap(true),
                                    PropertyFactory.iconIgnorePlacement(true),
                                    PropertyFactory.iconImage(BUS_IMAGE1),
                                    PropertyFactory.iconSize(0.4f)
                            ));
                            //Fixes offset of image
                            style.addLayer(new SymbolLayer(BUS_CALLOUT1, BUS_SOURCE1).withProperties(
                                    iconImage("{name}"),
                                    iconAnchor(ICON_ANCHOR_BOTTOM),
                                    iconAllowOverlap(true)//,
                                    //iconOffset(new Float[]{-2f, -28f})
                            ));

                            style.addImage(BUS_IMAGE2, BitmapFactory.decodeResource(MapViewActivity.this.getResources(), R.drawable.new_bus));
                            bus_source2 = new GeoJsonSource(BUS_SOURCE2, FeatureCollection.fromFeatures(busses.subList(1,2)));
                            style.addSource(bus_source2);
                            style.addLayer(new SymbolLayer(BUS_LAYER2, BUS_SOURCE2).withProperties(
                                    PropertyFactory.iconAllowOverlap(true),
                                    PropertyFactory.iconIgnorePlacement(true),
                                    PropertyFactory.iconImage(BUS_IMAGE2),
                                    PropertyFactory.iconSize(0.4f)
                            ));
                            //Fixes offset of image
                            style.addLayer(new SymbolLayer(BUS_CALLOUT2, BUS_SOURCE2).withProperties(
                                    iconImage("{name}"),
                                    iconAnchor(ICON_ANCHOR_BOTTOM),
                                    iconAllowOverlap(true)//,
                                    //iconOffset(new Float[]{-2f, -28f})
                            ));
                            style.addImage(BUS_IMAGE3, BitmapFactory.decodeResource(MapViewActivity.this.getResources(), R.drawable.new_bus));
                            bus_source3 = new GeoJsonSource(BUS_SOURCE3, FeatureCollection.fromFeatures(busses.subList(2,3)));
                            style.addSource(bus_source3);
                            style.addLayer(new SymbolLayer(BUS_LAYER3, BUS_SOURCE3).withProperties(
                                    PropertyFactory.iconAllowOverlap(true),
                                    PropertyFactory.iconIgnorePlacement(true),
                                    PropertyFactory.iconImage(BUS_IMAGE3),
                                    PropertyFactory.iconSize(0.4f)
                            ));
                            //Fixes offset of image
                            style.addLayer(new SymbolLayer(BUS_CALLOUT3, BUS_SOURCE3).withProperties(
                                    iconImage("{name}"),
                                    iconAnchor(ICON_ANCHOR_BOTTOM),
                                    iconAllowOverlap(true)//,
                                    //iconOffset(new Float[]{-2f, -28f})
                            ));



                            //Create highlighed "hack" bus
                            hackbus = new ArrayList<>();
                            Feature fh = Feature.fromGeometry(Point.fromLngLat(0,0));
                            hackbus.add(fh);
                            style.addImage(HBUS_IMAGE, BitmapFactory.decodeResource(MapViewActivity.this.getResources(), R.drawable.new_bus2));
                            hbus_source = new GeoJsonSource(HBUS_SOURCE, FeatureCollection.fromFeatures(hackbus));
                            style.addSource(hbus_source);
                            style.addLayer(new SymbolLayer(HBUS_LAYER, HBUS_SOURCE).withProperties(
                                    PropertyFactory.iconAllowOverlap(true),
                                    PropertyFactory.iconIgnorePlacement(true),
                                    PropertyFactory.iconImage(HBUS_IMAGE),
                                    PropertyFactory.iconSize(0.4f)
                            ));
                            //Fixes offset of image
                            style.addLayer(new SymbolLayer(HBUS_CALLOUT, HBUS_SOURCE).withProperties(
                                    iconImage("{name}"),
                                    iconAnchor(ICON_ANCHOR_BOTTOM),
                                    iconAllowOverlap(true)//,
                                    //iconOffset(new Float[]{-2f, -28f})
                            ));


                            enableLocationComponent(style);
                            mapboxMap.addOnMapClickListener(MapViewActivity.this);

                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "network error - busStops",
                                    Toast.LENGTH_LONG);
                            //System.out.println("App failure toast activated");

                            toast.show();
                        }

                    }

                });


        //Mapbox settings:
        mapboxMap.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);

        //todo might remove later
        if(zoomMin != null && zoomMax != null){
            mapboxMap.setMinZoomPreference(Integer.parseInt(zoomMin));
            mapboxMap.setMaxZoomPreference(Integer.parseInt(zoomMax));
        }else {
            mapboxMap.setMinZoomPreference(14);
            mapboxMap.setMaxZoomPreference(16);
        }
        mapboxMap.getUiSettings().setRotateGesturesEnabled(false);

    }


    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            //locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }
    private String loadJsonFromAsset(String nameOfLocalFile) throws IOException {
        InputStream is = getAssets().open("raw/"+nameOfLocalFile);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
    }

    //Side menu and help popup support:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.app_bar_search) {
            dia = new Dialog(MapViewActivity.this);
            dia.setContentView(R.layout.help_popup);
            dia.show();
            Button closewin = dia.findViewById(R.id.close_button);
            closewin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dia.dismiss();
                }
            });
            return true;
        }
        if(mtoggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //***********************************************************************************
    // Activity lifecycle method stubs
    //***********************************************************************************
    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        //Start a refresh timer
        handler.postDelayed(refreshTimer,refreshdelay);

        getSupportActionBar().setTitle(getString(R.string.LINC_map_title));

        WebServicesUtil.sendMessageRequest(true,false);
        WebServicesUtil.sendScheduleRequest(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        //Remove all active refresh timers
        handler.removeCallbacksAndMessages(null);
        //If a toast is present remove it
        try{
            toastCountDown.cancel();
            toast.cancel();
        }catch(Exception e){
            //whatever
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    //***********************************************************************************
    // data fetching methods
    //***********************************************************************************

    //Requires:
    //Result: Loads the map center and bounds from a the Mapconfig file
    public void loadConfig(){
        try  {
            //TODO remove later
            //List<Feature> busStops = new ArrayList<>();
            //InputStream is = getAssets().open("raw/MapConfig.txt");
            //tring data = convertStreamToString(is);
            //System.out.println(data);
            //String[] s = data.split("\n");
            //System.out.println("size: " + s.length);
            /*
            if(center == null){
                //Load center and bounds:
                for(String n : s){
                    String[] stopData = n.split(",");
                    if(stopData[0].equals("Center")){
                        System.out.println("Center data: " + Double.parseDouble(stopData[1]) +" "+ Double.parseDouble(stopData[2]));
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(stopData[1]),Double.parseDouble(stopData[2])))
                                .zoom(14.2)
                                .build());
                    }else if(stopData[0].equals("Northeast")){
                        BOUND_CORNER_NE = new LatLng(Double.parseDouble(stopData[1]),Double.parseDouble(stopData[2]));
                    }else if(stopData[0].equals("Southwest")){
                        BOUND_CORNER_SW = new LatLng(Double.parseDouble(stopData[1]),Double.parseDouble(stopData[2]));
                    }
                }
            }else{*/
            if(center != null) {
                String[] center_data = center.split(",");
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble(center_data[1]), Double.parseDouble(center_data[2])))
                        .zoom(Double.parseDouble(zoom))
                        .build());
                String[] ne_data = northeast.split(",");
                BOUND_CORNER_NE = new LatLng(Double.parseDouble(ne_data[1]), Double.parseDouble(ne_data[2]));
                String[] sw_data = southwest.split(",");
                BOUND_CORNER_SW = new LatLng(Double.parseDouble(sw_data[1]), Double.parseDouble(sw_data[2]));

                //}
                //set bounds
                RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
                        .include(BOUND_CORNER_NE)
                        .include(BOUND_CORNER_SW)
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Requires:
    //Returns: A list of features representing the bus stop locations
    public List<Feature> loadBusStops(String fileName){
        try  {
            List<Feature> busStops = new ArrayList<>();
            InputStream is = getAssets().open("raw/"+fileName+".txt");
            String data = convertStreamToString(is);
            //System.out.println(data);
            String[] s = data.split("\n");
            //System.out.println("size: " + s.length);
            for(String n : s){
                String[] stopData = n.split(",");
                Feature f = Feature.fromGeometry(Point.fromLngLat(Double.parseDouble(stopData[1]),Double.parseDouble(stopData[0])));
                f.addStringProperty("Name",stopData[2]);
                busStops.add(f);
                //System.out.println("New bus stop");

            }

            return busStops;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //Requires: an input stream
    //Returns: a string generates from the inputstream
    static String convertStreamToString(InputStream is) throws FileNotFoundException {
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    //overrides the mapbox default click handler
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
    }
    /**
     * This method handles click events for SymbolLayer symbols.
     * <p>
     * When a SymbolLayer icon is clicked, we moved that feature to the selected state.
     * </p>
     *
     * @param screenPoint the point on screen clicked
     */
    //TODO add dynamic arrival times
    private boolean handleClickIcon(PointF screenPoint) {


        //TODO debug remove later
        try{
            //toastCountDown.cancel();
            animation_countdown.cancel();
        }catch(Exception e){
            //whatever
        }

        // Bus stop clicked:
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, BS_LAYER);
        //System.out.println(features);
        if(!features.isEmpty()){
            try {

                String name = features.get(0).getStringProperty("Name");
                //System.out.println("Name: " + name + " " +features.toString());

                Bundle bl = new Bundle();
                bl.putString("entity","Busstop");
                bl.putString("name",name);
                ModalScheduleFragment fg = new ModalScheduleFragment();
                fg.setArguments(bl);
                fg.show(getSupportFragmentManager(),"testSchedule");

                startHighlightRouteAnimation(features);
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //Public bus stop clicked
        features = mapboxMap.queryRenderedFeatures(screenPoint, BSP_LAYER);
        //System.out.println(features);
        if(!features.isEmpty()){
            try {
                try{
                    toastCountDown.cancel();
                }catch(Exception e){
                    //whatever
                }

                String name = features.get(0).getStringProperty("Name");
                //System.out.println("Name: " + name + " " +features.toString() );
                Toast toast = Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG);
                toast.show();
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //Bus 1 clicked
        features = mapboxMap.queryRenderedFeatures(screenPoint,BUS_LAYER1);
        if (!features.isEmpty()) {
            String name = features.get(0).getStringProperty("Name");
            //System.out.println("name:" + name);

            Bundle bl = new Bundle();
            bl.putString("entity","1");
            ModalScheduleFragment fg = new ModalScheduleFragment();
            fg.setArguments(bl);
            fg.show(getSupportFragmentManager(),"testSchedule");

            return true;
        }
        //Bus 2 clicked
        features = mapboxMap.queryRenderedFeatures(screenPoint,BUS_LAYER2);
        if (!features.isEmpty()) {
            String name = features.get(0).getStringProperty("Name");
            //System.out.println("name:" + name);

            Bundle bl = new Bundle();
            bl.putString("entity","2");
            ModalScheduleFragment fg = new ModalScheduleFragment();
            fg.setArguments(bl);
            fg.show(getSupportFragmentManager(),"testSchedule");

            return true;
        }
        //Bus 3 clicked
        features = mapboxMap.queryRenderedFeatures(screenPoint,BUS_LAYER3);
        if (!features.isEmpty()) {

            String name = features.get(0).getStringProperty("Name");
            //System.out.println("name:" + name);

            Bundle bl = new Bundle();
            bl.putString("entity","3");
            ModalScheduleFragment fg = new ModalScheduleFragment();
            fg.setArguments(bl);
            fg.show(getSupportFragmentManager(),"testSchedule");

            return true;
        }

        //HighLighted bus clicked
        features = mapboxMap.queryRenderedFeatures(screenPoint,HBUS_LAYER);
        if (!features.isEmpty()) {
            //System.out.println("On_bus: " + on_bus);

            Bundle bl = new Bundle();
            bl.putString("entity",""+(on_bus+1));
            ModalScheduleFragment fg = new ModalScheduleFragment();
            fg.setArguments(bl);
            fg.show(getSupportFragmentManager(),"testSchedule");

            return true;
        }


        return false;
    }

    private void startHighlightRouteAnimation(List<Feature> features) {
        //Animation:
        routes.get(0).setProperties(
                lineColor(Color.TRANSPARENT)
        );
        routes.get(1).setProperties(
                lineColor(Color.TRANSPARENT)
        );
        routes.get(2).setProperties(
                lineColor(Color.TRANSPARENT)
        );

        //calculate number of transitions:
        animatej = new Boolean[3];
        String current_feature = features.get(0).getStringProperty("Stop_id");
        int j = 0;
        int num_transitions = 0;
        for(JSONArray i : local_route) {
            //check to ensure the current feature matches and if the route is active
            if (i.toString().contains(current_feature) && j+1 == bus_route_relation.get(j)) {
                animatej[j] = true;
                num_transitions++;

            }else{
                animatej[j] = false;
            }
            j++;
        }

        int animation_timer = 4000*num_transitions;
        // Set the countdown to display the toast
        animation_countdown = new CountDownTimer(animation_timer, 400) {
            int step = 1;
            public void onTick(long millisUntilFinished) {
                if(step % 10 == 0){
                    for(int i = 0; i < animatej.length;i++){
                        if(animatej[i]){ animatej[i] = false;step=1;break;}
                    }
                }
                boolean first_paint = false; //Paint on the first route
                for(int i = 0; i < animatej.length; i++) {
                    //Highlight half the frames, and only if they are to be animated

                    if(step % 2 == 0 && animatej[i] && !first_paint) {
                        if(i == 0) {
                            routes.get(i).setProperties(
                                lineColor(Color.GREEN)
                            );
                        }else if(i == 1){
                            routes.get(i).setProperties(
                                lineColor(Color.YELLOW)
                            );
                        }else if(i == 2){
                            routes.get(i).setProperties(
                                lineColor(Color.RED)
                            );
                        }
                        first_paint = true;
                    }else {
                        routes.get(i).setProperties(
                                lineColor(Color.TRANSPARENT)
                        );
                    }
                }
                step++;
            }
            public void onFinish() {
                toggleButton.setBackgroundResource(R.drawable.allrings);
                routes.get(0).setProperties(
                        lineColor(Color.GREEN)
                );
                routes.get(1).setProperties(
                        lineColor(Color.YELLOW)
                );
                routes.get(2).setProperties(
                        lineColor(Color.RED)
                );
            }
        }.start();
    }


    //Requires:
    //Result: updates the lastLocations array with the newest bus data from the webservice
    public void fetchNewBusLocations() {
        ArrayList<ArrayList<Double>> res = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {

                    String url = Variables.webServiceEndPoint+"/getBusLocations?id=1";
                    //System.out.println("URL: " + url);
                    URL urlObj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    //conn.setRequestProperty("Accept-Charset", "UTF-8");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setReadTimeout(6000);
                    conn.setConnectTimeout(8000);

                    conn.connect();

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.flush();
                    wr.close();

                    try {
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        String response = result.toString();
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            //System.out.println(arr.getJSONObject(i).getString("name"));
                            ArrayList<Double> t = new ArrayList<>();
                            t.add(Double.parseDouble(arr.getJSONObject(i).getString("lat")));
                            t.add(Double.parseDouble(arr.getJSONObject(i).getString("long")));
                            res.add(t);
                        }
                        //fetch new bus location data and see if it matches our expecations, if not ignore it and use old data
                        if(res != null && res.size() == 3) {
                            previousLocation = lastLocations; //Set old gps for directional arrows
                            lastLocations = res;
                        }

                    } catch (Exception e3) {
                        e3.printStackTrace();
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                } catch (IOException ee) {
                    ee.printStackTrace();

                }
        }});
        thread.start();
    }


    //Requires:
    //Result: updates the lastLocations array with the newest bus data from the webservice
    public void fetchNewRouteData() {
        ArrayList<ArrayList<Double>> res = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {

                    String url = Variables.webServiceEndPoint+"/getRouteData";
                    URL urlObj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    //conn.setRequestProperty("Accept-Charset", "UTF-8");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setReadTimeout(8000);
                    conn.setConnectTimeout(12000);

                    conn.connect();

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.flush();
                    wr.close();

                    try {
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        String response = result.toString();
                        SharedPreferences settings = getSharedPreferences("app", 0);

                        String old_data = settings.getString("route",null);

                        if(!old_data.equals(response)) {
                            SharedPreferences.Editor edit = settings.edit();
                            edit.putString("route", response);
                            edit.commit();
                        }
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                } catch (IOException ee) {
                    ee.printStackTrace();

                }
            }});
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Requires:
    //Result: takes the latest route data and populates data arrays
    public void loadNewRouteData(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app",0);
        String data = sharedPreferences.getString("route", null);

        if(data != null) {

            String[] d_array = data.split("\\|");

            route1 = d_array[0].replace("\\n", "").replace("u'", "").replace("'", "");
            route2 = d_array[1].replace("\\n", "").replace("u'", "").replace("'", "");
            route3 = d_array[2].replace("\\n", "").replace("u'", "").replace("'", "");
            center = d_array[3];
            northeast = d_array[4];
            southwest = d_array[5];
            zoomMin = d_array[6];
            zoomMax = d_array[7];
            zoom = d_array[8];

            //Extract bus stops dynamically
            stops = new ArrayList<>();
            stop_ids = new ArrayList<>();
            local_route = new ArrayList<>();
            try {
                JSONObject r1 = new JSONObject(route1);
                JSONObject r2 = new JSONObject(route2);
                JSONObject r3 = new JSONObject(route3);
                populate_but_stops(r1);
                populate_but_stops(r2);
                populate_but_stops(r3);

            } catch (Exception e) {
                e.printStackTrace();
            }

            //Check how many unique routes we have:
            ArrayList<String> test_array = new ArrayList<>();
            int j = 1;
            for (int i = 0; i < local_route.size(); i++) {
                String r = local_route.get(i).toString();
                if (test_array.contains(r)) {
                    //System.out.println("test data: " + bus_route_relation.get(test_array.indexOf(r)));
                    bus_route_relation.add(bus_route_relation.get(test_array.indexOf(r)));
                } else {
                    bus_route_relation.add(j);
                    j++;
                    test_array.add(r);
                }
            }
            //System.out.println("Route array test: " + bus_route_relation.toString());
        }
    }

    private void populate_but_stops(JSONObject r1) throws JSONException {
        JSONArray d = r1.getJSONArray("features").getJSONObject(0).getJSONObject("properties").getJSONArray("stops");
        local_route.add(d);
        for(int i = 0; i < d.length();i++){
            JSONObject s = d.getJSONObject(i);

            // Check if the stop already exists, if not add it
            String stop_id = s.getString("stop_id");
            if( !stop_ids.contains(stop_id)) {
                JSONArray latlon = s.getJSONArray("coordinates");
                Feature f = Feature.fromGeometry(Point.fromLngLat(Double.parseDouble(latlon.getString(0)), Double.parseDouble(latlon.getString(1))));
                f.addStringProperty("Name", s.getString("name"));
                f.addStringProperty("Stop_id", stop_id);
                stops.add(f);
                stop_ids.add(stop_id);
            }
        }
    }

    public void bluetoothEnabledCheck(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            WebServicesUtil.createNotification(getString(R.string.bluetoothtitle), getString(R.string.bluetoothmessage), this);
            // Bluetooth is not enabled :)
        }
    }




}




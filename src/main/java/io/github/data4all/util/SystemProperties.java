package io.github.data4all.util;

import java.util.Locale;

public class SystemProperties {

	// #######################################################
	// ######## String tag for preference properties #######
	// Preference file name
	public static final String SYSTEM_PREFERENCE_FILE = "mapit_presfile";

	// ############ Initialize in Application Scope ##############
	// ############ Initialize within MapITApplication ##############
	// the dimension of phone display
	public static final String DISPLAY_WIDTH = "display_width";
	public static final String DISPLAY_HEIGHT = "display_height";
	// the dimension of language PopUp window
	public static final String PW_LANG_WIDTH = "pw_lang_width";
	public static final String PW_LANG_HEIGHT = "pw_lang_height";
	// the dimension of UserPicker PopUp window
	public static final String PW_USERPICKER_WIDTH = "pw_userpicker_width";
	public static final String PW_USERPICKER_HEIGHT = "pw_userpicker_height";
	// the dimension of UserAge PopUp window
	public static final String PW_USERAGE_WIDTH = "pw_userage_width";
	public static final String PW_USERAGE_HEIGHT = "pw_userage_height";

	// the dimension of UserName PopUp window
	public static final String PW_USERNAME_WIDTH = "pw_username_width";
	public static final String PW_USERNAME_HEIGHT = "pw_username_height";
	// the dimension of common PopUp window
	public static final String PW_WIDTH = "pw_width";
	public static final String PW_HEIGHT = "pw_height";

	// the dimension of common PopUp window
	public static final String PW_DRAW_MODE_WIDTH = "pw_draw_mode_width";
	public static final String PW_DRAW_MODE_HEIGHT = "pw_draw_mode_height";
	// the size of system icon
	public static final String SYSTEM_ICON_SIZE = "system_icon_size";

	// the observer height
	public static final String OBSERVER_HEIGHT = "observer_height";

	// camera view angles (Float)
	public static final String CAMERA_VERTICAL_VIEW_ANGLE = "camera_vertical_view_angle";
	public static final String CAMERA_HORIZONTAIL_VIEW_ANGLE = "camera_horizontal_view_angle";
	// ############ End MapITApplication ##############
	// #######################################################

	// distance from camera to center point in virtual cs (Float)
	public static final String CAMERA_DISTANCE_TO_PIC = "camera_distance_to_pic";

	// the dimension of camera preview, according to the ratio of photo size
	public static final String CAMERA_PREVIEW_WIDTH = "camera_preview_width";
	public static final String CAMERA_PREVIEW_HEIGHT = "camera_preview_height";

	// the dimension of photo
	public static final String PHOTO_WIDTH = "photo_width";
	public static final String PHOTO_HEIGHT = "photo_height";
	// the dimension of photo when showing as a thumb nail
	public static final String PHOTO_ICON_WIDTH = "photo_icon_width";
	public static final String PHOTO_ICON_HEIGHT = "photo_icon_height";

	// the dimension of canvas for touching pick, according to the display
	// size(integer)
	public static final String PICK_CANVAS_WIDTH = "pick_canvas_width";
	public static final String PICK_CANVAS_HEIGHT = "pick_canvas_height";

	// ######## System static global properties #######
	// the system locale, English as default
	public static Locale SYS_LOCALE = new Locale("en_GB");
	// the system folder
	public static final String SYSTEM_FOLDER = "/MAPIT/";
	public static final String SYSTEM_IMG_PATH = SYSTEM_FOLDER + "photos/";
	public static final String SYSTEM_DB_PATH = SYSTEM_FOLDER + "db/";
	public static final String SYSTEM_LOG_PATH = SYSTEM_FOLDER + "log/";
	public static final String SYSTEM_DATA_PATH = SYSTEM_FOLDER + "data/";

	// the export data format
	public static final String OSM_FORMAT = "OSM";
	public static final String KML = "KML";

	// geographic properties
	public static final double EARTH_RADIUS = 6371004.00;

	// the time tag for single filter
	public static final int TWO_MINUTES = 1000 * 60 * 2;

	// the properties for location provider
	// every 10 ms being forced to get new location
	public static final int LOCATION_MIN_TIME = 10;
	// regardless of location changing
	public static final int LOCATION_MIN_DISTANCE = 0;
	// max time (in ms) between two location updates
	public static final int GPS_MAX_UPDATE_INTERVALL = 5000;

	// touching pick modes
	public static final String PICKER_FREE_MODE = "f";
	public static final String PICKER_LINE_MODE = "l";
	public static final String PICKER_BUILDING_MODE = "b";

	// orientation types
	public static final String ORIENTATION_BEARING_TYPE = "bearing";
	public static final String ORIENTATION_PITCH_TYPE = "pitch";
	public static final String ORIENTATION_ROTATE_TYPE = "rotate";

	// ### Map Properties ###
	// default location for location provider
	public static final double BREMEN_LAT = 53.0884572;
	public static final double BREMEN_LON = 8.8556671;
	// default map zoom level
	public static final int OSM_MAP_DEFAULT_ZOOM = 18;
	public static final int GOOGLE_MAP_DEFAULT_ZOOM = 23;

	// speech recognition service code
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	// distance tolerance for Douglas generalization
	public static final float DISTANCE_TOLERANCE = 30.0f;

	// ######## system globe varieties #######
	// the current object id, based on system time in ms
	public static long CURRENT_OBJECT_ID;

	// the URLs for remote servers
	public static String SERVER_URL = "http://mapit.informatik.uni-bremen.de:8080/";
	public static final String SERVER_URL_SAVE_SUFFIX = "mapITServer/savemapit";
	public static final String SERVER_URL_TEST_SUFFIX = "mapITServer/test";

	public static final int SAFE_TILE_ANGLE = 85;

}

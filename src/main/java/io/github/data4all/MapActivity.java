package io.github.data4all;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.graphics.AndroidResourceBitmap;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

public class MapActivity extends Activity {

	// /TEST DATA///
	// URl for the Test Map Download
	private static final String MAPURL = "http://download.mapsforge.org/maps/europe/iceland.map";
	// name of the map file in the external storage
	private static final String MAPFILE = "iceland.map";
	// LATITUDE
	private static final double LAT = 64.10829;
	// /LONGITUDE
	private static final double LON = -21.87206;
	// /////

	private MapView mapView;
	private TileCache tileCache;
	private TileRendererLayer tileRendererLayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidGraphicFactory.createInstance(this.getApplication());

		this.mapView = new MapView(this);
		setContentView(this.mapView);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		this.mapView.setClickable(true);
		this.mapView.getMapScaleBar().setVisible(true);
		this.mapView.setBuiltInZoomControls(true);
		this.mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
		this.mapView.getMapZoomControls().setZoomLevelMax((byte) 50);

		// create a tile cache of suitable size
		this.tileCache = AndroidUtil.createTileCache(this, "mapcache",
				mapView.getModel().displayModel.getTileSize(), 1f,
				this.mapView.getModel().frameBufferModel.getOverdrawFactor());
	}

	@Override
	protected void onStart() {
		super.onStart();

		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();
		String filename = extStorageDirectory + "/" + MAPFILE;
		File file = new File(filename);
		if (!file.exists()) {
			try {
				URL u = new URL(MAPURL);
				HttpURLConnection c = (HttpURLConnection) u.openConnection();
				c.setRequestMethod("GET");
				c.setDoOutput(true);
				c.connect();
				FileOutputStream output = new FileOutputStream(new File(
						extStorageDirectory, MAPFILE));

				InputStream in = new BufferedInputStream(u.openStream());

				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = in.read(buffer)) > 0) {
					output.write(buffer, 0, count);
				}
				output.flush();
				output.close();
				in.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.mapView.getModel().mapViewPosition
				.setCenter(new LatLong(LAT, LON));
		this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 12);

		// tile renderer layer using internal render theme
		this.tileRendererLayer = new TileRendererLayer(tileCache,
				this.mapView.getModel().mapViewPosition, false, false,
				AndroidGraphicFactory.INSTANCE);
		tileRendererLayer.setMapFile(getMapFile());
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

		// only once a layer is associated with a mapView the rendering starts
		this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

	}

	@Override
	protected void onStop() {
		super.onStop();
		this.mapView.getLayerManager().getLayers()
				.remove(this.tileRendererLayer);
		this.tileRendererLayer.onDestroy();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.tileCache.destroy();
		this.mapView.getModel().mapViewPosition.destroy();
		this.mapView.destroy();
		AndroidResourceBitmap.clearResourceBitmaps();
	}

	private File getMapFile() {
		File file = new File(Environment.getExternalStorageDirectory(), MAPFILE);
		return file;
	}

}

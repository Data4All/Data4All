package io.github.data4all.model.map;

import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.OverlayWithIW;
import org.osmdroid.views.MapView;

import android.graphics.Color;
import io.github.data4all.R;
import io.github.data4all.model.data.AbstractDataElement;

public class CustomInfoWindow extends BasicInfoWindow {
    
    // Default Stroke Color
    protected static final int DEFAULT_STROKE_COLOR = Color.BLUE;

    // Fill Color for Polygons
    protected static final int DEFAULT_FILL_COLOR = Color.argb(100, 0, 0, 255);
    
    // Default Marked Color
    protected static final int DEFAULT_MARKED_COLOR= Color.RED;

    AbstractDataElement element;
    OverlayWithIW overlay;

    public CustomInfoWindow(MapView mapView, AbstractDataElement element,
            OverlayWithIW overlay) {
        super(R.layout.bonuspack_bubble, mapView);

        this.element = element;
        this.overlay = overlay;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (overlay instanceof MapPolygon) {
            MapPolygon poly = (MapPolygon) overlay;
            poly.setFillColor(DEFAULT_FILL_COLOR);
            poly.setStrokeColor(DEFAULT_STROKE_COLOR);
        }else if(overlay instanceof MapLine){
            MapLine line = (MapLine) overlay; 
            line.setColor(DEFAULT_STROKE_COLOR);
        }
    }

    @Override
    public void onOpen(Object item) {
        super.onOpen(item);
        if (overlay instanceof MapPolygon) {
            MapPolygon poly = (MapPolygon) overlay;
            poly.setFillColor(DEFAULT_MARKED_COLOR);
            poly.setStrokeColor(DEFAULT_MARKED_COLOR);
        }else if(overlay instanceof MapLine){
            MapLine line = (MapLine) overlay; 
            line.setColor(DEFAULT_MARKED_COLOR);
        }
    }
}

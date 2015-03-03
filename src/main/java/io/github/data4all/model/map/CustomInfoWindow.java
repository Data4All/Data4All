package io.github.data4all.model.map;

import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.OverlayWithIW;
import org.osmdroid.views.MapView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import io.github.data4all.R;
import io.github.data4all.activity.AbstractActivity;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.model.data.AbstractDataElement;

public class CustomInfoWindow extends BasicInfoWindow implements
        OnClickListener, DialogInterface.OnClickListener {

    // Default Stroke Color
    protected static final int DEFAULT_STROKE_COLOR = Color.BLUE;

    // Fill Color for Polygons
    protected static final int DEFAULT_FILL_COLOR = Color.argb(100, 0, 0, 255);

    // Default Marked Color
    protected static final int DEFAULT_MARKED_COLOR = Color.RED;

    AbstractDataElement element;
    OverlayWithIW overlay;
    AbstractActivity activity;

    public CustomInfoWindow(MapView mapView, AbstractDataElement element,
            OverlayWithIW overlay, AbstractActivity activity) {
        super(R.layout.bonuspack_bubble, mapView);

        this.activity = activity;
        this.element = element;
        this.overlay = overlay;

        int id = R.id.bubble_delete;
        final Button delete = (Button) mView.findViewById(id);
        delete.setOnClickListener(this);

        id = R.id.bubble_edit;
        final Button edit = (Button) mView.findViewById(id);
        edit.setOnClickListener(this);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (overlay instanceof MapPolygon) {
            MapPolygon poly = (MapPolygon) overlay;
            poly.setFillColor(DEFAULT_FILL_COLOR);
            poly.setStrokeColor(DEFAULT_STROKE_COLOR);
        } else if (overlay instanceof MapLine) {
            MapLine line = (MapLine) overlay;
            line.setColor(DEFAULT_STROKE_COLOR);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bubble_delete:
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    mMapView.getContext());
            builder.setMessage(activity.getString(R.string.deleteDialog))
                    .setPositiveButton(activity.getString(R.string.yes), this)
                    .setNegativeButton(activity.getString(R.string.no), this)
                    .show();
            break;
        case R.id.bubble_edit:
            break;
        default:
            break;
        }
    }

    @Override
    public void onOpen(Object item) {
        super.onOpen(item);
        InfoWindow.closeAllInfoWindowsOn(mMapView);
        if (overlay instanceof MapPolygon) {
            MapPolygon poly = (MapPolygon) overlay;
            poly.setFillColor(DEFAULT_MARKED_COLOR);
            poly.setStrokeColor(DEFAULT_MARKED_COLOR);
        } else if (overlay instanceof MapLine) {
            MapLine line = (MapLine) overlay;
            line.setColor(DEFAULT_MARKED_COLOR);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.content.DialogInterface.OnClickListener#onClick(android.content
     * .DialogInterface, int)
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            // Yes button clicked
            mMapView.getOverlays().remove(overlay);
            final DataBaseHandler db = new DataBaseHandler(activity);
            db.deleteDataElement(element);
            db.close();
            mMapView.postInvalidate();
            close();
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            // No button clicked
            break;
        default:
            break;
        }
    }
}

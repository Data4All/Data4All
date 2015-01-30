package io.github.data4all.model.map;

import io.github.data4all.R;
import io.github.data4all.activity.AbstractMapActivity;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.views.MapView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;

/**
 * With LongClick deletable Polyline.
 * 
 * @author Oliver Schwartz
 *
 */
class MapLine extends Polyline implements DialogInterface.OnClickListener {

    AbstractMapActivity mapActivity;

    /**
     * Default constructor.
     * 
     * @param ctx
     *            the Context for the Overlay
     */
    public MapLine(AbstractMapActivity ctx) {
        super(ctx);

    }

    @Override
    public boolean onLongPress(final MotionEvent e, final MapView mapView) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                mapView.getContext());
        builder.setMessage(mapActivity.getString(R.string.deleteDialog))
                .setPositiveButton(mapActivity.getString(R.string.yes), this)
                .setNegativeButton(mapActivity.getString(R.string.no), this)
                .show();

        return true;

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            // Yes button clicked
            mapActivity.removeOverlayFromMap(this);
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            // No button clicked
            break;
        default:
            break;
        }
    }
}

package io.github.data4all.preference;

import io.github.data4all.R;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.model.data.User;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * This preference displays the current user and provides a dialog for logout.
 * 
 * @author timo
 */
public class CurrentUserPreference extends Preference {
    /**
     * The logged in user.
     */
    private User user;

    /**
     * Constructor to create a Preference.
     * 
     * @param context
     *            The Context this is associated with, through which it can
     *            access the current theme, resources, SharedPreferences, etc.
     */
    public CurrentUserPreference(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor that is called when inflating a Preference from XML.
     * 
     * @param context
     *            The Context this is associated with, through which it can
     *            access the current theme, resources, SharedPreferences, etc.
     * @param attrs
     *            The attributes of the XML tag that is inflating the
     *            preference.
     */
    public CurrentUserPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     * 
     * @param context
     *            The Context this is associated with, through which it can
     *            access the current theme, resources, SharedPreferences, etc.
     * @param attrs
     *            The attributes of the XML tag that is inflating the
     *            preference.
     * @param defStyleAttr
     *            An attribute in the current theme that contains a reference to
     *            a style resource that supplies default values for the view.
     *            Can be 0 to not look for defaults.
     */
    public CurrentUserPreference(Context context, AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initializes the preference and loads the user.
     */
    private void init() {
        final DataBaseHandler db = new DataBaseHandler(getContext());
        final List<User> allUser = db.getAllUser();
        db.close();
        if (!allUser.isEmpty()) {
            this.user = allUser.get(0);
        }
    }

    /**
     * Performs a logout for the current user.
     */
    private void logoutUser() {
        final DataBaseHandler db = new DataBaseHandler(getContext());
        db.deleteUser(this.user);
        db.close();
        this.user = null;
        this.notifyChanged();
    }

    /**
     * The name of the current user or a note that the user isn't logged in.
     */
    @Override
    public CharSequence getSummary() {
        if (user == null) {
            return getContext().getString(R.string.pref_currentuser_null);
        } else {
            return user.getUsername();
        }
    }

    /**
     * The logout dialog is only showable if a user is logged in.
     */
    @Override
    public boolean isEnabled() {
        return super.isEnabled() && (user != null);
    }

    /**
     * Cause this Preference is stored its data in a database, these is no need
     * to store a value in the shared preferences.
     */
    @Override
    public boolean isPersistent() {
        return false;
    }

    /**
     * Shows a logout dialog on click.
     */
    @Override
    protected void onClick() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.pref_currentuser_dialog_title)
                .setMessage(R.string.pref_currentuser_dialog_message)
                .setPositiveButton(android.R.string.yes, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CurrentUserPreference.this.logoutUser();
                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }
}

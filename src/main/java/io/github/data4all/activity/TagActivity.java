/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.handler.LastChoiceHandler;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.ClassifiedValue;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.util.SpeechRecognition;
import io.github.data4all.util.Tagging;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author Maurice Boyke
 *
 */
public class TagActivity extends AbstractActivity implements OnClickListener {

    // OSMElement Key
    protected static final String OSM = "OSM_ELEMENT";
    private static final int REQUEST_CODE = 1234;
    final Context context = this;
    // The Key of the Classified Tag
    private String key;
    // The map in where the selected Tags are saved
    private Map<Tag, String> map;
    // The list of all shown edittext
    private List<EditText> edit;
    // The boolean if the createDialog is started the first time
    private Boolean first;
    // The Dialog of Unclassified Tags
    private Dialog dialog1;
    // The array with all the Choices of the Classified tags
    private CharSequence[] array;
    // The alertDialog of the Classified keys
    private AlertDialog alert;
    // the alerDialog of the Classified Values
    private AlertDialog alert1;
    // The map were the String are saved wiht the real Tag
    private Map<String, ClassifiedTag> tagMap;
    // The logger
    private static final String TAG = "TagActivity";
    // The abstractDataElement of the Intent
    private AbstractDataElement element;
    // The Ressource
    private Resources res;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            If the activity is being re-initialized after previously being
     *            shut down then this Bundle contains the data it most recently
     *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
     *            is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.activity_tag);
        element = getIntent().getParcelableExtra(OSM);
        res = getResources();
        createAlertDialogKey();

    }

    /**
     * Creates the Dialog to choose the Key from the classified Tags
     * 
     */
    private void createAlertDialogKey() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                TagActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth);
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.drawable.header_listview, null);
        ((TextView) view.findViewById(R.id.titleDialog))
                .setText(R.string.SelectTag);
        ;
        alertDialog.setCustomTitle(view);
        final ImageButton speechStart = (ImageButton) view
                .findViewById(R.id.speech);
        speechStart.setOnClickListener(this);
        array = Tagging.getArrayKeys(
                getIntent().getExtras().getInt("TYPE_DEF"), res);
        tagMap = Tagging.getMapKeys(getIntent().getExtras().getInt("TYPE_DEF"),
                res);
        alertDialog.setItems(array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                key = (String) array[which];
                //jump in ResultviewActivity, when lastChoice is selected
                if("Last Choice".equalsIgnoreCase(key)){
                    map=LastChoiceHandler.getInstance().getLastChoice(getIntent().getExtras().getInt("TYPE_DEF"));
                    finish();
                }else{
                createAlertDialogValue();
                }
            }

        });
        alert = alertDialog.create();
        // fixes the Back Button
        alert.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return true;
            }
        });

        alert.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.show();

    }

    /**
     * Creates the Dialog to choose the Value of the ClassifiedTag
     * 
     */
    private void createAlertDialogValue() {
        array = Tagging.ClassifiedValueList(tagMap.get(key)
                .getClassifiedValues(), res);
        final Map<String, ClassifiedValue> classifiedMap = Tagging
                .classifiedValueMap(tagMap.get(key).getClassifiedValues(), res,
                        false);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                TagActivity.this);
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.drawable.header_listview, null);
        ((TextView) view.findViewById(R.id.titleDialog))
                .setText(R.string.SelectTag);
        ;
        alertDialogBuilder.setCustomTitle(view);
        alertDialogBuilder.setItems(array,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String value = (String) array[which];
                        String realValue = classifiedMap.get(value).getValue();
                       
                        map = new LinkedHashMap<Tag, String>();
                        map.put(tagMap.get(key), realValue);
                        Log.i(TAG, tagMap.get(key) + realValue);

                        createDialog(Tags.getAllAddressTags(),
                                getString(R.string.AddAddress), true);
                        
                    }
                });

        alert1 = alertDialogBuilder.create();
        alert1.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert1.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    alert1.dismiss();
                    createAlertDialogKey();
                    return true;
                }
                return true;
            }
        });
        alert1.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.speech:
            final Intent intent = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, REQUEST_CODE);
            alert.dismiss();
            break;
        case R.id.buttonNext:
            editTextToMap(first);
            createDialog(Tags.getAllContactTags(),
                    getString(R.string.AddContact), false);
            break;
        case R.id.buttonFinish:
            editTextToMap(first);
            finish();
            break;
        default:
            break;
        }
    }

    /**
     * saves all the edited Tags to the Map
     * 
     * @param first
     *            if true address tags else contact
     */
    private void editTextToMap(boolean first) {
        final List<String> tags = new ArrayList<String>();

        for (int i = 0; i < edit.size(); i++) {
            tags.add(edit.get(i).getText().toString());
        }
        if (first) {
            map = Tagging.addressToTag(tags, map);
        } else {
            map = Tagging.contactToTag(tags, map);
        }
        LastChoiceHandler.getInstance().setLastChoice(getIntent().getExtras().getInt("TYPE_DEF"),map);
        LastChoiceHandler.getInstance().save(this);
        dialog1.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            new Dialog(TagActivity.this);
            final ListView textList = (ListView) findViewById(R.id.listView1);
            final List<String> matchesText = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            SpeechRecognition.splitStrings(matchesText);
            // final Map<String, String> map =
            // SpeechRecognition.speechToTag(matchesText);
            matchesText.clear();
            for (Entry entry : map.entrySet()) {
                key = (String) entry.getKey();
                matchesText.add(key + "=" + map.get(key));
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, matchesText);
            textList.setAdapter(adapter);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Creates the Dialog for the unclassified Tags
     * 
     * @param arrayList
     *            List of Unclassified Tags
     * @param title
     *            The String which is the Title of the Dialog
     * @param first1
     *            Is a Boolean to check if its the first time call of the Method
     */

    public void createDialog(List<Tag> arrayList, String title,
            final Boolean first1) {

        dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.dialog_dynamic);
        dialog1.setTitle(title);
        final LinearLayout layout = (LinearLayout) dialog1
                .findViewById(R.id.dialogDynamic);
        final Button next = new Button(this);
        final Button finish = new Button(this);
        next.setText(R.string.next);
        finish.setText(R.string.finish);
        next.setId(R.id.buttonNext);
        finish.setId(R.id.buttonFinish);
        first = first1;
        edit = new ArrayList<EditText>();
        for (int i = 0; i < arrayList.size(); i++) {
            final EditText text = new EditText(this);
            final Tag tag = arrayList.get(i);
            if (tag.getLastValue() != null) {
                text.setText(tag.getLastValue());
            } else {
                text.setHint(tag.getHintRessource());
            }
            text.setHintTextColor(Color.DKGRAY);
            text.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            Log.i(TAG, "TYpoe " + arrayList.get(i).getType());
            text.setInputType(arrayList.get(i).getType());
            Log.i(TAG, text.toString());
            edit.add(text);
            layout.addView(text);
        }
        finish.setOnClickListener(this);
        next.setOnClickListener(this);

        if (Tagging.isContactTags(Tags.getAllContactTags().get(0)
                .getOsmObjects(), getIntent().getExtras().getInt("TYPE_DEF"))
                && first) {
            layout.addView(next);
        }
        layout.addView(finish);

        final InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        dialog1.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (imm.isAcceptingText()) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        // Log.i(TAG, "first " + first);
                        if (first) {
                            dialog1.dismiss();
                            createAlertDialogValue();
                            return true;
                        } else {
                            dialog1.dismiss();
                            createDialog(Tags.getAllAddressTags(),
                                    getString(R.string.AddAddress), true);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        dialog1.show();
    }

    @Override
    public void finish() {
        element.setTags(map);
        final Intent intent = new Intent(this, ResultViewActivity.class);
        intent.putExtra(OSM, element);
        intent.putExtra("TYPE_DEF", getIntent().getExtras().getInt("TYPE_DEF"));
        super.finish();
        startActivity(intent);
    }
}
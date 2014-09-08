package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.domain.SensorManager;

import java.util.List;

/** This activity is in charge of the creation of MonitoringItems.
 * Created by 7flying on 11/08/2014.
 */
public class MoniItemCreationActivity extends FragmentActivity {

    private EditText etName;
    private ImageView imagePreview;
    private Button buttonCreate;
    private List<Sensor> sensorList;
    private SensorCheckAdapter adapter;
    private ImageButton buttonTakePhoto;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_item_creation);
        buttonCreate = (Button) findViewById(R.id.button_create);
        buttonCreate.setEnabled(false);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonitoringItem monitoringItem = new MonitoringItem(etName.getText().toString());
               /*  Drawables are not serializable
                    monitoringItem.setIcon(imagePreview.getDrawable() == null ?
                        getResources().getDrawable(R.drawable.ic_leaf_green) :
                        imagePreview.getDrawable());
                */
                // TODO set icon from image
                monitoringItem.setIcon(R.drawable.ic_leaf_green);
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.isChecked(adapter.getItem(i))) {
                        monitoringItem.addSensor(adapter.getItem(i));
                    }
                }
                Intent returnIntent = new Intent();
                if (etName.isEnabled())
                    returnIntent.putExtra("moni-item", monitoringItem);
                else
                    returnIntent.putExtra("moni-item-result", monitoringItem);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        etName = (EditText) findViewById(R.id.et_moni_name);
        etName.addTextChangedListener(new TextWatcher() {
            // Check data
            public void afterTextChanged(Editable editable) {
                  buttonCreate.setEnabled(etName.getText().length() > 0);
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
        });
        imagePreview = (ImageView) findViewById(R.id.image_preview);
        buttonTakePhoto = (ImageButton) findViewById(R.id.button_take_photo);
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        ListView listViewSensos = (ListView) findViewById(R.id.list_check_sensors);
        sensorList =  SensorManager.getInstance(getApplicationContext())
                .getSensors();
        adapter = new SensorCheckAdapter(getApplicationContext(), R.layout.sensor_check_row,
                sensorList);
        listViewSensos.setAdapter(adapter);
        if(getIntent().hasExtra("moni-to-edit")) {
            MonitoringItem extra = (MonitoringItem) getIntent().getSerializableExtra("moni-to-edit");
            etName.setText(extra.getName());
            etName.setEnabled(false);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagePreview.setImageBitmap(imageBitmap);
        }
    }

    // TODO: coninue on: http://developer.android.com/training/camera/photobasics.html
}

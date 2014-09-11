package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/** This activity is in charge of the creation of MonitoringItems.
 * Created by 7flying on 11/08/2014.
 */
public class MoniItemCreationActivity extends FragmentActivity {

    private EditText etName;
    private ImageView imagePreview;
    private String photoPath = null;
    private Button buttonCreate;
    private List<Sensor> sensorList;
    private SensorCheckAdapter adapter;
    private ImageButton buttonTakePhoto, buttonFromGallery, buttonDefault;
    private static final int REQUEST_IMAGE_CAPTURE = 1, PICK_IMAGE = 2;

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
                // Set image
                monitoringItem.setPhotoPath(photoPath);
                // Sensors
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
        buttonFromGallery = (ImageButton) findViewById(R.id.button_from_gallery);
        buttonFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchSelectFromGalleryIntent();
            }
        });
        buttonDefault = (ImageButton) findViewById(R.id.button_leave_default);
        buttonDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePreview.setImageDrawable(getResources().getDrawable(R.drawable.ic_leaf_green));
                photoPath = null;
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
            if(extra.getPhotoPath() != null)
                imagePreview.setImageBitmap(BitmapFactory.decodeFile(extra.getPhotoPath()));
            else
                imagePreview.setImageDrawable(getResources().getDrawable(R.drawable.ic_leaf_green));
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photo = null;
            try {
                photo = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photo != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                addPhotoToGallery("file:" + photo.getAbsolutePath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                photoPath = photo.getPath();
            }
        }
    }

    private void dispatchSelectFromGalleryIntent() {
        Intent pickImageIntent = new Intent();
        pickImageIntent.setType("image/*");
        pickImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickImageIntent, ""), PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The user has taken a photo
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if(resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imagePreview.setImageBitmap(imageBitmap);
            }
        } else {
            // Image comes from gallery
            if(requestCode == PICK_IMAGE) {
                if(resultCode == RESULT_OK) {
                    Uri _uri = data.getData();
                    // Picked image
                    Cursor cursor = getContentResolver().query(_uri, new String[] {
                            MediaStore.Images.ImageColumns.DATA }, null, null, null);
                    cursor.moveToFirst();
                    // Get link from image
                    String imageFilePath = cursor.getString(0);
                    cursor.close();
                    photoPath = imageFilePath;
                    imagePreview.setImageBitmap(BitmapFactory.decodeFile(imageFilePath));
                }
            }
        }
    }

    /** Generates a unique file name.
     * @return file     */
    private File createImageFile() throws IOException {
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File ret = File.createTempFile(
                "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date()) + "_",
                ".jpg",
                storageDir);
        //addPhotoToGallery("file:" + ret.getAbsolutePath());
        return ret;
    }

    /** Given a photo path adds it to the gallery
     * @param photoPath - path of the photo to add
     */
    private void addPhotoToGallery(String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(photoPath));
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}

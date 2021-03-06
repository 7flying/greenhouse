package com.sevenflying.greenhouseclient.app.statustab;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.sevenflying.greenhouseclient.app.R;
import com.sevenflying.greenhouseclient.app.utils.ImageLoader;
import com.sevenflying.greenhouseclient.database.DBManager;
import com.sevenflying.greenhouseclient.app.utils.Extras;
import com.sevenflying.greenhouseclient.domain.MonitoringItem;
import com.sevenflying.greenhouseclient.domain.Sensor;
import com.sevenflying.greenhouseclient.net.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/** This activity is in charge of the creation of MonitoringItems.
 * Created by 7flying on 11/08/2014.
 */
public class MoniItemCreationActivity extends ActionBarActivity {

    private EditText etName;
    private ImageView imagePreview;
    private String photoPath = null;
    private Button buttonCreate;
    private SensorCheckAdapter adapter;
    private MonitoringItem current;
    private static final int REQUEST_IMAGE_CAPTURE = 1, PICK_IMAGE = 2;
    private static ImageLoader loader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if (loader == null)
            loader = new ImageLoader(getApplicationContext());
        setContentView(R.layout.activity_mon_item_creation);
        buttonCreate = (Button) findViewById(R.id.button_create);
        buttonCreate.setEnabled(false);
        buttonCreate.setTextColor(getResources().getColor(
                R.color.switch_thumb_normal_material_dark));
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Overwrite current Item (edition) or create a new one
                if (current == null)
                    current = new MonitoringItem(etName.getText().toString().trim());
                else
                    current.setName(etName.getText().toString().trim());
                // Set image
                current.setPhotoPath(photoPath);
                // Sensors
                current.clearSensors();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.isChecked(adapter.getItem(i))) {
                        current.addSensor(adapter.getItem(i));
                    }
                }
                Intent returnIntent = new Intent();
                if (etName.isEnabled())
                    returnIntent.putExtra(Extras.EXTRA_MONI, current);
                else
                    returnIntent.putExtra(Extras.EXTRA_MONI_RESULT, current);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        etName = (EditText) findViewById(R.id.et_moni_name);
        etName.addTextChangedListener(new TextWatcher() {
            // Check data
            public void afterTextChanged(Editable editable) {
                if (etName.getText().length() > 0) {
                    buttonCreate.setEnabled(true);
                    buttonCreate.setTextColor(getResources().getColor(
                            R.color.bright_foreground_inverse_material_dark));
                } else {
                    buttonCreate.setEnabled(false);
                    buttonCreate.setTextColor(getResources().getColor(
                            R.color.switch_thumb_normal_material_dark));
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
        });
        imagePreview = (ImageView) findViewById(R.id.image_preview);

        Button buttonTakePhoto = (Button) findViewById(R.id.button_take_photo);
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.DEBUGTAG, " $ MoniItemCreation, dispatch take picture");
                dispatchTakePictureIntent();
            }
        });

        Button buttonDefault = (Button) findViewById(R.id.button_leave_default);
        buttonDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loader.loadBitmapResource(R.drawable.ic_leaf_green, imagePreview);
                photoPath = null;
            }
        });
        ListView listViewSensors = (ListView) findViewById(R.id.list_check_sensors);
        List<Sensor> sensorList =  new DBManager(getApplicationContext()).getSensors();
        adapter = new SensorCheckAdapter(getApplicationContext(), R.layout.sensor_check_row,
                sensorList);
        listViewSensors.setAdapter(adapter);
        if (getIntent().hasExtra(Extras.EXTRA_MONI_EDIT)) {
            getSupportActionBar().setTitle(getResources().getString(R.string.title_edit_item));
            current = (MonitoringItem) getIntent().getSerializableExtra(Extras.EXTRA_MONI_EDIT);
            Log.d(Constants.DEBUGTAG, " $ MonItemCreation extraItem: " + current.toString());
            etName.setText(current.getName());
            etName.setEnabled(false);
            if (current.getPhotoPath() != null) {
                imagePreview.post(new Runnable() {
                    @Override
                    public void run() {
                        Matrix m = new Matrix();
                        m.postRotate(90);
                        Bitmap btm = BitmapFactory.decodeFile(current.getPhotoPath());
                        if (btm != null) {
                            Bitmap newb = Bitmap.createBitmap(btm, 0, 0, btm.getWidth(),
                                    btm.getHeight(), m, true);
                            imagePreview.setImageBitmap(newb);
                        }
                    }
                });
            } else
                loader.loadBitmapResource(R.drawable.ic_leaf_green, imagePreview);
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d(Constants.DEBUGTAG, " $ MonItemCreation dispatching take picture");

            File photo = null;
            try {
                photo = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photo != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

                photoPath = photo.getPath();
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                addPhotoToGallery("file:" + photo.getAbsolutePath());

                Log.d(Constants.DEBUGTAG, " $ MonItemCreation photopath-dispatch: " + photoPath);
                Log.d(Constants.DEBUGTAG, " $ MonItemCreation absolutepath-dispatch: "
                        + photo.getAbsolutePath());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The user has taken a photo
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                imagePreview.post(new Runnable() {
                    @Override
                    public void run() {
                        Matrix m = new Matrix();
                        m.postRotate(0);
                        Bitmap btm = BitmapFactory.decodeFile(photoPath);
                        if (btm != null) {
                            Bitmap newb = Bitmap.createBitmap(btm, 0, 0, btm.getWidth(), btm.getHeight(), m, true);
                            imagePreview.setImageBitmap(newb);
                        }
                    }
                });
            }
        } else {
            // Image comes from gallery
            if (requestCode == PICK_IMAGE) {
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
                    imagePreview.invalidate();
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
        addPhotoToGallery("file:" + ret.getAbsolutePath());
        Log.d(Constants.DEBUGTAG, " $ MonItemCreation returning file: " + ret);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basic_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.jjarvis2;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class Frag4 extends Fragment {
    View view;

    TextView tv_frag4_date, tv_calorie_breakfast, tv_calorie_lunch, tv_calorie_dinner, tv_calorie_total;
    ImageButton ibtn_previous_date, ibtn_next_date, ibtn_breakfast, ibtn_lunch, ibtn_dinner;
    String[] LABELS = {"baby_back_ribs", "bibimbap", "cheesecake", "chicken_wings", "chocolate_cake", "churros", "donuts",
                        "dumplings", "french_fries", "french_toast", "fried_rice", "garlic_bread", "gyoza", "hamburger",
                        "hot_dog", "ice_cream", "macarons", "omelette", "pizza", "ramen", "spaghetti_bolognese",
                        "spaghetti_carbonara", "steak", "sushi", "takoyaki","waffles"};
    int[] CALORIES = {612, 560, 321, 203, 370, 237, 452, 120, 311, 229, 163, 349, 330, 520,
                        289, 207, 403, 153, 266, 436, 351, 646, 270, 45, 70, 291};
    String when = null;
    int breakfast, lunch, dinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag4, container, false);

        getXmlId();
        getDate();

        ibtn_breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                when = "breakfast";
            }
        });
        ibtn_lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                when = "lunch";
            }
        });
        ibtn_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                when = "dinner";
            }
        });

        return view;
    }

    public void getDate() {
        Calendar mCalendar = Calendar.getInstance();

    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                Activity activity = getActivity();
                ContentResolver resolver = activity.getContentResolver();
                try {
                    InputStream inputStream = resolver.openInputStream(fileUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);

                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    imgBitmap = Bitmap.createBitmap(imgBitmap,0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), matrix, true);
                    imgBitmap = Bitmap.createScaledBitmap(imgBitmap, ibtn_breakfast.getWidth(), ibtn_breakfast.getWidth(), true);
                    if (when.equals("breakfast")) {
                        ibtn_breakfast.setImageBitmap(imgBitmap);
                    } else if (when.equals("lunch")) {
                        ibtn_lunch.setImageBitmap(imgBitmap);
                    } else if (when.equals("dinner")) {
                        ibtn_dinner.setImageBitmap(imgBitmap);
                    }
                    inputStream.close();

                    Interpreter tflite = getTfliteInterpreter("cnn.tflite");

                    float[][][][] bytes_img = new float[1][128][128][3];
                    imgBitmap = Bitmap.createScaledBitmap(imgBitmap, 128, 128, true);

                    for(int y = 0; y < 128; y++) {
                        for(int x = 0; x < 128; x++) {
                            int pixel = imgBitmap.getPixel(x, y);
                            int bValue = pixel & 0x0000FF;
                            int gValue = (pixel & 0x00FF00) >> 8;
                            int rValue = (pixel & 0xFF0000) >> 16;
                            bytes_img[0][y][x][0] = rValue / (float) 255;
                            bytes_img[0][y][x][1] = gValue / (float) 255;
                            bytes_img[0][y][x][2] = bValue / (float) 255;
                        }
                    }
                    float[][] output = new float[1][26];

                    tflite.run(bytes_img, output);
                    Log.d("predict", Arrays.toString(output[0]));
                    int index = 0;
                    float prediction = 0;
                    for(int i = 0; i < 26; i++) {
                        if (output[0][i] > prediction) {
                            index = i;
                            prediction = output[0][i];
                        }
                    }
                    String calorieInfo = LABELS[index] + "(" + Float.toString(prediction*100) + "%) " + CALORIES[index];
                    if (when.equals("breakfast")) {
                        tv_calorie_breakfast.setText(calorieInfo);
                        breakfast = CALORIES[index];
                    } else if (when.equals("lunch")) {
                        tv_calorie_lunch.setText(calorieInfo);
                        lunch = CALORIES[index];
                    } else if (when.equals("dinner")) {
                        tv_calorie_dinner.setText(calorieInfo);
                        dinner = CALORIES[index];
                    }
                    Log.d("predict", "음식: " + LABELS[index] + "\n칼로리: " + CALORIES[index] + "\n확률: " + Float.toString(prediction*100));
                    int totalCalorie = breakfast + lunch + dinner;
                    tv_calorie_total.setText(Integer.toString(totalCalorie));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(getActivity(), modelPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void getXmlId() {
        tv_frag4_date = view.findViewById(R.id.tv_frag4_date);
        tv_calorie_breakfast = view.findViewById(R.id.tv_calorie_breakfast);
        tv_calorie_lunch = view.findViewById(R.id.tv_calorie_lunch);
        tv_calorie_dinner = view.findViewById(R.id.tv_calorie_dinner);
        tv_calorie_total = view.findViewById(R.id.tv_calorie_total);
        ibtn_previous_date = view.findViewById(R.id.ibtn_previous_date);
        ibtn_next_date = view.findViewById(R.id.ibtn_next_date);
        ibtn_breakfast = view.findViewById(R.id.ibtn_breakfast);
        ibtn_lunch = view.findViewById(R.id.ibtn_lunch);
        ibtn_dinner = view.findViewById(R.id.ibtn_dinner);
    }
}


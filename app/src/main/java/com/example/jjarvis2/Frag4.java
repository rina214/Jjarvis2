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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class Frag4 extends Fragment {
    View view;

    TextView tv_frag4_date, tv_calorie_total;
    EditText et_calorie_breakfast, et_calorie_lunch, et_calorie_dinner,
            et_menu_breakfast, et_menu_lunch, et_menu_dinner;
    ImageButton ibtn_previous_date, ibtn_next_date, ibtn_breakfast, ibtn_lunch, ibtn_dinner;
    Button btn_frag4_save;
    String[] LABELS = {"baby_back_ribs", "bibimbap", "cheesecake", "chicken_wings", "chocolate_cake", "churros", "donuts",
            /*음식*/          "dumplings", "french_fries", "french_toast", "fried_rice", "garlic_bread", "gyoza", "hamburger",
            "hot_dog", "ice_cream", "macarons", "omelette", "pizza", "ramen", "spaghetti_bolognese",
            "spaghetti_carbonara", "steak", "sushi", "takoyaki","waffles"};
    int[] CALORIES = {612, 560, 321, 203, 370, 237, 452, 120, 311, 229, 163, 349, 330, 520,
            /*칼로리*/       289, 207, 403, 153, 266, 436, 351, 646, 270, 45, 70, 291};
    String when = null;
    Map<String, Integer> exercise = new HashMap<>();
    int date, breakfast, lunch, dinner, total;
    Calendar calendar;
    DatabaseReference mDatabase; //파이어베이스 데이터베이스
    boolean check = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag4, container, false);

        getXmlId();
        getDate();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ibtn_breakfast.setOnClickListener(new View.OnClickListener() { //아침 + 버튼을 누르면
            @Override
            public void onClick(View v) {
                openGallery();
                when = "breakfast";
            }
        });
        ibtn_lunch.setOnClickListener(new View.OnClickListener() { //점심 + 버튼을 누르면
            @Override
            public void onClick(View v) {
                openGallery();
                when = "lunch";
            }
        });
        ibtn_dinner.setOnClickListener(new View.OnClickListener() { //저녁 + 버튼을 누르면
            @Override
            public void onClick(View v) {
                openGallery();
                when = "dinner";
            }
        });
        ibtn_previous_date.setOnClickListener(new View.OnClickListener() { //전날 버튼 눌렀을 때
            @Override
            public void onClick(View v) {
                getPreviousDate();
            }
        });
        ibtn_next_date.setOnClickListener(new View.OnClickListener() { //다음날 버튼 눌렀을 때
            @Override
            public void onClick(View v) {
                getNextDate();
            }
        });
        btn_frag4_save.setOnClickListener(new View.OnClickListener() { //저장 버튼 눌렀을 때
            @Override
            public void onClick(View v) {
                saveAll();
            }
        });
        et_calorie_breakfast.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) { //입력이 끝났을 때
                breakfast = Integer.parseInt(s.toString());
                int totalCalorie = breakfast + lunch + dinner;
                tv_calorie_total.setText(Integer.toString(totalCalorie));
            }
        });
        et_calorie_lunch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                lunch = Integer.parseInt(s.toString());
                int totalCalorie = breakfast + lunch + dinner;
                tv_calorie_total.setText(Integer.toString(totalCalorie));
            }
        });
        et_calorie_dinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                dinner = Integer.parseInt(s.toString());
                int totalCalorie = breakfast + lunch + dinner;
                tv_calorie_total.setText(Integer.toString(totalCalorie));
            }
        });

        return view;
    }

    public void getDate() {
        calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(currentTime);
        tv_frag4_date.setText(strDate);
    } //오늘 날짜 설정

    public void getPreviousDate() {
        calendar.add(Calendar.DAY_OF_WEEK, -1); //1일 전
        Date currentTime = calendar.getTime();
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(currentTime);
        tv_frag4_date.setText(strDate);
    } //이전날

    public void getNextDate() {
        calendar.add(Calendar.DAY_OF_WEEK, 1); //1일 후
        Date currentTime = calendar.getTime();
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(currentTime);
        tv_frag4_date.setText(strDate);
    } //다음날

    public void saveAll() {
        String strDate = tv_frag4_date.getText().toString();
        strDate = strDate.replace(".", "");
        date = Integer.parseInt(strDate);
        if(et_calorie_breakfast.getText().toString().equals("")){
            breakfast = 0;
        } else
            breakfast = Integer.parseInt(et_calorie_breakfast.getText().toString());
        if(et_calorie_lunch.getText().toString().equals("")){
            lunch = 0;
        } else
            lunch = Integer.parseInt(et_calorie_lunch.getText().toString());
        if(et_calorie_dinner.getText().toString().equals("")){
            dinner = 0;
        } else
            dinner = Integer.parseInt(et_calorie_dinner.getText().toString());
        total = Integer.parseInt(tv_calorie_total.getText().toString());
        int year = Integer.parseInt(strDate.substring(0,4));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,Integer.parseInt(strDate.substring(4, 6))-1,Integer.parseInt(strDate.substring(6, 8)));
        String week = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));

        mDatabase.child("userdata").child("test2").child(String.valueOf(year)).child(week).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("snapshot", String.valueOf(dataSnapshot.getChildrenCount()));
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("snapshot", snapshot.toString());
                    specification userdata = snapshot.getValue(specification.class);
                    if (userdata.getDate() == date) {
                        exercise = userdata.getExercise();
                        specification spec = new specification(date,breakfast,lunch,dinner, total, exercise);
                        mDatabase.child("userdata").child("test2").child(String.valueOf(spec.year())).child(String.valueOf(spec.week())).child(String.valueOf(spec.getDate())).setValue(spec);
                        return;
                    }
                }
                specification spec = new specification(date,breakfast,lunch,dinner, total, exercise);
                mDatabase.child("userdata").child("test2").child(String.valueOf(spec.year())).child(String.valueOf(spec.week())).child(String.valueOf(spec.getDate())).setValue(spec);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void openGallery() { //갤러리 열기
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //갤러리 열고 result를 받을 때
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                Activity activity = getActivity();
                ContentResolver resolver = activity.getContentResolver();
                try {
                    InputStream inputStream = resolver.openInputStream(fileUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream); //사진 Bitmap
                    if(imgBitmap.getWidth() < imgBitmap.getHeight()) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        imgBitmap = Bitmap.createBitmap(imgBitmap,0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), matrix, true);
                    }
                    imgBitmap = Bitmap.createScaledBitmap(imgBitmap, ibtn_breakfast.getWidth(), ibtn_breakfast.getWidth(), true);
                    if (when.equals("breakfast")) {
                        ibtn_breakfast.setImageBitmap(imgBitmap);
                    } else if (when.equals("lunch")) {
                        ibtn_lunch.setImageBitmap(imgBitmap);
                    } else if (when.equals("dinner")) {
                        ibtn_dinner.setImageBitmap(imgBitmap);
                    }
                    inputStream.close();

                    Interpreter tflite = getTfliteInterpreter("cnn.tflite"); //머신러닝

                    float[][][][] bytes_img = new float[1][128][128][3];
                    imgBitmap = Bitmap.createScaledBitmap(imgBitmap, 128, 128, true); //머신러닝이 읽을 수 있게 사진 크기 조정 (128, 128, 3)

                    for(int y = 0; y < 128; y++) {
                        for(int x = 0; x < 128; x++) {
                            int pixel = imgBitmap.getPixel(x, y);
                            int bValue = pixel & 0x0000FF;
                            int gValue = (pixel & 0x00FF00) >> 8;
                            int rValue = (pixel & 0xFF0000) >> 16;
                            bytes_img[0][y][x][0] = rValue / (float) 255; //R
                            bytes_img[0][y][x][1] = gValue / (float) 255; //G
                            bytes_img[0][y][x][2] = bValue / (float) 255; //B
                        }
                    }
                    float[][] output = new float[1][26];

                    tflite.run(bytes_img, output); //머신러닝의 예측값을 output에 저장
                    Log.d("predict", Arrays.toString(output[0]));
                    int index = 0;
                    float prediction = 0;
                    for(int i = 0; i < 26; i++) {
                        if (output[0][i] > prediction) {
                            index = i;
                            prediction = output[0][i];
                        }
                    }
                    String menu = LABELS[index] + "(" + Float.toString(prediction*100).substring(0, 5) + "%) ";
                    String calorie = Integer.toString(CALORIES[index]); //[음식 (확률 %) 칼로리 kcal] 형식으로 표시
                    if (when.equals("breakfast")) {
                        et_menu_breakfast.setText(menu);
                        et_calorie_breakfast.setText(calorie);
                        breakfast = CALORIES[index];
                    } else if (when.equals("lunch")) {
                        et_menu_lunch.setText(menu);
                        et_calorie_lunch.setText(calorie);
                        lunch = CALORIES[index];
                    } else if (when.equals("dinner")) {
                        et_menu_dinner.setText(menu);
                        et_calorie_dinner.setText(calorie);
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
        et_calorie_breakfast = view.findViewById(R.id.et_calorie_breakfast);
        et_calorie_lunch = view.findViewById(R.id.et_calorie_lunch);
        et_calorie_dinner = view.findViewById(R.id.et_calorie_dinner);
        tv_calorie_total = view.findViewById(R.id.tv_calorie_total);
        ibtn_previous_date = view.findViewById(R.id.ibtn_previous_date);
        ibtn_next_date = view.findViewById(R.id.ibtn_next_date);
        ibtn_breakfast = view.findViewById(R.id.ibtn_breakfast);
        ibtn_lunch = view.findViewById(R.id.ibtn_lunch);
        ibtn_dinner = view.findViewById(R.id.ibtn_dinner);
        btn_frag4_save = view.findViewById(R.id.btn_frag4_save);
        et_menu_breakfast = view.findViewById(R.id.et_menu_breakfast);
        et_menu_lunch = view.findViewById(R.id.et_menu_lunch);
        et_menu_dinner = view.findViewById(R.id.et_menu_dinner);
    } //xml의 id를 받아옴
}


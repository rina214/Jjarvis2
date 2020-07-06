package com.example.jjarvis2;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
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
    String when = null; //아침 or 점심 or 저녁
    Map<String, Integer> exercise = new HashMap<>();
    int date, breakfast, lunch, dinner, total; //date : 20200531, breakfast : 아침식사 칼로리, lunch : 점심식사 칼로리, dinner : 저녁식사 칼로리, total : 아침 + 점심 + 저녁 칼로리
    String breakfast_menu, lunch_menu, dinner_menu; //아침, 점심, 저녁 식사 메뉴
    Calendar calendar;
    DatabaseReference mDatabase; //파이어베이스 데이터베이스
    StorageReference storage; //파이어베이스 스토리지
    Bitmap breakfastBitmap, lunchBitmap, dinnerBitmap; //아침, 점심, 저녁 식사 이미지
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userUid; //구글 계정 UID
    boolean breakfastHasMenu = false, lunchHasMenu = false, dinnerHasMenu = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag4, container, false);

        getXmlId(); //xml 파일로부터 id를 받아서 메모리에 객체화
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        userUid = user.getUid();
        Log.d("userUID", userUid);
        getDate(); //오늘 날짜를 받아옴
        ibtn_breakfast.setOnClickListener(new View.OnClickListener() { //아침 + 버튼을 누르면
            @Override
            public void onClick(View v) {
                openGallery(); //갤러리 열기
                when = "breakfast";
            }
        });
        ibtn_lunch.setOnClickListener(new View.OnClickListener() { //점심 + 버튼을 누르면
            @Override
            public void onClick(View v) {
                openGallery(); //갤러리 열기
                when = "lunch";
            }
        });
        ibtn_dinner.setOnClickListener(new View.OnClickListener() { //저녁 + 버튼을 누르면
            @Override
            public void onClick(View v) {
                openGallery(); //갤러리 열기
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
                if(s.toString().equals(""))
                    return;
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
                if(s.toString().equals(""))
                    return;
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
                if(s.toString().equals(""))
                    return;
                dinner = Integer.parseInt(s.toString());
                int totalCalorie = breakfast + lunch + dinner;
                tv_calorie_total.setText(Integer.toString(totalCalorie));
            }
        });
        et_menu_breakfast.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) { //입력이 끝났을 때
                if(s.toString().equals(""))
                    return;
                breakfast_menu = s.toString();
            }
        });
        et_menu_lunch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) { //입력이 끝났을 때
                if(s.toString().equals(""))
                    return;
                lunch_menu = s.toString();
            }
        });
        et_menu_dinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) { //입력이 끝났을 때
                if(s.toString().equals(""))
                    return;
                dinner_menu = s.toString();
            }
        });

        return view;
    }

    public void getDate() { //오늘 날짜 설정
        calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(currentTime);
        tv_frag4_date.setText(strDate);
        date = Integer.parseInt(strDate.replace(".", ""));
        getImageFromStorage(); //스토리지로부터 이미지를 받아옴
        getInfoFromDatabase(); //데이터베이스로부터 정보를 받아옴
    }

    public void getPreviousDate() { //이전날
        et_initialize();
        calendar.add(Calendar.DAY_OF_WEEK, -1); //1일 전
        Date currentTime = calendar.getTime();
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(currentTime);
        tv_frag4_date.setText(strDate);
        date = Integer.parseInt(strDate.replace(".", ""));
        getImageFromStorage(); //스토리지로부터 이미지를 받아옴
        getInfoFromDatabase(); //데이터베이스로부터 정보를 받아옴
    }


    public void getNextDate() { //다음날
        et_initialize();
        calendar.add(Calendar.DAY_OF_WEEK, 1); //1일 후
        Date currentTime = calendar.getTime();
        String strDate = new SimpleDateFormat("yyyy.MM.dd").format(currentTime);
        tv_frag4_date.setText(strDate);
        date = Integer.parseInt(strDate.replace(".", ""));
        getImageFromStorage(); //스토리지로부터 이미지를 받아옴
        getInfoFromDatabase(); //데이터베이스로부터 정보를 받아옴
    }

    public void et_initialize() { //날짜 바꿀 때 입력란 초기화
        et_menu_breakfast.setText(null);
        et_menu_lunch.setText(null);
        et_menu_dinner.setText(null);
        et_calorie_breakfast.setText(null);
        et_calorie_lunch.setText(null);
        et_calorie_dinner.setText(null);
    }

    public void getImageFromStorage() { //스토리지로부터 이미지를 받아옴
        final long ONE_MEGABYTE = 1024 * 1024;
        String pathBreakfast = userUid + "_" + date + "_breakfast.jpg"; //이미지 이름 형식 : userUid_20200531_breakfast.jpg
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://jjarvis2-d7912.appspot.com");
        storageReference.child(pathBreakfast).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) { //이미지가 있으면
                Bitmap imgBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length ); //이미지를 bytes로 받아 Bitmap으로 변환
                ibtn_breakfast.setImageBitmap(imgBitmap);
                breakfastBitmap = imgBitmap;
                breakfastHasMenu = true;
            }
        }).addOnFailureListener(new OnFailureListener() { //이미지가 없으면
            @Override
            public void onFailure(@NonNull Exception exception) {
                ibtn_breakfast.setImageResource(R.drawable.ic_add_black_24dp);
                breakfastHasMenu = false;
            }
        });
        String pathLunch = userUid + "_" + date + "_lunch.jpg";
        storageReference.child(pathLunch).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap imgBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length );
                ibtn_lunch.setImageBitmap(imgBitmap);
                lunchBitmap = imgBitmap;
                lunchHasMenu = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ibtn_lunch.setImageResource(R.drawable.ic_add_black_24dp);
                lunchHasMenu = false;
            }
        });
        String pathDinner = userUid + "_" + date + "_dinner.jpg";
        storageReference.child(pathDinner).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap imgBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length );
                ibtn_dinner.setImageBitmap(imgBitmap);
                dinnerBitmap = imgBitmap;
                dinnerHasMenu = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ibtn_dinner.setImageResource(R.drawable.ic_add_black_24dp);
                dinnerHasMenu = false;
            }
        });
    }

    public void getInfoFromDatabase() { //데이터베이스로부터 정보를 받아옴
        int year = date / 10000;
        int month = (date / 100) % 100;
        int day = date % 100;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month - 1, day);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        Log.d("rina", userUid);
        mDatabase.child("userdata").child(userUid).child(String.valueOf(year)).child(String.valueOf(week)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("snapshot", String.valueOf(dataSnapshot.getChildrenCount()));
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("snapshot", snapshot.toString());
                    specification userdata = snapshot.getValue(specification.class);
                    if (userdata.getDate() == date) { //해당 날짜가 데이터베이스에 존재하면 값을 읽어옴
                        et_menu_breakfast.setText(userdata.getBreakfast_menu());
                        et_menu_lunch.setText(userdata.getLunch_menu());
                        et_menu_dinner.setText(userdata.getDinner_menu());
                        et_calorie_breakfast.setText(String.valueOf(userdata.getBreakfast()));
                        et_calorie_lunch.setText(String.valueOf(userdata.getLunch()));
                        et_calorie_dinner.setText(String.valueOf(userdata.getDinner()));
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void saveAll() { //데이터베이스에 저장
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
        if(et_menu_breakfast.getText().toString().equals("")){
            breakfast_menu = null;
        } else
            breakfast_menu = et_menu_breakfast.getText().toString();
        if(et_menu_lunch.getText().toString().equals("")){
            lunch_menu = null;
        } else
            lunch_menu = et_menu_lunch.getText().toString();
        if(et_menu_dinner.getText().toString().equals("")){
            dinner_menu = null;
        } else
            dinner_menu = et_menu_dinner.getText().toString();
        total = Integer.parseInt(tv_calorie_total.getText().toString());
        int year = Integer.parseInt(strDate.substring(0,4));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,Integer.parseInt(strDate.substring(4, 6))-1,Integer.parseInt(strDate.substring(6, 8)));
        String week = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));

        mDatabase.child("userdata").child(userUid).child(String.valueOf(year)).child(week).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("snapshot", String.valueOf(dataSnapshot.getChildrenCount()));

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("snapshot", snapshot.toString());
                    specification userdata = snapshot.getValue(specification.class);
                    if (userdata.getDate() == date) {
                        exercise = userdata.getExercise();
                        specification spec = new specification(date, breakfast, lunch, dinner, total, breakfast_menu, lunch_menu, dinner_menu, exercise);
                        mDatabase.child("userdata").child(userUid).child(String.valueOf(spec.year())).child(String.valueOf(spec.week())).child(String.valueOf(spec.getDate())).setValue(spec);
                        delay("데이터를 저장하는 중입니다.", 2000); //2초 딜레이 (값 저장할 때 시간 걸림)
                        return;
                    }
                }
                specification spec = new specification(date,breakfast,lunch,dinner, total, breakfast_menu, lunch_menu, dinner_menu, exercise);
                mDatabase.child("userdata").child(userUid).child(String.valueOf(spec.year())).child(String.valueOf(spec.week())).child(String.valueOf(spec.getDate())).setValue(spec);
                delay("데이터를 저장하는 중입니다.", 2000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (breakfastHasMenu) { //아침 식사 이미지가 있으면
            ByteArrayOutputStream baosB = new ByteArrayOutputStream(); //이미지를 파이어베이스 스토리지에 저장
            breakfastBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baosB);
            byte[] breakfastData = baosB.toByteArray();
            UploadTask uploadTaskB = storage.child(userUid + "_" + date + "_breakfast.jpg").putBytes(breakfastData); //이미지 이름 : userUid_20200531_breakfast.jpg
            uploadTaskB.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {}
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
            });
        }
        if (lunchHasMenu) {
            ByteArrayOutputStream baosL = new ByteArrayOutputStream();
            lunchBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baosL);
            byte[] lunchData = baosL.toByteArray();
            UploadTask uploadTaskL = storage.child(userUid+"_" + date + "_lunch.jpg").putBytes(lunchData);
            uploadTaskL.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {}
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
            });
        }
        if (dinnerHasMenu) {
            ByteArrayOutputStream baosD = new ByteArrayOutputStream();
            dinnerBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baosD);
            byte[] dinnerData = baosD.toByteArray();
            UploadTask uploadTaskD = storage.child(userUid+"_" + date + "_dinner.jpg").putBytes(dinnerData);
            uploadTaskD.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {}
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
            });
        }
    }

    public void delay(String message, int millisecond) { //잠시 기다리는 Dialog 사용
        final ProgressDialog oDialog = new ProgressDialog(getContext(),
                android.R.style.Theme_DeviceDefault_Light_Dialog);
        oDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        oDialog.setMessage(message);
        oDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                oDialog.dismiss();
            }
        }, millisecond);
    }

    public void openGallery() { //폰의 갤러리 열기
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
                    ExifInterface exif = null;
                    exif = new ExifInterface(inputStream);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    imgBitmap = rotateBitmap(imgBitmap, orientation); //이미지가 자동 회전되는 것을 방지
                    imgBitmap = Bitmap.createScaledBitmap(imgBitmap, ibtn_breakfast.getWidth(), ibtn_breakfast.getWidth(), true); //이미지 크기를 정사각형으로 맞춤
                    if (when.equals("breakfast")) {
                        ibtn_breakfast.setImageBitmap(imgBitmap);
                        breakfastBitmap = imgBitmap;
                        breakfastHasMenu = true;
                    } else if (when.equals("lunch")) {
                        ibtn_lunch.setImageBitmap(imgBitmap);
                        lunchBitmap = imgBitmap;
                        lunchHasMenu = true;
                    } else if (when.equals("dinner")) {
                        ibtn_dinner.setImageBitmap(imgBitmap);
                        dinnerBitmap = imgBitmap;
                        dinnerHasMenu = true;
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

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) { //이미지 자동 회전 방지
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        } try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace(); return null;
        }
    }

    private Interpreter getTfliteInterpreter(String modelPath) { //tflite 모델을 읽음
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

package com.cookandroid.finaltest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    LinearLayout baseLayout, mainLayout;
    CalendarView cal;
    TextView wordText, dateText;
    Button inputButton, deleteButton;
    WebView searchWeb;
    View dialogView;
    EditText word1, meaning1, word2, meaning2, word3, meaning3;
    String fileName, words;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("매일 영단어");

        searchWeb = (WebView)findViewById(R.id.web);
        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        baseLayout = (LinearLayout)findViewById(R.id.baseLayout);
        cal = (CalendarView)findViewById(R.id.calendar);
        wordText = (TextView) findViewById(R.id.wordText);
        dateText = (TextView) findViewById(R.id.wordDate);
        inputButton = (Button) findViewById(R.id.inputButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        final Calendar c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH) + 1;
        int cDay = c.get(Calendar.DAY_OF_MONTH);

        checkedDay(cYear, cMonth, cDay);
        dateText.setText(Integer.toString(cYear) + "-" + Integer.toString(cMonth) + "-" + Integer.toString(cDay));

        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                checkedDay(year, month + 1, dayOfMonth);
                dateText.setText(Integer.toString(year) + "-" + Integer.toString(month + 1) + "-" + Integer.toString(dayOfMonth));
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filePath = "/data/data/com.cookandroid.finaltest/files/" + fileName;
                File wordFile = new File(filePath);

                if(wordFile.exists()){
                    boolean deleted = wordFile.delete();
                    if(deleted == true){
                        wordText.setText(" ");
                        inputButton.setText("단어입력");
                        Toast.makeText(getApplicationContext(), "단어삭제", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("단어입력");
                dlg.setView(dialogView);

                dlg.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        word1 = (EditText)dialogView.findViewById(R.id.word1);
                        meaning1 = (EditText)dialogView.findViewById(R.id.meaning1);
                        word2 = (EditText)dialogView.findViewById(R.id.word2);
                        meaning2 = (EditText)dialogView.findViewById(R.id.meaning2);
                        word3 = (EditText)dialogView.findViewById(R.id.word3);
                        meaning3 = (EditText)dialogView.findViewById(R.id.meaning3);

                        words = "word 1 : " + word1.getText().toString()  + " (" + meaning1.getText().toString() + ")" + "\r\n" +
                                "word 2 : " + word2.getText().toString() + " (" + meaning2.getText().toString() + ")" +  "\r\n" +
                                "word 3 : " + word3.getText().toString() + " (" + meaning3.getText().toString() + ")";

                        // fileName을 넣고 저장 시키는 메소드 호출
                        save(fileName, words);

                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });
    }

    private void save(String fileN, String words) {
        try {
            FileOutputStream outFs = openFileOutput(fileN, Context.MODE_PRIVATE);
            outFs.write(words.getBytes());
            outFs.close();

            //저장후 set text
            FileInputStream inFs = openFileInput(fileN);
            byte[] txt = new byte[300];
            inFs.read(txt);
            String str = new String(txt);
            inFs.close();
            wordText.setText(str);

            inputButton.setText("단어수정");
            Toast.makeText(getApplicationContext(), "단어저장", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "저장오류", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkedDay( int year, int monthOfYear, int dayOfMonth) {
        // 파일 이름 생성 ex) "2020_06_27.txt"
        fileName = year + "_" + monthOfYear + "_" + dayOfMonth + ".txt";

        // 단어 써뒀는지 체크 -> 읽어오기
        try {
            FileInputStream inFs = openFileInput(fileName);
            byte[] txt = new byte[300];
            inFs.read(txt);
            String str = new String(txt);
            inFs.close();

            wordText.setText(str);
            inputButton.setText("단어수정");

        } catch (Exception e) { // UnsupportedEncodingException , FileNotFoundException , IOException
            // 단어가 없으면 오류발생 -> 단어 입력 필요
            wordText.setText("");
            inputButton.setText("단어입력");
            e.printStackTrace();
            }
        }

    // 메뉴 클릭시
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.invalidateOptionsMenu();
        MenuInflater mInfater = getMenuInflater();
        mInfater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.searchMenu:
                searchWeb.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);

                searchWeb.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
                searchWeb.loadUrl("https://small.dic.daum.net/index.do?dic=eng");//웹뷰 실행
                searchWeb.setWebChromeClient(new WebChromeClient());//웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
                searchWeb.setWebViewClient(new WebViewClientClass());//새창열기 없이 웹뷰 내에서 다시 열기//페이지 이동 원활히 하기위해 사용
                return true;

            case R.id.mainMenu:
                searchWeb.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
                return true;
        }
        return false;
    }

    class WebViewClientClass extends WebViewClient {//페이지 이동
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("check URL",url);
            view.loadUrl(url);
            return true;
        }
    }
}

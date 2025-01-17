package com.project.soulsoundapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.soulsoundapp.R;
import com.project.soulsoundapp.helper.DatabaseHelper;
import com.project.soulsoundapp.model.User;
import com.project.soulsoundapp.service.ApiService;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyCodeActivity extends AppCompatActivity {
    private static final String TAG = "VerifyCodeActivity";
    TextView tvEmailReset;

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()>0){
                if(selectedETPosition==0){
                    selectedETPosition =1;
                    showKeyboard(otpET2);
                }else if(selectedETPosition == 1){
                    selectedETPosition =2;
                    showKeyboard(otpET3);
                }else if(selectedETPosition == 2){
                    selectedETPosition =3;
                    showKeyboard(otpET4);
                }
            }
        }
    };
    private EditText otpET1, otpET2, otpET3, otpET4;
    private TextView tvResendCode;
    private Button btnVerify;
    private boolean resendEnabled = false;
    private int resendTime = 60;
    private int selectedETPosition = 0;
    private int code;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        code = randomCode();
        ApiService.apiService.forgotPassword(getIntent().getStringExtra("email") ,Integer.toString(code))
                        .enqueue(new Callback<ApiService.ApiResponse<User>>() {
                            @Override
                            public void onResponse(Call<ApiService.ApiResponse<User>> call, Response<ApiService.ApiResponse<User>> response) {

                            }

                            @Override
                            public void onFailure(Call<ApiService.ApiResponse<User>> call, Throwable throwable) {

                            }
                        });
        addControls();
        addEvents();
        showKeyboard(otpET1);
        startCountDownTimer();
    }

    private void addEvents() {
        tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resendEnabled){
                    startCountDownTimer();
                }
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tvEmailReset.getText().toString();
                final String generateOtp = otpET1.getText().toString()+otpET2.getText().toString()+otpET3.getText().toString()+otpET4.getText().toString();
                if(generateOtp.length()==4){
                    String codeAsString = Integer.toString(code);
                    if(generateOtp.equals(codeAsString)) {
                        Intent intent = new Intent(VerifyCodeActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email",email);
                        startActivity(intent);
                    } else {
                        Toast.makeText(VerifyCodeActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerifyCodeActivity.this, "OTP must have 4 characters", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addControls() {
        otpET1 = findViewById(R.id.otpET1);
        otpET2 = findViewById(R.id.otpET2);
        otpET3 = findViewById(R.id.otpET3);
        otpET4 = findViewById(R.id.otpET4);
        tvResendCode = findViewById(R.id.tvResendCode);
        btnVerify = findViewById(R.id.btnVerify);
        databaseHelper = new DatabaseHelper(this);
        tvEmailReset = findViewById(R.id.tvEmailReset);

        Intent intent = getIntent();
        tvEmailReset.setText(intent.getStringExtra("email"));

        otpET1.addTextChangedListener(textWatcher);
        otpET2.addTextChangedListener(textWatcher);
        otpET3.addTextChangedListener(textWatcher);
        otpET4.addTextChangedListener(textWatcher);
    }

    private void showKeyboard(EditText otpET){
        otpET.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpET, InputMethodManager.SHOW_IMPLICIT);
    }

    private void startCountDownTimer(){
        resendEnabled = true;
        tvResendCode.setTextColor(Color.parseColor("#11FDD2"));

        new CountDownTimer(resendTime*1000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tvResendCode.setText("Resend Code ("+(millisUntilFinished/1000)+")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                code = randomCode();
                Log.d(TAG, "code verify :" + code);
                tvResendCode.setText("Resend Code");
                tvResendCode.setTextColor(getResources().getColor(R.color.icon_button));
            }
        }.start();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL){
            if(selectedETPosition == 3){
                selectedETPosition =2;
                showKeyboard(otpET3);
            }else if(selectedETPosition ==2){
                selectedETPosition = 1;
                showKeyboard(otpET2);
            }else if(selectedETPosition ==1){
                selectedETPosition = 0;
                showKeyboard(otpET1);
            }
            return true;
        }else {
            return super.onKeyUp(keyCode, event);
        }
    }

    public int randomCode() {
        Random random = new Random();
        return random.nextInt(8999)+1000;
    }
}
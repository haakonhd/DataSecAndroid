package no.hiof.geire.coursesapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {

    private Button SignInBtn, RegisterBtn;
    private TextView EmailTextView, PasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SignInBtn = (Button) findViewById(R.id.signInBtn);
        RegisterBtn = (Button) findViewById(R.id.registerBtn);
        EmailTextView = (TextView) findViewById(R.id.emailTextView);
        PasswordTextView = (TextView) findViewById(R.id.passwordTexView);

        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity(1);
            }
        });

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    public void openMainActivity(int logInValue){

        if(EmailTextView.getText().toString().equals("") || PasswordTextView.getText().toString().equals(""))
            Toast.makeText(this, "Email and password please", Toast.LENGTH_SHORT).show();
        else{
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("logged in as", logInValue);
            startActivity(intent);
        }
    }

    public void openRegiActivity(int registerValue){

        Intent intent = new Intent(this, RegiActivity.class);
        intent.putExtra("register as", registerValue);
        startActivity(intent);
    }

    public void showAlertDialog(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Register");
        alert.setMessage("Register as?");
        alert.setNegativeButton("student", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openRegiActivity(0);
            }
        });
        alert.setPositiveButton("foreleser", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openRegiActivity(1);
            }
        });
        alert.create().show();
    }
}

package no.hiof.geire.coursesapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import no.hiof.geire.coursesapp.model.Foreleser;
import no.hiof.geire.coursesapp.model.Person;
import no.hiof.geire.coursesapp.model.Student;
import no.hiof.geire.coursesapp.dataAccess.DatabaseAccess;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getForeleserArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getPersonArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getStudentArray;

public class SignInActivity extends AppCompatActivity {

    private Button SignInBtn, RegisterBtn;
    private TextView EmailTextView, PasswordTextView;
    private String jsonStringPersoner, jsonStringForelesere, jsonStringStudenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SignInBtn = (Button) findViewById(R.id.signInBtn);
        RegisterBtn = (Button) findViewById(R.id.registerBtn);
        EmailTextView = (TextView) findViewById(R.id.emailTextView);
        PasswordTextView = (TextView) findViewById(R.id.passwordTexView);

        downloadForelesereJSON("http://158.39.188.228/api/foreleser/read.php");
        downloadPersonerJSON("http://158.39.188.228/api/person/read.php");
        downloadStudenterJSON("http://158.39.188.228/api/student/read.php");

        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    public void checkEmailAndPassword(){

        int logInValue = 0;

        ArrayList<Foreleser> lecturers = new ArrayList<>();
        ArrayList<Person> persons = new ArrayList<>();
        ArrayList<Student> students = new ArrayList<>();

        try {
            lecturers = getForeleserArray(jsonStringForelesere);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            persons = getPersonArray(jsonStringPersoner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            students = getStudentArray(jsonStringStudenter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(EmailTextView.getText().toString().equals("") || PasswordTextView.getText().toString().equals("")) {
            Toast.makeText(this, "Email and password please", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int i = 0; i < persons.size(); i++) {
                String email = persons.get(i).getEpost();
                String password = persons.get(i).getPassord();
                if (EmailTextView.getText().toString().equals(email) && PasswordTextView.getText().toString().equals(password)) {

                    for (int x = 0; x < students.size(); x++) {

                        if (students.get(x).getIdStudent() == persons.get(i).getIdPerson()) {

                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("logged in as", 1);
                            intent.putExtra("id", persons.get(i).getIdPerson());
                            startActivity(intent);
                            break;
                        }
                    }

                    for (int y = 0; y < lecturers.size(); y++) {

                        if (lecturers.get(y).getIdForeleser() == persons.get(i).getIdPerson()) {

                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("logged in as", 2);
                            intent.putExtra("id", persons.get(i).getIdPerson());
                            startActivity(intent);
                            break;
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Email and password not valid", Toast.LENGTH_SHORT).show();
                }
            }
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


    //---------------------------Database stuff-------------------------------------------------------------------------------------------------

    private void setJsonStringForelesere(String json){
        jsonStringForelesere = json;
    }

    private void downloadForelesereJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                setJsonStringForelesere(s);
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    private void downloadPersonerJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringPersoner = s;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    private void downloadStudenterJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringStudenter = s;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }
}

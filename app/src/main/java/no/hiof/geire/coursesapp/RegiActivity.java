package no.hiof.geire.coursesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.hiof.geire.coursesapp.adapter.CourseRecyclerViewAdapter;
import no.hiof.geire.coursesapp.adapter.MessageRecyclerViewAdapter;
import no.hiof.geire.coursesapp.adapter.StudyProgramRecyclerViewAdapter;
import no.hiof.geire.coursesapp.dataAccess.DatabaseAccess;
import no.hiof.geire.coursesapp.model.Emne;
import no.hiof.geire.coursesapp.model.Studieretning;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getEmneArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getStudieretningArray;

public class RegiActivity extends AppCompatActivity implements CourseRecyclerViewAdapter.ItemClickListener, StudyProgramRecyclerViewAdapter.ItemClickListener {

    private TextView RegisterMainTextView;
    private EditText NameEditText;
    private EditText EmailEditText;
    private EditText PasswordEditText;
    private EditText ClassEditText;
    private TextView CourseTextView;

    Integer RegisterAs = 0;
    String jsonString;
    Emne course;

    CourseRecyclerViewAdapter courseAdapter;
    StudyProgramRecyclerViewAdapter studyProgramAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regi);

        Intent intent = getIntent();
        RegisterAs = intent.getIntExtra("register as", 0);

        RegisterMainTextView = findViewById(R.id.registerMainTextView);
        CourseTextView = findViewById(R.id.courseTextView);
        NameEditText = findViewById(R.id.nameEditText);
        EmailEditText = findViewById(R.id.emailEditText);
        PasswordEditText = findViewById(R.id.passwordEditText);
        ClassEditText = findViewById(R.id.classEditText);

        if(RegisterAs == 0) {
            RegisterMainTextView.setText("Registrer deg som student");
            CourseTextView.setText("Velg din studieretning");
            ClassEditText.setVisibility(View.VISIBLE);
            downloadStudieretningerJSON("http://158.39.188.228/api/studieretning/read.php");
        }
        else if(RegisterAs == 1) {
            RegisterMainTextView.setText("Registrer deg som foreleser");
            CourseTextView.setText("Velg et kurs du har hovedansvaret for");
            ClassEditText.setVisibility(View.INVISIBLE);
            downloadEmnerJSON("http://158.39.188.228/api/emne/read.php");
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        if(RegisterAs == 0)
            Toast.makeText(this, "You clicked " + studyProgramAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        else if(RegisterAs == 1) {
            course = courseAdapter.getItem(position);
            checkRegisterLecturerInput();
        }
    }

    private void checkRegisterLecturerInput(){
        if(!EmailEditText.getText().toString().equals("") && !NameEditText.getText().toString().equals("") && !PasswordEditText.getText().toString().equals("")) {
            if(PasswordEditText.getText().toString().length() > 7 && NameEditText.getText().toString().length() > 7) {
                String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(EmailEditText.getText().toString());
                if (matcher.matches()) {
                    registerLecturer();
                } else {
                    Toast.makeText(this, "Bad email", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Both password and username must be at least 8 characters", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerLecturer(){
        try {
            createForeleser();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Registration complete", Toast.LENGTH_SHORT).show();
    }

    private void fillCourseRecyclerView(String jsonString){

        ArrayList<Emne> courses = new ArrayList<>();

        try {
            courses = getEmneArray(jsonString);
        } catch (JSONException e) {
            Toast.makeText(this, "getEmneArray fail", Toast.LENGTH_SHORT).show();
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.registerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseRecyclerViewAdapter(this, courses);
        courseAdapter.setClickListener(this);
        recyclerView.setAdapter(courseAdapter);
    }

    private void fillStudyProgramRecyclerView(String jsonString){

        ArrayList<Studieretning> studyPrograms = new ArrayList<>();
        try {
            studyPrograms = getStudieretningArray(jsonString);
        } catch (JSONException e) {
            Toast.makeText(this, "getStudieretningArray fail", Toast.LENGTH_SHORT).show();
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.registerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studyProgramAdapter = new StudyProgramRecyclerViewAdapter(this, studyPrograms);
        studyProgramAdapter.setClickListener(this);
        recyclerView.setAdapter(studyProgramAdapter);
    }

    // fixes problem with two setClickListeners
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void createForeleser() throws JSONException {
        String json = "";

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("epost", EmailEditText.getText().toString());
        jsonObject.accumulate("navn", NameEditText.getText().toString());
        jsonObject.accumulate("passord", PasswordEditText.getText().toString());
        jsonObject.accumulate("bilde", NameEditText.getText().toString()+".png");
        jsonObject.accumulate("key", 0);

        json = jsonObject.toString();
        sendPost(json, "http://158.39.188.228/api/person/create.php");
    }

    private void sendPost(final String json, final String urlString) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Log.i("JSON", json);
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(json);

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }





    private void downloadEmnerJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                fillCourseRecyclerView(s);
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

    private void downloadStudieretningerJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                fillStudyProgramRecyclerView(s);
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

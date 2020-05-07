package no.hiof.geire.coursesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.hiof.geire.coursesapp.adapter.StudyProgramRecyclerViewAdapter;
import no.hiof.geire.coursesapp.model.Studieretning;

import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getStudieretningArray;

public class RegiActivity extends AppCompatActivity implements StudyProgramRecyclerViewAdapter.ItemClickListener{

    private TextView RegisterMainTextView;
    private EditText NameEditText;
    private EditText EmailEditText;
    private EditText PasswordEditText;
    private EditText ClassEditText;
    private TextView CourseTextView;
    private TextView StudyProgramTextView;
    private Button ConfirmRegistrationBtn;
    private Button AddStudyProgramBtn;
    private RecyclerView RegisterRecyclerView;

    private String jsonStringStudieretninger;

    private InputValidation iv = new InputValidation();

    StudyProgramRecyclerViewAdapter studyProgramAdapter;

    Integer RegisterAs = 0;
    Studieretning sr = new Studieretning();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regi);

        Intent intent = getIntent();
        RegisterAs = intent.getIntExtra("register as", 0);

        downloadStudieretningerJSON(getString(R.string.ip) + "/api/studieretning/getStudieretninger.php");

        RegisterMainTextView = findViewById(R.id.registerMainTextView);
        CourseTextView = findViewById(R.id.courseTextView);
        StudyProgramTextView = findViewById(R.id.chosenStudyProgramTextView);
        NameEditText = findViewById(R.id.nameEditText);
        EmailEditText = findViewById(R.id.emailEditText);
        PasswordEditText = findViewById(R.id.passwordEditText);
        ClassEditText = findViewById(R.id.classEditText);
        ConfirmRegistrationBtn = findViewById(R.id.confirmRegistrationBtn);
        AddStudyProgramBtn = findViewById(R.id.addStudyProgramBtn);
        RegisterRecyclerView = findViewById(R.id.registerRecyclerView);

        if(RegisterAs == 0) {
            RegisterMainTextView.setText("Registrer deg som student");
            CourseTextView.setText("Trykk 'confirm' når alle feltene er fylt ut korrekt");
            ClassEditText.setVisibility(View.VISIBLE);
            StudyProgramTextView.setVisibility(View.VISIBLE);
            AddStudyProgramBtn.setVisibility(View.VISIBLE);
            RegisterRecyclerView.setVisibility(View.INVISIBLE);
        }
        else if(RegisterAs == 1) {
            RegisterMainTextView.setText("Registrer deg som foreleser");
            CourseTextView.setText("Trykk 'confirm' når alle feltene er fylt ut korrekt");
            ClassEditText.setVisibility(View.INVISIBLE);
            StudyProgramTextView.setVisibility(View.INVISIBLE);
            AddStudyProgramBtn.setVisibility(View.INVISIBLE);
            RegisterRecyclerView.setVisibility(View.INVISIBLE);
        }

        ConfirmRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegisterAs == 0)
                    checkRegisterStudentInput();
                else if(RegisterAs == 1) {
                    checkRegisterLecturerInput();
                }
            }
        });

        AddStudyProgramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterRecyclerView.setVisibility(View.VISIBLE);
                fillStudyProgramRecyclerView(jsonStringStudieretninger);
            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {
        sr = studyProgramAdapter.getItem(position);
        StudyProgramTextView.setText(sr.getStudieretningNavn());
        RegisterRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void checkRegisterStudentInput(){
        if(!iv.isInputEmpty(EmailEditText.getText().toString()) && !iv.isInputEmpty(NameEditText.getText().toString()) && !iv.isInputEmpty(PasswordEditText.getText().toString()) && !iv.isInputEmpty(StudyProgramTextView.getText().toString())) {
            if(PasswordEditText.getText().toString().length() > 7 && NameEditText.getText().toString().length() > 4) {
                if(iv.checkPasswordStrength(PasswordEditText.getText().toString())) {
                    if (iv.emailIsValid(EmailEditText.getText().toString())) {
                        if (iv.personTypeValueIsValid(RegisterAs)) {
                            if (iv.nameContainsValidCharacters(NameEditText.getText().toString())) {
                                if (iv.studentYearIsValid(Integer.parseInt(ClassEditText.getText().toString()))) {
                                    registerStudent();
                                } else {
                                    Toast.makeText(this, "Please have a closer look at class year", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Please give your real name", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Something weird happened, try again", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Bad email", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(this, "Password must be at least 8 characters, include one upper case, one lower case and a digit", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Password must be at least 8 characters. Username at least 5", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkRegisterLecturerInput(){
        if(!iv.isInputEmpty(EmailEditText.getText().toString()) && !iv.isInputEmpty(NameEditText.getText().toString()) && !iv.isInputEmpty(PasswordEditText.getText().toString())) {
            if(PasswordEditText.getText().toString().length() > 7 && NameEditText.getText().toString().length() > 4) {
                if (iv.emailIsValid(EmailEditText.getText().toString())) {
                    if(iv.personTypeValueIsValid(RegisterAs)) {
                        if(iv.nameContainsValidCharacters(NameEditText.getText().toString())) {
                            registerLecturer();
                        }
                        else{
                            Toast.makeText(this, "Please give your real name", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(this, "Something weird happened, try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Bad email", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Password must be at least 8 characters. Username at least 5", Toast.LENGTH_SHORT).show();
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

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void registerStudent(){
        try {
            createStudent();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Registration complete", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    /*private void fillCourseRecyclerView(String jsonString){

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
    }*/

    private void fillStudyProgramRecyclerView(String jsonString){

        ArrayList<Studieretning> studyPrograms = new ArrayList<>();
        try {
            studyPrograms = getStudieretningArray(jsonString);
        } catch (JSONException e) {
            Toast.makeText(this, "getStudieretningArray fail", Toast.LENGTH_SHORT).show();
        }

        // set up the RecyclerView
        RegisterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studyProgramAdapter = new StudyProgramRecyclerViewAdapter(this, studyPrograms);
        studyProgramAdapter.setClickListener(this);
        RegisterRecyclerView.setAdapter(studyProgramAdapter);
    }

    /*// fixes problem with two setClickListeners
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }*/

    /*private String makeHash(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

        return hashed;
    }*/

    private void createStudent() throws JSONException {
        String json = "";

        //String password = makeHash(PasswordEditText.getText().toString());
        int kull = Integer.parseInt(ClassEditText.getText().toString());

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("navn", NameEditText.getText().toString());
        jsonObject.accumulate("epost", EmailEditText.getText().toString());
        jsonObject.accumulate("passord", PasswordEditText.getText().toString());
        jsonObject.accumulate("studieretning", sr.getIdStudieretning());
        jsonObject.accumulate("kull", ClassEditText.getText().toString());

        json = jsonObject.toString();
        sendPostNewUser(json, getString(R.string.ip) + "/api/student/opprettStudent.php");
    }

    private void createForeleser() throws JSONException {
        String json = "";

        //String password = makeHash(PasswordEditText.getText().toString());

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("navn", NameEditText.getText().toString());
        jsonObject.accumulate("epost", EmailEditText.getText().toString());
        jsonObject.accumulate("passord", PasswordEditText.getText().toString());
        jsonObject.accumulate("bilde", EmailEditText.getText().toString()+".png");
        jsonObject.accumulate("emner", CourseTextView.getText().toString());

        json = jsonObject.toString();
        sendPostNewUser(json, getString(R.string.ip) + "api/foreleser/opprettForeleser.php ");
    }

    private void sendPostNewUser(final String json, final String urlString) {
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

    private void downloadStudieretningerJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringStudieretninger = s;
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

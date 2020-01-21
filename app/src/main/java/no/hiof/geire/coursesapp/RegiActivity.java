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
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getEmneArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getJSON;
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
        ClassEditText = findViewById(R.id.classEditText);

        if(RegisterAs == 0) {
            RegisterMainTextView.setText("Registrer deg som student");
            CourseTextView.setText("Velg din studieretning");
            ClassEditText.setVisibility(View.VISIBLE);
            //fillStudyProgramRecyclerView();
        }
        else if(RegisterAs == 1) {
            RegisterMainTextView.setText("Registrer deg som foreleser");
            CourseTextView.setText("Velg et kurs du har hovedansvaret for");
            ClassEditText.setVisibility(View.INVISIBLE);
            fillCourseRecyclerView();
        }
    }
    @Override
    public void onItemClick(View view, int position) {
        if(RegisterAs == 0)
            Toast.makeText(this, "You clicked " + studyProgramAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        else if(RegisterAs == 1)
            Toast.makeText(this, "You clicked " + courseAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    private void fillCourseRecyclerView(){

        jsonString = getJSON("https://jsonplaceholder.typicode.com/todos/1");
        ArrayList<Emne> courses = new ArrayList<>();

        //Displaying a toast with the json string
        Toast.makeText(this, jsonString + " ***test***test***test***", Toast.LENGTH_LONG).show();

        /*try {
            courses = getEmneArray(jsonString);
        } catch (JSONException e) {
            Toast.makeText(this, "getEmneArray fail", Toast.LENGTH_SHORT).show();
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.registerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseRecyclerViewAdapter(this, courses);
        courseAdapter.setClickListener(this);
        recyclerView.setAdapter(courseAdapter);*/
    }

    private void fillStudyProgramRecyclerView(){

        jsonString = getJSON("http://158.39.188.228/api/studieretninger/read.php");
        ArrayList<Studieretning> studyPrograms = new ArrayList<>();
        try {
            studyPrograms = getStudieretningArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
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
}
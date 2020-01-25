package no.hiof.geire.coursesapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.hiof.geire.coursesapp.adapter.CourseRecyclerViewAdapter;
import no.hiof.geire.coursesapp.adapter.LecturerRecyclerViewAdapter;
import no.hiof.geire.coursesapp.adapter.MessageRecyclerViewAdapter;
import no.hiof.geire.coursesapp.adapter.StudyProgramRecyclerViewAdapter;
import no.hiof.geire.coursesapp.model.Emne;
import no.hiof.geire.coursesapp.model.Foreleser;
import no.hiof.geire.coursesapp.model.Melding;
import no.hiof.geire.coursesapp.dataAccess.DatabaseAccess;
import no.hiof.geire.coursesapp.model.PersonHarEmne;
import no.hiof.geire.coursesapp.model.Studieretning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.List;

import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getEmneArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getForeleserArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getMeldingArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getPersonHarEmneArray;

public class MainActivity extends AppCompatActivity implements MessageRecyclerViewAdapter.ItemClickListener, CourseRecyclerViewAdapter.ItemClickListener, LecturerRecyclerViewAdapter.ItemClickListener{

    private Button GoToSignInBtn, ConfirmPinBtn, MessageReplyBtn, SendMessageBtn, ChangePasswordBtn, AddSubjectBtn, TakePicureBtn;
    private TextView StatusTextView, EnterPinTextView, PinInfoTextView;
    ImageView ImageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;

    Integer SignedInAs, id;
    String jsonStringForelesere, jsonStringPersoner, jsonStringEmner, jsonStringPersonHarEmne, jsonStringMeldinger, mText;

    MessageRecyclerViewAdapter messageAdapter;
    LecturerRecyclerViewAdapter lecturerAdapter;
    CourseRecyclerViewAdapter courseAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        SignedInAs = intent.getIntExtra("logged in as", 0);
        id = intent.getIntExtra("id", 0);

        downloadMeldingerJSON("http://158.39.188.228/api/melding/read.php");
        downloadForelesereJSON("http://158.39.188.228/api/foreleser/read.php");
        downloadPersonerJSON("http://158.39.188.228/api/person/read.php");
        downloadEmnerJSON("http://158.39.188.228/api/emne/read.php");
        downloadPersonHarEmneJSON("http://158.39.188.228/api/person_har_emne/read.php");

        StatusTextView = findViewById(R.id.statusTextView);
        GoToSignInBtn = findViewById(R.id.goToSignInBtn);
        PinInfoTextView = findViewById(R.id.pinInfoTextView);
        EnterPinTextView = findViewById(R.id.enterPinTextView);
        ConfirmPinBtn = findViewById(R.id.confirmPinBtn);
        MessageReplyBtn = findViewById(R.id.messageReplyBtn);
        SendMessageBtn = findViewById(R.id.sendMessageBtn);
        ChangePasswordBtn = findViewById(R.id.changePasswordBtn);
        AddSubjectBtn = findViewById(R.id.addSubjectBtn);
        TakePicureBtn = findViewById(R.id.takePicureBtn);
        ImageView = findViewById(R.id.imageView);


        if (savedInstanceState != null) {
            imageBitmap = savedInstanceState.getParcelable("pic");
            ImageView.setImageBitmap(imageBitmap);
        }

        GoToSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignInActivity();
            }
        });

        ConfirmPinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check Pin
                //Fill recyclerview with corresponding classes messages.
            }
        });

        MessageReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check chosen message
                //Open replyBox and apply reply
            }
        });

        SendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Emne> emner = getLoggedInStudentsCourses();
                fillStudentsEmnerRecyclerView(emner);
                //Check chosen subject(emne)
                //Open messageBox and create message
                //inputMessageDialogBox();
                //Create Melding in database
            }
        });

        ChangePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check logged in person id
                //Open changePasswordBox and update this Person with new password

                inputChangePasswordDialogBox();
                //Update Person's password in database
            }
        });

        AddSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check person id
                //Open changePasswordBox and update Person with new password
            }
        });

        GoToSignInBtn.setVisibility(View.INVISIBLE);
        PinInfoTextView.setVisibility(View.INVISIBLE);
        EnterPinTextView.setVisibility(View.INVISIBLE);
        ConfirmPinBtn.setVisibility(View.INVISIBLE);
        MessageReplyBtn.setVisibility(View.INVISIBLE);
        SendMessageBtn.setVisibility(View.INVISIBLE);
        ChangePasswordBtn.setVisibility(View.INVISIBLE);
        AddSubjectBtn.setVisibility(View.INVISIBLE);
        TakePicureBtn.setVisibility(View.INVISIBLE);
        ImageView.setVisibility(View.INVISIBLE);

        if(SignedInAs == 0) {
            StatusTextView.setText("Not signed in (guest)");
            GoToSignInBtn.setVisibility(View.VISIBLE);
            EnterPinTextView.setVisibility(View.VISIBLE);
            PinInfoTextView.setVisibility(View.VISIBLE);
            ConfirmPinBtn.setVisibility(View.VISIBLE);
        }

        if(SignedInAs == 1) {
            StatusTextView.setText("Logged in as student");
            SendMessageBtn.setVisibility(View.VISIBLE);
            ChangePasswordBtn.setVisibility(View.VISIBLE);
        }
        if(SignedInAs == 2) {
            StatusTextView.setText("Logged in as foreleser");
            ChangePasswordBtn.setVisibility(View.VISIBLE);
            AddSubjectBtn.setVisibility(View.VISIBLE);
            MessageReplyBtn.setVisibility(View.VISIBLE);
            TakePicureBtn.setVisibility(View.VISIBLE);
            ImageView.setVisibility(View.VISIBLE);
        }
        if(SignedInAs == 3) {
            StatusTextView.setText("Logged in as admin");
            fillForelesereRecyclerView();
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        if(SignedInAs == 0) {
            Toast.makeText(this, "You clicked " + messageAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        }
        else if(SignedInAs == 1){
            Toast.makeText(this, "You clicked " + courseAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        }
        else if(SignedInAs == 2){
            Toast.makeText(this, "You clicked " + courseAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        }
    }

    private void openSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private ArrayList<Emne> getLoggedInStudentsCourses(){
        ArrayList<Emne> courses = new ArrayList<>();
        ArrayList<PersonHarEmne> personsHasCourses = new ArrayList<>();
        ArrayList<Emne> studentsCourses = new ArrayList<>();


        try {
            personsHasCourses = getPersonHarEmneArray(jsonStringPersonHarEmne);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            courses = getEmneArray(jsonStringEmner);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < personsHasCourses.size(); i++){

            if(id == personsHasCourses.get(i).getIdPerson()){

                for(int x = 0; x < courses.size(); x++){

                    if(courses.get(x).getEmnekode().equals(personsHasCourses.get(i).getEmnekode())){
                        studentsCourses.add(courses.get(x));
                    }
                }
            }
        }
        return studentsCourses;
    }

    private void fillMeldingerRecyclerView(String jsonString){

        ArrayList<Melding> messages = new ArrayList<>();
        try {
            messages = getMeldingArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageRecyclerViewAdapter(this, messages);
        messageAdapter.setClickListener(this);
        recyclerView.setAdapter(messageAdapter);
    }

    private void fillEmnerRecyclerView(String jsonString){

        ArrayList<Emne> courses = new ArrayList<>();
        try {
            courses = getEmneArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseRecyclerViewAdapter(this, courses);
        courseAdapter.setClickListener(this);
        recyclerView.setAdapter(courseAdapter);
    }

    private void fillStudentsEmnerRecyclerView(ArrayList<Emne> emner){

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseRecyclerViewAdapter(this, emner);
        courseAdapter.setClickListener(this);
        recyclerView.setAdapter(courseAdapter);
    }

    private void fillForelesereRecyclerView(){

        ArrayList<Foreleser> lecturer = new ArrayList<>();
        try {
            lecturer = getForeleserArray(jsonStringForelesere);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lecturerAdapter = new LecturerRecyclerViewAdapter(this, lecturer);
        lecturerAdapter.setClickListener(this);
        recyclerView.setAdapter(lecturerAdapter);
    }

    // fixes problem with two setClickListeners
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //---------------------------DialogBoxes------------------------------------------------------------------------------------------

    private void inputMessageDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter message");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mText = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private  void inputChangePasswordDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter new password");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mText = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //-------------------Camera-------------------------------------------------------------------------------------------------------

    public void takePic(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ImageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("pic", imageBitmap);
        super.onSaveInstanceState(outState);
    }


    //-------------------Database stuff-------------------------------------------------------------------------------------------------------


    private void downloadMeldingerJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringMeldinger = s;
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

    private void downloadForelesereJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringForelesere = s;
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

    private void downloadEmnerJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringEmner = s;
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

    private void downloadPersonHarEmneJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                jsonStringPersonHarEmne = s;
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

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
import no.hiof.geire.coursesapp.model.Person;
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
import android.util.Log;
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


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getEmneArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getForeleserArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getMeldingArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getPersonArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getPersonHarEmneArray;

public class MainActivity extends AppCompatActivity implements MessageRecyclerViewAdapter.ItemClickListener, CourseRecyclerViewAdapter.ItemClickListener, LecturerRecyclerViewAdapter.ItemClickListener{

    private Button GoToSignInBtn, ConfirmPinBtn, MessageReplyBtn, SendMessageBtn, ChangePasswordBtn, AddSubjectBtn, TakePicureBtn;
    private TextView StatusTextView, EnterPinTextView, PinInfoTextView;
    ImageView ImageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;

    private Integer SignedInAs, id;
    private String jsonStringForelesere, jsonStringPersoner, jsonStringEmner, jsonStringPersonHarEmne, jsonStringMeldinger, mText, navn;

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
        navn = intent.getStringExtra("name");

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
                String pin = EnterPinTextView.getText().toString();
                ArrayList<Melding> messages = getMeldingerFromPin(pin);
                if(messages.size() > 0) {
                    fillMeldingerRecyclerView(messages);
                    StatusTextView.setText("Not signed in (guest)\nEmnekode: " + messages.get(0).getEmnekode());
                }
                else{
                    makeToast("Ingen meldinger på oppgitt PIN-nummer");
                }
            }
        });

        MessageReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Melding> messages = getMessages();
                fillMeldingerRecyclerView(messages);
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
            StatusTextView.setText(navn + " (Student)");
            SendMessageBtn.setVisibility(View.VISIBLE);
            ChangePasswordBtn.setVisibility(View.VISIBLE);
        }
        if(SignedInAs == 2) {
            StatusTextView.setText(navn + " (Foreleser)");
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
            inputCommentOrReportDialogBox(messageAdapter.getItem(position));
        }
        else if(SignedInAs == 1){
            String emnekode = courseAdapter.getItem(position).getEmnekode();
            int foreleser = courseAdapter.getItem(position).getForeleser();
            inputMessageDialogBox(emnekode, foreleser);
        }
        else if(SignedInAs == 2){
            String emnekode = messageAdapter.getItem(position).getEmnekode();
            int student = messageAdapter.getItem(position).getIdForfatter();
            int idmelding = messageAdapter.getItem(position).getIdMelding();
            String innholdMelding = messageAdapter.getItem(position).getInnhold_melding();
            boolean rapportert = messageAdapter.getItem(position).isRappotert();
            inputReplyDialogBox(emnekode, student, innholdMelding, idmelding, rapportert);
        }
    }

    private void openSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void makeToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private String getNameOfSignedInPerson(int id){
        String navn = "";
        ArrayList<Person> persons = new ArrayList<>();
        try {
            persons = getPersonArray(jsonStringPersoner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < persons.size(); i++){
            if(persons.get(i).getIdPerson() == id){
                navn = persons.get(i).getNavn();
                break;
            }
        }
        return navn;
    }

    private ArrayList<Melding> getMeldingerFromPin(String pin){
        ArrayList<Emne> courses = new ArrayList<>();
        ArrayList<Melding> messages = new ArrayList<>();
        ArrayList<Melding> messagesFromPin = new ArrayList<>();

        try {
            courses = getEmneArray(jsonStringEmner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            messages = getMeldingArray(jsonStringMeldinger);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < courses.size(); i++){
            String coursePin = String.valueOf(courses.get(i).getPinNr());
            if(coursePin.equals(pin)){

                for(int x = 0; x < messages.size(); x++){

                    if(messages.get(x).getEmnekode().equals(courses.get(i).getEmnekode())){
                        messagesFromPin.add(messages.get(x));
                    }
                }
            }
        }
        return messagesFromPin;
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

            if(id == personsHasCourses.get(i).getIdPerson() && !personsHasCourses.get(i).isTilgangTilEmne()){

                for(int x = 0; x < courses.size(); x++){

                    if(courses.get(x).getEmnekode().equals(personsHasCourses.get(i).getEmnekode())){
                        studentsCourses.add(courses.get(x));
                    }
                }
            }
        }
        return studentsCourses;
    }

    private ArrayList<Melding> getMessages(){
        ArrayList<Melding> allMessages = new ArrayList<>();
        ArrayList<Emne> courses = new ArrayList<>();
        ArrayList<Melding> messages = new ArrayList<>();


        try {
            courses = getEmneArray(jsonStringEmner);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            allMessages = getMeldingArray(jsonStringMeldinger);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < courses.size(); i++){

            if(id == courses.get(i).getForeleser()){

                for(int x = 0; x < allMessages.size(); x++){

                    if(allMessages.get(x).getEmnekode().equals(courses.get(i).getEmnekode())){
                        messages.add(allMessages.get(x));
                    }
                }
            }
        }

        return messages;
    }

    private void fillMeldingerRecyclerView(ArrayList<Melding> messages){

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageRecyclerViewAdapter(this, messages);
        messageAdapter.setClickListener(this);
        recyclerView.setAdapter(messageAdapter);

        downloadMeldingerJSON("http://158.39.188.228/api/melding/read.php");
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

    private void sendMessage(String mText, String emnekode, Integer foreleser) throws JSONException {
        String json = "";

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("emnekode", emnekode);
        jsonObject.accumulate("idForeleser", foreleser.toString());
        jsonObject.accumulate("idForfatter", id.toString());
        jsonObject.accumulate("innhold_melding", mText);
        jsonObject.accumulate("innhold_svar", " ");
        jsonObject.accumulate("rapportert", false);

        json = jsonObject.toString();

        sendPost(json);
    }

    private void sendReply(String mText, String emnekode, Integer idStudent, String innholdMelding, Integer idMelding, boolean rapportert) throws JSONException {
        String json = "";
        String rapportertToString = "0";
        if(rapportert){
            rapportertToString = "1";
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("idMelding", idMelding.toString());
        jsonObject.accumulate("emnekode", emnekode);
        jsonObject.accumulate("idForeleser", id.toString());
        jsonObject.accumulate("idForfatter", idStudent.toString());
        jsonObject.accumulate("innhold_melding", innholdMelding);
        jsonObject.accumulate("innhold_svar", mText);
        jsonObject.accumulate("rapportert", rapportertToString);

        json = jsonObject.toString();

        updatePost(json);
    }

    private void sendReport(Melding message) throws JSONException {
        String json = "";
        String rapportertToString = "1";

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("idMelding", String.valueOf(message.getIdMelding()));
        jsonObject.accumulate("emnekode", message.getEmnekode());
        jsonObject.accumulate("idForeleser", String.valueOf(message.getIdForeleser()));
        jsonObject.accumulate("idForfatter", String.valueOf(message.getIdForfatter()));
        jsonObject.accumulate("innhold_melding", message.getInnhold_melding());
        jsonObject.accumulate("innhold_svar", message.getInnhold_svar());
        jsonObject.accumulate("rapportert", rapportertToString);

        json = jsonObject.toString();

        updatePost(json);
    }

    public void updatePost(final String json){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://158.39.188.228/api/melding/update.php");
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setDoInput(true);
                    httpCon.setRequestMethod("PUT");
                    httpCon.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    httpCon.setRequestProperty("Accept","application/json");
                    OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
                    out.write(json);
                    out.close();

                    httpCon.getInputStream();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void sendPost(final String json) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://158.39.188.228/api/melding/create.php");
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

    //---------------------------DialogBoxes------------------------------------------------------------------------------------------

    private void inputMessageDialogBox(final String emnekode, final int foreleser){
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
                try {
                    sendMessage(mText, emnekode, foreleser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void inputReplyDialogBox(final String emnekode, final int idStudent, final String innholdMelding, final int idMelding, final boolean rapportert){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter reply");

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
                try {
                    sendReply(mText, emnekode, idStudent, innholdMelding, idMelding, rapportert);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void inputCommentOrReportDialogBox(final Melding message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter comment or report");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Comment", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mText = input.getText().toString();
                //Create Kommentar
            }
        });
        builder.setNegativeButton("Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sendReport(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
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
        builder.setMessage("(Changing password is not a feature)");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //mText = input.getText().toString();
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

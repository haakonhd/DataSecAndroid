package no.hiof.geire.coursesapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.hiof.geire.coursesapp.adapter.CourseRecyclerViewAdapter;
import no.hiof.geire.coursesapp.adapter.LecturerRecyclerViewAdapter;
import no.hiof.geire.coursesapp.adapter.MessageRecyclerViewAdapter;
import no.hiof.geire.coursesapp.dataAccess.GelfLogger;
import no.hiof.geire.coursesapp.model.Emne;
import no.hiof.geire.coursesapp.model.Foreleser;
import no.hiof.geire.coursesapp.model.Melding;
import no.hiof.geire.coursesapp.model.Person;
import no.hiof.geire.coursesapp.model.PersonHarEmne;

import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getEmneArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getForeleserArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getMeldingArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getPersonArray;
import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getPersonHarEmneArray;

public class MainActivity extends AppCompatActivity implements MessageRecyclerViewAdapter.ItemClickListener, CourseRecyclerViewAdapter.ItemClickListener, LecturerRecyclerViewAdapter.ItemClickListener{

    private Button GoToSignInBtn, ConfirmPinBtn, MessageReplyBtn, SendMessageBtn, ChangePasswordBtn, AddCourseBtn, TakePicureBtn;
    private TextView StatusTextView, EnterPinTextView, PinInfoTextView;
    ImageView ImageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;

    private Integer SignedInAs, id, AddCourseBtnPressed, SendMessageBtnPressed, MessageReplyBtnPressed;
    private String jsonStringForelesere, jsonStringPersoner, jsonStringEmner, jsonStringPersonHarEmne, jsonStringMeldinger, jsonStringStudieretninger, mText, navn;

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

        /*downloadMeldingerJSON(getString(R.string.ip) + "/api/melding/read.php");
        downloadForelesereJSON(getString(R.string.ip) + "/api/foreleser/read.php");
        downloadPersonerJSON(getString(R.string.ip) + "/api/person/read.php");
        downloadPersonHarEmneJSON(getString(R.string.ip) + "/api/person_har_emne/read.php");*/

        downloadEmnerJSON(getString(R.string.ip) + "/api/emne/getEmner.php");//OK
        downloadStudieretningerJSON(getString(R.string.ip) + "/api/studieretning/getStudieretninger.php");//OK

        StatusTextView = findViewById(R.id.statusTextView);
        GoToSignInBtn = findViewById(R.id.goToSignInBtn);
        PinInfoTextView = findViewById(R.id.pinInfoTextView);
        EnterPinTextView = findViewById(R.id.enterPinTextView);
        ConfirmPinBtn = findViewById(R.id.confirmPinBtn);
        MessageReplyBtn = findViewById(R.id.messageReplyBtn);
        SendMessageBtn = findViewById(R.id.sendMessageBtn);
        ChangePasswordBtn = findViewById(R.id.changePasswordBtn);
        AddCourseBtn = findViewById(R.id.addCourseBtn);
        TakePicureBtn = findViewById(R.id.takePicureBtn);
        ImageView = findViewById(R.id.imageView);


        if (savedInstanceState != null) {
            imageBitmap = savedInstanceState.getParcelable("pic");
            ImageView.setImageBitmap(imageBitmap);
        }

        GoToSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GelfLogger gl = new GelfLogger();
                gl.gelfLogger("SignInBtn was pressed!");
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
                MessageReplyBtnPressed = 1;
                SendMessageBtnPressed = 0;
                AddCourseBtnPressed = 0;
                ArrayList<Melding> messages = getMessages();
                fillMeldingerRecyclerView(messages);
            }
        });

        SendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessageBtnPressed = 1;
                AddCourseBtnPressed = 0;
                MessageReplyBtnPressed = 0;
                ArrayList<Emne> emner = getLoggedInStudentsCourses();
                fillStudentsEmnerRecyclerView(emner);
            }
        });

        ChangePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputChangePasswordDialogBox();
            }
        });

        AddCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCourseBtnPressed = 1;
                SendMessageBtnPressed = 0;
                MessageReplyBtnPressed = 0;
                if(SignedInAs == 1){
                    fillEmnerRecyclerView(jsonStringEmner);
                }
                else if(SignedInAs == 2){
                    fillEmnerRecyclerView(jsonStringEmner);
                }
            }
        });

        GoToSignInBtn.setVisibility(View.INVISIBLE);
        PinInfoTextView.setVisibility(View.INVISIBLE);
        EnterPinTextView.setVisibility(View.INVISIBLE);
        ConfirmPinBtn.setVisibility(View.INVISIBLE);
        MessageReplyBtn.setVisibility(View.INVISIBLE);
        SendMessageBtn.setVisibility(View.INVISIBLE);
        ChangePasswordBtn.setVisibility(View.INVISIBLE);
        AddCourseBtn.setVisibility(View.INVISIBLE);
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
            //SendMessageBtn.setVisibility(View.VISIBLE);
            AddCourseBtn.setVisibility(View.VISIBLE);
            ChangePasswordBtn.setVisibility(View.VISIBLE);
        }
        if(SignedInAs == 2) {
            StatusTextView.setText(navn + " (Foreleser)");
            ChangePasswordBtn.setVisibility(View.VISIBLE);
            AddCourseBtn.setVisibility(View.VISIBLE);
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
            if(SendMessageBtnPressed == 1) {
                String emnekode = courseAdapter.getItem(position).getEmnekode();
                int foreleser = courseAdapter.getItem(position).getForeleser();
                inputMessageDialogBox(emnekode, foreleser);
            }
            if(AddCourseBtnPressed == 1){
                String emnekode = courseAdapter.getItem(position).getEmnekode();
                int foreleser = courseAdapter.getItem(position).getForeleser();
                String emnenavn = courseAdapter.getItem(position).getEmnenavn();
                sendCourseAccessRequestDialogBox(emnekode, foreleser, emnenavn, id);
            }
        }
        else if(SignedInAs == 2){
            if(MessageReplyBtnPressed == 1) {
                String emnekode = messageAdapter.getItem(position).getEmnekode();
                int student = messageAdapter.getItem(position).getIdForfatter();
                int idmelding = messageAdapter.getItem(position).getIdMelding();
                String innholdMelding = messageAdapter.getItem(position).getInnhold_melding();
                boolean rapportert = messageAdapter.getItem(position).isRappotert();
                int rapportertAv = messageAdapter.getItem(position).getRapportert_av();
                inputReplyDialogBox(emnekode, student, innholdMelding, idmelding, rapportert, rapportertAv);
            }

            if(AddCourseBtnPressed == 1){
                String emnekode = courseAdapter.getItem(position).getEmnekode();
                int foreleser = courseAdapter.getItem(position).getForeleser();
                String emnenavn = courseAdapter.getItem(position).getEmnenavn();
                int pin = courseAdapter.getItem(position).getPinNr();
                try {
                    emneToJson(emnekode, emnenavn, pin, foreleser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
            if(persons.get(i).getIdPerson().equals(id)){
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

        downloadMeldingerJSON(getString(R.string.ip) + "/api/melding/read.php");
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

    private void sendMessage(String mText, String emnekode, Integer foreleser, Integer id) throws JSONException {
        String json = "";

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("emnekode", emnekode);
        jsonObject.accumulate("idForfatter", id.toString());
        jsonObject.accumulate("innhold_melding", mText);

        json = jsonObject.toString();

        sendPost(json);
    }

    private void sendReply(String mText, String emnekode, Integer idStudent, String innholdMelding, Integer idMelding, boolean rapportert, Integer rapportertAv) throws JSONException {
        String json = "";
        String rapportertToString = String.valueOf(rapportert);

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("idMelding", idMelding.toString());
        jsonObject.accumulate("emnekode", emnekode);
        jsonObject.accumulate("idForeleser", id.toString());
        jsonObject.accumulate("idForfatter", idStudent.toString());
        jsonObject.accumulate("innhold_melding", innholdMelding);
        jsonObject.accumulate("innhold_svar", mText);
        jsonObject.accumulate("rapportert", rapportertToString);
        jsonObject.accumulate("rapportert_av", rapportertAv.toString());

        json = jsonObject.toString();

        updatePost(json);
    }

    private void sendReport(Melding m) throws JSONException {
        String json = "";

        String rapportertToString = String.valueOf(!m.isRappotert());
        String rapportertAv = String.valueOf(m.getRapportert_av());

        if(SignedInAs == 1){
            rapportertAv = id.toString();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("idMelding", String.valueOf(m.getIdMelding()));
        jsonObject.accumulate("emnekode", m.getEmnekode());
        jsonObject.accumulate("idForeleser", String.valueOf(m.getIdForeleser()));
        jsonObject.accumulate("idForfatter", String.valueOf(m.getIdForeleser()));
        jsonObject.accumulate("innhold_melding", m.getInnhold_melding());
        jsonObject.accumulate("innhold_svar", m.getInnhold_svar());
        jsonObject.accumulate("rapportert", rapportertToString);
        jsonObject.accumulate("rapportert_av", rapportertAv);

        json = jsonObject.toString();

        updatePost(json);
    }

    public void updatePost(final String json){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getString(R.string.ip) + "/api/melding/update.php");
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

    private void emneToJson(String emnekode, String emnenavn, int pin, Integer foreleser) throws JSONException {
        String json = "";

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("emnekode", emnekode);
        jsonObject.accumulate("emnenavn", emnenavn);
        jsonObject.accumulate("PIN", pin);
        jsonObject.accumulate("foreleser", id.toString());

        json = jsonObject.toString();

        updateEmne(json);
    }

    public void updateEmne(final String json){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getString(R.string.ip) + "/api/emne/update.php");
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
                    URL url = new URL(getString(R.string.ip) + "/api/melding/create.php");
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

    public void sendCourseAccessRequest(String mText, Integer idstudent, String emnekode, String innholdMelding, int idMelding, boolean rapportert, Integer rapportertAv) throws JSONException {
        String json = "";

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("person_id", idstudent.toString());
        jsonObject.accumulate("emne_emnekode", emnekode);
        jsonObject.accumulate("tilgang_til_emne", "1");

        json = jsonObject.toString();

        createPersonHarEmne(json);

        sendReply(mText, emnekode, idstudent, innholdMelding, idMelding, rapportert, rapportertAv);
    }

    public void createPersonHarEmne(final String json) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getString(R.string.ip) + "/api/person_har_emne/create.php");
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
                    sendMessage(mText, emnekode, foreleser, id);
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

    private void sendCourseAccessRequestDialogBox(final String emnekode, final int foreleser, final String emnenavn, final int idStud){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request access to '" + emnenavn + "'.\n Add a nice message on why you want access!");

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
                    sendMessage(mText, emnekode, foreleser, idStud);
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

    private void inputReplyDialogBox(final String emnekode, final int idStudent, final String innholdMelding, final int idMelding, final boolean rapportert, final Integer rapportertAv){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter reply");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Send reply and deny student access", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mText = input.getText().toString();
                try {
                    sendReply(mText, emnekode, idStudent, innholdMelding, idMelding, rapportert, rapportertAv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNeutralButton("Send reply and give student access", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mText = input.getText().toString();
                try {
                    sendCourseAccessRequest(mText, idStudent, emnekode, innholdMelding, idMelding, rapportert, rapportertAv);
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

        super.onActivityResult(requestCode, resultCode, data);
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

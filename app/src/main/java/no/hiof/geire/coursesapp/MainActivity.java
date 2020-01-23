package no.hiof.geire.coursesapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.hiof.geire.coursesapp.adapter.MessageRecyclerViewAdapter;
import no.hiof.geire.coursesapp.model.Emne;
import no.hiof.geire.coursesapp.model.Melding;
import no.hiof.geire.coursesapp.dataAccess.DatabaseAccess;
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

import static no.hiof.geire.coursesapp.dataAccess.DatabaseAccess.getMeldingArray;

public class MainActivity extends AppCompatActivity implements MessageRecyclerViewAdapter.ItemClickListener{

    private Button GoToSignInBtn, ConfirmPinBtn, MessageReplyBtn, SendMessageBtn, ChangePasswordBtn, AddSubjectBtn, TakePicureBtn;
    private TextView StatusTextView, EnterPinTextView, PinInfoTextView;
    ImageView ImageView;

    MessageRecyclerViewAdapter adapter;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;

    Integer SignedInAs;
    String jsonString, mText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                //fillRecyclerView("http://158.39.188.228/api/melding/read.php");
                //Check chosen subject(emne)
                //Open messageBox and create message
                inputMessageDialogBox();
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

        Intent intent = getIntent();
        SignedInAs = intent.getIntExtra("logged in as", 0);

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
            //fillRecyclerView("http://158.39.188.228/api/melding/read.php");
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
            //fillRecyclerView();
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    private void openSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void fillRecyclerView(String URL){

        //jsonString = getJSON(URL);
        ArrayList<Melding> messages = new ArrayList<>();
        try {
            messages = getMeldingArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageRecyclerViewAdapter(this, messages);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
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































    /*private void getJSON(final String urlWebService) {
        *//*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * *//*
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {



                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }*/
}

package no.hiof.geire.coursesapp.dataAccess;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import no.hiof.geire.coursesapp.model.Emne;
import no.hiof.geire.coursesapp.model.Foreleser;
import no.hiof.geire.coursesapp.model.Melding;
import no.hiof.geire.coursesapp.model.Person;
import no.hiof.geire.coursesapp.model.PersonHarEmne;
import no.hiof.geire.coursesapp.model.Student;
import no.hiof.geire.coursesapp.model.Studieretning;

public class DatabaseAccess {



    public static ArrayList<Melding> getMeldingArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONObject jsnobject = new JSONObject(json);
        JSONArray jsonArray = jsnobject.getJSONArray("meldinger");
        ArrayList<Melding> messages = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Melding message = new Melding(obj.optInt("idMelding"), obj.optString("innhold_melding"), obj.optInt("idForfatter"),
                    obj.optString("emnekode"), obj.optBoolean("rapportert"), obj.optInt("idForeleser"), obj.optString("innhold_svar"), obj.optInt("rapportert_av"));
            messages.add(message);
        }

        return messages;
    }

    public static ArrayList<Emne> getEmneArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONObject jsnobject = new JSONObject(json);
        JSONArray jsonArray = jsnobject.getJSONArray("emner");
        ArrayList<Emne> courses = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj = jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Emne course = new Emne();
            course.setEmnekode(obj.optString("emnekode"));
            course.setEmnenavn(obj.optString("emnenavn"));
            course.setPinNr(obj.optInt("PIN"));
            course.setForeleser(obj.optInt("foreleser"));
            courses.add(course);
        }

        return courses;
    }

    public static ArrayList<Studieretning> getStudieretningArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONObject jsnobject = new JSONObject(json);
        JSONArray jsonArray = jsnobject.getJSONArray("studieretninger");
        ArrayList<Studieretning> studyPrograms = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Studieretning studyProgram = new Studieretning(obj.optString("idStudieretning"), obj.optString("navn_studieretning"));
            studyPrograms.add(studyProgram);
        }

        return studyPrograms;
    }

    public static ArrayList<Foreleser> getForeleserArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONObject jsnobject = new JSONObject(json);
        JSONArray jsonArray = jsnobject.getJSONArray("forelesere");
        ArrayList<Foreleser> lecturers = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Foreleser lecturer = new Foreleser();
            lecturer.setIdForeleser(obj.optInt("idForeleser"));
            lecturer.setBildeURL(obj.optString("bilde"));

            lecturers.add(lecturer);
        }

        return lecturers;
    }

    public static ArrayList<Student> getStudentArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONObject jsnobject = new JSONObject(json);
        JSONArray jsonArray = jsnobject.getJSONArray("studenter");
        ArrayList<Student> students = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Student student = new Student();
            student.setIdStudent(obj.optInt("idStudent"));
            student.setKull(obj.optInt("kull"));
            student.setStudieretning(obj.optString("studieretning"));
            students.add(student);
        }

        return students;
    }

    public static ArrayList<Person> getPersonArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONObject jsnobject = new JSONObject(json);
        JSONArray jsonArray = jsnobject.getJSONArray("personer");
        ArrayList<Person> persons = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            Person person = new Person(obj.optInt("idPerson"), obj.optString("epost"), obj.optString("navn"),
                    obj.optString("passord"), obj.optBoolean("godkjent_bruker"));
            persons.add(person);
        }

        return persons;
    }

    public static ArrayList<PersonHarEmne> getPersonHarEmneArray (String json) throws JSONException {
        //creating a json array from the json string
        JSONObject jsnobject = new JSONObject(json);
        JSONArray jsonArray = jsnobject.getJSONArray("person_har_emne");
        ArrayList<PersonHarEmne> personsHasCourses = new ArrayList<>();

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj= jsonArray.getJSONObject(i);

            //getting the data from the json object and putting it inside object array
            PersonHarEmne personsHasCourse = new PersonHarEmne(obj.optInt("person_id"), obj.optString("emne_emnekode"), obj.optBoolean("tilgang_til_emne"));
            personsHasCourses.add(personsHasCourse);
        }

        return personsHasCourses;
    }
}

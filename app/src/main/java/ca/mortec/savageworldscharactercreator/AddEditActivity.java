package ca.mortec.savageworldscharactercreator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.sql.SQLException;

/**
 * Created by Mike on 11/28/2016.
 *
 * Allows user to add a new character or edit an existing one
 */

//callback method implemented by MainActivity
public class AddEditActivity extends Fragment{

    public interface AddEditActivityListener
    {
        //called after edit complete so character can be re-displayed
        void onAddEditCompleted(long rowID);
    }

    AddEditActivityListener listener;
    Bundle characterInfoBundle;//arguments for editing a character
    Button saveCharacterButton;

    //EditText for SW Character information
    EditText nameEditText;
    EditText placeholder2Text;
    EditText placeholder3Text;
    EditText placeholder4Text;
    EditText placeholder5Text;
    EditText placeholder6Text;
    EditText placeholder7Text;

    long Num;//database Num (number) of the character

    //set AddEditActivityListener when fragment attached
    @Override
    public void onAttach(Context context) {

        System.out.println("PROGRAM-TRACE: AddEditActivity---onAttach called");
        super.onAttach(context);
        listener = (AddEditActivityListener) context;

    }

    //remove AddEditActivityListener when Fragment detached
    @Override
    public void onDetach() {

        System.out.println("PROGRAM-TRACE: AddEditActivity---onDetach called");
        super.onDetach();
        listener = null;
    }

    //called when Fragment's view needs to be created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("PROGRAM-TRACE: AddEditActivity---onCreateView called");
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);//save fragment across config changes
        setHasOptionsMenu(true);//fragment has menu items to display

        //inflate GUI and get references to EditTexts
        View view = inflater.inflate(R.layout.add_edit_activity, container, false);

        nameEditText = (EditText) view.findViewById(R.id.nametext);
        placeholder2Text = (EditText) view.findViewById(R.id.info_two_text);
        placeholder3Text = (EditText) view.findViewById(R.id.info_three_text);
        placeholder4Text = (EditText) view.findViewById(R.id.info_four_text);
        placeholder5Text = (EditText) view.findViewById(R.id.info_five_text);
        placeholder6Text = (EditText) view.findViewById(R.id.info_six_text);
        placeholder7Text = (EditText) view.findViewById(R.id.info_seven_text);

        characterInfoBundle = getArguments();//null if creating new character

        if(characterInfoBundle != null)
        {
            Num = characterInfoBundle.getLong(MainActivity.Num);
            nameEditText.setText(characterInfoBundle.getString("name"));
            //communicate objects with Database
            placeholder2Text.setText(characterInfoBundle.getString("placeholder2"));
            placeholder3Text.setText(characterInfoBundle.getString("placeholder3"));
            placeholder4Text.setText(characterInfoBundle.getString("placeholder4"));
            placeholder5Text.setText(characterInfoBundle.getString("placeholder5"));
            placeholder6Text.setText(characterInfoBundle.getString("placeholder6"));
            placeholder7Text.setText(characterInfoBundle.getString("placeholder7"));
        }

        //set Save Contact Button's event listener
        saveCharacterButton = (Button) view.findViewById(R.id.button);

        saveCharacterButton.setOnClickListener(saveCharacterButtonClicked);


        return view;
    }

    //responds to event generated when user saves a character
    View.OnClickListener saveCharacterButtonClicked = new View.OnClickListener()
    {


        @Override
        public void onClick(View v)
        {
            System.out.println("PROGRAM-TRACE: AddEditActivity---onClick called, character to saved/edited");
            if (nameEditText.getText().toString().trim().length() != 0) {

                //ASyncTask to save character, then notify listener
                AsyncTask<Object, Object, Object> saveCharacterTask = new AsyncTask<Object, Object, Object>() {

                    @Override
                    protected Object doInBackground(Object... params) {
                        System.out.println("PROGRAM-TRACE: AddEditActivity---doInBackground called, character saving changes");
                        try {
                            saveCharacter();//save character to the database
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    /*@Override
                    protected void onPostExecute(Object result) {
                        System.out.println("test12");
                        // Hide soft keyboard
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                        listener.onAddEditCompleted(Num);
                    }*/
                };//end ASyncTask
                //save the character to the database using a seperate thread
                saveCharacterTask.execute((Object[]) null);
            }
            else//required character name is blank, so display error dialog
            {
                System.out.println("PROGRAM-TRACE: AddEditActivity---required character name is blank, so display error dialog");
                DialogFragment errorsaving = new DialogFragment(){
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.errormessage);
                        builder.setPositiveButton(R.string.ok, null);
                        return builder.create();

                    }
                };
                errorsaving.show(getFragmentManager(), "Error Saving Information");

                /*
                *
                *  errormessage();
                * */
            }//end method onClick
        }
    };//end OnClickListener saveCharacterButtonClicked


    //saves character information to the database
    private void saveCharacter() throws SQLException {
        //get DatabaseConnector to interact with the SQLite database
        System.out.println("PROGRAM-TRACE: AddEditActivity---saveCharacter called");
        DBHandler dbhandler = new DBHandler(getActivity());
        if(characterInfoBundle == null)
        {
            System.out.println("PROGRAM-TRACE: saveCharacter---if characterInfoBundle == null insert the character information into the database");
            //insert the character information into the database
            Num = dbhandler.insertCharacter(
                    nameEditText.getText().toString(),
                    placeholder2Text.getText().toString(),
                    placeholder3Text.getText().toString(),
                    placeholder4Text.getText().toString(),
                    placeholder5Text.getText().toString(),
                    placeholder6Text.getText().toString(),
                    placeholder7Text.getText().toString()
            );
        }
        else
        {
            System.out.println("PROGRAM-TRACE: saveCharacter--- else(characterInfoBundle != null) update the character information into the database");
            dbhandler.updateCharacter(Num,
                    nameEditText.getText().toString(),
                    placeholder2Text.getText().toString(),
                    placeholder3Text.getText().toString(),
                    placeholder4Text.getText().toString(),
                    placeholder5Text.getText().toString(),
                    placeholder6Text.getText().toString(),
                    placeholder7Text.getText().toString());
        }//end method save character
    }

    public void errormessage()
    {
       // Toast.makeText(view, "msg msg", Toast.LENGTH_SHORT).show();
        //TODO IMPLEMENT?
    }
}

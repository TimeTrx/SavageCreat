package ca.mortec.savageworldscharactercreator;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.sql.SQLException;

/**
 * Created by Mike on 11/28/2016.
 * Displays one character's details at a time.
 */

public class CharacterDetails extends Fragment {

    //callback methods implemented by MainActivity
    public interface character_detailsListener
    {
        //called when a character is deleted
        void onCharacterDeleted();

        //called to pass Bundle of character's info for editing
        void onEditCharacter(Bundle arguments);
    }

    character_detailsListener listener;

    long Num = -1;//selected character's Num (Number)

    TextView nameInXml; //displays character's name
    TextView placeholder2InXml; //displays character's
    TextView placeholder3InXml; //displays character's
    TextView placeholder4InXml; //displays character's
    TextView placeholder5InXml; //displays character's
    TextView placeholder6InXml; //displays character's
    TextView placeholder7InXml; //displays character's


    //sets the character_detailsListener when fragment attached
    @Override
    public void onAttach(Context context) {

        System.out.println("PROGRAM-TRACE: CharacterDetails---onAttach called");
        super.onAttach(context);
        listener = (character_detailsListener) context;

    }

    //removes character_detailsListener when fragment detached
    @Override
    public void onDetach() {

        System.out.println("PROGRAM-TRACE: CharacterDetails---onDetach called");
        super.onDetach();
        listener = null;

    }

    //called when the CharacterDetails view needs to be created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("PROGRAM-TRACE: CharacterDetails---onCreateView called");
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true); //save fragment across config changes
        //CharacterDetails is retored get the saved Num
        if(savedInstanceState != null)
        {
            System.out.println("PROGRAM-TRACE: onCreateView---get the CharacterDetails from restored state");
            Num = savedInstanceState.getLong(MainActivity.Num);
        }
        else
        {
            //get Bundle of arguments then extract the character Num
            Bundle arguments = getArguments();
            if(arguments != null)
            {
                System.out.println("PROGRAM-TRACE: onCreateView---get Bundle of arguments then extract the character Num");
                Num = arguments.getLong(MainActivity.Num);
            }
        }
        //inflate the CharacterDetails layout
        View view = inflater.inflate(R.layout.character_details, container, false);
        setHasOptionsMenu(true);
        //get the EditTexts
        nameInXml = (TextView) view.findViewById(R.id.nametext);
        placeholder2InXml = (TextView) view.findViewById(R.id.info_two_text);
        placeholder3InXml = (TextView) view.findViewById(R.id.info_three_text);
        placeholder4InXml = (TextView) view.findViewById(R.id.info_four_text);
        placeholder5InXml = (TextView) view.findViewById(R.id.info_five_text);
        placeholder6InXml = (TextView) view.findViewById(R.id.info_six_text);
        placeholder7InXml = (TextView) view.findViewById(R.id.info_seven_text);
        System.out.println("PROGRAM-TRACE: onCreateView---get all EditTexts and return there values");
        return view;
    }

    //called when the CharacterDetails resumes
    @Override
    public void onResume() {

        System.out.println("PROGRAM-TRACE: CharacterDetails---onResume called");
        super.onResume();
        new LoadCharacterTask().execute(Num);

    }

    //save currently displayed character's Num
    @Override
    public void onSaveInstanceState(Bundle outState) {
        System.out.println("PROGRAM-TRACE: CharacterDetails---onSaveInstanceState called");
        super.onSaveInstanceState(outState);
        outState.putLong(MainActivity.Num, Num);
    }

    //display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        System.out.println("PROGRAM-TRACE: CharacterDetails---onCreateOptionsMenu called");
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_character_list,menu);

    }

    //handle menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        System.out.println("PROGRAM-TRACE: CharacterDetails---onOptionsItemSelected called");
        switch(item.getItemId())
        {

            case R.id.action_edit:
                System.out.println("PROGRAM-TRACE: onOptionsItemSelected---action_edit clicked");
                //create Bundle containing data to edit
                Bundle arguments = new Bundle();
                arguments.putLong(MainActivity.Num, Num);
                arguments.putCharSequence("name", nameInXml.getText());
                arguments.putCharSequence("info_two_text", placeholder2InXml.getText());
                arguments.putCharSequence("info_three_text", placeholder3InXml.getText());
                arguments.putCharSequence("info_four_text", placeholder4InXml.getText());
                arguments.putCharSequence("info_five_text", placeholder5InXml.getText());
                arguments.putCharSequence("info_six_text", placeholder6InXml.getText());
                arguments.putCharSequence("info_seven_text", placeholder7InXml.getText());
                listener.onEditCharacter(arguments);//pass Bundle to listener
                return true;

            case R.id.action_delete:
                System.out.println("PROGRAM-TRACE: onOptionsItemSelected---action_delete clicked");
                deleteCharacter();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //performs database query outside GUI thread
    public class LoadCharacterTask extends AsyncTask<Long, Object, Cursor>
    {
        DBHandler DBHandler = new DBHandler(getActivity());

        //open database and get Cursor representing specified character's data
        @Override
        protected Cursor doInBackground(Long... params)
        {
            try {
                DBHandler.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return DBHandler.getOneCharacter(params[0]);
        }

        //use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result) {
            System.out.println("PROGRAM-TRACE: LoadCharacterTask---onPostExecute called");
            super.onPostExecute(result);
            result.moveToFirst();//move to the first item
            //communicate objects with Database
            int nameIndex = result.getColumnIndex("name");
            int twoIndex = result.getColumnIndex("placeholder2");
            int threeIndex = result.getColumnIndex("placeholder3");
            int fourIndex = result.getColumnIndex("placeholder4");
            int fiveIndex = result.getColumnIndex("placeholder5");
            int sixIndex = result.getColumnIndex("placeholder6");
            int sevenIndex = result.getColumnIndex("placeholder7");
            //fill TextView with the retrieved data
            nameInXml.setText(result.getString(nameIndex));
            placeholder2InXml.setText(result.getString(twoIndex));
            placeholder3InXml.setText(result.getString(threeIndex));
            placeholder4InXml.setText(result.getString(fourIndex));
            placeholder5InXml.setText(result.getString(fiveIndex));
            placeholder6InXml.setText(result.getString(sixIndex));
            placeholder7InXml.setText(result.getString(sevenIndex));

            result.close();//close the result cursor
            try {
                DBHandler.close();//close database connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    //delete a character
    public void deleteCharacter()
    {
        System.out.println("PROGRAM-TRACE: CharacterDetails---deleteCharacter called");
        //use FragmentManager to display the confirm delete Dialog
        confirmDelete.show(getFragmentManager(), "confirm delete");
    }

    //DialogFragment  to confirm deletion of character
    public DialogFragment confirmDelete = new DialogFragment()
    {
        //create an AlertDialog and return it
        @Override
        public Dialog onCreateDialog(Bundle bundle)
        {
            System.out.println("PROGRAM-TRACE: CharacterDetails---DialogFragment(TOAST)---->onCreateDialog called");
            //create a new AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.confirm_title);
            builder.setMessage(R.string.confirm_message);
            //provide an OK button that simply dismisses the dialog
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int button)

                        {
                            final DBHandler DBHandler = new DBHandler(getActivity());
                            //ASyncTask deletes character and notifies listener
                            AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
                                @Override
                                protected Object doInBackground(Long... params) {
                                    try {
                                        DBHandler.deleteCharacter(params[0]);

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                                @Override
                                protected void onPostExecute(Object result) {
                                    listener.onCharacterDeleted();
                                }
                            };
                            //execute the ASyncTask to delete character at Num
                            deleteTask.execute(Num); //convert to just : Num instead?
                        }
                    }

            );
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            return builder.create();//return the AlertDialog
        }
    };
}

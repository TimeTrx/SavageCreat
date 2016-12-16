package ca.mortec.savageworldscharactercreator;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;



/**
 * Created by Mike on 11/9/2016.
 * This File will host all the app's Fragments
 */

public class MainActivity extends AppCompatActivity implements CharacterListFragment.CharacterListFragmentListener, CharacterDetails.character_detailsListener, AddEditActivity.AddEditActivityListener { //Possible refactor later

    //Stores the Row ID in the bundle passed to fragments
    public static final String Num = "row_id";
    //Container for the Character List, displays the char list
    CharacterListFragment characterListFragment;

    //Displays the Character List Fragment on load
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("PROGRAM-TRACE: MainActivity---onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);
        //If Activity is restored, don't do anything
        if(savedInstanceState != null)
        {
            System.out.println("PROGRAM-TRACE: Activity is restored, onCreate -> return");
            return;
        }
        //Check for Phone (small devices)
        if(findViewById(R.id.listView) != null)
        {
            System.out.println("PROGRAM-TRACE: Checked for phone is true");
            //creates the fragment
            characterListFragment = new CharacterListFragment();
            //adds the fragment to the layout
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.listView, characterListFragment);
            // causes CharacterListFragment to be added to the user's view
            transaction.commit();
            System.out.println("PROGRAM-TRACE: Commit -> listView for characterList");
        }
    }


    //Called when MainActivity Resumes, May remove it/refactor**********
    @Override
    public void onResume()
    {
        System.out.println("PROGRAM-TRACE: MainActivity---onResume called");
        super.onResume();
    }


    //display the character's details for the selected character
    public void onCharacterSelected(long numberRows)
    {
        System.out.println("PROGRAM-TRACE: MainActivity---onCharacterSelected called");
        //if a small device
        if(findViewById(R.id.listView)!= null)
        {
            System.out.println("PROGRAM-TRACE: small device detected -> onCharacterSelected");
            displayCharacter(numberRows, R.id.listView);
        }
    }


    //Displays the Character
    public void displayCharacter(long numberRows, int viewID)
    {
        System.out.println("PROGRAM-TRACE: MainActivity---displayCharacter called");
        CharacterDetails character_detail = new CharacterDetails();
        //Specify the gloabl variable Num for the argument to be passed to CharacterDetails
        Bundle arguments = new Bundle();
        arguments.putLong(Num, numberRows);
        character_detail.setArguments(arguments);
        //FragmentTransaction displays the Character detail
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewID, character_detail);
        transaction.addToBackStack(null);
        //displays the correct CharacterDetails
        transaction.commit();
    }



    @Override
    public void onAddCharacter()
    {
        System.out.println("PROGRAM-TRACE: MainActivity---onAddCharacter called");
        //displays the add_edit_activity to add a new character
        displayAddEditActivity(R.id.listView, null);
    }

    private void displayAddEditActivity(int viewID, Bundle arguments)
    {
        System.out.println("PROGRAM-TRACE: MainActivity---displayAddEditActivity called");
        //displays fragment for adding a new or editing a character
        AddEditActivity AddEditActivity = new AddEditActivity();
        //editing character
        if(arguments != null)
        {
            AddEditActivity.setArguments(arguments);
        }
        //use FragmentTransaction to display the add_edit_activity
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewID, AddEditActivity);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    //return to character list when currently displayed character is deleted
    @Override
    public void onCharacterDeleted()
    {
        System.out.println("PROGRAM-TRACE: MainActivity---onCharacterDeleted called");
        getFragmentManager().popBackStack();//removes top of back stack
        if(findViewById(R.id.listView)== null)//tablet
        {
            System.out.println("PROGRAM-TRACE: tablet -> onCharacterDeleted called");
        }
            characterListFragment.updateCharacterList();
    }

    //displays the AddEditActivity to edit an existing character
    @Override
    public void onEditCharacter(Bundle arguments)
    {
        System.out.println("PROGRAM-TRACE: MainActivity---onEditCharacter called");
        displayAddEditActivity(R.id.listView, arguments);
    }

    //update GUI after new character or updated character saved
    @Override
    public void onAddEditCompleted(long numberRows)
    {
        System.out.println("PROGRAM-TRACE: MainActivity---onAddEditCompleted called");
        getFragmentManager().popBackStack(); //removes top of back stack
    }
}
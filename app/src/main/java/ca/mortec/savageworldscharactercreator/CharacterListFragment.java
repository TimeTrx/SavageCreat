package ca.mortec.savageworldscharactercreator;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.sql.SQLException;

/**
 * Created by Mike on 11/28/2016.
 * Displays the list of characters
 */

public class CharacterListFragment extends ListFragment{

    //callback methods implemented by MainActivity
    public interface CharacterListFragmentListener
    {
        //called when user selects a character
        public void onCharacterSelected(long num); //Refactor ****

        //called when users adds a character
        public void onAddCharacter(); //Refactor ******
    }

    //responds to the user touching a character's name in the ListView
    AdapterView.OnItemClickListener viewCharacterListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long number)
        {
            listener.onCharacterSelected(number);
        }
    };//end viewCharacterListener

    private CharacterListFragmentListener listener;
    private ListView characterListView; //Refactor ********
    private CursorAdapter characterAdapter;//adapter for the ListView

    //set CharacterListFragment when fragment attached
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        listener = (CharacterListFragmentListener) activity;

    }


    //remove CharacterListFragment when Fragment detached
    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    //called after View created
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true); //save fragment across config changes
        setHasOptionsMenu(true); //this fragment has menu items to display
        //set text to display when there are no characters
        setEmptyText(getResources().getString(R.string.no_characters));
        //get ListView reference and configure ListView
        characterListView = getListView();
        characterListView.setOnItemClickListener(viewCharacterListener);
        characterListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //map each character's name to a TextView in the ListView layout
        String[] from = new String[] {"name"};
        int[] to = new int[] {android.R.id.text1};
        characterAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, from, to, 0);
        setListAdapter(characterAdapter); //sets adapter that supplies data
    }

    //when fragments resumes, use a GetCharacterTask to load contacts
    @Override
    public void onResume()
    {
        super.onResume();
        new GetCharactersTask().execute((Object[]) null);
    }

    // performs database query outside GUI thread
    private class GetCharactersTask extends AsyncTask<Object, Object, Cursor>
    {
        DBHandler databaseConnector = new DBHandler(getActivity());

        //open database and return Cursor for all characters
        @Override
        protected Cursor doInBackground(Object... params)
        {
            try {
                databaseConnector.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return databaseConnector.getAllCharacters();
        }

        //use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            characterAdapter.changeCursor(result); //sets the adapter's Cursor
            try {
                databaseConnector.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }//end GetCharactersTask
    }

    //when fragment stops, close Cursor and remove from contactAdapter
    @Override
    public void onStop()
    {
        Cursor cursor = characterAdapter.getCursor();
        characterAdapter.changeCursor(null);

        if(cursor != null)
        {
            cursor.close();// release the Cursor's resources
        }
        super.onStop();

    }


    //display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_char, menu);

    }

    //handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_add:
                listener.onAddCharacter();
                return true;

        }

        return super.onOptionsItemSelected(item); //call the method's super
    }


    //update data set
    public void updateCharacterList()
    {
        new GetCharactersTask().execute((Object[]) null);
    }
}

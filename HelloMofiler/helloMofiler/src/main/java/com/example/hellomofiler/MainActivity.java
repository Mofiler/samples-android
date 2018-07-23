package com.example.hellomofiler;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mofiler.Mofiler;
import com.mofiler.api.ApiListener;

public class MainActivity extends ActionBarActivity {

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
     
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{

    	private Mofiler mof;
    	private int iTestCounter;

    	
        public PlaceholderFragment() {
        }
        

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Button btnsend = (Button) rootView.findViewById(R.id.send_data);
            btnsend.setOnClickListener(new OnClickListener() {
     			@Override
    			public void onClick(View v) {
     	        	try {
     	                mof.injectValue("mykey" + iTestCounter, "myvalue");
     	                iTestCounter++;
     	                ((Button)v).setText("Send Data to Mofiler - "+ iTestCounter);
     	        	}
     	        	catch(Exception ex) {
     	        		System.err.println(ex.getMessage());
     	        		ex.printStackTrace();
     	        	}
     				
    			}
    		});

            Button btnrecv = (Button) rootView.findViewById(R.id.receive_data);
            btnrecv.setOnClickListener(new OnClickListener() {
     			@Override
    			public void onClick(View v) {
     	        	try {
     	                mof.getValue("mykey0", "username", "johndoe", new ApiListener() {
							@Override
							public void onResponse(int reqCode, JSONObject response) {
								// no op
								Log.d(MainActivity.class.getName(), response.toString());
                                Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onError(int reqCode, JSONObject originalPayload, VolleyError error) {
								//no op
								Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
							}
						});
     	        	}
     	        	catch(Exception ex) {
     	        		System.err.println(ex.getMessage());
     	        		ex.printStackTrace();
     	        	}
     				
    			}
    		});

            Button btnflush = (Button) rootView.findViewById(R.id.flush_data);
            btnflush.setOnClickListener(new OnClickListener() {
     			@Override
    			public void onClick(View v) {
     	        	try {
     	                mof.flushDataToMofiler();
     	        	}
     	        	catch(Exception ex) {
     	        		System.err.println(ex.getMessage());
     	        		ex.printStackTrace();
     	        	}
     				
    			}
    		});

            return rootView;
        }

		@Override
		public void onAttach(Context context) {
			super.onAttach(context);
			mof = Mofiler.getInstance(context);
		}
	}

    
}

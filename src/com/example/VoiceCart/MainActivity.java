package com.example.VoiceCart;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
 
/**
 * A very simple application to handle Voice Recognition intents
 * and display the results
 */
public class MainActivity extends Activity implements OnItemSelectedListener
{
 
    private static final int REQUEST_CODE = 1234;
    private static List<String> itemList = new ArrayList<String>(
            Arrays.asList("milk", "cabbage", "rice", "oil", "curd", "tomato","potato"));
    private EditText wordsList;
    private EditText kgsList;
    private EditText expList;
    private Spinner spinner1;
    private EditText txtView;
    private String initialDate;
    private String initialMonth;
    private String initialYear;
    private DatePickerDialog dialog = null;
    protected ImageView _image;
    protected String _path;
    private File output=null;
    private TextToSpeech tts;




    private final static String STORETEXT="storetext";
    private final static String STOREKGS="storekgs";
    private final static String STOREHISTORY="storehistory";




 
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_recog);
 
        ImageButton speakButton = (ImageButton) findViewById(R.id.speakButton);
       // ImageButton ocrButton = (ImageButton) findViewById(R.id.ocrButton);
       // ocrButton.setOnClickListener(new ButtonClickHandler());
        _image = ( ImageView ) findViewById( R.id.image );
        _path = Environment.getExternalStorageDirectory() + "/images/make_machine_example.jpg";





        wordsList = (EditText) findViewById(R.id.list);
        kgsList = (EditText) findViewById(R.id.kgs);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        ImageButton btn = (ImageButton) findViewById(R.id.button1);
        txtView = (EditText) findViewById(R.id.textView1);
        txtView.setText("01/01/2014");
        

        addListenerOnSpinnerItemSelection();
        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            
        }
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar dtTxt = null;

              String preExistingDate = (String) txtView.getText().toString();
              
              if(preExistingDate != null && !preExistingDate.equals("")){
                  StringTokenizer st = new StringTokenizer(preExistingDate,"/");
                      initialMonth = st.nextToken();
                      initialDate = st.nextToken();
                      initialYear = st.nextToken();
                      if(dialog == null)
                      dialog = new DatePickerDialog(v.getContext(),
                                       new PickDate(),Integer.parseInt(initialYear),
                                       Integer.parseInt(initialMonth)-1,
                                       Integer.parseInt(initialDate));
                      dialog.updateDate(Integer.parseInt(initialYear),
                                       Integer.parseInt(initialMonth)-1,
                                       Integer.parseInt(initialDate));
                      
              } else {
                  dtTxt = Calendar.getInstance();
                  if(dialog == null)
                  dialog = new DatePickerDialog(v.getContext(),new PickDate(),dtTxt.getTime().getYear(),dtTxt.getTime().getMonth(),
                                                      dtTxt.getTime().getDay());
                  dialog.updateDate(dtTxt.getTime().getYear(),dtTxt.getTime().getMonth(),
                                                      dtTxt.getTime().getDay());
              }
                
                dialog.show();
            }
            
        });
        
    }
 
    /**
     * Handle the action of the button being clicked
     */
    private class PickDate implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
            view.updateDate(year, monthOfYear, dayOfMonth);
            txtView.setText(monthOfYear+1+"/"+dayOfMonth+"/"+year);
            dialog.hide();
        }
        
    }
    public void saveButtonClicked(View v)
    {
    	String date = txtView.getText().toString().replace("/", "_");
		 Log.d("date is", date);
		 String autoFillHist = "";
  	   try {
      		
     		InputStream in = openFileInput(STOREHISTORY);
     		Log.d("restoring from", STOREHISTORY);
     		if (in != null) {
     			InputStreamReader tmp=new InputStreamReader(in);
     			BufferedReader reader=new BufferedReader(tmp);
     			String str;
     			StringBuilder buf=new StringBuilder();
     			while ((str = reader.readLine()) != null) {
     				buf.append(str+"\n");
     			}
     		 autoFillHist = buf.toString();
     			in.close();
     		}
     		Log.d("auto fill history before save is", autoFillHist);
     	}
     	
     	catch (java.io.FileNotFoundException e) {
     		 
     		// that's OK, we probably haven't created it yet
     		 
     		}

     	catch (Throwable t) {
    		 
     		Toast
     		 
     		.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
     		 
     		.show();
     		 
     		}
    	try {
    		OutputStreamWriter out = 
    		new OutputStreamWriter(openFileOutput(STORETEXT + date, 0));
    		out.write(wordsList.getText().toString());
    		String [] cart = wordsList.getText().toString().split("\n");
    		String [] hist = autoFillHist.split("\n");
    		autoFillHist = "";
    		for (int i=0;i<cart.length;i++) {
    			Boolean match = false;
                 for (int idx=0;idx<hist.length;++idx) {
                	 
                	 //int idx = Arrays.asList(hist).indexOf(cart[i]);
                	 
                	 String current = hist[idx];
                	 String item = current.split(",")[0];
                	 if (!item.equals(cart[i])) {
                		 continue;
                	 }
                	 match = true;
                	 String freq = current.split(",")[1];
                	 String lastDt = current.split(",")[2];
                	 int lastYr = Integer.parseInt(lastDt.split("_")[2]);
                	 int lastMt = Integer.parseInt(lastDt.split("_")[0]);
                	 int lastDy = Integer.parseInt(lastDt.split("_")[1]);

                	 int currYr = Integer.parseInt(date.split("_")[2]);
                	 int currMt = Integer.parseInt(date.split("_")[0]);
                	 int currDy = Integer.parseInt(date.split("_")[1]);
                	 int newFreq = currDy + 30*currMt + 365*currYr - lastDy - 30*lastMt - 365*lastYr;
                	 if (newFreq == 0){
                		 continue;
                	 }
                	 hist[idx] = item + "," + newFreq + "," + Integer.toString(currMt) + "_" + Integer.toString(currDy) + "_" + Integer.toString(currYr);
                     break;
                	 
                 } 
                 if (!match)  {
                	 autoFillHist += '\n' + cart[i] + "," + "0" + "," +  date;
                	 
                 }
    		}
    		for (int i=0; i<hist.length;i++) {
    			autoFillHist += '\n' + hist[i];
    		}
    		
    		out.close();
    		Toast
    		.makeText(this, "The contents are saved in the file.", Toast.LENGTH_LONG)
    		.show();
    		wordsList.setText("");
    		
    		}
    		 
    		catch (Throwable t) {
    		Toast
    		.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
    		.show();
    		}	
    	   try {
    		OutputStreamWriter out = 
    		new OutputStreamWriter(openFileOutput(STOREKGS + date, 0));
    		out.write(kgsList.getText().toString());
    		out.close();
    		Toast
    		.makeText(this, "The contents are saved in the file.", Toast.LENGTH_LONG)
    		.show();
    		kgsList.setText("");
    		}
    		 
    		catch (Throwable t) {
    		Toast
    		.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
    		.show();
    		}	
    	  Log.d("Srinath",autoFillHist);
    	   try {
       		OutputStreamWriter out = 
       		new OutputStreamWriter(openFileOutput(STOREHISTORY, 0));
       		Log.d("auto fill history after save is", autoFillHist);

       		out.write(autoFillHist);
       		out.close();
       		Toast
       		.makeText(this, "The contents are saved in the file.", Toast.LENGTH_LONG)
       		.show();
       		}
       		 
       		catch (Throwable t) {
       		Toast
       		.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
       		.show();
       		}	
    }
    
    public void emailButtonClicked(View v)
    {
    	 Intent i = new Intent(Intent.ACTION_SEND);
         i.setType("message/rfc822");
         i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"srinath1984@gmail.com"});
         i.putExtra(Intent.EXTRA_SUBJECT, "VoiceCart");
         i.putExtra(Intent.EXTRA_TEXT   , wordsList.getText().toString());
         try {
             startActivity(Intent.createChooser(i, "Send mail..."));
         } catch (android.content.ActivityNotFoundException ex) {
             Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
         }
    }
    public void autoFillButtonClicked(View v)
    {
    	String date = txtView.getText().toString().replace("/", "_");
    	Log.d("came","here to autofill");
    	 try {
    		 String autoFillHist;
    		 InputStream in = openFileInput(STOREHISTORY);
      		Log.d("restoring from", STOREHISTORY);
      		if (in != null) {
      			InputStreamReader tmp=new InputStreamReader(in);
      			BufferedReader reader=new BufferedReader(tmp);
      			String str;
      			StringBuilder buf=new StringBuilder();
      			while ((str = reader.readLine()) != null) {
      				buf.append(str+"\n");
      			}
      			//wordsList.setText(buf.toString());
      			String[] bufArr = buf.toString().split("\n");
      			String finalStr = "";
      			for (int i=0;i<bufArr.length;++i) {
      			    if (bufArr[i].trim().length() == 0) {
      			    	continue;
      			    }
                    String item = bufArr[i].split(",")[0];
                    int    freq = Integer.parseInt(bufArr[i].split(",")[1]);
                    String lastDt =bufArr[i].split(",")[2];
                    if (freq == 0) {
                    	continue;
                    }
                    
                    int lastYr = Integer.parseInt(lastDt.split("_")[2]);
                    int lastMt = Integer.parseInt(lastDt.split("_")[0]);
                    int lastDy = Integer.parseInt(lastDt.split("_")[1]);

                    int currYr = Integer.parseInt(date.split("_")[2]);
                    int currMt = Integer.parseInt(date.split("_")[0]);
                    int currDy = Integer.parseInt(date.split("_")[1]);
                    int newFreq = currDy + 30*currMt + 365*currYr - lastDy - 30*lastMt - 365*lastYr;
                    if (newFreq >= freq) {
                        if (finalStr == "") {
                                finalStr = item;
                        } else {
                                finalStr += '\n' + item;
                        }
                    }

      			}
      			wordsList.setText(finalStr);
       			in.close();
      		}
    	 }
      	
      	catch (java.io.FileNotFoundException e) {
      		// that's OK, we probably haven't created it yet
      		}

      	catch (Throwable t) {
      		Toast
      		.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
      		.show();
      		}
    }
    
    public void restoreButtonClicked(View v)
    
    {
    	String date = txtView.getText().toString().replace("/", "_");

		 Log.d("date is", date);
    	try {
     		
    		InputStream in = openFileInput(STORETEXT + date);
    		Log.d("restoring from", STORETEXT + date);
    		if (in != null) {
    			InputStreamReader tmp=new InputStreamReader(in);
    			BufferedReader reader=new BufferedReader(tmp);
    			String str;
    			StringBuilder buf=new StringBuilder();
    			while ((str = reader.readLine()) != null) {
    				buf.append(str+"\n");
    			}
    		 
    			in.close();
    			wordsList.setText(buf.toString());
    		}
    	}
    	
    	catch (java.io.FileNotFoundException e) {
    		 
    		// that's OK, we probably haven't created it yet
    		 
    		}

    	catch (Throwable t) {
   		 
    		Toast
    		 
    		.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
    		 
    		.show();
    		 
    		}
try {
     		
    		InputStream in = openFileInput(STOREKGS + date);
    		Log.d("restoring from", STOREKGS + date);
    		if (in != null) {
    			InputStreamReader tmp=new InputStreamReader(in);
    			BufferedReader reader=new BufferedReader(tmp);
    			String str;
    			StringBuilder buf=new StringBuilder();
    			while ((str = reader.readLine()) != null) {
    				buf.append(str+"\n");
    			}
    		 
    			in.close();
    			kgsList.setText(buf.toString());
    		}
    	}
    	
    	catch (java.io.FileNotFoundException e) {
    		 
    		// that's OK, we probably haven't created it yet
    		 
    		}
    		 
    		catch (Throwable t) {
    		 
    		Toast
    		 
    		.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
    		 
    		.show();
    		 
    		}
    		 
    		}
 
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }
    public class ButtonClickHandler implements View.OnClickListener 
    {
        public void onClick( View view ){
        	try {
        		FileOutputStream fos = openFileOutput("MyFile.jpg", Context.MODE_WORLD_WRITEABLE);
        		fos.close();
        		output = new File(getFilesDir() + File.separator + "MyFile.jpg");
        		startActivityForResult(
        		        new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        		            .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output))
        		        , 0);
        		}
        		catch(IOException e) {

        		}
        	//startCameraActivity();
        }
    }
    protected void startCameraActivity()
    {
        File file = new File( _path );
        Uri outputFileUri = Uri.fromFile( file );
        	
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
        	
        startActivityForResult( intent, 0 );
    }
    
    
    
    
    
    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);

        startActivityForResult(intent, REQUEST_CODE);
    }
    public void addListenerOnSpinnerItemSelection(){
    	spinner1.setOnItemSelectedListener(this);
    }
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i( "MakeMachine", "resultCode: " + resultCode );
        Log.i( "MakeMachine", "requestCode: " + requestCode );

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String[] mStringArray = new String[matches.size()];
            mStringArray = matches.toArray(mStringArray);
            ArrayList<String> resList = new ArrayList<String>();
            String res = "";
            Log.d("curr focus is",Integer.toString(getCurrentFocus().getId()));
            Log.d("edit text fc is",Integer.toString(wordsList.getId()));


            for(int i = 0; i < mStringArray.length ; i++){
              String[] separated = mStringArray[i].split(" ");
              if (separated[0].equals("cancel")) {
            	  String[] partial = wordsList.getText().toString().split("\n");
            	  int toBeDel = partial.length;
            	  try {
            		    toBeDel = Integer.parseInt(separated[2]) - 1;
            		} catch(NumberFormatException nfe) {
            		   System.out.println("Could not parse " + nfe);
            		} 
            	  Log.d("tbd", Integer.toString(toBeDel));

            	  if (toBeDel >= partial.length) {
            		  return;
            	  }
            	  for (int j=0; j< partial.length; j++) {
            		  if (j == toBeDel) {
            			  continue;
            		  }
            		 
            		  if ( res!= "") {
            		      res += '\n' + partial[j];
            		  } else {
            			  res = partial[j];
            		  
            		  }
            	  }

            	  wordsList.setText(res);
            	  res = "";
            	  partial = kgsList.getText().toString().split("\n");
            	  for (int j=0; j< partial.length; j++) {
            		  if (j == toBeDel) {
            			  continue;
            		  }
            		 
            		  if ( res!= "") {
            		      res += '\n' + partial[j];
            		  } else {
            			  res = partial[j];
            		  
            		  }
            	  }
            	  kgsList.setText(res);
            	  
            	  return;
              }
             

  			  res+=mStringArray[i];
              break;
            	//for (String curVal : itemList){
      			  //Log.d("relevant item ",(mStringArray[i]));

            		//  if (curVal.contains(mStringArray[i])){
            			//  resList.add(curVal);
            			 // res+=curVal;
            		 // }
            	//	}
                
            }
            int cF = 0;
            if (kgsList.getId() == getCurrentFocus().getId()) {
            	cF = 1;
            }
            if (res != "" ) {
            	if (cF == 0) {
                	  String fff = "";
                  	  String[] partial =wordsList.getText().toString().split("\n");
                  	for (int k=0; k< partial.length; k++) {
                  	  if (partial[k] == "") {
                  		  continue;
                  	  }
                  	  if (fff == "") {
                  	     fff += partial[k];
                  	  } else {
                  		  fff += '\n' + partial[k];
                  	  }
                  	  
                    }
                  	 if (fff != "") {
                     	fff += '\n' + res;
                       } else {
                     	  fff = res;
                       }
                  	 Log.d("relevant",wordsList.getText().toString());
            		if (wordsList.getText().toString().trim().length()==0) {
            			Log.d("relevant","here");
            			wordsList.setText(res);
            		} else {
            			wordsList.setText(wordsList.getText().toString() + '\n'+ res);
            		}
            		wordsList.setText(fff);

            		kgsList.requestFocus();
            		//tts.speak("enter quantity", TextToSpeech.QUEUE_FLUSH, null);
            	} else {
            		Log.d("relevant",kgsList.getText().toString());
              	  String[] partial = kgsList.getText().toString().split("\n");
              	  String fff = "";
                  for (int k=0; k< partial.length; k++) {
                	  if (partial[k] == "") {
                		  continue;
                	  }
                	  if (fff == "") {
                	     fff += partial[k];
                	  } else {
                		  fff += '\n' + partial[k];
                	  }
                	  
                  }
                  if (fff != "") {
                	fff += '\n' + res;
                  } else {
                	  fff = res;
                  }

            		if (kgsList.getText().toString().trim().length()==0) {
            			Log.d("relevant","here");
            			kgsList.setText(fff);
            		} else {
            			kgsList.setText(kgsList.getText().toString() + '\n'+ res);
            		}
            		kgsList.setText(fff);
            		wordsList.requestFocus();
            		
            	}
            }
           
        }
        if (requestCode == 0 && resultCode == RESULT_OK) {
        	Log.d ("came here","man");
    		//onPhotoTaken();
        	Intent i=new Intent(Intent.ACTION_VIEW);
            
            i.setDataAndType(Uri.fromFile(output), "image/jpeg");
            startActivity(i);
            finish();
          
            try {
            	InputStream is = openFileInput("MyFile.jpg");
            	BitmapFactory.Options options = new BitmapFactory.Options();
            	//options.inSampleSize = 4;
            	Bitmap retrievedBitmap = BitmapFactory.decodeStream(is, null, options);
                _image.setImageBitmap(retrievedBitmap);
Log.d("CAAA","DAA");
            	}
            	catch(IOException e) {

            	}

        }

        
        super.onActivityResult(requestCode, resultCode, data);
    }
    protected void onPhotoTaken()
    {
       // _taken = true;
        	
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        	
        Bitmap bitmap = BitmapFactory.decodeFile( _path, options );
        _image.setImageBitmap(bitmap);
        	
       // _field.setVisibility( View.GONE );
    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
            long id) {
		  Log.d(" item ",parent.getItemAtPosition(pos).toString() );
		  String selected = parent.getItemAtPosition(pos).toString();
		  Log.d("selected is ", selected);
		  if (selected.equals("Email")) {
			  Log.d("relevant","email");
			  emailButtonClicked(view);
		  } else if(selected.equals("Save")) {
			  saveButtonClicked(view);
		  } else if(selected.equals("Restore")) {
			  restoreButtonClicked(view);
		  } else if(selected.equals("Autofill")) {
			  Log.d("you selected","autofill");
		      autoFillButtonClicked(view);
		  }

		  parent.setSelection(0);

    		//Toast.makeText(parent.getContext(), 
              //  "On Item Select : \n" + parent.getItemAtPosition(pos).toString(),
               // Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
 
    }
   
}


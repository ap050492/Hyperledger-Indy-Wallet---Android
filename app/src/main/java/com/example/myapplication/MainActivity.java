package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.LibIndy;
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.anoncreds.Anoncreds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    String walletName = "myWallet";
    String walletKey = "myWallet";
    private Button backup_wallet_button;
    private Button create_wallet_button;
    private Button import_wallet_button;
    private Button create_wallet_master_key;
    private TextView did_textview;

    private String myVerKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Os.setenv("EXTERNAL_STORAGE", getExternalFilesDir(null).getAbsolutePath(), true);
            System.loadLibrary("indy");
        } catch (ErrnoException e) {
            e.printStackTrace();
        }

        backup_wallet_button = (Button)findViewById(R.id.backup_wallet_button);
        create_wallet_button = (Button)findViewById(R.id.create_wallet_button);
        import_wallet_button = (Button)findViewById(R.id.import_wallet_button);
        create_wallet_master_key = (Button)findViewById(R.id.create_wallet_master_key);
        did_textview = (TextView)findViewById(R.id.did_textview);

        backup_wallet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ExportWalletTask().execute();
            }
        });



        create_wallet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWallet();
            }
        });

        import_wallet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new importWalletTask().execute();
                Log.d("Hi_sas","hii");
            }
        });

        create_wallet_master_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMasterSecret();
            }
        });
    }

    public void createWallet()
    {
        LibIndy.init();
        if(LibIndy.isInitialized()){

            try {
                String myWalletConfig = new JSONObject().put("id", walletName).toString();
                String myWalletCredentials= new JSONObject().put("key", walletKey).toString();
                Wallet.createWallet(myWalletConfig, myWalletCredentials).get();
                Wallet myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get();

                // 4. Create My Did
                DidResults.CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(myWallet, "{}").get();
                String myDid = createMyDidResult.getDid();
                myVerKey = createMyDidResult.getVerkey();

                did_textview.setText(myDid);
                myWallet.closeWallet().get();
                Log.d("Hi_myWallet",myDid);
            } catch (ExecutionException e) {
                Log.d("Hi_ExecutionException",e.toString());
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("HI_InterruptedException",e.toString());
            } catch (IndyException e) {
                e.printStackTrace();
                Log.d("HI_IndyException",e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class ExportWalletTask extends AsyncTask {
        ProgressDialog progressDialog;

        @Override
        protected Object doInBackground(Object[] objects) {


            JSONObject exportConfigJson = new JSONObject();
            try {
                exportConfigJson.put("path","/storage/emulated/0/Protech/backup/protech.wallet" );
                exportConfigJson.put("key", "test");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.d("Hi_eror",e.toString());
                e.printStackTrace();
            }

            Log.d("Hi_tostring", exportConfigJson.toString());
            Log.d("Hi_path", String.valueOf(exportConfigJson));

            try {
                String myWalletConfig = new JSONObject().put("id", walletName).toString();
                String myWalletCredentials= new JSONObject().put("key", walletKey).toString();
                Wallet myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get();

                return Wallet.exportWallet(myWallet, String.valueOf(exportConfigJson));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Hi_1",e.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("Hi_2",e.toString());
            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.d("Hi_3",e.toString());
            } catch (IndyException e) {
                e.printStackTrace();
                Log.d("Hi_4",e.toString());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Downloading", "Downloading..Please Wait", true);
        }

        @Override
        protected void onPostExecute(Object o) {


            progressDialog.dismiss();

        }


    }

    private class importWalletTask extends AsyncTask {
        ProgressDialog progressDialog;

        @Override
        protected Object doInBackground(Object[] objects) {

            JSONObject configJson = new JSONObject();
            JSONObject credentialsJson = new JSONObject();
            JSONObject importConfigJson = new JSONObject();

            File file = new File("/storage/emulated/0/Protech/ap.txt");

            if(file.exists()){
                Log.d("Hi_","File Exists");
            }else{
                Log.d("Hi_","File Not Exists");
            }

            try {
                configJson.put("id","test" );

                credentialsJson.put("key","test" );

                importConfigJson.put("path","/storage/emulated/0/Protech/ap.txt" );
                importConfigJson.put("key","test" );

                return Wallet.importWallet(String.valueOf(configJson),String.valueOf(credentialsJson),String.valueOf(importConfigJson));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.d("Hi_eror",e.toString());
                e.printStackTrace();
            } catch (IndyException e) {
                Log.d("Hi_erorindy",e.toString());
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Downloading", "Downloading..Please Wait", true);

        }

        @Override
        protected void onPostExecute(Object o) {

            try {
                String myWalletConfig = new JSONObject().put("id", "test").toString();
                String myWalletCredentials= new JSONObject().put("key", "test").toString();
                Wallet myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get();
                String createMyDidResult   = Did.getListMyDidsWithMeta(myWallet).get();

                Log.d("Hi_",createMyDidResult);

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IndyException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        }
    }

    public void createMasterSecret(){
        Log.d("Hi_asd","call");
        try {
            String myWalletConfig = new JSONObject().put("id", walletName).toString();

            String myWalletCredentials= new JSONObject().put("key", walletKey).toString();
            Wallet myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get();
            String masterSecretId = Anoncreds.proverCreateMasterSecret(myWallet, null).get();
            Log.d("Hi_result", masterSecretId);
            Log.d("Hi_result", did_textview.getText().toString());
            Log.d("Hi_result", String.valueOf(myWallet.getWalletHandle()));
        } catch (JSONException e) {
            Log.d("Hi_JSONException",e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d("Hi_InterruptedException",e.toString());
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d("Hi_ExecutionException",e.toString());
            e.printStackTrace();
        } catch (IndyException e) {
            Log.d("Hi_IndyException",e.toString());
            e.printStackTrace();
        }
    }
//
}

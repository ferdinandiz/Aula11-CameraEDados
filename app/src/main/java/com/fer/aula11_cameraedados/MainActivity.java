package com.fer.aula11_cameraedados;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Button btnTexto;
    ImageButton btnSalvar, btnCarregar, btnEnviar, btnCamera;
    EditText edSuperior, edInferior;
    TextView txtSuperior, txtInferior;
    ImageView imagem;
    SeekBar seekSuperior, seekInferior;

    String arqImagem;
    Uri imagemUri;
    int progressoSuperior = 40, progressoInferior = 40;
    int requestCode;
    String[] permissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTexto = findViewById(R.id.btnTexto);
        btnCamera = findViewById(R.id.btnCamera);
        btnCarregar = findViewById(R.id.btnCarregar);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setEnabled(true);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSalvar.setEnabled(false);
        txtInferior = findViewById(R.id.txt01);
        txtSuperior = findViewById(R.id.txt02);
        edSuperior = findViewById(R.id.edsuperior);
        edInferior = findViewById(R.id.edinferior);
        seekSuperior = findViewById(R.id.seekBar1);
        seekInferior = findViewById(R.id.seekBar2);
        imagem = findViewById(R.id.imageView);

        seekSuperior.setMax(100);
        seekInferior.setMax(100);
        seekSuperior.setProgress(progressoSuperior);
        seekInferior.setProgress(progressoInferior);

        Intent fotoCarregada = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Intent fotoCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent principal = getIntent();

        requestCode = principal.getIntExtra("requestCode",0);

        activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                            if(Environment.isExternalStorageManager()){
                                Toast.makeText(getApplicationContext(), "Permissão Concedida!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Permissão Negada!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        );

        if(requestCode == 1){
//            TODO: Continuar o requestCode
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Images.Media.TITLE,"Nova Imagem");
//            values.put(MediaStore.Images.Media.DESCRIPTION,"Imagem da Camera");
//            imagemUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
//            fotoCamera.putExtra(MediaStore.EXTRA_OUTPUT, imagemUri);
        }

        seekInferior.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressoInferior = progress;
                txtInferior.setTextSize(progressoInferior);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekSuperior.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressoSuperior = progress;
                txtSuperior.setTextSize(progressoSuperior);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSuperior.setText(edSuperior.getText().toString());
                txtInferior.setText(edInferior.getText().toString());
                edSuperior.setText("");
                edInferior.setText("");
                btnEnviar.setEnabled(false);
                btnSalvar.setEnabled(true);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.putExtra("requestCode",1);
                startActivity(i);
                finish();
            }
        });

        btnCarregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.putExtra("requestCode",2);
                startActivity(i);
                finish();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar();
            }
        });


    }

    private void enviar() {
        String diretorio = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath() + "/Aula11App/"+arqImagem;
        File f = new File(diretorio);
        ContentValues values = new ContentValues(2);
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/png");
        values.put(MediaStore.Images.Media.DATA, f.getAbsolutePath());
        Uri uri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent,"Compartilhar usando..."));
    }
}
package com.fer.aula11_cameraedados;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        btnEnviar.setEnabled(false);
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
        Toast.makeText(this, "requestCode: "+requestCode, Toast.LENGTH_SHORT).show();
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
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE,"Nova Imagem");
            values.put(MediaStore.Images.Media.DESCRIPTION,"Imagem da Camera");
            imagemUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
            fotoCamera.putExtra(MediaStore.EXTRA_OUTPUT, imagemUri);
            openSomeActivityForResult(fotoCamera, requestCode);
        } else if (requestCode == 2){
            fotoCarregada.setType("*/*");
            fotoCarregada = Intent.createChooser(fotoCarregada,
                    "Escolha um arquivo!");
            openSomeActivityForResult(fotoCarregada, requestCode);
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

        if(!temCamera()){
            btnCamera.setEnabled(false);
        }

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

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions()){
                    View relativeCenter = findViewById(R.id.relativeCenter);
                    Bitmap bitmap = screenShot(relativeCenter);
                    arqImagem = "MinhaFoto_"+System.currentTimeMillis()+".png";
                    armazenarImagem(bitmap,arqImagem);
                    btnEnviar.setEnabled(true);
                    btnSalvar.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Meme salvo com sucesso!!", Toast.LENGTH_SHORT).show();
                }else{
                    requestPermission();
                }
            }
        });

        if(ContextCompat.checkSelfPermission(
                MainActivity.this,
                WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this,
                    WRITE_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{WRITE_EXTERNAL_STORAGE},
                        1);
            }else{
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }


    }

    public void requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", new Object[]{
                        getApplicationContext().getPackageName()
                })));
                activityResultLauncher.launch(intent);
            } catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activityResultLauncher.launch(intent);
            }
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, permissions,8);
        }
    }

    boolean checkPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return Environment.isExternalStorageManager();
        else{
            int readCheck = ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED || writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void openSomeActivityForResult(Intent intentData, int requestCode) {
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK) {
                        if (requestCode == 1 && intentData != null) {
                            Bundle bundle = intentData.getExtras();
                            if(bundle != null){
                                imagem.setImageURI(imagemUri);
                                btnSalvar.setEnabled(true);
                                btnEnviar.setEnabled(false);
                            }else{
                                Toast.makeText(MainActivity.this, "Algum erro aconteceu com a foto!", Toast.LENGTH_SHORT).show();
                            }
                        } else if (requestCode == 2 && intentData != null){
                            Intent resultado = result.getData();
                            Uri pegarImagem = resultado.getData();
                            imagem.setImageURI(pegarImagem);
                            btnSalvar.setEnabled(true);
                            btnEnviar.setEnabled(false);
                        }
                    }
                }
            });
    }

    public Boolean temCamera(){
        return getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY
        );
    }

    public static Bitmap screenShot(View v){
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void armazenarImagem(Bitmap bitmap, String arquivo){
        String diretorio = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Aula11App";
        File dir = new File(diretorio);
        if(!dir.exists()){
            if(dir.mkdir()){
                Toast.makeText(this,"Pasta Criada: "+diretorio,Toast.LENGTH_SHORT).show();
            }
        }
        File file = new File(diretorio,arquivo);
        if(!file.exists()) file = new File(dir,arquivo);
        try{
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
            Toast.makeText(this,"Salvo com Sucesso!",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Falha na hora de salvar!",Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Sem Permissão Suficiente!",Toast.LENGTH_SHORT).show();
                    finish();
                }else if (
                        ContextCompat.checkSelfPermission(MainActivity.this,
                        WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                }
                break;
            case 8:
                if(grantResults.length >0){
                    boolean readPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(readPermission && writePermission)
                        Toast.makeText(this, "Permissão Concedida!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "Permissão Negada!", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }
}
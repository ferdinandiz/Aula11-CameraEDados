package com.fer.aula11_cameraedados;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    ImageButton btnCarregar, btnSalvar, btnCamera, btnEnviar;
    Button btnTexto;
    EditText txsuperior, txinferior;
    TextView txt01, txt02;
    ImageView imagem;
    String arqImagem;
    Uri imagemUri;

    //seekbar
    SeekBar seekBar1;
    int progress1 = 40;
    SeekBar seekBar2;
    int progress2 = 40;
    Boolean color = false;
    int requestCode;

    String[] permissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCarregar = findViewById(R.id.btnCarregar);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCamera = findViewById(R.id.btnCamera);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnTexto = findViewById(R.id.btnTexto);
        txsuperior = findViewById(R.id.edsuperior);
        txinferior = findViewById(R.id.edinferior);
        txt01 = findViewById(R.id.txt01);
        txt02 = findViewById(R.id.txt02);
        imagem = findViewById(R.id.imageView);
        seekBar1 = findViewById(R.id.seekBar1);
        seekBar2 = findViewById(R.id.seekBar2);
        btnSalvar.setEnabled(false);
        btnEnviar.setEnabled(false);
        seekBar1.setMax(100);
        seekBar1.setProgress(progress1);

        Intent fotoCarregada = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Intent fotoCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent principal = getIntent();

        requestCode = principal.getIntExtra("requestCode", 0);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            Toast.makeText(getApplicationContext(), "Permissão Concedida!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissão Negada!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        if (requestCode == 1) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Nova Imagem");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Imagem vinda da camera");
            imagemUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            fotoCamera.putExtra(MediaStore.EXTRA_OUTPUT, imagemUri);
            openSomeActivityForResult(fotoCamera, requestCode);
        } else if (requestCode == 2) {
            fotoCarregada.setType("*/*");
            fotoCarregada = Intent.createChooser(fotoCarregada, "Escolha um arquivo");
            openSomeActivityForResult(fotoCarregada, requestCode);
        }

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress1 = progress;
                //txt01.setText(""+progress1);
                txt01.setTextSize(progress1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar2.setMax(100);
        seekBar2.setProgress(progress1);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress2 = progress;
                //txt02.setText(""+progress2);
                txt02.setTextSize(progress2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        txt01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (color) {
                    txt01.setTextColor(Color.BLACK);
                    txt02.setTextColor(Color.BLACK);
                } else {
                    txt01.setTextColor(Color.WHITE);
                    txt02.setTextColor(Color.WHITE);
                }
                color = !color;
            }
        });
        txt02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (color) {
                    txt01.setTextColor(Color.BLACK);
                    txt02.setTextColor(Color.BLACK);
                } else {
                    txt01.setTextColor(Color.WHITE);
                    txt02.setTextColor(Color.WHITE);
                }
                color = !color;
            }
        });
        btnTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt01.setText(txsuperior.getText().toString());
                txt02.setText(txinferior.getText().toString());
                txsuperior.setText("");
                txinferior.setText("");
                btnEnviar.setEnabled(false);
                btnSalvar.setEnabled(true);
            }
        });
        btnCarregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.putExtra("requestCode", 2);
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
                if (checkPermission()) {
                    View relCenter = findViewById(R.id.relativeCenter);
                    Bitmap bitmap = screenShot(relCenter);
                    arqImagem = "Aula11_" + System.currentTimeMillis() + ".png";
                    armazenar(bitmap, arqImagem);
                    btnEnviar.setEnabled(true);
                    btnSalvar.setEnabled(false);
                } else {
                    requestPermission();
                }
            }
        });
        if (!temCamera()) {
            btnCamera.setEnabled(false);
        }
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.putExtra("requestCode", 1);
                startActivity(i);
                finish();
            }
        });
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        //openSomeActivityForResult(i, 2);
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("andoid.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
                activityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 30);
        }
    }

    boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED || writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void openSomeActivityForResult(Intent data, int requestCode) {
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult resultCode) {
                        if (resultCode.getResultCode() == RESULT_OK) {
                            if (requestCode == 2 && data != null) {
                                Intent resultado = resultCode.getData();
                                Uri pegarImagem = resultado.getData();
                                imagem.setImageURI(pegarImagem);
                                btnSalvar.setEnabled(true);
                                btnEnviar.setEnabled(false);
                            } else if (requestCode == 1 && data != null) {
                                Bundle bundle = data.getExtras();

                                if (bundle != null) {
                                    //Bitmap foto = (Bitmap) bundle.get("data");
                                    imagem.setImageURI(imagemUri);
                                    btnSalvar.setEnabled(true);
                                    btnEnviar.setEnabled(false);
                                } else {
                                    Toast.makeText(MainActivity.this, "Algum erro ocorreu", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
        someActivityResultLauncher.launch(data);
    }

    public Boolean temCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public static Bitmap screenShot(View v) {
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void armazenar(Bitmap bitmap, String arquivo) {
        String diretorio = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Aula11";
        File dir = new File(diretorio);
        if (!dir.exists()) {
            if (dir.mkdir()) {
                Toast.makeText(this, "Pasta Criada: " + diretorio, Toast.LENGTH_SHORT).show();
            }
        }
        File file = new File(diretorio, arquivo);
        if (!file.exists()) file = new File(dir, arquivo);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Salvo com Sucesso!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro na hora de Salvar!", Toast.LENGTH_SHORT).show();
        }
    }

    void enviar() {
        String diretorio = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Aula11/" + arqImagem;
        File f = new File(diretorio);
        ContentValues values = new ContentValues(2);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATA, f.getAbsolutePath());
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Compartilhar usando..."));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: { // referente à permissão de usuário
                if (grantResults.length <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Sem Permissão Suficiente!", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                }
                break;
            }
            case 30: {
                if (grantResults.length > 0) {
                    boolean readPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (readPermission && writePermission)
                        Toast.makeText(getApplicationContext(), "Permissão Concedida!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Permissão Negada!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Você Negou a Permissão!", Toast.LENGTH_SHORT).show();
                break;
            }
        }

    }
}
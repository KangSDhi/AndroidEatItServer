package com.example.androideatitserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androideatitserver.Common.Common;
import com.example.androideatitserver.Decoration.LinearDecoration;
import com.example.androideatitserver.Interface.ItemClickListener;
import com.example.androideatitserver.Model.ModelMakanan;
import com.example.androideatitserver.ViewHolder.FoodListMViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public class FoodListManagement extends AppCompatActivity {

    private Toolbar toolbar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Makanan");
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    FirebaseRecyclerAdapter<ModelMakanan, FoodListMViewHolder> adapter;

    ExtendedFloatingActionButton fabTambahMenu;

    TextInputLayout LayNamaMakanan, LayKategori, LayHarga, LayDiskon, LayDeskripsi;
    TextInputEditText EdtNamaMakanan, EdtKategori, EdtHarga, EdtDiskon, EdtDeskripsi;
    MaterialButton BtnSelect, BtnUpload, BtnTambah, BtnBatal;

    int intDiskon = 0;

    String KeyFood = "";
    String HeaderToolbar = "";

    final int PICK_IMAGE_REQUEST = 71;

    Uri saveUri;

    ModelMakanan modelMakanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list_management);

        toolbar = (Toolbar)findViewById(R.id.toolbarFoodListM);
        setSupportActionBar(toolbar);

        fabTambahMenu = (ExtendedFloatingActionButton)findViewById(R.id.fabTambahMenu);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerMenuListM);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                /*if (dy > 0 || dy < 0 && fabTambahMenu.isShown()){
                    fabTambahMenu.hide();
                }*/
                if (dy > 0){
                    fabTambahMenu.hide();
                }else if (dy < 0){
                    fabTambahMenu.show();
                }
                //super.onScrolled(recyclerView, dx, dy);
            }
        });

        if (getIntent() != null){
            KeyFood = getIntent().getStringExtra("keyFood");
            HeaderToolbar = getIntent().getStringExtra("headerToolbar");
            if (KeyFood != null){
                memuatListMenuByOrder(KeyFood, HeaderToolbar);
            }else {
                memuatListMenu();
            }
        }

    }

    private void memuatListMenuByOrder(final String keyFood, String headerToolbar) {

        fabTambahMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabTambahMenuByOrder(keyFood);
            }
        });

        toolbar.setTitle(headerToolbar);
        adapter = new FirebaseRecyclerAdapter<ModelMakanan, FoodListMViewHolder>(
                ModelMakanan.class,
                R.layout.item_r_food_list_m,
                FoodListMViewHolder.class,
                databaseReference.orderByChild("MenuId").equalTo(keyFood)
        ) {
            @Override
            protected void populateViewHolder(FoodListMViewHolder foodListMViewHolder, ModelMakanan modelMakanan, int i) {
                Picasso.get().load(modelMakanan.getGambar()).into(foodListMViewHolder.ImgMakanan);
                foodListMViewHolder.TextMakanan.setText(modelMakanan.getNama());
            }
        };
        recyclerView.setAdapter(adapter);

        int padding = 16;
        recyclerView.addItemDecoration(new LinearDecoration(padding));
    }

    private void fabTambahMenuByOrder(String keyFood) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(keyFood);
        alertDialog.show();
    }


    private void memuatListMenu() {

        fabTambahMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahMenu();
            }
        });

        adapter = new FirebaseRecyclerAdapter<ModelMakanan, FoodListMViewHolder>(
                ModelMakanan.class,
                R.layout.item_r_food_list_m,
                FoodListMViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(FoodListMViewHolder foodListMViewHolder, final ModelMakanan modelMakanan, int i) {
                Picasso.get().load(modelMakanan.getGambar()).into(foodListMViewHolder.ImgMakanan);
                foodListMViewHolder.TextMakanan.setText(modelMakanan.getNama());

                foodListMViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Toast.makeText(FoodListManagement.this, ""+modelMakanan.getNama(), Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("Adapter", modelMakanan.getNama());
            }
        };
        recyclerView.setAdapter(adapter);


        int padding = 16;
        recyclerView.addItemDecoration(new LinearDecoration(padding));
    }

    private void tambahMenu() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflaterHeader = getLayoutInflater();
        View headerLayout = inflaterHeader.inflate(R.layout.header_dialog, null);
        TextView headerText = headerLayout.findViewById(R.id.textheader);
        headerText.setText("Tambah Makanan");
        dialog.setCustomTitle(headerLayout);
        LayoutInflater inflater = this.getLayoutInflater();
        View contentLayout = inflater.inflate(R.layout.add_modify_menu_layout, null);
        LayNamaMakanan = contentLayout.findViewById(R.id.layNamaMakanan);
        LayKategori = contentLayout.findViewById(R.id.layKategori);
        LayHarga = contentLayout.findViewById(R.id.layHarga);
        LayDiskon = contentLayout.findViewById(R.id.layDiskon);
        LayDeskripsi = contentLayout.findViewById(R.id.layDeskripsi);
        EdtNamaMakanan = contentLayout.findViewById(R.id.edtNamaMakanan);
        EdtKategori = contentLayout.findViewById(R.id.edtKategori);
        EdtHarga = contentLayout.findViewById(R.id.edtHarga);
        EdtDiskon = contentLayout.findViewById(R.id.edtDiskon);
        EdtDeskripsi = contentLayout.findViewById(R.id.edtDeskripsi);
        BtnSelect = contentLayout.findViewById(R.id.btnSelect);
        BtnUpload = contentLayout.findViewById(R.id.btnUpload);
        BtnTambah = contentLayout.findViewById(R.id.btnTambah);
        BtnBatal = contentLayout.findViewById(R.id.btnBatal);

        EdtDiskon.setText("0");

        BtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihGambar();
            }
        });

        BtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unggahGambar();
            }
        });

        BtnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String NamaMakanan = EdtNamaMakanan.getText().toString();
                String Kategori = EdtKategori.getText().toString();
                String Harga = EdtHarga.getText().toString();
                String Diskon = EdtDiskon.getText().toString();
                String Deskripsi = EdtDeskripsi.getText().toString();



                if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Harga.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.5.1
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Harga.isEmpty() && Diskon.isEmpty()){
                    //C5.4.1
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Harga.isEmpty() && Deskripsi.isEmpty()){
                    //C5.4.2
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.4.3
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Harga.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.4.4
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Kategori.isEmpty() && Harga.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.4.5
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Harga.isEmpty()){
                    //C5.3.1
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Diskon.isEmpty()){
                    //C5.3.2
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.3
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Harga.isEmpty() && Diskon.isEmpty()){
                    //C5.3.4
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Harga.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.5
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.6
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Kategori.isEmpty() && Harga.isEmpty() && Diskon.isEmpty()){
                    //C5.3.7
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (Kategori.isEmpty() && Harga.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.8
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Kategori.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.9
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Harga.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.10
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty()){
                    //C5.2.1
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Harga.isEmpty()){
                    //C5.2.2
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Diskon.isEmpty()){
                    //C5.2.3
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Deskripsi.isEmpty()){
                    //C5.2.4
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Kategori.isEmpty() && Harga.isEmpty()){
                    //C5.2.5
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (Kategori.isEmpty() && Diskon.isEmpty()){
                    //C5.2.6
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (Kategori.isEmpty() && Deskripsi.isEmpty()){
                    //C5.2.7
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Harga.isEmpty() && Diskon.isEmpty()){
                    //C5.2.8
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (Harga.isEmpty() && Deskripsi.isEmpty()){
                    //C5.2.9
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.2.10
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty()){
                    //C5.1.1
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (Kategori.isEmpty()){
                    //C5.1.2
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (Harga.isEmpty()){
                    //C5.1.3
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (Diskon.isEmpty()){
                    //C5.1.4
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (Deskripsi.isEmpty()){
                    //C5.1.5
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else {
                    intDiskon = Integer.parseInt(Diskon);
                    if (intDiskon > 100){
                        Toast.makeText(FoodListManagement.this, "Diskon Tidak Valid!", Toast.LENGTH_SHORT).show();
                    }else {
                        if (modelMakanan != null){
                            dialog.dismiss();
                            databaseReference.push().setValue(modelMakanan);
                            returnResult(modelMakanan, Harga, Diskon);
                        }
                    }
                }
            }
        });

        BtnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setView(contentLayout);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void returnResult(ModelMakanan modelMakanan, String harga, String diskon) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Hasil");
        dialog.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View card_result = inflater.inflate(R.layout.card_result, null);
        TextView TextNamaMakanan = card_result.findViewById(R.id.txtNamaMakanan);
        TextView Textkategori = card_result.findViewById(R.id.txtKategori);
        TextView TextHargaNormal = card_result.findViewById(R.id.txtHargaNormal);
        TextView TextDiskon = card_result.findViewById(R.id.txtDiskon);
        TextView TextHargaDiskon = card_result.findViewById(R.id.txtHargaDiskon);
        TextNamaMakanan.setText(modelMakanan.getNama());
        Textkategori.setText(modelMakanan.getMenuId());
        TextHargaNormal.setText(NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format((double)Integer.parseInt(modelMakanan.getHargaNormal())));
        TextDiskon.setText(modelMakanan.getDiskon()+" %");
        TextHargaDiskon.setText(NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format((double)Integer.parseInt(modelMakanan.getHargaDiskon())));
        dialog.setView(card_result);
        dialog.setPositiveButton("Selesai", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void unggahGambar() {
        if (saveUri != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Mengunggah....");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("gambarMakanan/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(FoodListManagement.this, "Gambar Terungah!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    int Harga = Integer.parseInt(EdtHarga.getText().toString());
                                    Double doubleDiskon = Double.parseDouble(EdtDiskon.getText().toString()) / 100;
                                    Double HargaFinal = Harga - (Harga * doubleDiskon);
                                    Log.d("Harga", String.valueOf(Harga));
                                    Log.d("intDiskon", String.valueOf(doubleDiskon));
                                    Log.d("HargaFinal", String.format("%.0f", HargaFinal));
                                    modelMakanan = new ModelMakanan(
                                            EdtNamaMakanan.getText().toString().toUpperCase(),
                                            EdtKategori.getText().toString(),
                                            EdtHarga.getText().toString(),
                                            String.format("%.0f", HargaFinal),
                                            uri.toString(),
                                            EdtDeskripsi.getText().toString(),
                                            EdtDiskon.getText().toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(FoodListManagement.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double proses = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Mengunggah "+proses+" %");
                        }
                    });
        }
    }

    private void pilihGambar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            saveUri = data.getData();
            BtnSelect.setIcon(getDrawable(R.drawable.ic_twotone_done_24px));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(FoodListManagement.this, Dasbor.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.Update)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else if (item.getTitle().equals(Common.Delete)){
            showDeleteDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(final String key, final ModelMakanan item) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Edit Data Makanan");
        LayoutInflater inflater = getLayoutInflater();
        View modify_menu_layout = inflater.inflate(R.layout.add_modify_menu_layout, null);
        //LayoutEditText
        LayNamaMakanan = modify_menu_layout.findViewById(R.id.layNamaMakanan);
        LayKategori = modify_menu_layout.findViewById(R.id.layKategori);
        LayHarga = modify_menu_layout.findViewById(R.id.layHarga);
        LayDiskon = modify_menu_layout.findViewById(R.id.layDiskon);
        LayDeskripsi = modify_menu_layout.findViewById(R.id.layDeskripsi);
        //EditText
        EdtNamaMakanan = modify_menu_layout.findViewById(R.id.edtNamaMakanan);
        EdtKategori = modify_menu_layout.findViewById(R.id.edtKategori);
        EdtHarga = modify_menu_layout.findViewById(R.id.edtHarga);
        EdtDiskon = modify_menu_layout.findViewById(R.id.edtDiskon);
        EdtDeskripsi = modify_menu_layout.findViewById(R.id.edtDeskripsi);
        //Button
        BtnSelect = modify_menu_layout.findViewById(R.id.btnSelect);
        BtnUpload = modify_menu_layout.findViewById(R.id.btnUpload);
        BtnTambah = modify_menu_layout.findViewById(R.id.btnTambah);
        BtnBatal = modify_menu_layout.findViewById(R.id.btnBatal);

        //data
        EdtNamaMakanan.setText(item.getNama());
        EdtKategori.setText(item.getMenuId());
        EdtHarga.setText(item.getHargaNormal());
        EdtDiskon.setText(item.getDiskon());
        EdtDeskripsi.setText(item.getDeskripsi());

        BtnTambah.setText("Ubah");

        BtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihGambar();
            }
        });

        BtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gantiGambar(item);
            }
        });

        BtnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NamaMakanan = EdtNamaMakanan.getText().toString();
                String Kategori = EdtKategori.getText().toString();
                String Harga = EdtHarga.getText().toString();
                String Diskon = EdtDiskon.getText().toString();
                String Deskripsi = EdtDeskripsi.getText().toString();

                if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Harga.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.5.1
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Harga.isEmpty() && Diskon.isEmpty()){
                    //C5.4.1
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Harga.isEmpty() && Deskripsi.isEmpty()){
                    //C5.4.2
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.4.3
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Harga.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.4.4
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Kategori.isEmpty() && Harga.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.4.5
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Harga.isEmpty()){
                    //C5.3.1
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Diskon.isEmpty()){
                    //C5.3.2
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.3
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Harga.isEmpty() && Diskon.isEmpty()){
                    //C5.3.4
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Harga.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.5
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.6
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Kategori.isEmpty() && Harga.isEmpty() && Diskon.isEmpty()){
                    //C5.3.7
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (Kategori.isEmpty() && Harga.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.8
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Kategori.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.9
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Harga.isEmpty() && Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.3.10
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty() && Kategori.isEmpty()){
                    //C5.2.1
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Harga.isEmpty()){
                    //C5.2.2
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Diskon.isEmpty()){
                    //C5.2.3
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (NamaMakanan.isEmpty() && Deskripsi.isEmpty()){
                    //C5.2.4
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Kategori.isEmpty() && Harga.isEmpty()){
                    //C5.2.5
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (Kategori.isEmpty() && Diskon.isEmpty()){
                    //C5.2.6
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (Kategori.isEmpty() && Deskripsi.isEmpty()){
                    //C5.2.7
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Harga.isEmpty() && Diskon.isEmpty()){
                    //C5.2.8
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (Harga.isEmpty() && Deskripsi.isEmpty()){
                    //C5.2.9
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (Diskon.isEmpty() && Deskripsi.isEmpty()){
                    //C5.2.10
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else if (NamaMakanan.isEmpty()){
                    //C5.1.1
                    LayNamaMakanan.setError("Form Nama Makanan Kosong!");
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (Kategori.isEmpty()){
                    //C5.1.2
                    LayNamaMakanan.setError(null);
                    LayKategori.setError("Form Kategori Kosong!");
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (Harga.isEmpty()){
                    //C5.1.3
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError("Form Harga Kosong!");
                    LayDiskon.setError(null);
                    LayDeskripsi.setError(null);
                } else if (Diskon.isEmpty()){
                    //C5.1.4
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError("Form Diskon Kosong!");
                    LayDeskripsi.setError(null);
                } else if (Deskripsi.isEmpty()){
                    //C5.1.5
                    LayNamaMakanan.setError(null);
                    LayKategori.setError(null);
                    LayHarga.setError(null);
                    LayDiskon.setError(null);
                    LayDeskripsi.setError("Form Deskripsi Kosong!");
                } else {
                    Double DblDiskon = Double.parseDouble(Diskon) / 100;
                    Double HargaDiskon = Double.parseDouble(Harga) - (Double.parseDouble(Harga) * DblDiskon);
                    Log.d("Diskon", DblDiskon.toString());
                    Log.d("HargaDiskon", HargaDiskon.toString());
                    if (DblDiskon > 1){
                        Toast.makeText(FoodListManagement.this, "Diskon Tidak Valid!", Toast.LENGTH_SHORT).show();
                    }else {
                        item.setNama(NamaMakanan.toUpperCase());
                        item.setMenuId(Kategori);
                        item.setHargaNormal(Harga);
                        item.setDiskon(Diskon);
                        item.setHargaDiskon(String.format("%.0f", HargaDiskon));
                        item.setDeskripsi(Deskripsi);
                        databaseReference.child(key).setValue(item);
                        alertDialog.dismiss();
                    }
                }
            }
        });

        BtnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(modify_menu_layout);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void gantiGambar(final ModelMakanan item) {
        if (saveUri != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Mengunggah...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("gambarMakanan/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(FoodListManagement.this, "Terunggah", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setGambar(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(FoodListManagement.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Mengunggah "+progress+" %");
                        }
                    });
        }
    }

    private void showDeleteDialog(final String key, ModelMakanan item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Hapus Data Makanan");
        alertDialog.setMessage("Apakah Anda Yakin Menghapus "+item.getNama());
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child(key).removeValue();
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}

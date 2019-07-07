package com.example.androideatitserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androideatitserver.Common.Common;
import com.example.androideatitserver.Decoration.LinearDecoration;
import com.example.androideatitserver.Interface.ItemClickListener;
import com.example.androideatitserver.Model.ModelKategori;
import com.example.androideatitserver.Navigation.BottomNavigationDrawerFragment;
import com.example.androideatitserver.ViewHolder.DasborViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class Dasbor extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;
    MaterialButton BtnSelect, BtnUpload, BtnTambah, BtnBatal;
    TextInputEditText EdtName;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Kategori");
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    FirebaseRecyclerAdapter<ModelKategori, DasborViewHolder> adapter;

    BottomAppBar bottomAppBar;

    ModelKategori modelKategori;

    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasbor);

        bottomAppBar = (BottomAppBar)findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setTitle("Kategori");

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerHome);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        memuatKategori();
    }

    public void showAddDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflaterHeader = getLayoutInflater();
        View header_alert_dialog = inflaterHeader.inflate(R.layout.header_dialog, null);
        TextView TextHeader = header_alert_dialog.findViewById(R.id.textheader);
        TextHeader.setText("Tambah Kategori");
        dialog.setCustomTitle(header_alert_dialog);
        dialog.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View add_category_layout = inflater.inflate(R.layout.add_modify_category_layout, null);
        EdtName = (TextInputEditText)add_category_layout.findViewById(R.id.edtName);
        BtnSelect = (MaterialButton)add_category_layout.findViewById(R.id.btnSelect);
        BtnUpload = (MaterialButton)add_category_layout.findViewById(R.id.btnUpload);
        BtnTambah = (MaterialButton)add_category_layout.findViewById(R.id.btnTambah);
        BtnBatal = (MaterialButton)add_category_layout.findViewById(R.id.btnBatal);

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
                Toast.makeText(Dasbor.this, "Kategori "+EdtName.getText().toString()+" Berhasil Ditambahkan!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                if (modelKategori != null){
                    databaseReference.push().setValue(modelKategori);
                }
            }
        });

        BtnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setView(add_category_layout);
        dialog.show();
    }

    private void unggahGambar() {
        if (saveUri != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Mengunggah....");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("gambarKategori/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Dasbor.this, "Terunggah!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    modelKategori = new ModelKategori(EdtName.getText().toString().toUpperCase(), uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Dasbor.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            saveUri = data.getData();
            BtnSelect.setIcon(getDrawable(R.drawable.ic_twotone_done_24px));
        }
    }

    private void pilihGambar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Pilih Gambar"),PICK_IMAGE_REQUEST);
    }

    private void memuatKategori() {
        adapter = new FirebaseRecyclerAdapter<ModelKategori, DasborViewHolder>(
                ModelKategori.class,
                R.layout.item_r_dasbor,
                DasborViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(DasborViewHolder dasborViewHolder, final ModelKategori modelKategori, final int i) {
                Picasso.get().load(modelKategori.getGambar()).into(dasborViewHolder.ImageKategori);
                dasborViewHolder.NameKategori.setText(modelKategori.getNama());
                dasborViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(Dasbor.this, FoodListManagement.class);
                        intent.putExtra("keyFood", adapter.getRef(i).getKey());
                        intent.putExtra("headerToolbar", modelKategori.getNama());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        int padding = 16;

        recyclerView.addItemDecoration(new LinearDecoration(padding));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                BottomNavigationDrawerFragment bottomNavigationDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavigationDrawerFragment.show(getSupportFragmentManager(), bottomNavigationDrawerFragment.getTag());
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

    private void showDeleteDialog(final String key, final ModelKategori item) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Hapus Data");
        dialog.setMessage("Apakah anda yakin menghapus data "+item.getNama()+" ?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child(key).removeValue();
                Toast.makeText(Dasbor.this, item.getNama()+" berhasil dihapus!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showUpdateDialog(final String key, final ModelKategori item) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflaterHeader = getLayoutInflater();
        View header_category = inflaterHeader.inflate(R.layout.header_dialog, null);
        TextView headerCategory = header_category.findViewById(R.id.textheader);
        headerCategory.setText("Ubah Kategori");
        dialog.setCustomTitle(header_category);
        dialog.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View modify_category_layout  = inflater.inflate(R.layout.add_modify_category_layout, null);
        EdtName = modify_category_layout.findViewById(R.id.edtName);
        EdtName.setText(item.getNama());
        BtnSelect = modify_category_layout.findViewById(R.id.btnSelect);
        BtnUpload = modify_category_layout.findViewById(R.id.btnUpload);
        BtnTambah = modify_category_layout.findViewById(R.id.btnTambah);
        BtnBatal = modify_category_layout.findViewById(R.id.btnBatal);
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
                item.setNama(EdtName.getText().toString().toUpperCase());
                databaseReference.child(key).setValue(item);
                dialog.dismiss();
            }
        });

        BtnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(modify_category_layout);
        dialog.show();
    }

    private void gantiGambar(final ModelKategori item) {
        if (saveUri != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Mengunggah...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("gambarKategori/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Dasbor.this, "Terunggah!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Dasbor.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
}

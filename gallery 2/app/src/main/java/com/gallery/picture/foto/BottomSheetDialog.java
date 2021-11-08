package com.gallery.picture.foto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.gallery.picture.foto.Adapters.CollectionsNestedRecyclerViewAdapter;
import com.gallery.picture.foto.ui.SelectionImageActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.gallery.picture.foto.Fragments.SecondFragment.folderListArray;


public class BottomSheetDialog extends BottomSheetDialogFragment {


    CollectionsNestedRecyclerViewAdapter collectionsNestedRecyclerViewAdapter;
    TextView newCollection;
    boolean isFileFromSdCard = false;
    String sdCardPath;
    String rootPath, compressPath, extractPath;
    int sdCardPermissionType = 0;
    int folder_counter = 1;
    EditText add;
    Button save;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        v.setBackgroundColor(Color.TRANSPARENT);
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        save = v.findViewById(R.id.save);
        Button cancel_button = v.findViewById(R.id.cancel_button);
        RelativeLayout parentRelative = v.findViewById(R.id.parentRelative);
        add = v.findViewById(R.id.add);
        newCollection = v.findViewById(R.id.newCollection);

        add.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!validateTitle()){
                    return;}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(!validateTitle())
                    return;


                Intent in = new Intent(getActivity(), SelectionImageActivity.class);
                in.putExtra("newCollection", add.getText().toString());
                in.putExtra("type", "CopyMove");
                startActivity(in);




                dismiss();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });





        return v;
    }
    private boolean validateTitle() {
        String val = add.getText().toString();

        if(val.isEmpty()) {

            return false;
        }
        else if(folderListArray.containsKey(val)){
            add.setError("Collection already exists!");
            return false;
        }
        else {
            add.setError(null);
            return true;
        }
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }



    }


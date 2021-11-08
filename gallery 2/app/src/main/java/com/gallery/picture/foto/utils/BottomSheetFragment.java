package com.gallery.picture.foto.utils;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gallery.picture.foto.Interface.BottomListner;
import com.gallery.picture.foto.R;
import com.gallery.picture.foto.utils.PreferencesManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class BottomSheetFragment extends BottomSheetDialogFragment {
    View view;

    BottomListner listner;
    RadioGroup radio_button_group, group_order;
    RadioButton radio_Name_Asc, radio_Name_Des, radio_Size_Asc, radio_Size_Des, radio_Time_Asc, radio_Time_Dec, radio_descending, radio_ascending;

    public BottomSheetFragment() {
    }


    public BottomSheetFragment(BottomListner listner) {
        this.listner = listner;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = dialog.getWindow();
            if (window != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                GradientDrawable dimDrawable = new GradientDrawable();

                GradientDrawable navigationBarDrawable = new GradientDrawable();
                navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
                navigationBarDrawable.setColor(getResources().getColor(R.color.white));

                Drawable[] layers = {dimDrawable, navigationBarDrawable};

                LayerDrawable windowBackground = new LayerDrawable(layers);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    windowBackground.setLayerInsetTop(1, metrics.heightPixels);
                }

                window.setBackgroundDrawable(windowBackground);
            }
        }
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_sort, container, false);
        initView();
        return view;
    }

    private void initView() {
        radio_button_group = view.findViewById(R.id.radio_button_group);
        group_order = view.findViewById(R.id.group_order);
        radio_Name_Asc = view.findViewById(R.id.radio_Name_Asc);
        radio_Name_Des = view.findViewById(R.id.radio_Name_Des);
        radio_Size_Asc = view.findViewById(R.id.radio_Size_Asc);
        radio_Size_Des = view.findViewById(R.id.radio_Size_Des);
        radio_Time_Asc = view.findViewById(R.id.radio_Time_Asc);
        radio_Time_Dec = view.findViewById(R.id.radio_Time_Dec);
        radio_ascending = view.findViewById(R.id.radio_ascending);
        radio_descending = view.findViewById(R.id.radio_descending);
        LinearLayout btn_cancel = view.findViewById(R.id.btn_cancel);
        LinearLayout btn_done = view.findViewById(R.id.btn_done);

        int sortType = PreferencesManager.getSortType(getActivity());

        if (sortType == 1) {
            radio_Name_Asc.setChecked(true);
            radio_ascending.setChecked(true);
        } else if (sortType == 2) {
//            radio_Name_Des.setChecked(true);

            radio_Name_Asc.setChecked(true);
            radio_descending.setChecked(true);

        } else if (sortType == 3) {
            radio_Size_Asc.setChecked(true);
            radio_descending.setChecked(true);
        } else if (sortType == 4) {
//            radio_Size_Des.setChecked(true);
            radio_Size_Asc.setChecked(true);
            radio_ascending.setChecked(true);

        } else if (sortType == 5) {
            radio_Time_Asc.setChecked(true);
            radio_descending.setChecked(true);
        } else if (sortType == 6) {
            radio_Time_Asc.setChecked(true);
            radio_ascending.setChecked(true);

        } else {
            radio_Name_Asc.setChecked(true);
        }


//        radio_button_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                int grop_id = group.getCheckedRadioButtonId();
//
//                if (grop_id == R.id.radio_Name_Asc) {
//                    listner.onBottomClick(1);
//                } else if (grop_id == R.id.radio_Size_Asc) {
//                    listner.onBottomClick(3);
//                } else if (grop_id == R.id.radio_Time_Asc) {
//                    listner.onBottomClick(5);
//                } else if (grop_id == R.id.radio_Name_Des) {
//                    listner.onBottomClick(2);
//                } else if (grop_id == R.id.radio_Size_Des) {
//                    listner.onBottomClick(4);
//                } else if (grop_id == R.id.radio_Time_Dec) {
//                    listner.onBottomClick(6);
//                }
//
//            }
//        });


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int grop_id = group_order.getCheckedRadioButtonId();
                int sort_id = radio_button_group.getCheckedRadioButtonId();

                boolean isAscending = false;
                if (grop_id == R.id.radio_ascending) {
                    isAscending = true;
                }



                if (sort_id == R.id.radio_Name_Asc) {
                    if (isAscending)
                        listner.onBottomClick(1);
                    else
                        listner.onBottomClick(2);

                } else if (sort_id == R.id.radio_Time_Asc) {
                    if (isAscending)
                        listner.onBottomClick(6);
                    else
                        listner.onBottomClick(5);

                } else if (sort_id == R.id.radio_Size_Asc) {
                    if (isAscending)
                        listner.onBottomClick(4);
                    else
                        listner.onBottomClick(3);
                }


//                if (sort_id == R.id.radio_Name_Asc) {
//                    if (isAscending)
//                        listner.onBottomClick(1);
//                    else
//                        listner.onBottomClick(2);
//
//                } else if (sort_id == R.id.radio_Time_Asc) {
//                    if (isAscending)
//                        listner.onBottomClick(5);
//                    else
//                        listner.onBottomClick(6);
//
//                } else if (sort_id == R.id.radio_Size_Asc) {
//                    if (isAscending)
//                        listner.onBottomClick(3);
//                    else
//                        listner.onBottomClick(4);
//                }

                dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }


}

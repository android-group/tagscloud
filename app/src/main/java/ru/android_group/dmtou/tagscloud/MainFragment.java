package ru.android_group.dmtou.tagscloud;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

public class MainFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = "MainFragment";
    private static final String ARG_PARAM_INDEX_MIND = "ARG_PARAM_INDEX_MIND";

    private int indexMind = -1;

    @IdRes
    private int subIndexMind = 0;

    RelativeLayout cloudRelativeLayout;

    private static final String NEW_MIND_TITLE = "your mind";

    // массив из всех мыслей
    private ArrayList tags = new ArrayList();

    private EditText activeMindET;

    private Random random = new Random();

    /*
    * Размер экрана фрагмента
    * @TODO сделать более точные координаты
    * */
    private int cloudWidth;
    private int cloudHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            indexMind = getArguments().getInt(ARG_PARAM_INDEX_MIND);
            /*
            * @TODO
            * load from Data Base by 'indexMind'
            * */
        } else {
            /*
            * @TODO
            * load main minds without parent
            * */
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIsEditActiveView(false);
            }
        });
        return view;
    }

    GestureDetectorCompat gestureDetector;

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gestureDetector = new GestureDetectorCompat(getActivity(), new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                openSubCloudByView(activeMindET);
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setIsEditActiveView(true);
                return false;
            }
        });

        /*
        * Находим главный лайаут в xml, для добавления в него тегов
        * */
        cloudRelativeLayout = (RelativeLayout) view.findViewById(R.id.cloud_relative_layout);

        /*
        * Определяем размер экрана
        * */
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        cloudHeight = (size.y / 3) * 2;
        cloudWidth = (size.x / 3) * 2;
        Log.i(TAG, "Открыли новое облако cloud_relative_layout: width (" + cloudWidth + ") ,height (" + cloudHeight + ")");

        /*
        * Добавляем слушательна кнопку New
        * */
        Button newMindBtn = (Button) view.findViewById(R.id.new_mind_btn);
        newMindBtn.setOnClickListener(this);

        /*
        * @TODO
        * Если этот фрагмент открылся с параметром - старый фрагмент,
        * тогда добавляем 'мысль' по середине экрана
        * */
        if(indexMind != -1) {
            addMind(indexMind, 0, 0);
        }
    }



    private void setIsEditActiveView(boolean focusableInTouchMode) {
        if(activeMindET == null) {
            return;
        }

        activeMindET.setFocusableInTouchMode(focusableInTouchMode);
        activeMindET.setFocusable(focusableInTouchMode);
        if(!focusableInTouchMode) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activeMindET.getWindowToken(), 0);
        }
    }

    /*
    * Добавление 'мысли' в рандомное место на экран (но не больше чем cloudHeight, cloudWidth)
    * */
    private void addMind(int newId, int leftMargin, int topMargin) {
        Log.i(TAG, "Добавляем новую мысль с id: " + newId);
        EditText newMindEditText = new EditText(getContext());
        newMindEditText.setId(newId);
        newMindEditText.setGravity(Gravity.CENTER);
        String title = String.format("%s %d", NEW_MIND_TITLE, newId);
        newMindEditText.setText(title);
        newMindEditText.setFocusableInTouchMode(false);
        newMindEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        newMindEditText.setTextSize(40);
        newMindEditText.setTextColor(Color.BLACK);
        newMindEditText.setTypeface(null, Typeface.BOLD_ITALIC);

        // Добавить 'мысль' на страницу в рандомное место
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(leftMargin, topMargin, 0, 0);
        newMindEditText.setLayoutParams(params);

        cloudRelativeLayout.addView(newMindEditText);

        newMindEditText.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                activeMindET = (EditText) v;
                return gestureDetector.onTouchEvent(event);
            }
        });

        // @TODO сохранение в БД
        tags.add(newMindEditText);
        Log.i(TAG, "Добавили новую мысль на позицию: leftMargin" + params.leftMargin + ",topMargin" + params.topMargin);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_mind_btn) {
            Log.i(TAG, "Клик по кнопке New");
            int leftMargin = random.nextInt(cloudWidth);
            int topMargin = random.nextInt(cloudHeight);
            addMind(subIndexMind, leftMargin, topMargin);
            subIndexMind++;
        }
    }

    private void openSubCloudByView(View v) {
        Log.i(TAG, "Клик по мысли с id: " + v.getId());

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(ARG_PARAM_INDEX_MIND, v.getId());
        startActivity(intent);
    }
}

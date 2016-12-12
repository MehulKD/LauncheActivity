package xyz.hanks.launchactivity;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 带有 EditText 的 checkbox
 * Created by hanks on 2016/12/9.
 */

public class CheckBoxLayout extends LinearLayout {

    private LayoutInflater inflater;
    private boolean singleChioce;
    private boolean singleItem;

    public CheckBoxLayout(Context context) {
        this(context, null);
    }

    public CheckBoxLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBoxLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxLayout, defStyleAttr, 0);
        singleChioce = a.getBoolean(R.styleable.CheckBoxLayout_singleChoice, false);
        singleItem = a.getBoolean(R.styleable.CheckBoxLayout_singleItem, false);


        setOrientation(VERTICAL);
        inflater = LayoutInflater.from(context);
        init();
    }

    public void addCheckEditor(String text) {
        addCheckEditor(text, "");
    }

    public void addCheckEditor(String text, String hint) {
        if (text == null || (singleItem && getChildCount() != 0)) {
            return;
        }
        final View child = newItemView();
        final CheckBox checkBox = (CheckBox) child.findViewById(R.id.checkBox);
        final EditText editText = (EditText) child.findViewById(R.id.editText);
        editText.setText(text);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (singleChioce) {
                        for (int i = 0; i < getChildCount(); i++) {
                            CheckBox box = (CheckBox)  getChildAt(i).findViewById(R.id.checkBox);
                            if (box == checkBox) {
                                continue;
                            }
                            box.setChecked(false);
                        }
                    }
                }
            }
        });
        checkBox.setChecked(true);
        if (!TextUtils.isEmpty(hint)) {
            editText.setHint(hint);
        }
        addView(child, 0);
    }

    private void init() {

    }

    public List<String> getSelectedString(){
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (((CheckBox)view.findViewById(R.id.checkBox)).isChecked()) {
                stringList.add(((EditText)view.findViewById(R.id.editText)).getText().toString());
            }
        }
        return stringList;
    }

    private View newItemView() {
        return inflater.inflate(R.layout.layout_edit_checkbox, this, false);
    }
}

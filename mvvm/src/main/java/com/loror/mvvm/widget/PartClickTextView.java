package com.loror.mvvm.widget;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PartClickTextView extends androidx.appcompat.widget.AppCompatTextView {

    public interface OnPartClick {
        void onClick(View v, int position);
    }

    private OnPartClick onPartClick;

    public PartClickTextView(@NonNull Context context) {
        super(context);
    }

    public PartClickTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartClickTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnPartClick(OnPartClick onPartClick) {
        this.onPartClick = onPartClick;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text instanceof String) {
            String s = text.toString();
            List<Index> indexes = new ArrayList<>();
            int i = 0;
            do {
                int start = s.indexOf("((", i);
                if (start < 0) {
                    break;
                }
                int end = s.indexOf("))", start);
                if (end < 0) {
                    break;
                }
                s = s.substring(0, start) + s.substring(start + 2, end) + s.substring(end + 2);
                i = end - 4;
                Index index = new Index(start, end - 2);
                indexes.add(index);
            } while (true);
            int size = indexes.size();
            if (size > 0) {
                SpannableString builder = new SpannableString(s);
                for (int j = 0; j < size; j++) {
                    Index index = indexes.get(j);
                    int finalJ = j;
                    builder.setSpan(new ClickableSpan() {
                                        @Override
                                        public void onClick(@NonNull View widget) {
                                            if (onPartClick != null) {
                                                onPartClick.onClick(PartClickTextView.this, finalJ);
                                            }
                                        }

                                        @Override
                                        public void updateDrawState(@NonNull TextPaint ds) {
                                            ds.setColor(ds.linkColor);
                                        }
                                    }
                            , index.start, index.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                super.setText(builder, type);
                setMovementMethod(LinkMovementMethod.getInstance());
                return;
            }
        }
        super.setText(text, type);
    }

    private static class Index {

        private final int start;
        private final int end;

        public Index(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}

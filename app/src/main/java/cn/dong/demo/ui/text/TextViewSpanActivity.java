package cn.dong.demo.ui.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.LineHeightSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;

import butterknife.InjectView;
import cn.dong.demo.R;
import cn.dong.demo.ui.common.BaseActivity;
import cn.dong.demo.util.T;
import cn.dong.demo.util.TextViewClickMovement;
import timber.log.Timber;

/**
 * @author dong on 15/4/8.
 */
public class TextViewSpanActivity extends BaseActivity {
    @InjectView(R.id.text)
    TextView mTextView;
    @InjectView(R.id.text2)
    TextView mText2View;
    @InjectView(R.id.text3)
    TextView mText3View;

    @Override
    protected int initPageLayoutID() {
        return R.layout.activity_text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testSpan();
        testClickableSpan();
        testMovement();
    }

    private void testSpan() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("www.baidu.\n");
        ssb.setSpan(new URLSpan("http://www.baidu.com"), 0, 10, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ssb.append("测试测试测试测试测\n");
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 11, 20, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ssb.append("测试 测试 测试测\n");
        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), 21, 30, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        // tag 1
        ssb.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.primary)), 21, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new TagSpan(), 21, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // tag 2
        ssb.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.primary)), 24, 26, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        // tag 3
        ssb.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.primary)), 27, 30, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        ssb.append("测试测试测试测试测\n");
        ssb.setSpan(new ForegroundColorSpan(Color.GRAY), 31, 40, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ssb.append("测试测试测试测试测\n");
        ssb.setSpan(new UnderlineSpan(), 41, 50, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ssb.append("测试\n");
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ssb.setSpan(new ImageSpan(drawable), 51, 52, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        String line1 = "line 1 20\n";
        ssb.append(line1);
        ssb.setSpan(new HeightSpan(20), ssb.length() - line1.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String line2 = "line 2 30\n";
        ssb.append(line2);
        ssb.setSpan(new HeightSpan(30), ssb.length() - line2.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String line3 = "line 3 40\n";
        ssb.append(line3);
        ssb.setSpan(new HeightSpan(40), ssb.length() - line3.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String line4 = "line 4 50\nline 5 50\n";
        ssb.append(line4);
        ssb.setSpan(new HeightSpan(50), ssb.length() - line4.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTextView.setText(ssb);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void testClickableSpan() {
        String target = "aaa www.baidu.com bbb www.google.com cccddd";
        mText2View.setText(getURLSpan(target));
        mText2View.setMovementMethod(LinkMovementMethod.getInstance());
        mText2View.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                T.shortT(mContext, "long click");
                Selection.removeSelection(((Spannable) mText2View.getText()));
                return true;
            }
        });
    }

    private SpannableStringBuilder getURLSpan(String content) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        Matcher matcher = Patterns.WEB_URL.matcher(content);
        while (matcher.find()) {
            Timber.d("start=%d end=%d group=%s", matcher.start(), matcher.end(), matcher.group());
            final String url = matcher.group();
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    T.shortT(mContext, url);
                }
            }, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ssb;
    }

    private void testMovement() {
        String target = "aaa www.baidu.com bbb www.google.com cccddd 123456 eee abc@gmail.com ffffff";
        mText3View.setText(target);
        mText3View.setMovementMethod(new TextViewClickMovement(new TextViewClickMovement.OnTextViewClickMovementListener() {
            @Override
            public void onLinkClicked(String linkText, TextViewClickMovement.LinkType linkType) {
                Timber.d("onLinkClicked linkText=%s linkType=%s", linkText, linkType);
            }

            @Override
            public void onLongClick(String text) {
                Timber.d("onLongClick text=%s", text);
            }
        }, mContext));
    }

    private static class HeightSpan extends CharacterStyle implements LineHeightSpan {
        private int height;

        public HeightSpan(int height) {
            this.height = height;
        }

        @Override
        public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
            fm.bottom += height;
            fm.descent += height;
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            tp.bgColor = Color.GRAY;
        }
    }

    private static class TagSpan extends ClickableSpan {

        @Override
        public void onClick(View widget) {
            Toast.makeText(widget.getContext(), "tag click", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void updateDrawState(TextPaint tp) {

        }
    }

    private static class Tag2Span extends ImageSpan {
        public Tag2Span(Drawable d) {
            super(d);
        }

        public Tag2Span(Context context, Bitmap b) {
            super(context, b);
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            p.setColor(Color.RED);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.RED);
        }
    }

}

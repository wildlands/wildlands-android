package nl.wildlands.wildlandseducation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import nl.wildlands.wildlandseducation.R;

/**
 * Created by chris on 17/03/15.
 * For Calligraphy.
 */
public class TextField extends TextView {

    public TextField(final Context context, final AttributeSet attrs) {
        super(context, attrs, R.attr.textFieldStyle);
    }

}

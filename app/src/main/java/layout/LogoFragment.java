package layout;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.lang.reflect.Array;

import ru.discode.passwords.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogoFragment extends Fragment {

    public TextView asterisk;
    private static String[] chars = {
            "*","(",")","&","@","$","^","!",
    };
    private int i;
    public LogoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_logo, container, false);
        asterisk= (TextView) layout.findViewById(R.id.logo_asterisk);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        runAnimation();

    }

    private void runAnimation() {
        if(this.asterisk != null) {
            final Handler h = new Handler();
            final int delay = 200;
            i = chars.length - 1;
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(i >= 0) {
                        changeLogoChar();
                        h.postDelayed(this, delay);
                    }
                }
            }, delay);
        }
    }

    private void changeLogoChar() {
        if(this.asterisk != null) {
            if(i >= 0) {
                asterisk.setText(chars[i]);
                i--;
            }
        }
    }
}

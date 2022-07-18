package cn.xjiangwei.RobotHelper.Service;

import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class FastInputIME extends InputMethodService {

    private static InputConnection ic;

    @Override
    public void onWindowShown() {
        super.onWindowShown();
        FastInputIME.ic = getCurrentInputConnection();

    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        FastInputIME.ic = null;
    }

    public static boolean TextInput(String text){
        if (ic == null) return false;

        ic.commitText(text,1);
        return true;
    }
}

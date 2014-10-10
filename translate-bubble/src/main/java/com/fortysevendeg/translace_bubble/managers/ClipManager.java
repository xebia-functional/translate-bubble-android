package com.fortysevendeg.translace_bubble.managers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipManager {

    private ClipboardManager clipboard;

    // getText is called multiples times. We try to fix this problem
    private CharSequence previousText;

    public ClipManager(Context context) {
        this.clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public CharSequence getText() {
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            CharSequence aux = clip.getItemAt(0).getText();
            if (aux != null && aux.length() > 0 && !aux.equals(previousText)) {
                previousText = aux;
                return aux;
            }
        }
        return null;
    }

    public ClipboardManager getClipboard() {
        return clipboard;
    }

    public void reset() {
        previousText = null;
    }
}

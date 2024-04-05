package com.example.incomeexpensemanager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.incomeexpensemanager.R


@SuppressLint("StaticFieldLeak")
object SweetToast {
    // initializations
    private var view: View? = null
    private var myView: View? = null
    private var layoutInflater: LayoutInflater? = null
    private var toast: Toast? = null

    /**
     * For Default Toast
     */
    // for default short toast
    fun defaultShort(context: Context?, string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    // for default long toast
    fun defaultLong(context: Context?, string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }

    /** For Long Toast  */
    fun longToast(context: Context, string: String) {
        myView = inflateMyLayout(context)
        setBackgroundLayout(R.drawable.round_shape_info)
        setToastText(string, Color.WHITE)
        setToastIcon(R.drawable.ic_info_outline)
        toast = Toast(context)
        toast!!.view = myView
        toast!!.setDuration(Toast.LENGTH_LONG)
        toast!!.show()
    }

    /**
     * For SUCCESS Toast
     */
    fun success(context: Context, string: String) {
        myView = inflateMyLayout(context)
        setBackgroundLayout(R.drawable.round_shape_success)
        setToastText(string, Color.WHITE)
        setToastIcon(R.drawable.ic_done)
        toast = Toast(context)
        toast!!.view = myView
        toast!!.setGravity(Gravity.BOTTOM, 0, 200)
        toast!!.setDuration(Toast.LENGTH_SHORT)
        toast!!.show()
    }

    /**
     * For INFO Toast
     */
    fun info(context: Context, string: String) {
        myView = inflateMyLayout(context)
        setBackgroundLayout(R.drawable.round_shape_info)
        setToastText(string, Color.WHITE)
        setToastIcon(R.drawable.ic_info_outline)
        toast = Toast(context)
        toast!!.view = myView
        toast!!.setGravity(Gravity.BOTTOM, 0, 200)
        toast!!.setDuration(Toast.LENGTH_SHORT)
        toast!!.show()
    }
    /** For WARNING Toast  */ //    public static void warning(Context context, String string){
    //        myView = inflateMyLayout(context);
    //        setBackgroundLayout(R.drawable.round_shape_warning);
    //        setToastText(string, Color.WHITE);
    //        setToastIcon(R.drawable.ic_info);
    //        toast = new Toast(context);
    //        toast.setView(myView);
    //        toast.setDuration(Toast.LENGTH_SHORT);
    //        toast.show();
    //    }
    /**
     * For ERROR Toast
     */
    fun error(context: Context, string: String) {
        myView = inflateMyLayout(context)
        setBackgroundLayout(R.drawable.round_shape_error)
        setToastText(string, Color.WHITE)
        setToastIcon(R.drawable.ic_close)
        toast = Toast(context)
        toast!!.view = myView
        toast!!.setGravity(Gravity.BOTTOM, 0, 200)
        toast!!.setDuration(Toast.LENGTH_SHORT)
        toast!!.show()
    }

    // inflate for separate layout
    private fun inflateMyLayout(context: Context): View? {
        layoutInflater = LayoutInflater.from(context)
        view = layoutInflater?.inflate(R.layout.toast_layout, null)
        return view
    }

    private fun setBackgroundLayout(resId: Int) {
        val layout = view!!.findViewById<LinearLayout>(R.id.toastLay)
        layout.setBackgroundResource(resId)
    }

    private fun setToastText(string: String, textColor: Int) {
        val toastTitle = view!!.findViewById<TextView>(R.id.toastTitle)
        toastTitle.text = string
        toastTitle.setTextColor(textColor)
    }

    private fun setToastIcon(resId: Int): ImageView {
        val toastIcon = view!!.findViewById<ImageView>(R.id.toastIcon)
        toastIcon.setImageResource(resId)
        return toastIcon
    }
}
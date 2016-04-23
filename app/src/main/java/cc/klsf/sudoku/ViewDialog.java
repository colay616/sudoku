package cc.klsf.sudoku;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by kuaileshifu on 2016/1/8.
 * Email:815856515@qq.com
 */
public class ViewDialog extends Dialog {
    private Context context;
    private View view;
    public ViewDialog(Context context,View view) {
        super(context);
        this.context = context;
        this.view = view;
    }
    public ViewDialog(Context context) {
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //关闭dialog的标题栏
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
    }

    public void setView(View view){
        this.view = view;
    }

    public void setOnClicklistener(String idName, View.OnClickListener onClickListener){
        int resId =  context.getResources().getIdentifier(idName, "id",context.getPackageName());
        View view = findViewById(resId);
        view.setOnClickListener(onClickListener);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveText;
        private String negativeText;
        private OnClickListener positiveClickListener;
        private OnClickListener negativeClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }
        public Builder setMessage(String message){
            this.message = message;
            return this;
        }
        public Builder setPositiveButton(String text, OnClickListener listener){
            this.positiveText = text;
            this.positiveClickListener = listener;
            return this;
        }
        public Builder setNegativeButton(String text, OnClickListener listener){
            this.negativeText = text;
            this.negativeClickListener = listener;
            return this;
        }

        public ViewDialog show(){
            ViewDialog viewDialog = create();
            viewDialog.show();
            return viewDialog;
        }

        public ViewDialog create(){
            final ViewDialog viewDialog = new ViewDialog(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_alert,null,false);
            TextView titleTv = (TextView)view.findViewById(R.id.alert_title);
            TextView messageTv = (TextView)view.findViewById(R.id.alert_text);
            Button positiveBtn = (Button)view.findViewById(R.id.alert_positive);
            Button negativeBtn = (Button)view.findViewById(R.id.alert_negative);
            if(title != null){
                titleTv.setText(title);
            }
            if(message != null){
                messageTv.setText(message);
            }
            if(positiveClickListener != null){
                positiveBtn.setText(positiveText);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveClickListener.onClick(viewDialog, DialogInterface.BUTTON_POSITIVE);
                    }
                });
            }
            if(negativeClickListener != null){
                negativeBtn.setText(negativeText);
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negativeClickListener.onClick(viewDialog,DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }
            viewDialog.setView(view);
            return viewDialog;

        }

    }


}

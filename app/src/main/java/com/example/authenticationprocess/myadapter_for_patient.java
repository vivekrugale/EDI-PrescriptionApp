package com.example.authenticationprocess;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.security.KeyStore;
import java.util.Calendar;

public class myadapter_for_patient extends FirebaseRecyclerAdapter<model_patient_recview,myadapter_for_patient.myviewholder> {

    public myadapter_for_patient(@NonNull FirebaseRecyclerOptions<model_patient_recview> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder myviewholder, int i, @NonNull model_patient_recview model_patient_recview) {
        myviewholder.tabname.setText(model_patient_recview.getTabname());
        myviewholder.time1.setText(model_patient_recview.getTime1());
        myviewholder.time3.setText(model_patient_recview.getTime3());
        myviewholder.time2.setText(model_patient_recview.getTime2());

        //Notification works........
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR,12);
        calendar.set(Calendar.MINUTE,18);
        //calendar.set(Calendar.SECOND,30);

        Context context = myviewholder.itemView.getContext();
        Intent intent = new Intent(context, Notification_reciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow_for_patient,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView tabname,time1,time2,time3;
        public myviewholder(@NonNull View itemView) {
            super(itemView);


            tabname = (TextView)itemView.findViewById(R.id.tabletname);
            time1 = (TextView)itemView.findViewById(R.id.time1);
            time2 = (TextView)itemView.findViewById(R.id.time2);
            time3 = (TextView)itemView.findViewById(R.id.time3);
            img = (ImageView)itemView.findViewById(R.id.img1);
        }
    }
}

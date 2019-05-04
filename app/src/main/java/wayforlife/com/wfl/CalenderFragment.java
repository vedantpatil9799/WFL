package wayforlife.com.wfl;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wayforlife.com.wfl.Modal_class.single_event;


public class CalenderFragment extends Fragment {

    private FloatingActionButton fab_addEvent;
    private DatabaseReference mReference,reference;
    private CompactCalendarView event_calendar;
    private ListView listView;
    private View addEvent;
    private AlertDialog dialog;
    private final Calendar mCalendar= Calendar.getInstance();
    private String myFormat = "dd/MM/yy";
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    private SimpleDateFormat head=new SimpleDateFormat("MMM-yyy", Locale.getDefault());
    private TextView heading;
    private ArrayList<single_event> list;
    private EditText event_title,event_date,event_desc,event_address;
    private boolean isListGot=false;
    private Date todayDate;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private boolean isAdmin=false;

    public CalenderFragment() {
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance().getReference();

        database.child("User-details").child(auth.getUid()).child("panel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type=dataSnapshot.getValue(String.class);
                if(type.equals("admin"))
                {
                    isAdmin=true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_calender, container, false);
        heading=view.findViewById(R.id.frag_calendar_head);
        fab_addEvent=view.findViewById(R.id.frag_calendar_addEvent_fab);

        if(!isAdmin)
        {
            fab_addEvent.setVisibility(View.GONE);
        }
        mReference= FirebaseDatabase.getInstance().getReference();
        event_calendar=view.findViewById(R.id.frag_calendar_calendarView);
        event_calendar.setUseThreeLetterAbbreviation(true);
        listView=view.findViewById(R.id.frag_calendar_event_listview);
        heading.setText(head.format(event_calendar.getFirstDayOfCurrentMonth()).toString());


        getData(head.format(event_calendar.getFirstDayOfCurrentMonth()).toString());

        heading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getData(heading.getText().toString());
            }
        });
        InitializeDialog();


        event_calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                event_calendar.setCurrentSelectedDayBackgroundColor(R.color.colorAccent);
                List<Event> events=event_calendar.getEvents(dateClicked);
                for(Event e : events)
                {
                    Toast.makeText(getContext(), ""+e.getData().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                heading.setText(head.format(firstDayOfNewMonth));

            }
        });

        final DatePickerDialog.OnDateSetListener date=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR,year);
            mCalendar.set(Calendar.MONTH,month);
            mCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                event_date.setText(sdf.format(mCalendar.getTime()));

            }
        };



        fab_addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                event_date.setText(sdf.format(mCalendar.getTime()));


            }
        });

        event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(),date,mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });



        return view;
    }


    private void InitializeDialog()
    {
       LayoutInflater inflater=getActivity().getLayoutInflater();
        addEvent=inflater.inflate(R.layout.add_event_calendar,null);
        AlertDialog.Builder newEvent= new AlertDialog.Builder(getActivity());
        newEvent.setTitle("Add New Event");
        newEvent.setView(addEvent);
        newEvent.setCancelable(true);

        newEvent.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getContext(), "Added event", Toast.LENGTH_SHORT).show();
                try{
                AddEventToCalendar();
            }
            catch (ParseException e)
            {}

            }
        });

        newEvent.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        event_title=addEvent.findViewById(R.id.etx_event_title);
        event_date=addEvent.findViewById(R.id.etx_event_date);
        event_desc=addEvent.findViewById(R.id.etx_event_desc);
        event_address=addEvent.findViewById(R.id.etx_event_address);
        dialog=newEvent.create();
    }


    private void getData(String month) {

        list=new ArrayList<>();
        list.clear();
        mReference.child("Event").child(month).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> it=dataSnapshot.getChildren();
                for(DataSnapshot d : it)
                {
                    single_event e=d.getValue(single_event.class);
                    list.add(e);
                }
                ListViewAdapter adapter=new ListViewAdapter(list);
                listView.setAdapter(adapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private void AddEventToCalendar() throws ParseException {

        if(TextUtils.isEmpty(event_date.getText().toString()) || TextUtils.isEmpty(event_title.getText().toString()) || TextUtils.isEmpty(event_desc.getText().toString()) || TextUtils.isEmpty(event_address.getText().toString())) {

        }
        else
            {
                reference = mReference.child("Event");
                todayDate = sdf.parse(sdf.format(mCalendar.getTime()));
                Date SelectedDate = sdf.parse(event_date.getText().toString());
                long difference = Math.abs(todayDate.getTime() - SelectedDate.getTime());
                long diffdays = difference / (24 * 60 * 60 * 1000);
                Toast.makeText(getActivity(), "" + diffdays, Toast.LENGTH_SHORT).show();
                single_event event = new single_event(event_date.getText().toString(), event_title.getText().toString(), event_desc.getText().toString(), event_address.getText().toString());
                Toast.makeText(getContext(), event_date.getText().toString(), Toast.LENGTH_SHORT).show();
                reference.child(head.format(sdf.parse(event_date.getText().toString()))).push().setValue(event);
                Toast.makeText(getContext(), head.parse(event_date.getText().toString()).toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Event Added Successful", Toast.LENGTH_SHORT).show();
                event_desc.setText("");
                event_address.setText("");
                event_title.setText("");
            }
    }


    private class ListViewAdapter extends BaseAdapter
    {
        private ArrayList<single_event> list;
        private SimpleDateFormat sdf1,sdf2,sdf;
        public ListViewAdapter(ArrayList<single_event> list) {

            this.list=list;
            sdf=new SimpleDateFormat("dd/MM/yy");
            sdf1=new SimpleDateFormat("dd");
            sdf2=new SimpleDateFormat("MMMMMM-yyyy");
        }

        @Override
        public int getCount()
        {
            return list.size();


        }

        @Override
        public Object getItem(int position)
        {
            return list.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return  position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            LayoutInflater inflater=getLayoutInflater();
            View singleItem=inflater.inflate(R.layout.single_item_cal_event,parent,false);
            TextView title=singleItem.findViewById(R.id.cal_event_title);
            TextView date=singleItem.findViewById(R.id.cal_event_date);
            TextView desc=singleItem.findViewById(R.id.cal_event_description);
            TextView address=singleItem.findViewById(R.id.cal_event_address);
            TextView month=singleItem.findViewById(R.id.cal_event_month);

            single_event event=list.get(position);
            Date date1= null;
            try {
                date1 = sdf.parse(event.getEventDate());
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            title.setText("Name:"+event.getEventTitle());
          date.setText(sdf1.format(date1));
            desc.setText("About:"+event.getEventDesc());
          month.setText(sdf2.format(date1));
            address.setText("Address:"+event.getEventAddress());
            return singleItem;


        }
    }

}

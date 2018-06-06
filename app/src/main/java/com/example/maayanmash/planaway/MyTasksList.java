package com.example.maayanmash.planaway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.maayanmash.planaway.Model.ModelFirebase;
import com.example.maayanmash.planaway.Model.entities.Destination;
import com.example.maayanmash.planaway.Model.entities.SubTask;
import com.example.maayanmash.planaway.Model.entities.TaskRow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyTasksList extends AppCompatActivity {
    private ListAdapter adapter = new ListAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks_list);

        ListView list = findViewById(R.id.tasks_list);

        ModelFirebase.getInstance().getMyDestinationsByID(ModelFirebase.getInstance().getuID(), new MapsActivity.GetDestinationsForUserIDCallback() {
            @Override
            public void onDestination(ArrayList<Destination> destinations, String taskID, List<TaskRow> taskRowList) {
                //Log.d("TEMP", "<><><><><><><><><><" + taskRowList.toString());
                for (TaskRow tr: taskRowList) {
                    adapter.data.add(tr);
                }
                adapter.notifyDataSetChanged();
            }
        });

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG","item in row " + i + "was selected");
            }
        });
    }
    public interface OnTodayMySubTasksCallback{
        void  getSubTasks(List<SubTask> list);
    }

    class ListAdapter extends BaseAdapter {
        public List<TaskRow> data = new ArrayList<>();


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = LayoutInflater.from(MyTasksList.this).inflate(R.layout.subtask_row,null);
                CheckBox cb = view.findViewById(R.id.subtask_row_checkbox_id);
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = (int) view.getTag();
                        TaskRow tr = data.get(index);
                        Boolean isDone=!tr.isDone();
                        tr.setDone(isDone);
                        ModelFirebase.getInstance().updateDestinationArrivalForTask(tr.getDid(),isDone);
                    }
                });
            }
            TaskRow tr = data.get(i);
            Log.d("TEMP", "<><><><><><><><" + data.get(i).toString());
            CheckBox cb = view.findViewById(R.id.subtask_row_checkbox_id);
            cb.setTag(i);
            cb.setChecked(tr.isDone());
            TextView name = view.findViewById(R.id.subtask_row_name_id);
            TextView address = view.findViewById(R.id.subtask_row_address_id);
            name.setText(tr.getName());
            address.setText(tr.getAddress());
            return view;
        }
    }
}

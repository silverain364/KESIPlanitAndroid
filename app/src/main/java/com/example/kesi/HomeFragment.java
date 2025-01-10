package com.example.kesi;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.*;
import com.example.kesi.model.CalendarBoxDto;
import com.example.kesi.model.CalendarDayDto;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

public class HomeFragment extends Fragment {


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_item_month_tmp, container, false);
        TextView monthTv = root.findViewById(R.id.month);
        TextView yearTv = root.findViewById(R.id.year);
        RecyclerView calenderRv = root.findViewById(R.id.month_recycler);


        CalendarAdapter calendarAdapter = new CalendarAdapter(new LinkedList<>(), getParentFragmentManager());
        for (int i = -1; i <= 1; i++)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                calendarAdapter.addItem(LocalDate.now().plusMonths(i));
            }


        calenderRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        calenderRv.setAdapter(calendarAdapter);

        SnapHelper snapHelper = new PagerSnapHelper(); //한 번에 한 페이지가 스크롤 되도록하는 것
        if(calenderRv.getOnFlingListener() == null) //혹시나 이미 연결된 스냅 동작이 있는지 확인
            snapHelper.attachToRecyclerView(calenderRv);

        calenderRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) { //스크롤 상태일 때 실행되는 리스너
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) { //스크롤이 멎췄다면
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if(layoutManager == null) return;

                    int position = layoutManager.findFirstCompletelyVisibleItemPosition();


                    LocalDate date = calendarAdapter.getItem(position);

                    monthTv.setText(date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                    yearTv.setText(String.valueOf(date.getYear()));

                    //페이지를 미리 추가
                    if(position == 0) { //앞에 온 경우 페이지 추가
                        calendarAdapter.addFirstItem(date.minusMonths(1));
                        calendarAdapter.notifyItemInserted(0);
                    }
                    if(position == calendarAdapter.getItemCount() - 1) { //뒤에 온 경우 페이지 추가
                        calendarAdapter.addItem(date.plusMonths(1));
                        calendarAdapter.notifyItemInserted(calendarAdapter.getItemCount() - 1);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager =
                        LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
            }
        });


        calenderRv.scrollToPosition(calendarAdapter.getItemCount() / 2);

        return root;
    }
}
class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.Viewholder>{
    private LinkedList<LocalDate> dates;
    private final FragmentManager fragmentManager;
    public CalendarAdapter(LinkedList<LocalDate> dates, FragmentManager fragmentManager){
        this.dates = dates;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_calendar_month, viewGroup, false);
        return new Viewholder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull @NotNull CalendarAdapter.Viewholder viewholder, int i) {
        viewholder.bind(dates.get(i));

    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public LocalDate getItem(int position) {return dates.get(position);}

    public void addItem(LocalDate localDate){
        dates.add(localDate);
    }

    public void addFirstItem(LocalDate localDate){
        dates.addFirst(localDate);
    }

    class Viewholder extends RecyclerView.ViewHolder{
        private RecyclerView calendarContentRv;

        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            calendarContentRv = itemView.findViewById(R.id.calendarContentView);
            calendarContentRv.setLayoutManager(new GridLayoutManager(itemView.getContext(), 7));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(LocalDate date){ //Todo. 나중에 최적화가 필요할 것 같음
            //날짜 구하기
            CalendarContentAdapter calendarAdapter = new CalendarContentAdapter(new ArrayList<>(), fragmentManager);
            LocalDate firstDayDate = LocalDate.of(date.getYear(), date.getMonth(), 1); //1일이 무슨요일은지 알기위해서 해당 월에 1일로 설정된 객체를 생성
            LocalDate lastMonthDate = date.minusMonths(1); //저번달 date가져오기

            //1 ~ 7 : 월 ~ 일 -> 0 ~ 6 : 일 ~ 토
            int firstDay = firstDayDate.getDayOfWeek().getValue() % 7;
            int lastMonthLastDay = lastMonthDate.lengthOfMonth(); //저번달 총 일을 가져오기

            for(int i = 1; i <= firstDay; i++){
                calendarAdapter.addItem(new CalendarBoxDto(lastMonthLastDay - firstDay + i, CalendarBoxDto.LAST_MONTH));
            }

            for(int i = 1; i <= date.lengthOfMonth(); i++)
                calendarAdapter.addItem(new CalendarBoxDto(i, CalendarBoxDto.NOW_MONTH));

            for(int i = 1; i <= 35 - (date.lengthOfMonth() + firstDay); i++)
                calendarAdapter.addItem(new CalendarBoxDto(i, CalendarBoxDto.NEXT_MONTH));

            calendarContentRv.setAdapter(calendarAdapter);
        }
    }
}



class CalendarContentAdapter extends RecyclerView.Adapter<CalendarContentAdapter.Viewholder>{
    private ArrayList<CalendarBoxDto> calendarBoxDtos;
    private final FragmentManager fragmentManager;

    public CalendarContentAdapter(ArrayList<CalendarBoxDto> calendarBoxDtos, FragmentManager fragmentManager){
        this.calendarBoxDtos = calendarBoxDtos;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_calendar_content, viewGroup, false);
        return new CalendarContentAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Viewholder viewholder, int i) {
        viewholder.bind(calendarBoxDtos.get(i));
    }

    @Override
    public int getItemCount() {
        return calendarBoxDtos.size();
    }

    public void addItem(CalendarBoxDto calendarBoxDto){
        calendarBoxDtos.add(calendarBoxDto);
    }

    class Viewholder extends RecyclerView.ViewHolder{
        private TextView dayTv;

        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            dayTv = itemView.findViewById(R.id.dayTv);
        }

        public void bind(CalendarBoxDto calendarBoxDto){
            dayTv.setText(String.valueOf(calendarBoxDto.getDay()));
            if(calendarBoxDto.getMonthState() != CalendarBoxDto.NOW_MONTH){
                dayTv.setTextColor(itemView.getContext().getResources().getColor(R.color.gray));

            }

            //여기서 시작 -> 날짜를 클릭했을 때 이벤트
            itemView.setOnClickListener(view -> {
                Toast.makeText(itemView.getContext(), dayTv.getText().toString() + "을 클릭!", Toast.LENGTH_SHORT).show();
                //DialogFragment 추가하기
            });
        }
    }
}

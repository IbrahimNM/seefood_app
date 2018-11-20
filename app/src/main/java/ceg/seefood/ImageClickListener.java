package ceg.seefood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

public class ImageClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener _Listener;

    public interface OnItemClickListener{
        public void onItemClick(View view, int position);
    }

    GestureDetector gestureDetector;

    public ImageClickListener(Context context, OnItemClickListener listener){
        _Listener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
           @Override
           public boolean onSingleTapUp(MotionEvent e){
               return true;
           }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e){
        View view1 = view.findChildViewUnder(e.getX(),e.getY());

        if(view1 != null && _Listener != null && gestureDetector.onTouchEvent(e)){
            _Listener.onItemClick(view1, view.getChildAdapterPosition(view1));
            return true;
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent){
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept){

    }
}

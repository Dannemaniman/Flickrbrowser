package com.example.flickrbrowser

import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
//This is one of the most complicated things you'll do in android framework but its very flexible and it lets you do all sorts of cool things with animations in a recyclerview.
//Theres a few other ways to check for clicks on items in a recycler view, and a bit of googling will throw up some alternatives.
//A popular approach is to implement the listener in either the adapter or the view holder, and that can be easier to implement,
//depending on what you want to do when you get an event. Those approaches are well documented, but this one doesnt seem to be.
//But we use this method because its the one that Google suggest.


//All approaches to implement a recycler listener use a callback mechanism which is the usual way of notifying an activity that something is being clicked.
class RecyclerItemClickListener(context: Context, recyclerView: RecyclerView, private val listener: OnRecyclerClickListener)
    : RecyclerView.SimpleOnItemTouchListener(){
    //extendar .SimpleOnItemTouchListener vilket är den enkla versionen av clicklistener

    private val TAG = "RecyclerItemClickListen" //Obs max 23 bokstäver!

    //Callback metoderna
    interface OnRecyclerClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }

    //add the gestureDetector
    private val gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
        //So what we've done is very similar to adding an onClickListener to a button.
        //We're creating an anonymous class that extends a simple on Gesture Listener, then overriding the methods we're interested in.
        //Metoderna här returnar oftast true eller false, dvs ifall jag har hanterat den typen av touch event.
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            Log.d(TAG, ".onSingleTapUp: Starts")
            val childView = recyclerView.findChildViewUnder(e.x, e.y) //Hitta Child under coordinaterna
            Log.d(TAG, ".onSingleTapUp: calling listener.onItemClick")
            childView.let {
                listener.onItemClick(childView!!, recyclerView.getChildAdapterPosition(childView)) // skicka in vyn, och positionen
                //.onItemClick = interface metod
            }

            return true
        }

        override fun onLongPress(e: MotionEvent) {
            Log.d(TAG, ".onLongPress: Starts")
            val childView = recyclerView.findChildViewUnder(e.x, e.y)
            Log.d(TAG, ".onLongPress calling listener.onItemLongClick")
            listener.onItemLongClick(childView!!, recyclerView.getChildAdapterPosition(childView)) //.onItemLongClick = interface metod
        }
    })

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        Log.d(TAG, ".onInterceptTouchEvent: starts $e")
     //   return super.onInterceptTouchEvent(rv, e)
        //So what Ive done by using the return true, is intercepted the touch event, then told the system that we've handled every single event.
        //But now the recycler_view wont scroll because I told it that I've handled the touch events (when I haven't).
        //If we dont handle it we have to return false so that whatever else is listening can do its job.
        //In fact, if we dont return false we should return the result of calling the super method, because that gives anything else that
        //listens for events a chance to respond.
       // return true

        //Anything that the gestureDetectors ontouchEvent deals with should return true, anything it doesnt handle should return false,
        //So that something else can deal with it.
        val result = gestureDetector.onTouchEvent(e)
        Log.d(TAG, ".onInterceptTouchEvent() returning $result")
        return result //Kommer att lämna false ifall jag inte har hanterat eventen, vilket betyder att android då istället sköter det. true betyder att jag har löst det och att android inte lägger sig i.

        //Android framework calls the onInterceptTouchevent fun when a gesture is being performed on the screen, which then calls the
        //onTouch function of the gesture detector. That then calls an appropriate fun for the gesture in question, (onLongPress, onSingleTapUp)
        //which may or may not have been overriden by our anonymous gesture detector compat class.
        //But we want the onTouch to return false when we havent covered a certain gesture, else we for example wont be able to scroll
    }
}
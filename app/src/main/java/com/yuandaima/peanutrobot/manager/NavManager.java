package com.yuandaima.peanutrobot.manager;

import android.util.Log;

import com.keenon.sdk.component.navigation.PeanutNavigation;
import com.keenon.sdk.component.navigation.common.Navigation;
import com.keenon.sdk.component.navigation.route.RouteNode;
import com.yuandaima.peanutrobot.bean.MyPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class NavManager implements Navigation.Listener {
  public interface SessionNavigationListener {
    void onSessionStateChanged(int sessionGeneration, int state, int schedule);

    void onSessionRoutePrepared(int sessionGeneration, RouteNode... routeNodes);

    void onSessionError(int sessionGeneration, int code);
  }

  private static final String TAG = "NavManager";
  private static volatile NavManager mInstance;
  private List<RouteNode> nodeList = new ArrayList<>();
  private List<Navigation.Listener> listeners = new CopyOnWriteArrayList<>();
  private PeanutNavigation mPeanutNavigation;
  private List<MyPoint> targets = new ArrayList<>();
  private int blockTimeout;
  private int repeatCount;
  private boolean arrivalEnabled;
  private boolean initialized;
  private volatile int navigationSessionGeneration;

  private NavManager() {
  }

  public static NavManager getInstance() {
    if (mInstance == null) {
      synchronized (NavManager.class) {
        if (mInstance == null) {
          mInstance = new NavManager();
        }
      }
    }
    return mInstance;
  }


  /**
   * @param listener
   * @param timeOut       If timeout > 0 ,when the robot blocking for <timeOut> seconds , robot will callback  state<Navigation.STATE_BLOCKING> for you by the
   *                      listener ,you can do what you want to do,for example ,you can skip the tartget you are leading to  or ga back to orgin point;
   *                      If timeout = 0 ,do not use the timeout policy
   *                      Duration is : 0 ~ 300
   * @param repeatTime    Example:if you setTargets a-b-c , repeatTime = 2 ,it will be a-b-c-a-b-c ;
   *                      -1 is infinite
   * @param arrivalEnable true: open the arrival policy ,it means when the robot blocked within 1meter from the target point for 5seconds ,it will
   *                      be  recognized as destinationed ! callback is <Navigation.STATE_DESTINATION>,too.
   */
  public void init(Navigation.Listener listener, int timeOut, int repeatTime, boolean arrivalEnable) {
    listeners.clear();
    setListener(listener);
    blockTimeout = timeOut;
    repeatCount = repeatTime;
    arrivalEnabled = arrivalEnable;
    initialized = true;
    if (mPeanutNavigation == null) {
      createNavigationSession(repeatCount);
      Log.d(TAG, "init , mPeanutNavigation init  ");
    }
  }

  public int recreateNavigationSession(int sessionRepeatCount) {
    if (!initialized) {
      throw new IllegalStateException("NavManager must be initialized before creating a session");
    }
    navigationSessionGeneration++;
    PeanutNavigation oldNavigation = mPeanutNavigation;
    mPeanutNavigation = null;
    if (oldNavigation != null) {
      oldNavigation.release();
    }
    createNavigationSession(sessionRepeatCount);
    return navigationSessionGeneration;
  }

  public int getRepeatCount() {
    return repeatCount;
  }

  public int getNavigationSessionGeneration() {
    return navigationSessionGeneration;
  }

  private void createNavigationSession(int sessionRepeatCount) {
    int sessionGeneration = ++navigationSessionGeneration;
    PeanutNavigation.Builder builder = new PeanutNavigation.Builder()
        .setListener(new SdkSessionNavigationListener(sessionGeneration))
        .enableDefaultArrival(arrivalEnabled)
        .setRepeatCount(sessionRepeatCount)
        .setBlockingTimeOut(blockTimeout);
    mPeanutNavigation = builder.build();
  }

  private boolean isCurrentSession(int sessionGeneration) {
    return sessionGeneration == navigationSessionGeneration;
  }

  private final class SdkSessionNavigationListener implements Navigation.Listener {
    private final int sessionGeneration;

    private SdkSessionNavigationListener(int sessionGeneration) {
      this.sessionGeneration = sessionGeneration;
    }

    @Override
    public void onStateChanged(int state, int schedule) {
      if (isCurrentSession(sessionGeneration)) {
        dispatchSessionStateChanged(sessionGeneration, state, schedule);
      }
    }

    @Override
    public void onRouteNode(int index, RouteNode routeNode) {
      if (isCurrentSession(sessionGeneration)) {
        NavManager.this.onRouteNode(index, routeNode);
      }
    }

    @Override
    public void onRoutePrepared(RouteNode... routeNodes) {
      if (isCurrentSession(sessionGeneration)) {
        dispatchSessionRoutePrepared(sessionGeneration, routeNodes);
      }
    }

    @Override
    public void onDistanceChanged(float distance) {
      if (isCurrentSession(sessionGeneration)) {
        NavManager.this.onDistanceChanged(distance);
      }
    }

    @Override
    public void onError(int code) {
      if (isCurrentSession(sessionGeneration)) {
        dispatchSessionError(sessionGeneration, code);
      }
    }

    @Override
    public void onEvent(int event) {
      if (isCurrentSession(sessionGeneration)) {
        NavManager.this.onEvent(event);
      }
    }
  }

  private void dispatchSessionStateChanged(int sessionGeneration, int state, int schedule) {
    for (Navigation.Listener listener : listeners) {
      if (listener instanceof SessionNavigationListener) {
        ((SessionNavigationListener) listener).onSessionStateChanged(
            sessionGeneration,
            state,
            schedule
        );
      } else if (listener != null) {
        listener.onStateChanged(state, schedule);
      }
    }
  }

  private void dispatchSessionRoutePrepared(int sessionGeneration, RouteNode... routeNodes) {
    for (Navigation.Listener listener : listeners) {
      if (listener instanceof SessionNavigationListener) {
        ((SessionNavigationListener) listener).onSessionRoutePrepared(
            sessionGeneration,
            routeNodes
        );
      } else if (listener != null) {
        listener.onRoutePrepared(routeNodes);
      }
    }
  }

  private void dispatchSessionError(int sessionGeneration, int code) {
    for (Navigation.Listener listener : listeners) {
      if (listener instanceof SessionNavigationListener) {
        ((SessionNavigationListener) listener).onSessionError(sessionGeneration, code);
      } else if (listener != null) {
        listener.onError(code);
      }
    }
  }

  public MyPoint getCurNode() {
    if (mPeanutNavigation.getCurrentNode() == null) {
      return null;
    }
    int indexCur = mPeanutNavigation.getCurrentPosition();
    Log.d(TAG, "indexCur : " + indexCur);
    if (indexCur >= 0 && indexCur < targets.size()) {
      return targets.get(indexCur);
    }
    return null;
  }

  public MyPoint getNextNode() {
    if (mPeanutNavigation.getNextNode() == null) {
      return null;
    }
    int indexNext = mPeanutNavigation.getCurrentPosition() + 1;
    Log.d(TAG, "indeNex : " + indexNext);
    if (indexNext >= 0 && targets != null && targets.size() != 0) {
      return targets.get(indexNext % targets.size());
    }
    return null;
  }


  public List<MyPoint> getTargets() {
    return targets;
  }


  public void setTargets(List<MyPoint> targets) {
    Log.d(TAG, "setTargets" + targets);
    this.targets.clear();
    this.targets.addAll(targets);
    nodeList.clear();
    for (int i = 0; i < this.targets.size(); i++) {
      if (this.targets.get(i).getRouteNode() == null) {
        Log.d(TAG, "routeNode ==  " + nodeList);
      } else {
        Log.d(TAG, "nodelist.add  " + nodeList);
        nodeList.add(this.targets.get(i).getRouteNode());
      }
    }
    if (mPeanutNavigation != null) {
      Log.d(TAG, "set nodelist : " + nodeList);
      mPeanutNavigation.setTargets(nodeList);
    }
  }
  public RouteNode[] getRouteNodes(){
     return mPeanutNavigation.getRouteNodes();
  }

  public PeanutNavigation getmPeanutNavigation(){
    return  mPeanutNavigation;
  }

  public void prepare() {
    mPeanutNavigation.prepare();
  }

  /**
   * @param ready true ： start
   *              false ： pause
   */
  public void readyGo(boolean ready) {
    if (mPeanutNavigation != null) {
        Log.d("navigatenext","readyGo2=====");
      mPeanutNavigation.setPilotWhenReady(ready);
    }
  }

  public void stop() {
    if (mPeanutNavigation != null) {
      mPeanutNavigation.stop();
    }
  }

  public boolean isLastNode(){
      return  mPeanutNavigation.isLastNode();
  }
  /**
   * go to the next point
   */
  public void nextDes() {
    mPeanutNavigation.pilotNext();
  }

  /**
   * @param speed 20 ~100
   */
  public void setSpeed(int speed) {
    mPeanutNavigation.setSpeed(speed);
  }

  public void setListener(Navigation.Listener listener) {
    if (listener == null) {
      return;
    }
    listeners.add(listener);
  }

  public void removeListener(Navigation.Listener listener) {
    listeners.remove(listener);
  }

  public void release() {
    targets.clear();
    listeners.clear();
    navigationSessionGeneration++;
    if (mPeanutNavigation != null) {
      mPeanutNavigation.release();
      mPeanutNavigation = null;
    }
    initialized = false;
    mInstance = null;
  }


  public void setArrivalControlEnable(boolean enable) {
    mPeanutNavigation.setArrivalControlEnable(enable);
  }

  public boolean isLastRepeat() {
    return mPeanutNavigation.isLastRepeat();
  }

  @Override
  public void onStateChanged(int state, int schedule) {
    for (Navigation.Listener listener : listeners) {
      if (listener != null) {
        listener.onStateChanged(state, schedule);
      }
    }
  }

  @Override
  public void onRouteNode(int index, RouteNode routeNode) {
    Log.d(TAG, "onRouteNode ---->index : " + index + ",routeNode : " + routeNode.toString());
    for (Navigation.Listener listener : listeners) {
      if (listener != null) {
        listener.onRouteNode(index, routeNode);
      }
    }
  }

  @Override
  public void onRoutePrepared(RouteNode... routeNodes) {
    Log.d(TAG, "onRoutePrepared length: " + routeNodes.length);
    for (Navigation.Listener listener : listeners) {
      listener.onRoutePrepared(routeNodes);
    }
  }

  @Override
  public void onDistanceChanged(float distance) {
    Log.d(TAG, "onDistanceChanged : " + distance);
    for (Navigation.Listener listener : listeners) {
      listener.onDistanceChanged(distance);
    }
  }

  @Override
  public void onError(int code) {
    Log.d(TAG, "onError : " + code);
    for (Navigation.Listener listener : listeners) {
      listener.onError(code);
    }
  }

  @Override
  public void onEvent(int event) {

  }

}

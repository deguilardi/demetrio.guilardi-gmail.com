package ca.uqac.truckie.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import ca.uqac.truckie.MyUser;
import ca.uqac.truckie.R;
import ca.uqac.truckie.activities.DeliveryDetailsActivity;
import ca.uqac.truckie.activities.LoginActivity;
import ca.uqac.truckie.activities.MainActivity;

public class MyWidgetProvider extends AppWidgetProvider {

    private static final int REQUEST_CODE = 321;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action != null && action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, MyWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_list_view);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        MyUser.init();
        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent intent = new Intent(context, MyWidgetRemoteViewsService.class);
            remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);

            // The empty view is displayed when the collection has no items. It should be a sibling of the collection view.
//           TODO remoteViews.setEmptyView();

            // click event handler for the title, launches the app when the user clicks on title
            Intent titleIntent = new Intent(context, MyUser.isLogged() ? MainActivity.class :  LoginActivity.class);
            PendingIntent titlePendingIntent = PendingIntent.getActivity(context, 0, titleIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.txt_title, titlePendingIntent);

            // template to handle the click listener for each item
            Intent clickIntentTemplate = new Intent(context, DeliveryDetailsActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntentTemplate);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

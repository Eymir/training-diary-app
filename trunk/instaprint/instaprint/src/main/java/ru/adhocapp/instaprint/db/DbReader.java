package ru.adhocapp.instaprint.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.adhocapp.instaprint.db.entity.Order;
import ru.adhocapp.instaprint.db.entity.OrderStatus;

/**
 * Created by Lenovo on 22.06.2014.
 */
public class DbReader {
    private final DBHelper dbHelper;

    public DbReader(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public List<Order> getOrdersWithStatus(OrderStatus... statuses) {
        List<Order> list = new ArrayList<Order>();
        if (statuses != null && statuses.length > 0) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String sql = "";
            for (int i = 0; i < statuses.length; i++) {
                OrderStatus os = statuses[i];
                if (i == 0) {
                    sql = "SELECT ID, DATE FROM CLIENT_ORDER WHERE STATUS = '" + os.name() + "' ORDER BY DATE DESC; ";
                } else {
                    sql += "UNION ALL SELECT ID, DATE FROM CLIENT_ORDER WHERE STATUS = '" + os.name() + "'  ORDER BY DATE DESC; ";
                }
            }
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex("ID"));
                list.add(dbHelper.getOrderById(db, id));
            }
        }
        return list;
    }
}

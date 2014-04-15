package ru.adhocapp.instaprint.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.adhocapp.instaprint.db.entity.Address;
import ru.adhocapp.instaprint.db.entity.Entity;
import ru.adhocapp.instaprint.db.entity.EntityManager;
import ru.adhocapp.instaprint.db.entity.Order;
import ru.adhocapp.instaprint.db.entity.OrderStatus;
import ru.adhocapp.instaprint.db.entity.PurchaseDetails;
import ru.adhocapp.instaprint.util.Const;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper mInstance = null;

    private final static int DB_VERSION = 1;
    public final static String DATABASE_NAME = "InstaPrintDB";
    public final static String ORDER_TABLE = "CLIENT_ORDER";
    public final static String ADDRESS_TABLE = "ADDRESS";
    public final static String PURCHASE_DETAILS_TABLE = "PURCHASE_DETAILS";

    public Context CONTEXT;
    public EntityManager EM;

    public static DBHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        this.CONTEXT = context;
        this.EM = new EntityManager(this);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Const.LOG_TAG, "onDowngrade. oldVer: " + oldVersion + " newVer: " + newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAddressTable(db);
        createPurchaseDetailsTable(db);
        createOrderTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createOrderTable(SQLiteDatabase db) {
        db.execSQL("create table CLIENT_ORDER ("
                + "ID integer primary key autoincrement,"
                + "TEXT text,"
                + "STATUS text,"
                + "DATE datetime,"
                + "PHOTO_PATH text,"
                + "ADDRESS_FROM_ID integer,"
                + "ADDRESS_TO_ID integer,"
                + "PURCHASE_DETAILS_ID integer,"
                //Сначала не подумал что их может и не быть
//                + "FOREIGN KEY(ADDRESS_FROM_ID) REFERENCES ADDRESS(id),"
//                + "FOREIGN KEY(ADDRESS_TO_ID) REFERENCES ADDRESS(id),"
                + "FOREIGN KEY(PURCHASE_DETAILS_ID) REFERENCES PURCHASE_DETAILS(id)" + ");");
        Log.d(Const.LOG_TAG, "--- onCreate table CLIENT_ORDER  ---");
    }

    private void createAddressTable(SQLiteDatabase db) {
        db.execSQL("create table ADDRESS ("
                + "ID integer primary key autoincrement,"
                + "FULL_ADDRESS text,"
                + "FULL_NAME text,"
                + "ZIPCODE text" + ");");
        Log.d(Const.LOG_TAG, "--- onCreate table ADDRESS  ---");
    }

    private void createPurchaseDetailsTable(SQLiteDatabase db) {
        db.execSQL("create table PURCHASE_DETAILS ("
                + "ID integer primary key autoincrement,"
                + "ORDER_NUMBER text,"
                + "PAY_DATE datetime,"
                + "PRICE double" + ");");
        Log.d(Const.LOG_TAG, "--- onCreate table PURCHASE_DETAILS  ---");
    }


    public long insertAddress(SQLiteDatabase db, Address address) {
        ContentValues cv = new ContentValues();
        cv.put("FULL_ADDRESS", address.getFullAddress());
        cv.put("FULL_NAME", address.getFullName());
        cv.put("ZIPCODE", address.getZipCode());
        long id = db.insert(ADDRESS_TABLE, null, cv);
        return id;
    }

    public long insertOrder(SQLiteDatabase db, Order order) {
        ContentValues cv = new ContentValues();
        cv.put("TEXT", order.getText());
        if (order.getDate() != null)
            cv.put("DATE", order.getDate().getTime());
        cv.put("PHOTO_PATH", order.getPhotoPath());
        if (order.getAddressFrom() != null)
            cv.put("ADDRESS_FROM_ID", order.getAddressFrom().getId());
        if (order.getAddressTo() != null)
            cv.put("ADDRESS_TO_ID", order.getAddressTo().getId());
        if (order.getPurchaseDetails() != null)
            cv.put("PURCHASE_DETAILS_ID", order.getPurchaseDetails().getId());
        cv.put("STATUS", order.getStatus().name());
        long id = db.insert(ORDER_TABLE, null, cv);
        return id;
    }

    public long insertPurchaseDetails(SQLiteDatabase db, PurchaseDetails purchaseDetails) {
        ContentValues cv = new ContentValues();
        cv.put("ORDER_NUMBER", purchaseDetails.getOrderNumber());
        cv.put("PAY_DATE", purchaseDetails.getPayDate().getTime());
        cv.put("PRICE", purchaseDetails.getPrice());
        long id = db.insert(PURCHASE_DETAILS_TABLE, null, cv);
        return id;
    }

    public void delete(SQLiteDatabase db, Entity entity) {
        String table = "";
        if (entity instanceof Address) {
            table = ADDRESS_TABLE;
        }
        if (entity instanceof PurchaseDetails) {
            table = PURCHASE_DETAILS_TABLE;
        }
        if (entity instanceof Order) {
            table = ORDER_TABLE;
        }
        db.delete(table, "id = ?",
                new String[]{String.valueOf(entity.getId())});
    }

    public void updateAddress(SQLiteDatabase db, Address address) {
        ContentValues cv = new ContentValues();
        cv.put("FULL_ADDRESS", address.getFullAddress());
        cv.put("FULL_NAME", address.getFullName());
        cv.put("ZIPCODE", address.getZipCode());
        db.update(ADDRESS_TABLE, cv, "id = ? ",
                new String[]{String.valueOf(address.getId())});
    }


    public void updatePurchaseDetails(SQLiteDatabase db, PurchaseDetails purchaseDetails) {
        ContentValues cv = new ContentValues();
        cv.put("ORDER_NUMBER", purchaseDetails.getOrderNumber());
        cv.put("PAY_DATE", purchaseDetails.getPayDate().getTime());
        cv.put("PRICE", purchaseDetails.getPrice());
        db.update(PURCHASE_DETAILS_TABLE, cv, "id = ? ",
                new String[]{String.valueOf(purchaseDetails.getId())});

    }

    public void updateOrder(SQLiteDatabase db, Order order) {
        ContentValues cv = new ContentValues();
        cv.put("TEXT", order.getText());
        if (order.getDate() != null)
            cv.put("DATE", order.getDate().getTime());
        cv.put("PHOTO_PATH", order.getPhotoPath());
        if (order.getAddressFrom() != null)
            cv.put("ADDRESS_FROM_ID", order.getAddressFrom().getId());
        if (order.getAddressTo() != null)
            cv.put("ADDRESS_TO_ID", order.getAddressTo().getId());
        if (order.getPurchaseDetails() != null)
            cv.put("PURCHASE_DETAILS_ID", order.getPurchaseDetails().getId());
        cv.put("STATUS", order.getStatus().name());
        db.update(ORDER_TABLE, cv, "id = ? ",
                new String[]{String.valueOf(order.getId())});

    }

    public Address getAddressById(SQLiteDatabase db, Long primaryKey) {
        String sqlQuery = "select * from " + ADDRESS_TABLE +
                " where id = ? ";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(primaryKey)});
        if (c.moveToFirst()) {
            Long id = c.getLong(c.getColumnIndex("ID"));
            String full_address = c.getString(c.getColumnIndex("FULL_ADDRESS"));
            String full_name = c.getString(c.getColumnIndex("FULL_NAME"));
            String index = c.getString(c.getColumnIndex("ZIPCODE"));
            return new Address(id, full_address, index, full_name);
        }
        if (c != null) c.close();
        return null;
    }

    public PurchaseDetails getPurchaseDetailsById(SQLiteDatabase db, Long primaryKey) {
        String sqlQuery = "select * from " + PURCHASE_DETAILS_TABLE +
                " where id = ? ";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(primaryKey)});
        if (c.moveToFirst()) {
            Long id = c.getLong(c.getColumnIndex("ID"));
            String order_number = c.getString(c.getColumnIndex("ORDER_NUMBER"));
            Long pay_date = c.getLong(c.getColumnIndex("PAY_DATE"));
            Double price = c.getDouble(c.getColumnIndex("PRICE"));
            return new PurchaseDetails(id, order_number, new Date(pay_date), price);
        }
        if (c != null) c.close();
        return null;
    }

    public Order getOrderById(SQLiteDatabase db, Long primaryKey) {
        String sqlQuery = "select * from " + ORDER_TABLE +
                " where id = ? ";
        Cursor c = db
                .rawQuery(sqlQuery, new String[]{String.valueOf(primaryKey)});
        if (c.moveToFirst()) {
            Long id = c.getLong(c.getColumnIndex("ID"));
            String text = c.getString(c.getColumnIndex("TEXT"));
            String status = c.getString(c.getColumnIndex("STATUS"));
            String photo_path = c.getString(c.getColumnIndex("PHOTO_PATH"));
            Long date = c.getLong(c.getColumnIndex("DATE"));
            Long address_from_id = c.getLong(c.getColumnIndex("ADDRESS_FROM_ID"));
            Long address_to_id = c.getLong(c.getColumnIndex("ADDRESS_TO_ID"));
            Long purchase_details_id = c.getLong(c.getColumnIndex("PURCHASE_DETAILS_ID"));
            return new Order(id, getAddressById(db, address_from_id), getAddressById(db, address_to_id), text, photo_path, new Date(date), getPurchaseDetailsById(db, purchase_details_id), OrderStatus.valueOf(status));
        }
        if (c != null) c.close();
        return null;
    }

    public List getAllAddresses(SQLiteDatabase db) {
        List list = new ArrayList();
        String sqlQuery = "select * from " + ADDRESS_TABLE;
        Cursor c = db
                .rawQuery(sqlQuery, null);
        while (c.moveToNext()) {
            Long id = c.getLong(c.getColumnIndex("ID"));
            String full_address = c.getString(c.getColumnIndex("FULL_ADDRESS"));
            String full_name = c.getString(c.getColumnIndex("FULL_NAME"));
            String index = c.getString(c.getColumnIndex("ZIPCODE"));
            list.add(new Address(id, full_address, index, full_name));
        }
        if (c != null) c.close();
        return list;
    }

    public List getAllPurchaseDetails(SQLiteDatabase db) {
        List list = new ArrayList();
        String sqlQuery = "select * from " + PURCHASE_DETAILS_TABLE;
        Cursor c = db
                .rawQuery(sqlQuery, null);
        while (c.moveToNext()) {
            Long id = c.getLong(c.getColumnIndex("ID"));
            String order_number = c.getString(c.getColumnIndex("ORDER_NUMBER"));
            Long pay_date = c.getLong(c.getColumnIndex("PAY_DATE"));
            Double price = c.getDouble(c.getColumnIndex("PRICE"));
            list.add(new PurchaseDetails(id, order_number, new Date(pay_date), price));
        }
        if (c != null) c.close();
        return list;
    }

    public List getAllOrders(SQLiteDatabase db) {
        List list = new ArrayList();
        String sqlQuery = "select * from " + ORDER_TABLE;
        Cursor c = db
                .rawQuery(sqlQuery, null);
        while (c.moveToNext()) {
            Long id = c.getLong(c.getColumnIndex("ID"));
            String text = c.getString(c.getColumnIndex("TEXT"));
            String status = c.getString(c.getColumnIndex("STATUS"));
            String photo_path = c.getString(c.getColumnIndex("PHOTO_PATH"));
            Long date = c.getLong(c.getColumnIndex("DATE"));
            Long address_from_id = c.getLong(c.getColumnIndex("ADDRESS_FROM_ID"));
            Long address_to_id = c.getLong(c.getColumnIndex("ADDRESS_TO_ID"));
            Long purchase_details_id = c.getLong(c.getColumnIndex("PURCHASE_DETAILS_ID"));
            list.add(new Order(id, getAddressById(db, address_from_id), getAddressById(db, address_to_id), text, photo_path, new Date(date), getPurchaseDetailsById(db, purchase_details_id), OrderStatus.valueOf(status)));
        }
        if (c != null) c.close();
        return list;
    }
}
